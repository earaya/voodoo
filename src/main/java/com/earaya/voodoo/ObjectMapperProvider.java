package com.earaya.voodoo;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {

    @Override
    public ObjectMapper getContext(Class<?> type) {
        ObjectMapper mapper= new ObjectMapper();
        mapper.configure(Feature.INDENT_OUTPUT, true);
	    AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
	    mapper.setDeserializationConfig(mapper.copyDeserializationConfig().withAnnotationIntrospector(introspector));
	    mapper.setSerializationConfig(mapper.copySerializationConfig().withAnnotationIntrospector(introspector));
        return mapper;
    }
}
