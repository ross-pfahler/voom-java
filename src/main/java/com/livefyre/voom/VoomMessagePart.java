package com.livefyre.voom;

import java.util.Map;

import com.google.protobuf.Message;

public class VoomMessagePart {
	private Message message;
	private VoomHeaders headers;
	
	public VoomMessagePart(Map<String, String> headers, Message message) {
	    this.headers = new VoomHeaders(headers);
	    this.message = message;
	}
	
   public VoomMessagePart(VoomMessagePart other) {
       this(other.getHeaders().toMap(), 
               other.message.newBuilderForType().mergeFrom(other.message).build());
    }
	
	public Message getMessage() {
		return message;
	}
	
	public VoomHeaders getHeaders() {
		return headers;
	}
}
