package com.livefyre.voom.codec;

import java.io.IOException;
import java.util.List;

import javax.mail.internet.ContentType;

import com.livefyre.voom.VoomMessagePart;

public interface TypeCodec <EncodesTo, DecodesTo> {
    public EncodesTo encodePart(VoomMessagePart<DecodesTo> msg) throws IOException;
    public VoomMessagePart<DecodesTo> decodePart(ContentType contentType, EncodesTo data) throws IOException;
    
    public List<String> getDecodableTypes();
}
