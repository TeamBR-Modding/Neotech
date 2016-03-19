package com.dyonovan.neotech.client.modelfactory
import com.dyonovan.neotech.client.modelfactory.models.ToolModel
import com.dyonovan.neotech.lib.Reference
import com.google.common.collect.{ImmutableMap, Maps}
import com.teambr.bookshelf.client.ModelHelper
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.client.model.{IPerspectiveAwareModel, TRSRTransformation}
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
        val builder = Maps.newHashMap[TransformType, TRSRTransformation]()
        builder.putAll(IPerspectiveAwareModel.MapWrapper.getTransforms(ModelHelper.DEFAULT_TOOL_STATE))

        event.getModelRegistry.putObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "electricPickaxe", "inventory"),
            new ToolModel(
                event.getModelRegistry.getObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "electricPickaxe", "inventory")).asInstanceOf[IFlexibleBakedModel],
                ImmutableMap.copyOf(builder)))

        event.getModelRegistry.putObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "electricSword", "inventory"),
            new ToolModel(
                event.getModelRegistry.getObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "electricSword", "inventory")).asInstanceOf[IFlexibleBakedModel],
                ImmutableMap.copyOf(builder)))
    }
}
