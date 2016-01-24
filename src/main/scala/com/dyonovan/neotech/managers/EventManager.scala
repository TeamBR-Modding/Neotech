package com.dyonovan.neotech.managers

import com.dyonovan.neotech.events.{OnPlayerLoginEvent, OnCraftedEvent}
import net.minecraftforge.fml.common.FMLCommonHandler

object EventManager {

    def init(): Unit = {
        //OnCrafted
        FMLCommonHandler.instance().bus().register(OnCraftedEvent)
        FMLCommonHandler.instance().bus().register(OnPlayerLoginEvent)
    }
}
