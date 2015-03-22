package com.dyonovan.jatm.client;

import com.dyonovan.jatm.client.modelfactory.ModelGenerator;
import com.dyonovan.jatm.common.CommonProxy;
import com.dyonovan.jatm.handlers.RenderHandler;

public class ClientProxy extends CommonProxy {

    public void init() {
        super.init();

        RenderHandler.init();
        ModelGenerator.register();
    }

}
