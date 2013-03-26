package com.livefyre.voom;

import java.util.Map;
import java.util.Map.Entry;

public abstract class AbstractMessage {
    protected VoomHeaders headers;
    
    public AbstractMessage() {
        headers = new VoomHeaders();
    }
    
    public void putHeaders(Map<String, String> headers) {
        for (Entry<String, String> entry:headers.entrySet()) {
            putHeader(entry.getKey(), entry.getValue());
        }
    }
    
    public void putHeaders(VoomHeaders headers) {
        this.headers.putAll(headers.toMap());
    }

    public void putHeader(String key, String value) {
        headers.put(key, value);
    }

    public VoomHeaders getHeaders() {
        return headers;
    }
    
    public String getHeader(String key) {
        return headers.get(key);
    }
}
