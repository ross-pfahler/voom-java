package com.livefyre.voom.codec.mime;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;

import javax.mail.MessagingException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.livefyre.voom.ProtobufLoader;
import com.livefyre.voom.ProtobufLoader.ProtobufLoadError;
import com.livefyre.voom.VoomMessage;
import com.livefyre.voom.VoomMessagePart;
import com.livefyre.voom.pb.test.TestMessage;
import com.livefyre.voom.pb.test.TestMessage.TestComposedMessage;

@RunWith(JUnit4.class)
public class TestMimeCodec {
    public ProtobufLoader protoLoader;
    public MimeDecoder decoder;
    public MimeEncoder encoder;
    
    @Before
    public void setUp() {
        protoLoader = new ProtobufLoader("com.livefyre.");
        decoder = new MimeDecoder(protoLoader);
        encoder = new MimeEncoder();
    }
    
    @Test
    public void testDecode() throws MessagingException, IOException, ProtobufLoadError {
        String testMime = "Content-Type: multipart/mixed; boundary=\"===============3998256615686540032==\"\nMIME-Version: 1.0\n\n--===============3998256615686540032==\nMIME-Version: 1.0\nContent-Transfer-Encoding: base64\nContent-Type: application/vnd.google.protobuf; proto=\"voom.pb.test.TestMessage\"\n\nCgNzdHIQAxoNCgtjb21wb3NlZFN0cg==\n\n--===============3998256615686540032==--";
        VoomMessage msg = decoder.decodeMultipart(testMime.getBytes());
        assertEquals(msg.getMessages().size(), 1);
        
        TestMessage pb_msg = (TestMessage)msg.getMessages().get(0).getMessage();
        assertEquals(pb_msg.getAnInt(), 3);
        assertEquals(pb_msg.getAString(), "str");
    }
    
    @Test
    public void testEncode() throws MessagingException, IOException {
        TestMessage.Builder builder = TestMessage.newBuilder();
        builder.setAnInt(3);
        builder.setAString("str");
        TestComposedMessage.Builder composed = TestComposedMessage.newBuilder();
        composed.setAString("composedStr");
        builder.setComposed(composed.build());
        
        VoomMessage msg = new VoomMessage(new HashMap<String, String>());
        VoomMessagePart part = new VoomMessagePart(null, builder.build());
        msg.addMessage(part);
        
        String res = new String(encoder.encodeMultipart(msg));
        assertTrue(res.contains("CgNzdHIQAxoNCgtjb21wb3NlZFN0cg=="));
    }
}
