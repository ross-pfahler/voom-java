package com.livefyre.voom.codec.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.mail.internet.ContentType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.livefyre.voom.VoomMessagePart;
import com.livefyre.voom.codec.TypeCodec;

public class JsonCodec implements TypeCodec <JSONArray, JSONObject> {

    @Override
    public JSONArray encodePart(VoomMessagePart<JSONObject> msg) {
        JSONArray container = new JSONArray();
        JSONObject headers = new JSONObject(msg.getHeaders().toMap());
        container.put(headers).put(msg.getBody());
        return container;
    }

    @Override
    public VoomMessagePart<JSONObject> decodePart(ContentType contentType, JSONArray data) throws IOException {
        try {
            return decodePartInternal(data);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }

    public VoomMessagePart<JSONObject> decodePartInternal(JSONArray data) throws JSONException {
        JSONObject jsonHeaders = data.getJSONObject(0);
        HashMap<String, String> headers = new HashMap<String, String>();
        
        for (String name:JSONObject.getNames(jsonHeaders)) {
            headers.put(name, jsonHeaders.getString(name));
        }
        
        return new JsonMessagePart(headers, data.getJSONObject(1));
    }

    @Override
    public List<String> getDecodableTypes() {
        return new ArrayList<String>(Arrays.asList("application/json"));
    }
}
