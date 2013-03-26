package com.livefyre.voom.codec;

import java.io.IOException;

import com.livefyre.voom.VoomMessage;

public interface MessageCodec <DecodesTo> {
    public byte[] encodeMessage(VoomMessage<DecodesTo> msg) throws IOException;
    public VoomMessage<DecodesTo> decodeMessage(byte[] data) throws IOException;
}
