package com.earaya.voodoo.assets;

import org.eclipse.jetty.util.resource.Resource;

import java.io.IOException;

public class ClassPathAssetsComponent extends AssetsComponent {

    public ClassPathAssetsComponent(String assetsPath) {
        super(assetsPath);
    }

    @Override
    protected Resource getBaseResource() throws IOException {
        return Resource.newClassPathResource(assetsPath);
    }
}
