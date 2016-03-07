package com.dyonovan.neotech.client.modelfactory
import com.dyonovan.neotech.client.modelfactory.models.{ModelHelper, ModelTank, ToolModel}
import com.dyonovan.neotech.lib.Reference
import com.google.common.collect.{ImmutableMap, Maps}
import mcmultipart.client.multipart.ModelMultipartContainer
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType
import net.minecraft.client.resources.model.{IBakedModel, ModelResourceLocation}
import net.minecraft.util.RegistrySimple
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.client.model.{IFlexibleBakedModel, IPerspectiveAwareModel, TRSRTransformation}
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.{EventPriority, SubscribeEvent}

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer


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
        //tanks
        event.modelRegistry.putObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "ironTank", "inventory"),
            new ModelTank(event.modelRegistry.getObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "ironTank", "inventory"))))
        event.modelRegistry.putObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "goldTank", "inventory"),
            new ModelTank(event.modelRegistry.getObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "goldTank", "inventory"))))
        event.modelRegistry.putObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "diamondTank", "inventory"),
            new ModelTank(event.modelRegistry.getObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "diamondTank", "inventory"))))
        event.modelRegistry.putObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "creativeTank", "inventory"),
            new ModelTank(event.modelRegistry.getObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "creativeTank", "inventory"))))
        event.modelRegistry.putObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "voidTank", "inventory"),
            new ModelTank(event.modelRegistry.getObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "voidTank", "inventory"))))

        val itemModelsToReadd = new ArrayBuffer[(ModelResourceLocation, IBakedModel)]()
        for(modelLocation <- event.modelRegistry.asInstanceOf[RegistrySimple[ModelResourceLocation, IBakedModel]].getKeys) {
            if(modelLocation.getResourceDomain.equalsIgnoreCase(Reference.MOD_ID) &&
                    (modelLocation.getResourcePath.contains("pipeStructure") ||
                            modelLocation.getResourcePath.contains("BasicInterface"))) {
                if(modelLocation.toString.contains("down=2,east=0,north=0,south=0,up=1,west=0") ||
                    modelLocation.toString.contains("down=true,east=false,north=false,south=false,up=true,west=false")) {
                    // Preserve the old model for item rendering
                    val oldModel = event.modelRegistry.getObject(modelLocation)
                    val itemModelLocation = new ModelResourceLocation(modelLocation.toString + ",inventory")
                    val tuple = (itemModelLocation, oldModel)
                    itemModelsToReadd += tuple
                }

                // Create Multipart world obj
                event.modelRegistry.putObject(modelLocation, new ModelMultipartContainer(event.modelRegistry.getObject(modelLocation)))
            }
        }

        for(pair <- itemModelsToReadd)
            event.modelRegistry.putObject(pair._1, pair._2)

        val builder = Maps.newHashMap[TransformType, TRSRTransformation]()
        builder.putAll(IPerspectiveAwareModel.MapWrapper.getTransforms(ModelHelper.DEFAULT_TOOL_STATE))

        event.modelRegistry.putObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "electricPickaxe", "inventory"),
            new ToolModel(
                event.modelRegistry.getObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "electricPickaxe", "inventory")).asInstanceOf[IFlexibleBakedModel],
                ImmutableMap.copyOf(builder)))

        event.modelRegistry.putObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "electricSword", "inventory"),
            new ToolModel(
                event.modelRegistry.getObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "electricSword", "inventory")).asInstanceOf[IFlexibleBakedModel],
                ImmutableMap.copyOf(builder)))
    }
}
