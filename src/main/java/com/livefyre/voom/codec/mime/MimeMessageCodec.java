package com.livefyre.voom.codec.mime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.livefyre.voom.VoomMessage;
import com.livefyre.voom.VoomMessagePart;
import com.livefyre.voom.codec.impl.MessageCodec;
import com.livefyre.voom.codec.TypeCodec;

public class MimeMessageCodec<T> extends MessageCodec<BodyPart, T> {
    public MimeMessageCodec(TypeCodec<BodyPart, T> partCodec) {
        super(partCodec);
    }

    @Override
    public byte[] encodeMessage(VoomMessage<T> msg) throws IOException {
        try {
            return encodeMessageInternal(msg);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }
    
    private byte[] encodeMessageInternal(VoomMessage<T> msg) throws MessagingException, IOException {
        MimeMultipart mp = new MimeMultipart();
        
        for (VoomMessagePart<T> part:msg.getMessages()) {
            mp.addBodyPart(encodePart(part));
        }
        
        MimeMessage mimeMsg = new MimeMessage(Session.getDefaultInstance(new Properties()));
        mimeMsg.addHeader("content-type", "multipart/mixed");
        mimeMsg.setContent(mp);
        mimeMsg.removeHeader("Message-Id");
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        mimeMsg.writeTo(os);
        
        return os.toByteArray();
    }

    @Override
    public VoomMessage<T> decodeMessage(byte[] data) throws IOException {
        try {
            return decodeMessageInternal(data);
        } catch (MessagingException e) {
            throw new IOException(e);
        }
    }
    
    private VoomMessage<T> decodeMessageInternal(byte[] data) throws MessagingException, IOException {
        InputStream is = new ByteArrayInputStream(data);
        MimeMessage msg = new MimeMessage(Session.getDefaultInstance(new Properties()), is);
        
        Map<String, String> headers = getMimeHeaders(msg);
        VoomMessage<T> voomMsg = new VoomMessage<T>(headers);
        
        BufferMultipartDataSource ds = new BufferMultipartDataSource(msg.getInputStream());
        MimeMultipart multipart = new MimeMultipart(ds);
        
        for (int i=0; i<multipart.getCount(); i++) {
            BodyPart part = multipart.getBodyPart(i);
            voomMsg.addMessage(decodePart(new ContentType(part.getContentType()), part));
        }
        return voomMsg;        
    }
    
    private Map<String, String> getMimeHeaders(MimeMessage msg) throws MessagingException {
        HashMap<String, String> voomHeaders = new HashMap<String, String>();
        
        @SuppressWarnings("unchecked")
        ArrayList<Header> headers = Collections.list((Enumeration<Header>)msg.getAllHeaders());
        
        for (Header header:headers) {
            voomHeaders.put(header.getName(), header.getValue());
        }
        return voomHeaders;
    }
    
    private class BufferMultipartDataSource implements DataSource {
        InputStream inputStream;
        
        public BufferMultipartDataSource(InputStream is) throws IOException {
            inputStream = is;
        }
        public InputStream getInputStream() throws IOException {
            return inputStream;
        }
        public OutputStream getOutputStream() throws IOException {
            return null;
        }
        public String getContentType() {
            return "multipart/mixed";
        }
        public String getName() {
            return "BufferMultipartDataSource";
        }
    }
    
}
