package com.talis.jersey.config;

import org.codehaus.jackson.JsonNode;

import java.io.File;
import java.io.IOException;

public class ConfigFactory<T> {

    public static <T> ConfigFactory<T> forClass(Class<T> configClass) {
        return new ConfigFactory<>(configClass);
    }

    private final Class<T> configClass;
    private final JsonConfigReader jsonConfigReader;

    public ConfigFactory(Class<T> configClass) {
        this.configClass = configClass;
        this.jsonConfigReader = new JsonConfigReader();
    }

    public T buildConfig(File configFile) throws IOException {
        final JsonNode jsonNode = parse(configFile);
        return jsonConfigReader.readValue(jsonNode, configClass);
    }

    private JsonNode parse(File file) throws IOException {
        return jsonConfigReader.readValue(file, JsonNode.class);
    }
}
