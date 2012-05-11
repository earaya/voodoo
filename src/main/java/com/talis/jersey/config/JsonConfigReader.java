package com.talis.jersey.config;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.File;
import java.io.IOException;

public class JsonConfigReader {

    private final JsonFactory jsonFactory;
    private final ObjectMapper mapper;

    public JsonConfigReader() {
        this.jsonFactory = new MappingJsonFactory();
        jsonFactory.enable(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT);
        jsonFactory.enable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        jsonFactory.enable(JsonGenerator.Feature.QUOTE_FIELD_NAMES);
        jsonFactory.enable(JsonParser.Feature.ALLOW_COMMENTS);
        jsonFactory.enable(JsonParser.Feature.AUTO_CLOSE_SOURCE);

        this.mapper = (ObjectMapper) jsonFactory.getCodec();
        mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(DeserializationConfig.Feature.READ_ENUMS_USING_TO_STRING);
    }

    /**
     * Deserializes the given {@link File} as an instance of the given type.
     *
     * @param src          a JSON {@link File}
     * @param valueType    the {@link Class} to deserialize {@code src} as
     * @param <T>          the type of {@code valueType}
     * @return the contents of {@code src} as an instance of {@code T}
     * @throws IOException if there is an error reading from {@code src} or parsing its contents
     */
    public <T> T readValue(File src, Class<T> valueType) throws IOException {
        return mapper.readValue(src, valueType);
    }

    /**
     * Deserializes the given {@link JsonNode} as an instance of the given type.
     *
     * @param root            a {@link JsonNode}
     * @param valueTypeRef    a {@link TypeReference} of the type to deserialize {@code src} as
     * @param <T>             the type of {@code valueTypeRef}
     * @return the contents of {@code src} as an instance of {@code T}
     * @throws IOException if there is an error mapping {@code src} to {@code T}
     */
    public <T> T readValue(JsonNode root, Class<T> klass) throws IOException {
        return mapper.readValue(root, klass);
    }
}
