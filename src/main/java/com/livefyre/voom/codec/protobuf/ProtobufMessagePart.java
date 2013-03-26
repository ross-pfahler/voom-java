package com.livefyre.voom.codec.protobuf;

import java.util.Map;

import com.google.protobuf.Message;
import com.livefyre.voom.VoomMessagePart;

public class ProtobufMessagePart extends VoomMessagePart<Message> {
    public ProtobufMessagePart(Map<String, String> headers, Message body) {
        super(headers, body);
    }

    public ProtobufMessagePart(Message body) {
        super(body);
    }

    private ProtobufMessagePart(ProtobufMessagePart other) {
        super(other.getHeaders().toMap(), 
                other.getBody().newBuilderForType().mergeFrom(other.getBody()).build());
    }
    

    @Override
    protected VoomMessagePart<Message> clone() {
        return new ProtobufMessagePart(this);
    }

}
