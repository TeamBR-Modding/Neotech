package com.dyonovan.neotech.events

import com.dyonovan.neotech.common.items.ItemTrashBag
import net.minecraftforge.common.MinecraftForge

object EventManager {

    def init(): Unit = {
        //OnCrafted
        MinecraftForge.EVENT_BUS.register(OnCraftedEvent)
        MinecraftForge.EVENT_BUS.register(OnPlayerLoginEvent)
        MinecraftForge.EVENT_BUS.register(ItemTrashBag)
    }
}
