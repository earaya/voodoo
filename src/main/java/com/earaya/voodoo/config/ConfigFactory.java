package com.earaya.voodoo.config;

import org.codehaus.jackson.JsonNode;

import java.io.File;
import java.io.IOException;

public class ConfigFactory<T> {

    private final Class<T> configClass;

    public static <T> ConfigFactory<T> forClass(Class<T> configClass) {
        return new ConfigFactory<>(configClass);
    }

    public ConfigFactory(Class<T> configClass) {
        this.configClass = configClass;
    }

    public T buildConfig(File configFile) throws IOException {
        final JsonNode jsonNode = parse(configFile);
        return JsonConfigReader.readValue(jsonNode, configClass);
    }

    private JsonNode parse(File file) throws IOException {
        return JsonConfigReader.readValue(file, JsonNode.class);
    }
}
