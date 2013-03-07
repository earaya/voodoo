package com.earaya.voodoo.config;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ConfigFactoryTest {

    @Test
    public void flatConfigTest() throws URISyntaxException, IOException {
        ConfigFactory<FlatConfig> flatConfigConfigFactory = ConfigFactory.forClass(FlatConfig.class);
        URI fileUri = FlatConfig.class.getResource("flat.conf.json").toURI();
        File file = new File(fileUri);
        FlatConfig flatConfig = flatConfigConfigFactory.buildConfig(file);

        assert (flatConfig != null);
        assert (flatConfig.aString.equals("someString"));
        assert (flatConfig.anInt == 3);
    }

    @Test
    public void nestedConfigTest() throws URISyntaxException, IOException {
        ConfigFactory<NestedConfig> nestedConfigConfigFactory = ConfigFactory.forClass(NestedConfig.class);
        URI fileUri = FlatConfig.class.getResource("nested.conf.json").toURI();
        File file = new File(fileUri);
        NestedConfig nestedConfig = nestedConfigConfigFactory.buildConfig(file);

        assert (nestedConfig != null);
        assert (nestedConfig.childConfig != null);
        assert (nestedConfig.childConfig.anInt == 4);
        assert (nestedConfig.aString.equals("aString"));
    }

    @Test
    public void flatConfigWithConstructorsTest() throws URISyntaxException, IOException {
        ConfigFactory<FlatConfigWithConstructors> flatConfigWithConstructorsConfigFactory =
                ConfigFactory.forClass(FlatConfigWithConstructors.class);
        URI fileUri = FlatConfigWithConstructors.class.getResource("flat.conf.json").toURI();
        File file = new File(fileUri);
        FlatConfigWithConstructors flatConfigWithConstructors =
                flatConfigWithConstructorsConfigFactory.buildConfig(file);

        assert (flatConfigWithConstructors != null);
        assert (flatConfigWithConstructors.aString.equals("someString"));
        assert (flatConfigWithConstructors.anInt == 3);
    }
}
