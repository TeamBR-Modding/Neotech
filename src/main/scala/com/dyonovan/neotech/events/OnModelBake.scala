package com.dyonovan.neotech.events

import com.dyonovan.neotech.managers.BlockManager
import mcmultipart.client.multipart.ModelMultipartContainer
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 2/29/2016
  */
object OnModelBake {

    @SubscribeEvent
    def onModelBakeEvent(event: ModelBakeEvent): Unit = {
        val res = new ModelResourceLocation("neotech:pipeStructure", "normal")
        event.modelRegistry.putObject(res, new ModelMultipartContainer(event.modelRegistry.getObject(res)))
    }

}
