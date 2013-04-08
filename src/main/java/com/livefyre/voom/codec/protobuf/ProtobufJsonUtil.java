package com.livefyre.voom.codec.protobuf;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
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
        	if (field.isRepeated() && input.getRepeatedFieldCount(field) == 0) {
        		continue;
        	}
            if (!field.isRepeated() && !input.hasField(field)) {
            	continue;
            }
            result.put(field.getName(), getFieldValue(input, field));
            
        }
        return result;
    }
    
    private JSONArray getRepeatedFieldValue(Message msg, FieldDescriptor field) throws JSONException {
        JSONArray result = new JSONArray();
        
        for (int i=0; i<msg.getRepeatedFieldCount(field); i++) {
            result.put(getFieldValueNotRepeated(msg, field, msg.getRepeatedField(field, i)));
        }
        
        return result;
    }
    
    private Object getFieldValueNotRepeated(Message msg, FieldDescriptor field, Object value) throws JSONException {
        if (field.getJavaType() == JavaType.MESSAGE) {
            return protobufToJson((Message)value);
        }
        if (field.getJavaType() == JavaType.ENUM) {
        	return ((EnumValueDescriptor) value).getNumber();
        }
        return value;
    }
    
    private Object getFieldValue(Message msg, FieldDescriptor field) throws JSONException {
        if (field.isRepeated()) {
            return getRepeatedFieldValue(msg, field);
        }
        return getFieldValueNotRepeated(msg, field, msg.getField(field));
    }
    
    /**
     * Converts a JSONObject to a Protobuf Message.
     * The input should contain fields that map to the Protobuf's
     * field names. Composite messages, enums, and basic types are handled.
     * 
     * @param input The data.
     * @param msgType The type of message to create.
     * @return
     * @throws JSONException
     * @throws ProtobufLoadError
     * @throws ClassNotFoundException
     */
    public Message jsonToProtobuf(JSONObject input, Class<? extends Message> msgType) throws JSONException, ProtobufLoadError, ClassNotFoundException {
        Message.Builder builder = loader.getProtoBuilder(msgType);
        Descriptor descr = builder.getDescriptorForType();
        
        for (FieldDescriptor field: descr.getFields()) {
        	// Check if this field is set in the input, skip if not provided.
            if (!input.has(field.getName())) {
                continue;
            }
            
            // Get the raw value, it will be converted to the appropriate type later.
            Object value = input.get(field.getName());

            // For repeated fields, cast to an array, and add the value for each member
            if (field.isRepeated()) {
                JSONArray arrValue = (JSONArray) value;
                for (int idx=0; idx<arrValue.length(); idx++) {
                    builder.addRepeatedField(field, getValueFromJson(field, arrValue.get(idx)));
                }
                continue;
            }

            // Non-repeated field, just set.
            builder.setField(field, getValueFromJson(field, value));
        }
        
        return builder.buildPartial();
    }
    
    /**
     * Converts JSON data into a format appropriate for setting
     * on a Protobuf model based on the type information contained in 
     * field.
     * 
     * @param field The Protobuf field containing the type information.
     * @param value The value.
     * @return
     * @throws ClassNotFoundException
     * @throws JSONException
     * @throws ProtobufLoadError
     */
    public Object getValueFromJson(FieldDescriptor field, Object value) throws ClassNotFoundException, JSONException, ProtobufLoadError {
    	// Composite fields must recursively call jsonToProtobuf
        if (field.getJavaType() == JavaType.MESSAGE) {
            Class<? extends Message> composedType = (Class<? extends Message>) loader.getProtoClass(field.getMessageType().getFullName());
            return jsonToProtobuf((JSONObject)value, composedType);
        }
        
        // Enum fields must fetch the EnumValueDesriptor for the integer input.
        if (field.getJavaType() == JavaType.ENUM) {
        	return field.getEnumType().findValueByNumber((int)value);
        }
        
        // All other fields can just set their value.
        return value;
    }
}
