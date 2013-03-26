package com.livefyre.voom.codec.protobuf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;

import org.apache.commons.codec.binary.Base64;

import com.google.protobuf.Message;
import com.livefyre.voom.ProtobufLoader;
import com.livefyre.voom.VoomMessagePart;
import com.livefyre.voom.ProtobufLoader.ProtobufLoadError;
import com.livefyre.voom.codec.TypeCodec;

public class MimeProtobufBinaryCodec implements TypeCodec<BodyPart, Message> {
    private ProtobufLoader loader;
    
    public MimeProtobufBinaryCodec(ProtobufLoader loader) {
        this.loader = loader;
    }

    public BodyPart encodePart(VoomMessagePart<Message> msg) throws IOException {
        try {
            return encodePartInternal(msg);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }

    private BodyPart encodePartInternal(VoomMessagePart<Message> msg) throws MessagingException {
        InternetHeaders headers = new InternetHeaders();
        headers.addHeader("Content-Transfer-Encoding", "base64");
        
        ContentType contentType = new ContentType();
        contentType.setPrimaryType("application");
        contentType.setSubType("vnd.google.protobuf");
        contentType.setParameter("proto", msg.getBody().getDescriptorForType().getFullName());
        headers.addHeader("content-type", contentType.toString());
        
        for (Map.Entry<String, String> entry : msg.getHeaders().toMap().entrySet()) {
            headers.addHeader(entry.getKey(), entry.getValue());
        }
        
        return new MimeBodyPart(headers, Base64.encodeBase64(msg.getBody().toByteArray()));
    }
    
    public VoomMessagePart<Message> decodePart(ContentType contentType, BodyPart data) throws IOException {
        try {
            return decodePartInternal(contentType, data);
        } catch (ProtobufLoadError | MessagingException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }

    public VoomMessagePart<Message> decodePartInternal(ContentType contentType, BodyPart data) throws ProtobufLoadError, IOException, MessagingException {
        Message body = loader.load(contentType.getParameter("proto"), data.getInputStream());
        return new ProtobufMessagePart(body);
    }
    
    public List<String> getDecodableTypes() {
        return new ArrayList<String>(Arrays.asList("application/vnd.google.protobuf"));
    }
}
