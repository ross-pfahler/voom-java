package com.livefyre.voom;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.mail.internet.ContentType;
import javax.mail.internet.ParseException;

public class VoomHeaders {
    private Map<String, String> headers;
    
    public VoomHeaders() {
        this.headers = new HashMap<String, String>();
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
        headers.put(key.toLowerCase(), value);
    }
    
    public String get(String key) {
        return headers.get(key);
    }
    
    public Map<String, String> toMap() {
        return new HashMap<String, String>(headers);
    }
    
    public ContentType contentType() {
        String ctype = headers.get("content-type");
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
        return headers.get("reply_to");
    }
    
    public String correlationId() {
        return headers.get("correlation_id");
    }
}
