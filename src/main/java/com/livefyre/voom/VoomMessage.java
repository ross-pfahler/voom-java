    package com.livefyre.voom;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

public class VoomMessage<T> {
	private ArrayList<VoomMessagePart<T>> messages;
	private VoomHeaders headers;
	
	public VoomMessage() {
	    headers = new VoomHeaders();
	    messages = new ArrayList<VoomMessagePart<T>>();
	}
	
	public VoomMessage(VoomHeaders headers, ArrayList<VoomMessagePart<T>> messages) {
	    this(headers);
		this.messages.addAll(messages);
	}

    public VoomMessage(Map<String, String> headers, ArrayList<VoomMessagePart<T>> messages) {
        this(new VoomHeaders(headers), messages);
    }
    
    public VoomMessage(VoomHeaders headers) {
        this();
        this.headers = headers;
    }

	public VoomMessage(Map<String, String> headers) {
	    this(new VoomHeaders(headers));
	}
	
	public VoomMessage(VoomMessage<T> other) {
	    this();
	    headers.putAll(other.headers.toMap());
	    for (VoomMessagePart<T> part:other.getMessages()) {
	        messages.add(part.clone());
	    }
    }
	
	public void addMessage(VoomMessagePart<T> msg) {
		messages.add(msg);
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
        headers.put(key.toLowerCase(), value);
    }

	public ArrayList<VoomMessagePart<T>> getMessages() {
		return messages;
	}

	public VoomHeaders getHeaders() {
		return headers;
	}
	
	public String getHeader(String key) {
	    return headers.get(key);
	}
	
}
