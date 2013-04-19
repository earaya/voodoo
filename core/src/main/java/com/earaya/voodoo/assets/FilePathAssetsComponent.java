package com.earaya.voodoo.assets;

import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class FilePathAssetsComponent extends AssetsComponent {

    private final Logger logger = LoggerFactory.getLogger(FilePathAssetsComponent.class);

    public FilePathAssetsComponent(String assetsPath) throws IOException {
        super(Resource.newResource(assetsPath));
    }
}
