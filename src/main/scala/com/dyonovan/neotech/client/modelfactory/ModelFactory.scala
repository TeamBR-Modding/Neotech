package com.dyonovan.neotech.client.modelfactory

import com.dyonovan.neotech.client.modelfactory.models.{ModelPipe, ModelTank}
import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.managers.BlockManager
import com.dyonovan.neotech.pipes.blocks.BlockPipe
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.{ModelBakeEvent, TextureStitchEvent}
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.{EventPriority, SubscribeEvent}

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

    var STRUCTURE_PIPE : TextureAtlasSprite = null
    var STRUCTURE_PIPE_BASIC_SPEED : TextureAtlasSprite = null

    var BASIC_ITEM_SOURCE : TextureAtlasSprite = null
    var BASIC_ITEM_SOURCE_EXTRAS : TextureAtlasSprite = null
    var BASIC_ITEM_SINK : TextureAtlasSprite = null
    var BASIC_ITEM_SINK_EXTRAS : TextureAtlasSprite = null

    var BASIC_ENERGY_SOURCE : TextureAtlasSprite = null
    var BASIC_ENERGY_SOURCE_EXTRAS : TextureAtlasSprite = null
    var BASIC_ENERGY_SINK : TextureAtlasSprite = null
    var BASIC_ENERGY_SINK_EXTRAS : TextureAtlasSprite = null

    var BASIC_FLUID_SOURCE : TextureAtlasSprite = null
    var BASIC_FLUID_SOURCE_EXTRAS : TextureAtlasSprite = null
    var BASIC_FLUID_SINK : TextureAtlasSprite = null
    var BASIC_FLUID_SINK_EXTRAS : TextureAtlasSprite = null

    // Get the default model resource location for a block state
    // Used to put an entry into the model registry
    def getModelResourceLocation(state: IBlockState): ModelResourceLocation = {
        new ModelResourceLocation(Block.blockRegistry.getNameForObject(state.getBlock).asInstanceOf[ResourceLocation], new DefaultStateMapper().getPropertyString(state.getProperties))
    }
}

class ModelFactory {

    val pipeRegistry = new ArrayBuffer[BlockPipe]

    @SubscribeEvent(priority = EventPriority.LOWEST)
    def textureStitch(event: TextureStitchEvent.Pre): Unit = {
        //Here is where we want to stitch in all our extra stuff. Since Minecraft won't let us do this anymore forge has
        //been nice enough to give us a little helper event. Thanks Forge!

        //Pipes
        ModelFactory.STRUCTURE_PIPE = event.map.registerSprite(new ResourceLocation(Reference.MOD_ID + ":blocks/structurePipe"))
        ModelFactory.STRUCTURE_PIPE_BASIC_SPEED = event.map.registerSprite(new ResourceLocation(Reference.MOD_ID + ":blocks/structureBasicSpeed"))

        ModelFactory.BASIC_ITEM_SOURCE = event.map.registerSprite(new ResourceLocation(Reference.MOD_ID + ":blocks/basicItemSource"))
        ModelFactory.BASIC_ITEM_SOURCE_EXTRAS = event.map.registerSprite(new ResourceLocation(Reference.MOD_ID + ":blocks/basicItemSourceExtras"))
        ModelFactory.BASIC_ITEM_SINK = event.map.registerSprite(new ResourceLocation(Reference.MOD_ID + ":blocks/basicItemSink"))
        ModelFactory.BASIC_ITEM_SINK_EXTRAS = event.map.registerSprite(new ResourceLocation(Reference.MOD_ID + ":blocks/basicItemSinkExtras"))

        ModelFactory.BASIC_ENERGY_SOURCE = event.map.registerSprite(new ResourceLocation(Reference.MOD_ID + ":blocks/basicEnergySource"))
        ModelFactory.BASIC_ENERGY_SOURCE_EXTRAS = event.map.registerSprite(new ResourceLocation(Reference.MOD_ID + ":blocks/basicEnergySourceExtras"))
        ModelFactory.BASIC_ENERGY_SINK = event.map.registerSprite(new ResourceLocation(Reference.MOD_ID + ":blocks/basicEnergySink"))
        ModelFactory.BASIC_ENERGY_SINK_EXTRAS = event.map.registerSprite(new ResourceLocation(Reference.MOD_ID + ":blocks/basicEnergySinkExtras"))

        ModelFactory.BASIC_FLUID_SOURCE = event.map.registerSprite(new ResourceLocation(Reference.MOD_ID + ":blocks/basicFluidSource"))
        ModelFactory.BASIC_FLUID_SOURCE_EXTRAS = event.map.registerSprite(new ResourceLocation(Reference.MOD_ID + ":blocks/basicFluidSourceExtras"))
        ModelFactory.BASIC_FLUID_SINK = event.map.registerSprite(new ResourceLocation(Reference.MOD_ID + ":blocks/basicFluidSink"))
        ModelFactory.BASIC_FLUID_SINK_EXTRAS = event.map.registerSprite(new ResourceLocation(Reference.MOD_ID + ":blocks/basicFluidSinkExtras"))
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    def bakeModels(event: ModelBakeEvent): Unit = {
        val itemModelMesher = Minecraft.getMinecraft.getRenderItem.getItemModelMesher

        //Pipes
        pipeRegistry.foreach((blockPipe : BlockPipe) => event.modelRegistry.putObject(new ModelResourceLocation(Reference.MOD_ID + ":" + blockPipe.name, "normal"), new ModelPipe()))
        pipeRegistry.foreach((blockPipe : BlockPipe) => event.modelRegistry.putObject(new ModelResourceLocation(Reference.MOD_ID + ":" + blockPipe.name, "inventory"), new ModelPipe()))
        pipeRegistry.foreach((blockPipe : BlockPipe) => itemModelMesher.register(Item.getItemFromBlock(blockPipe), 0, new ModelResourceLocation
        (Reference.MOD_ID +
                ":" + blockPipe.name, "inventory")))

        //tanks
        event.modelRegistry.putObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "ironTank", "normal"), new ModelTank)
        event.modelRegistry.putObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "goldTank", "normal"), new ModelTank)
        event.modelRegistry.putObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "diamondTank", "normal"), new ModelTank)
        event.modelRegistry.putObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "creativeTank", "normal"), new ModelTank)
        event.modelRegistry.putObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "ironTank", "inventory"), new ModelTank)
        event.modelRegistry.putObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "goldTank", "inventory"), new ModelTank)
        event.modelRegistry.putObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "diamondTank", "inventory"), new ModelTank)
        event.modelRegistry.putObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "creativeTank", "inventory"), new ModelTank)
        itemModelMesher.register(Item.getItemFromBlock(BlockManager.ironTank), 0, new ModelResourceLocation(Reference
                .MOD_ID + ":ironTank", "inventory"))
        itemModelMesher.register(Item.getItemFromBlock(BlockManager.goldTank), 0, new ModelResourceLocation(Reference
                .MOD_ID + ":goldTank", "inventory"))
        itemModelMesher.register(Item.getItemFromBlock(BlockManager.diamondTank), 0, new ModelResourceLocation(Reference
                .MOD_ID + ":diamondTank", "inventory"))
        itemModelMesher.register(Item.getItemFromBlock(BlockManager.creativeTank), 0, new ModelResourceLocation(Reference
                .MOD_ID + ":creativeTank", "inventory"))
    }
}
