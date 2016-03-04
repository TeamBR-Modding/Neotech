package com.dyonovan.neotech.client

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.{Phase, ClientTickEvent}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis "pauljoda"
  * @since 3/4/2016
  */
object ClientTickHandler {
    @SubscribeEvent
    def clientTickEnd(event : ClientTickEvent): Unit = {
        if(event.phase == Phase.END) {
            if(KeybindHandler.radialMenu.isKeyDown)
                KeybindHandler.keyPressed(KeybindHandler.radialMenu)
        }
    }
}
