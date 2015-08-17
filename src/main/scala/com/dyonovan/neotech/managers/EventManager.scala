package com.dyonovan.neotech.managers

import com.dyonovan.neotech.events.OnCraftedEvent
import net.minecraftforge.fml.common.FMLCommonHandler

object EventManager {

    def init(): Unit = {
        //OnCrafted
        FMLCommonHandler.instance().bus().register(OnCraftedEvent)
    }
}
