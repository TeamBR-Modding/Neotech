package com.dyonovan.neotech.client.modelfactory

import com.dyonovan.neotech.client.modelfactory.models.ModelBaker
import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.managers.BlockManager
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.{ModelBakeEvent, TextureStitchEvent}
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

    // Get the default model resource location for a block state
    // Used to put an entry into the model registry
    def getModelResourceLocation(state: IBlockState): ModelResourceLocation = {
        new ModelResourceLocation(Block.blockRegistry.getNameForObject(state.getBlock).asInstanceOf[ResourceLocation], new DefaultStateMapper().getPropertyString(state.getProperties))
    }
}

class ModelFactory {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    def textureStitch(event: TextureStitchEvent.Pre): Unit = {
        //Here is where we want to stitch in all our extra stuff. Since Minecraft won't let us do this anymore forge has
        //been nice enough to give us a little helper event. Thanks Forge!

        //Machine Sides
        event.map.registerSprite(new ResourceLocation(Reference.MOD_ID + ":blocks/machine_side"))

        //Furnace
        event.map.registerSprite(new ResourceLocation(Reference.MOD_ID + ":blocks/electricFurnace_front"))
        event.map.registerSprite(new ResourceLocation(Reference.MOD_ID + ":blocks/electricFurnaceActive_front"))
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    def bakeModels(event: ModelBakeEvent): Unit = {
        val itemModelMesher = Minecraft.getMinecraft.getRenderItem.getItemModelMesher

        event.modelRegistry.putObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "electricFurnace", "normal"),
            new ModelBaker())
        event.modelRegistry.putObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "electricFurnace", "inventory"),
            new ModelBaker())
        itemModelMesher.register(Item.getItemFromBlock(BlockManager.electricFurnace), 0, new ModelResourceLocation(Reference.MOD_ID +
                ":" + "electricFurnace", "inventory"))
    }
}
