package com.livefyre.voom.codec.mime;

import java.io.ByteArrayInputStream;
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
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.google.protobuf.Message;
import com.livefyre.voom.ProtobufLoader;
import com.livefyre.voom.ProtobufLoader.ProtobufLoadError;
import com.livefyre.voom.VoomMessagePart;
import com.livefyre.voom.VoomMessage;

public class MimeDecoder {
    private ProtobufLoader protobufLoader;
    
	public MimeDecoder(ProtobufLoader protobufLoader) {
        super();
        this.protobufLoader = protobufLoader;
    }

    public VoomMessagePart decode(Part msg) throws MessagingException, ProtobufLoader.ProtobufLoadError, IOException {
		String[] rawCtypes = msg.getHeader("Content-Type");
		if (rawCtypes.length == 0) {
			throw new MessagingException();
		}
		
		ContentType ctype = new ContentType(rawCtypes[0]);
		String protoType = ctype.getParameter("proto");
		
		Message proto = protobufLoader.load(protoType, msg.getInputStream());
		return new VoomMessagePart(getHeaders(msg), proto);
	}
	
	public VoomMessagePart decode(byte[] input) throws MessagingException, ProtobufLoader.ProtobufLoadError, IOException {
		return decode(decodeMime(input));
	}
	
	private Map<String, String> getHeaders(Part msg) throws MessagingException {
		HashMap<String, String> voomHeaders = new HashMap<String, String>();
		
		@SuppressWarnings("unchecked")
		ArrayList<Header> headers = Collections.list((Enumeration<Header>)msg.getAllHeaders());
		
		for (Header header:headers) {
			voomHeaders.put(header.getName(), header.getValue());
		}
		return voomHeaders;
	}
	
	public VoomMessage decodeMultipart(byte[] input) throws MessagingException, IOException, ProtobufLoadError {
		MimeMessage msg = decodeMime(input);
		Map<String, String> headers = getHeaders(msg);
		VoomMessage voomMsg = new VoomMessage(headers);
		
		BufferMultipartDataSource ds = new BufferMultipartDataSource(msg.getInputStream());
		MimeMultipart multipart = new MimeMultipart(ds);
		
		for (int i=0; i<multipart.getCount(); i++) {
			voomMsg.addMessage(decode(multipart.getBodyPart(i)));
		}
		return voomMsg;
	}
	
	private MimeMessage decodeMime(byte[] input) throws MessagingException {
		InputStream is = new ByteArrayInputStream(input);
		return new MimeMessage(Session.getDefaultInstance(new Properties()), is); 
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
