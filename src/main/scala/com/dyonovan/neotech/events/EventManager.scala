package com.dyonovan.neotech.events
import com.dyonovan.neotech.common.items.ItemTrashBag
import com.dyonovan.neotech.utils.TimeUtils
import net.minecraftforge.common.MinecraftForge

object EventManager {

    def init(): Unit = {
        MinecraftForge.EVENT_BUS.register(OnCraftedEvent)
        MinecraftForge.EVENT_BUS.register(TimeUtils)
        MinecraftForge.EVENT_BUS.register(OnPlayerLoginEvent)
        MinecraftForge.EVENT_BUS.register(AttackEvent)
        MinecraftForge.EVENT_BUS.register(ItemTrashBag)
    }
}
