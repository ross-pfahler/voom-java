package com.livefyre.voom.codec.protobuf;

import javax.mail.BodyPart;

import com.google.protobuf.Message;
import com.livefyre.voom.codec.TypeCodec;
import com.livefyre.voom.codec.mime.MimeMessageCodec;

public class MimeProtobufMessageCodec extends MimeMessageCodec<Message> {
    public MimeProtobufMessageCodec(TypeCodec<BodyPart, Message> partCodec) {
        super(partCodec);
    }
}
