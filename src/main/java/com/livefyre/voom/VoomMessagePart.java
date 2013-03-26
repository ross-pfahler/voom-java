package com.livefyre.voom;

import java.util.HashMap;
import java.util.Map;

public abstract class VoomMessagePart<T> {
	protected T body;
	protected VoomHeaders headers;
	
	public VoomMessagePart(VoomHeaders headers, T body) {
	    this.headers = headers;
	    this.body = body;
	}

   public VoomMessagePart(Map<String, String> headers, T body) {
       this(new VoomHeaders(headers), body);
    }

   public VoomMessagePart(T body) {
       this(new HashMap<String, String>(), body);
    }

   protected abstract VoomMessagePart<T> clone();
   
//   {
//       this(other.getHeaders().toMap(), 
//               other.message.newBuilderForType().mergeFrom(other.message).build());
//    }

	public T getBody() {
		return body;
	}
	
	public VoomHeaders getHeaders() {
		return headers;
	}
}
