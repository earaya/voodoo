package com.earaya.voodoo.assets;

import org.eclipse.jetty.util.resource.Resource;

public class ClassPathAssetsComponent extends AssetsComponent {

    public ClassPathAssetsComponent(String assetsPath) {
        super(Resource.newClassPathResource(assetsPath));
    }
}
