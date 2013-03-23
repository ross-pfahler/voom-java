package com.livefyre.voom;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.protobuf.Message;
import com.livefyre.voom.ProtobufLoader.ProtobufLoadError;
import com.livefyre.voom.pb.test.TestMessage;

@RunWith(JUnit4.class)
public class TestProtobufLoader {
    public ProtobufLoader loader = new ProtobufLoader(null);
    
    @Test
    public void testLoad() throws ProtobufLoadError {
        TestMessage.Builder builder = TestMessage.newBuilder();
        builder.setAString("value");
        
        TestMessage msg = builder.build();
        
        ByteArrayInputStream is = new ByteArrayInputStream(msg.toByteArray());
        
        TestMessage decoded = (TestMessage)loader.load(loader.getFullClassName(msg), is);
        
        assertEquals(decoded.getAString(), "value");
        
    }
}
