package com.livefyre.voom.codec.protobuf;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;
import com.google.protobuf.Message;
import com.livefyre.voom.ProtobufLoader;
import com.livefyre.voom.ProtobufLoader.ProtobufLoadError;

public class ProtobufJsonUtil {
    private ProtobufLoader loader;
    
    public ProtobufJsonUtil(ProtobufLoader loader) {
        super();
        this.loader = loader;
    }

    public JSONObject protobufToJson(Message input) throws JSONException {
        JSONObject result = new JSONObject();        
        Descriptor desc = input.getDescriptorForType();
        
        for (FieldDescriptor field:desc.getFields()) {
            result.put(field.getFullName(), getFieldValue(input, field));
        }
        return result;
    }
    
    private JSONArray getRepeatedFieldValue(Message msg, FieldDescriptor field) throws JSONException {
        Message.Builder builder = msg.newBuilderForType();
        JSONArray result = new JSONArray();
        
        for (int i=0; i<builder.getRepeatedFieldCount(field); i++) {
            result.put(getFieldValueNotRepeated(msg, field, builder.getRepeatedField(field, i)));
        }
        
        return result;
    }
    
    private Object getFieldValueNotRepeated(Message msg, FieldDescriptor field, Object value) throws JSONException {
        if (field.getJavaType() == JavaType.MESSAGE) {
            return protobufToJson((Message)value);
        }
        return value;
    }
    
    private Object getFieldValue(Message msg, FieldDescriptor field) throws JSONException {
        Message.Builder builder = msg.newBuilderForType();
        if (field.isRepeated()) {
            return getRepeatedFieldValue(msg, field);
        }
        return getFieldValueNotRepeated(msg, field, builder.getField(field));
    }
    
    public Message jsonToProtobuf(JSONObject input, Class<? extends Message> msgType) throws JSONException, ProtobufLoadError, ClassNotFoundException {
        Message.Builder builder = loader.getProtoBuilder(msgType);
        Descriptor descr = builder.getDescriptorForType();
        
        for (FieldDescriptor field: descr.getFields()) {
            if (!input.has(field.getName())) {
                continue;
            }
            Object value = input.get(field.getName());
            
            if (field.getJavaType() == JavaType.MESSAGE) {
                Class<? extends Message> composedType = (Class<? extends Message>) loader.getProtoClass(field.getMessageType().getFullName());
                builder.setField(field, jsonToProtobuf((JSONObject)value, composedType));
                continue;
            }
            
            if (field.isRepeated()) {
                JSONArray arrValue = (JSONArray) value;
                for (int idx=0; idx<arrValue.length(); idx++) {
                    builder.setRepeatedField(field, idx, arrValue.get(idx));
                }
                continue;
            }
            builder.setField(field, value);
        }
        
        return builder.buildPartial();
    }
}
