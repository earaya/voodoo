package com.talis.jersey.config;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ConfigFactoryTest {

    @Test
    public void simpleConfigTest() throws URISyntaxException, IOException {
        ConfigFactory<SimpleConfig> simpleConfigConfigFactory = ConfigFactory.forClass(SimpleConfig.class);
        URI fileUri = SimpleConfig.class.getResource("simple.conf.json").toURI();
        File file = new File(fileUri);
        SimpleConfig simpleConfig = simpleConfigConfigFactory.buildConfig(file);

        assert(simpleConfig != null);
        assert(simpleConfig.aString.equals("someString"));
        assert(simpleConfig.anInt == 3);
    }

    @Test
    public void nestedConfigTest() throws URISyntaxException, IOException {
        ConfigFactory<NestedConfig> nestedConfigConfigFactory = ConfigFactory.forClass(NestedConfig.class);
        URI fileUri = SimpleConfig.class.getResource("nested.conf.json").toURI();
        File file = new File(fileUri);
        NestedConfig nestedConfig = nestedConfigConfigFactory.buildConfig(file);

        assert(nestedConfig != null);
        assert(nestedConfig.childConfig != null);
        assert(nestedConfig.childConfig.anInt == 4);
        assert(nestedConfig.aString.equals("aString"));
    }
}
