package com.livefyre.voom.codec.vnd;

import java.io.IOException;
import java.util.List;

import javax.mail.internet.ContentType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.protobuf.Message;
import com.livefyre.voom.ProtobufLoader;
import com.livefyre.voom.ProtobufLoader.ProtobufLoadError;
import com.livefyre.voom.VoomMessagePart;
import com.livefyre.voom.codec.TypeCodec;
import com.livefyre.voom.codec.protobuf.ProtobufJsonUtil;
import com.livefyre.voom.codec.protobuf.ProtobufMessagePart;

public class LivefyreJsonProtobufCodec implements TypeCodec<JSONArray, Message> {
    private ProtobufLoader loader;
    
    public LivefyreJsonProtobufCodec(ProtobufLoader loader) {
        super();
        this.loader = loader;
    }

    @Override
    public JSONArray encodePart(VoomMessagePart<Message> msg) throws IOException {
        try {
            return encodePartInternal(msg);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }
    
    private JSONArray encodePartInternal(VoomMessagePart<Message> msg) throws JSONException {
        JSONObject headers = new JSONObject();
        ContentType contentType = new ContentType();
        contentType.setPrimaryType("application");
        contentType.setSubType("application/vnd.livefyre.protobuf+json");
        contentType.setParameter("proto", msg.getBody().getDescriptorForType().getFullName());
        contentType.setParameter("serial", "map+name");
        headers.put("Content-Type", contentType.toString());
        headers.put("Content-Transfer-Encoding", "inline");
        
        JSONArray encoded = new JSONArray();
        encoded.put(headers);
        encoded.put(new ProtobufJsonUtil(loader).protobufToJson(msg.getBody()));
        return encoded;
    }

    
    @Override
    public VoomMessagePart<Message> decodePart(ContentType contentType, JSONArray data) throws IOException {
        try {
            return decodePartInternal(contentType, data);
        } catch (JSONException | ProtobufLoadError | ClassNotFoundException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }
    
    public VoomMessagePart<Message> decodePartInternal(ContentType contentType, JSONArray data) throws IOException, JSONException, ProtobufLoadError, ClassNotFoundException {
//        if (contentType.getParameter("serial") != "map+name") {
//            throw new IOException(String.format("Unable to decode %s, of content_type='%s'",
//                    contentType.getParameter("serial"), contentType.toString()));
//        }
        
        ProtobufJsonUtil jsonUtil = new ProtobufJsonUtil(loader);
        JSONObject bodyJson = data.getJSONObject(1);
        
        Message body = jsonUtil.jsonToProtobuf(bodyJson, 
                loader.getProtoClass(contentType.getParameter("proto")));
        return new ProtobufMessagePart(body);
    }

    @Override
    public List<String> getDecodableTypes() {
        // TODO Auto-generated method stub
        return null;
    }

}
