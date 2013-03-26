package com.livefyre.voom.codec.impl;

import java.io.IOException;

import javax.mail.internet.ContentType;

import com.livefyre.voom.VoomMessage;
import com.livefyre.voom.VoomMessagePart;
import com.livefyre.voom.codec.TypeCodec;

public abstract class MessageCodec <EncodesTo, DecodesTo> implements com.livefyre.voom.codec.MessageCodec<DecodesTo>{
    private TypeCodec<EncodesTo, DecodesTo> partCodec;
    
    public MessageCodec(TypeCodec<EncodesTo, DecodesTo> partCodec) {
        this.partCodec = partCodec;
    }
    abstract public byte[] encodeMessage(VoomMessage<DecodesTo> msg) throws IOException;
    abstract public VoomMessage<DecodesTo> decodeMessage(byte[] data) throws IOException;
    
    protected EncodesTo encodePart (VoomMessagePart<DecodesTo> part) throws IOException {
        return partCodec.encodePart(part);
    }

    protected VoomMessagePart<DecodesTo> decodePart(ContentType contentType, EncodesTo data) throws IOException {
        return partCodec.decodePart(contentType, data);
    }
}
