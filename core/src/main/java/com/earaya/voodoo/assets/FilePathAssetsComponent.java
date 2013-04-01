package com.earaya.voodoo.assets;

import org.eclipse.jetty.util.resource.Resource;

import java.io.IOException;

public class FilePathAssetsComponent extends AssetsComponent {

    public FilePathAssetsComponent(String assetsPath) {
        super(assetsPath);
    }

    @Override
    protected Resource getBaseResource() throws IOException {
        return Resource.newResource(assetsPath);
    }
}
