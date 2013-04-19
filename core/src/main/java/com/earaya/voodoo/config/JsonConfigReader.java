package com.earaya.voodoo.config;

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

    private static final JsonFactory JSON_FACTORY;
    private static final ObjectMapper JSON_MAPPER;

    static {
        JSON_FACTORY = new MappingJsonFactory();
        JSON_FACTORY.enable(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT);
        JSON_FACTORY.enable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        JSON_FACTORY.enable(JsonGenerator.Feature.QUOTE_FIELD_NAMES);
        JSON_FACTORY.enable(JsonParser.Feature.ALLOW_COMMENTS);
        JSON_FACTORY.enable(JsonParser.Feature.AUTO_CLOSE_SOURCE);

        JSON_MAPPER = (ObjectMapper) JSON_FACTORY.getCodec();
        JSON_MAPPER.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        JSON_MAPPER.disable(DeserializationConfig.Feature.READ_ENUMS_USING_TO_STRING);
    }

    /**
     * Deserializes the given {@link File} as an instance of the given type.
     *
     * @param src       a JSON {@link File}
     * @param valueType the {@link Class} to deserialize {@code src} as
     * @param <T>       the type of {@code valueType}
     * @return the contents of {@code src} as an instance of {@code T}
     * @throws IOException if there is an error reading from {@code src} or parsing its contents
     */
    public static <T> T readValue(File src, Class<T> valueType) throws IOException {
        return JSON_MAPPER.readValue(src, valueType);
    }

    /**
     * Deserializes the given {@link JsonNode} as an instance of the given type.
     *
     * @param root  a {@link JsonNode}
     * @param klass a {@link TypeReference} of the type to deserialize {@code src} as
     * @param <T>   the type of {@code valueTypeRef}
     * @return the contents of {@code src} as an instance of {@code T}
     * @throws IOException if there is an error mapping {@code src} to {@code T}
     */
    public static <T> T readValue(JsonNode root, Class<T> klass) throws IOException {
        return JSON_MAPPER.readValue(root, klass);
    }
}
