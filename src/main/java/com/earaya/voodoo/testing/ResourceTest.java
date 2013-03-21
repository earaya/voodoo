package com.earaya.voodoo.testing;

import com.earaya.voodoo.components.JacksonMessageBodyProvider;
import com.earaya.voodoo.exceptions.DefaultExceptionMapper;
import com.google.common.collect.Sets;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.LowLevelAppDescriptor;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.util.Set;

public abstract class ResourceTest extends PowerMockTestCase {

    private JerseyTest test;
    private final Set<Object> singletons = Sets.newHashSet();

    protected abstract void setUpResources() throws Exception;

    protected void addSingleton(Object singleton) {
        singletons.add(singleton);
    }

    protected Client client() {
        return test.client();
    }

    @BeforeClass
    @Override
    protected void beforePowerMockTestClass() throws Exception {
        super.beforePowerMockTestClass();
        setUpJersey();
    }

    @AfterClass
    @Override
    protected void afterPowerMockTestClass() throws Exception {
        super.afterPowerMockTestClass();
        tearDownJersey();
    }

    protected void setUpJersey() throws Exception {
        setUpResources();

        this.test = new JerseyTest() {

            @Override
            protected AppDescriptor configure() {
                DefaultResourceConfig resourceConfig = new DefaultResourceConfig();
                // Features
                resourceConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);

                // Voodoo "Providers"
                resourceConfig.getSingletons().add(new JacksonMessageBodyProvider());
                resourceConfig.getSingletons().add(new DefaultExceptionMapper());

                resourceConfig.getSingletons().addAll(singletons);

                return new LowLevelAppDescriptor.Builder(resourceConfig).build();
            }
        };

        test.setUp();
    }

    protected void tearDownJersey() throws Exception {
        if (test != null) {
            test.tearDown();
        }
    }

}
