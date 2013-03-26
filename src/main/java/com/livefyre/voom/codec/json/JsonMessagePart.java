package com.livefyre.voom.codec.json;

import java.util.Map;

import org.json.JSONObject;

import com.livefyre.voom.VoomMessagePart;

public class JsonMessagePart extends VoomMessagePart<JSONObject> {
    public JsonMessagePart(Map<String, String> headers, JSONObject body) {
        super(headers, body);
    }
    
    private JsonMessagePart(JsonMessagePart other) {
        super(other.getHeaders().toMap(), new JSONObject(other.getBody()));
    }
    
    @Override
    protected VoomMessagePart<JSONObject> clone() {
        return new JsonMessagePart(this);
    }

}
