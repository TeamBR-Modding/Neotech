package com.dyonovan.neotech.client;

import com.dyonovan.neotech.client.modelfactory.ModelGenerator;
import com.dyonovan.neotech.common.CommonProxy;
import com.dyonovan.neotech.handlers.RenderHandler;

public class ClientProxy extends CommonProxy {

    public void init() {
        super.init();

        RenderHandler.init();
        ModelGenerator.register();
    }

}
