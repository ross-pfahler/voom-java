package com.livefyre.voom;

import java.io.IOException;
import java.io.InputStream;

import com.google.protobuf.GeneratedMessage.Builder;
import com.google.protobuf.Message;

public class ProtobufLoader {
    private String prefix;
    
    
    public ProtobufLoader(String prefix) {
        super();
        this.prefix = prefix;
        if (this.prefix == null) {
            this.prefix = "";
        }
    }

    public String getFullClassName(Message msg) {
        String javaPackage = msg.getDescriptorForType().getFile().getOptions().getJavaPackage();
        return (new StringBuilder(javaPackage))
                .append(".")
                .append(msg.getDescriptorForType().getFullName()).toString();
    }
    
	public Message load(String type, InputStream input) throws ProtobufLoadError {
		@SuppressWarnings("rawtypes")
		String fullName = (new StringBuilder(prefix)).append(type).toString();
		Builder builder = getProtoBuilder(getProtoClass(fullName));
		try {
			return builder.mergeFrom(input).build();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ProtobufLoadError();
		} 
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Class<? extends Message> getProtoClass(String protoClassName)
	{
	  String containerName = protoClassName.substring(0, protoClassName.lastIndexOf("."));
	  String className = protoClassName.substring(protoClassName.lastIndexOf(".") + 1);

	  try
	  {
	    Class container = Class.forName(containerName);
	    for (Class candidate : container.getDeclaredClasses()) {
	      if (candidate.getSimpleName().equals(className)) {
	        return (Class<? extends Message>)candidate;
	      }
	    }
	    throw new ClassNotFoundException();
	  } catch (ClassNotFoundException e) {
	    e.printStackTrace();
	  }
	  return null;
	}

	public Builder getProtoBuilder(Class<? extends Message> klass) throws ProtobufLoadError {
		try {
			return ((Builder)klass.getMethod("newBuilder", new Class[0]).invoke(null, new Object[0]));
		} catch (Exception e) {
			e.printStackTrace();
			throw new ProtobufLoadError();
			// TODO Auto-generated catch block
			
		}
	}
	
	public class ProtobufLoadError extends Exception {
		
	}

}