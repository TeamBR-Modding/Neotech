package com.dyonovan.neotech.client.modelfactory

import com.dyonovan.neotech.client.modelfactory.models.ModelTank
import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.managers.BlockManager
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.model.{IBakedModel, ModelResourceLocation}
import net.minecraft.item.Item
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.{EventPriority, SubscribeEvent}

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 12, 2015
 */
object ModelFactory {
    val INSTANCE = new ModelFactory

    def register(): Unit = {
        MinecraftForge.EVENT_BUS.register(INSTANCE)
    }
}

class ModelFactory {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    def bakeModels(event: ModelBakeEvent): Unit = {
        val itemModelMesher = Minecraft.getMinecraft.getRenderItem.getItemModelMesher

        //tanks
        event.modelRegistry.putObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "ironTank", "inventory"),
            new ModelTank(event.modelRegistry.getObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "ironTank", "inventory"))))
        event.modelRegistry.putObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "goldTank", "inventory"),
            new ModelTank(event.modelRegistry.getObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "goldTank", "inventory"))))
        event.modelRegistry.putObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "diamondTank", "inventory"),
            new ModelTank(event.modelRegistry.getObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "diamondTank", "inventory"))))
        event.modelRegistry.putObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "creativeTank", "inventory"),
            new ModelTank(event.modelRegistry.getObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "creativeTank", "inventory"))))
    }
}
