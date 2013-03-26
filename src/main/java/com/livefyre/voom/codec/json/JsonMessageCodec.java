package com.livefyre.voom.codec.json;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.livefyre.voom.VoomMessage;
import com.livefyre.voom.VoomMessagePart;
import com.livefyre.voom.codec.impl.MessageCodec;
import com.livefyre.voom.codec.TypeCodec;

public class JsonMessageCodec<T> extends MessageCodec <JSONObject, T> {

    public JsonMessageCodec(TypeCodec<JSONObject, T> partCodec) {
        super(partCodec);
    }

    @Override
    public byte[] encodeMessage(VoomMessage<T> msg) throws IOException {
        try {
            return encodeMessageInternal(msg);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }
    
    private byte[] encodeMessageInternal(VoomMessage<T> msg) throws IOException, JSONException {
        JSONArray container = new JSONArray();
        JSONObject headers = new JSONObject();
        
        for (Entry<String, String> header:msg.getHeaders().toMap().entrySet()) {
            headers.put(header.getKey(), header.getValue());
        }
        
        container.put(headers);
        
        for (VoomMessagePart<T> part: msg.getMessages()) {
            container.put(encodePart(part));
        }
        
        StringWriter sw = new StringWriter();
        return container.write(sw).toString().getBytes();
    }

    @Override
    public VoomMessage<T> decodeMessage(byte[] data) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

}
