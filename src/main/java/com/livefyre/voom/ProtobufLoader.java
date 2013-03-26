package com.livefyre.voom;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.protobuf.Message;

public class ProtobufLoader {
    public String prefix;
    
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
		Message.Builder builder;
        try {
            builder = getProtoBuilder(getProtoClass(type));
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
            return null;
        }
		try {
			return builder.mergeFrom(input).build();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ProtobufLoadError();
		} 
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Class<? extends Message> getProtoClass(String protoClassName) throws ClassNotFoundException {
      String fullName = (new StringBuilder(prefix)).append(protoClassName).toString();
      
	  String containerName = fullName.substring(0, fullName.lastIndexOf("."));
	  ArrayList<String> classNames = new ArrayList<String>(Arrays.asList(fullName.substring(fullName.lastIndexOf(".") + 1)));
	          
	  Class container;
	  
	  while (true) {
	      try {
            container = Class.forName(containerName);
        } catch (ClassNotFoundException e) {
            classNames.add(0, containerName.substring(containerName.lastIndexOf(".") + 1));
            containerName = containerName.substring(0, containerName.lastIndexOf("."));
            if (containerName == "") {
                throw e; 
            }
            continue;
        }
	    break;
	  }
	  return loadClassFromContainer(container, classNames);
   }

	
	public Class<? extends Message> loadClassFromContainer(Class<?> container, ArrayList<String> classNames) {
	  String className = classNames.remove(0);
	    
	  try
	  {
	    for (Class candidate : container.getDeclaredClasses()) {
	      if (candidate.getSimpleName().equals(className)) {
	          if (classNames.isEmpty()) {
	              return (Class<? extends Message>)candidate;
	          }
	          return loadClassFromContainer(candidate, classNames);
	      }
	    }
	    throw new ClassNotFoundException();
	  } catch (ClassNotFoundException e) {
	    e.printStackTrace();
	  }
	  return null;
	}

	public Message.Builder getProtoBuilder(Class<? extends Message> klass) throws ProtobufLoadError {
		try {
			return ((Message.Builder)klass.getMethod("newBuilder", new Class[0]).invoke(null, new Object[0]));
		} catch (Exception e) {
			e.printStackTrace();
			throw new ProtobufLoadError();
			// TODO Auto-generated catch block
			
		}
	}
	
	public class ProtobufLoadError extends Exception {
		
	}

}