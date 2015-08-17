package com.dyonovan.neotech.client.modelfactory

import com.dyonovan.neotech.client.modelfactory.models.{ModelTank, ModelPipe, ModelMachines}
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
import net.minecraftforge.client.event.{ ModelBakeEvent, TextureStitchEvent }
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.{ EventPriority, SubscribeEvent }

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
    var BASIC_ITEM_SOURCE : TextureAtlasSprite = null
    var BASIC_ITEM_SINK : TextureAtlasSprite = null

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

        //Machine Sides
        event.map.registerSprite(new ResourceLocation(Reference.MOD_ID + ":blocks/machine_side"))

        //Furnace
        event.map.registerSprite(new ResourceLocation(Reference.MOD_ID + ":blocks/electricFurnace_front"))
        event.map.registerSprite(new ResourceLocation(Reference.MOD_ID + ":blocks/electricFurnaceActive_front"))

        //Crusher
        event.map.registerSprite(new ResourceLocation(Reference.MOD_ID + ":blocks/electricCrusher_front"))
        event.map.registerSprite(new ResourceLocation(Reference.MOD_ID + ":blocks/electricCrusherActive_front"))

        //FurnaceGenerator
        event.map.registerSprite(new ResourceLocation(Reference.MOD_ID + ":blocks/furnaceGenerator_front"))
        event.map.registerSprite(new ResourceLocation(Reference.MOD_ID + ":blocks/furnaceGeneratorActive_front"))

        //Pipes
        ModelFactory.STRUCTURE_PIPE = event.map.registerSprite(new ResourceLocation(Reference.MOD_ID + ":blocks/structurePipe"))
        ModelFactory.BASIC_ITEM_SOURCE = event.map.registerSprite(new ResourceLocation(Reference.MOD_ID + ":blocks/basicItemSource"))
        ModelFactory.BASIC_ITEM_SINK = event.map.registerSprite(new ResourceLocation(Reference.MOD_ID + ":blocks/basicItemSink"))
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    def bakeModels(event: ModelBakeEvent): Unit = {
        val itemModelMesher = Minecraft.getMinecraft.getRenderItem.getItemModelMesher

        event.modelRegistry.putObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "electricFurnace", "normal"),
            new ModelMachines())
        event.modelRegistry.putObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "electricFurnace", "inventory"),
            new ModelMachines())
        itemModelMesher.register(Item.getItemFromBlock(BlockManager.electricFurnace), 0, new ModelResourceLocation(Reference.MOD_ID +
                ":" + "electricFurnace", "inventory"))

        event.modelRegistry.putObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "electricCrusher", "normal"),
            new ModelMachines())
        event.modelRegistry.putObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "electricCrusher", "inventory"),
            new ModelMachines())
        itemModelMesher.register(Item.getItemFromBlock(BlockManager.electricCrusher), 0, new ModelResourceLocation
        (Reference.MOD_ID +
                ":" + "electricCrusher", "inventory"))

        event.modelRegistry.putObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "furnaceGenerator", "normal"),
            new ModelMachines())
        event.modelRegistry.putObject(new ModelResourceLocation(Reference.MOD_ID + ":" + "furnaceGenerator", "inventory"),
            new ModelMachines())
        itemModelMesher.register(Item.getItemFromBlock(BlockManager.furnaceGenerator), 0, new ModelResourceLocation
        (Reference.MOD_ID +
                ":" + "furnaceGenerator", "inventory"))

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
