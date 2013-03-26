package com.livefyre.voom.codec.json;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.livefyre.voom.VoomHeaders;

public class JsonUtils {
    public static VoomHeaders getHeaders(JSONObject jsonHeaders) throws JSONException {
        Map<String, String> headers = new HashMap<String, String>();
        
        for (String name: JSONObject.getNames(jsonHeaders)) {
            headers.put(name, jsonHeaders.getString(name));
        }
        
        return new VoomHeaders(headers);
    }
}
