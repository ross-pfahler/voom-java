package com.livefyre.voom;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import com.livefyre.voom.ProtobufLoader.ProtobufLoadError;
import com.livefyre.voom.codec.protobuf.ProtobufJsonUtil;
import com.livefyre.voom.pb.test.TestMessage;

public class TestProtobufJsonUtil {
    @Test
    public void testJsonToProtobuf() throws JSONException, ProtobufLoadError, ClassNotFoundException {
        ProtobufLoader loader = new ProtobufLoader("com.livefyre.voom.pb.");
        ProtobufJsonUtil pbJson = new ProtobufJsonUtil(loader);
        
        JSONObject testJson = new JSONObject("{\"aString\": \"aValue\", \"composed\": {\"aString\": \"aComposedValue\"}}");
        pbJson.jsonToProtobuf(testJson, TestMessage.class);
    }
    
    @Test
    public void testEmptyJson() throws JSONException, ProtobufLoadError, ClassNotFoundException {
        ProtobufLoader loader = new ProtobufLoader("com.livefyre.voom.pb.");
        ProtobufJsonUtil pbJson = new ProtobufJsonUtil(loader);
        
        JSONObject testJson = new JSONObject("{}");
        pbJson.jsonToProtobuf(testJson, TestMessage.class);
    }

}
