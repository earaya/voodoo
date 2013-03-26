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
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import java.util.Set;

@SuppressWarnings("UnusedDeclaration")
@RunWith(VoodooResourceRunner.class)
public abstract class ResourceTest {

    private JerseyTest test;
    private final Set<Object> singletons = Sets.newHashSet();

    protected abstract void setUpResources() throws Exception;

    protected void addSingleton(Object singleton) {
        singletons.add(singleton);
    }

    protected Client client() {
        return test.client();
    }

    @BeforeOnce
    public final void beforeTestClass() throws Exception {
        MockitoAnnotations.initMocks(this);
        setUpJersey();
    }

    @AfterOnce
    public final void afterTestClass() throws Exception {
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