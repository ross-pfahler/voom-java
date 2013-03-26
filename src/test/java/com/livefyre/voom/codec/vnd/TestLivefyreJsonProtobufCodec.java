package com.livefyre.voom.codec.vnd;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.mail.internet.ContentType;
import javax.mail.internet.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.google.protobuf.Message;
import com.livefyre.voom.ProtobufLoader;
import com.livefyre.voom.VoomMessagePart;
import com.livefyre.voom.codec.protobuf.ProtobufMessagePart;
import com.livefyre.voom.pb.test.TestMessage;

public class TestLivefyreJsonProtobufCodec {
    public LivefyreJsonProtobufCodec codec;
    
    @Before
    public void setUp() {
        ProtobufLoader loader = new ProtobufLoader("com.livefyre.voom.pb.");
        codec = new LivefyreJsonProtobufCodec(loader);
    }
    
    @Test
    public void testEncodePart() throws IOException, JSONException {
        TestMessage.Builder msg = TestMessage.newBuilder();
        msg.setAString("value");
        msg.setAnInt(123);
        
        ProtobufMessagePart part = new ProtobufMessagePart(msg.build());
        part.putHeader("someKey", "someValue");
        part.putHeader("anotherKey", "anotherValue");
        
        JSONArray result = codec.encodePart(part);
        assertEquals(result.length(), 2);
        JSONObject jsonHeaders = result.getJSONObject(0);
        assertEquals(jsonHeaders.length(), 2);
        assertEquals("application/vnd.livefyre.protobuf+json; proto=test.TestMessage; serial=map+name", jsonHeaders.getString("Content-Type").replaceAll("\r\n",  "").replaceAll("\t", ""));
        assertEquals("inline", jsonHeaders.getString("Content-Transfer-Encoding"));
        
        JSONObject jsonData = result.getJSONObject(1);
        assertEquals(jsonData.length(), 2);
        assertEquals(jsonData.getString("aString"), "value");
        assertEquals(jsonData.getInt("anInt"), 123);
    }
    
    @Test
    public void testDecodePart() throws ParseException, IOException, JSONException {
        String cType = "application/vnd.livefyre.protobuf+json; proto=test.TestMessage; serial=map+name";
        String data = "[" +
        		"{" +
        		    "\"Content-Type\": \"" + cType + "\"," +
        		    "\"Content-Transfer-Encoding\": \"inline\"" +
        		"}," +
        		"{" +
        		    "\"aString\": \"aValue\"" +
    		    "}" +
		    "]";
        
        VoomMessagePart<Message> result = codec.decodePart(new ContentType(cType), new JSONArray(data));
        Message body = result.getBody();
        assertEquals("test.TestMessage", body.getDescriptorForType().getFullName());
        assertEquals(body.getField(TestMessage.getDescriptor().findFieldByName("aString")), "aValue");
        
    }
}
