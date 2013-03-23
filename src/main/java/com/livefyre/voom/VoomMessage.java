package com.livefyre.voom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.mail.internet.ContentType;
import javax.mail.internet.ParseException;

import com.google.protobuf.Message;


public class VoomMessage {
	private ArrayList<VoomMessagePart> messages;
	private VoomHeaders headers;
	
	public VoomMessage() {
	    headers = new VoomHeaders();
	    messages = new ArrayList<VoomMessagePart>();
	}
	
	public VoomMessage(Map<String, String> headers, ArrayList<VoomMessagePart> messages) {
	    this(headers);
		this.messages.addAll(messages);
	}
	
	public VoomMessage(Map<String, String> headers) {
	    this();
	    this.headers.putAll(headers);
	}
	
	public VoomMessage(VoomMessage other) {
	    this();
	    headers.putAll(other.headers.toMap());
	    for (VoomMessagePart part:other.getMessages()) {
	        messages.add(new VoomMessagePart(part));
	    }
    }
	
	public void addMessage(VoomMessagePart msg) {
		messages.add(msg);
	}
	
    public void addMessage(Message msg) {
        messages.add(new VoomMessagePart(null, msg));
    }
    
    public void putHeaders(Map<String, String> headers) {
        for (Entry<String, String> entry:headers.entrySet()) {
            putHeader(entry.getKey(), entry.getValue());
        }
    }
    
    public void putHeader(String key, String value) {
        headers.put(key.toLowerCase(), value);
    }

	public ArrayList<VoomMessagePart> getMessages() {
		return messages;
	}

	public VoomHeaders getHeaders() {
		return headers;
	}
	
}
