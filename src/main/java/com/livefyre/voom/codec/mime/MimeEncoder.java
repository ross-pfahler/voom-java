package com.livefyre.voom.codec.mime;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.codec.binary.Base64;

import com.livefyre.voom.VoomMessagePart;
import com.livefyre.voom.VoomMessage;

public class MimeEncoder {
	public MimeBodyPart encode(VoomMessagePart msg) throws MessagingException {
		InternetHeaders headers = new InternetHeaders();
		
		if (msg.getHeaders().contentType() == null) {
			ContentType ctype = new ContentType();
			ctype.setPrimaryType("application");
			ctype.setSubType("vnd.google.protobuf");
			ctype.setParameter("proto", msg.getMessage().getDescriptorForType().getFullName());
			headers.addHeader("content-type", ctype.toString());
		}
		
		headers.addHeader("Content-Transfer-Encoding", "base64");
		
		for (Map.Entry<String, String> entry : msg.getHeaders().toMap().entrySet()) {
			headers.addHeader(entry.getKey(), entry.getValue());
		}
		
		byte[] encoded = msg.getMessage().toByteArray();
		return new MimeBodyPart(headers, Base64.encodeBase64(encoded));
	}
	
	public byte[] encodeMultipart(VoomMessage msgMp) throws MessagingException, IOException {
		MimeMultipart mp = new MimeMultipart();
		
		for (VoomMessagePart msg:msgMp.getMessages()) {
			mp.addBodyPart(encode(msg));
		}
		
		MimeMessage mimeMsg = new MimeMessage(Session.getDefaultInstance(new Properties()));
		mimeMsg.addHeader("content-type", "multipart/mixed");
		mimeMsg.setContent(mp);
		mimeMsg.removeHeader("Message-Id");
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		mimeMsg.writeTo(os);
		
		return os.toByteArray();
	}
	
}
