package com.livefyre.voom;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.mail.internet.ContentType;
import javax.mail.internet.ParseException;

public class VoomHeaders {
    private Map<String, String> valueMap;
    private Map<String, String> keyMap;
    
    public VoomHeaders() {
        this.valueMap = new HashMap<String, String>();
        this.keyMap = new HashMap<String, String>();
    }

    public VoomHeaders(Map<String, String> headers) {
        this();
        if (headers != null) {
            putAll(headers);
        }
    }
        
    public void putAll(Map<String, String> headers) {
        for (Entry<String, String> entry:headers.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }
    
    public void put(String key, String value) {
        if (value == null) {
            return;
        }
        valueMap.put(key.toLowerCase(), value);
        keyMap.put(key.toLowerCase(), key);
    }
    
    public void putInt(String key, Integer value) {
        if (value == null) {
            return;
        }
        put(key, value.toString());
    }
    
    public String get(String key) {
        return valueMap.get(key.toLowerCase());
    }
    
    public Map<String, String> toMap() {
        Map<String, String> result = new HashMap<String, String>();
        for (Entry<String, String> entry:valueMap.entrySet()) {
            result.put(keyMap.get(entry.getKey()), entry.getValue());
        }
        return result;
    }
    
    public ContentType contentType() {
        String ctype = get("content-type");
        if (ctype == null) {
            return null;
        }
        try {
            return new ContentType(ctype);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
 
    public String replyTo() {
        return get("reply_to");
    }
    
    public String correlationId() {
        return get("correlation_id");
    }
}
