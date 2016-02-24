package com.dyonovan.neotech.client

import com.dyonovan.neotech.client.modelfactory.ModelFactory
import com.dyonovan.neotech.client.renderers._
import com.dyonovan.neotech.common.CommonProxy
import com.dyonovan.neotech.common.entities.EntityNet
import com.dyonovan.neotech.common.tiles.AbstractMachine
import com.dyonovan.neotech.common.tiles.misc.{TileAttractor, TileMobStand}
import com.dyonovan.neotech.common.tiles.storage.{TileDimStorage, TileFlushableChest, TileTank}
import com.dyonovan.neotech.events.RenderingEvents
import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.managers.{BlockManager, MetalManager}
import com.dyonovan.neotech.pipes.tiles.energy.EnergyInterfacePipe
import com.dyonovan.neotech.pipes.tiles.fluid.FluidInterfacePipe
import com.dyonovan.neotech.pipes.tiles.item.ItemInterfacePipe
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.entity.{Render, RenderManager}
import net.minecraft.client.resources.IReloadableResourceManager
import net.minecraft.client.resources.model.ModelBakery
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.{ClientRegistry, IRenderFactory, RenderingRegistry}
import net.minecraftforge.fml.common.registry.GameRegistry

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 07, 2015
 */
class ClientProxy extends CommonProxy {

    /**
     * Called during the preInit phase of the mod loading
     *
     * This is where you would register blocks and such
     */
    override def preInit() = {
        RenderingRegistry.registerEntityRenderingHandler(classOf[EntityNet], new IRenderFactory[EntityNet] {
            override def createRenderFor(manager: RenderManager): Render[_ >: EntityNet] = new RenderNet(manager)
        })
        
        //Setup sub items
        val baseString = "neotech:pipeStructure"
        ModelBakery.addVariantName( GameRegistry.findItem(Reference.MOD_ID, "pipeStructure"),
            baseString + "_black",
            baseString + "_blue",
            baseString + "_brown",
            baseString + "_cyan",
            baseString + "_gray",
            baseString + "_green",
            baseString + "_light_blue",
            baseString + "_lime",
            baseString + "_magenta",
            baseString + "_orange",
            baseString + "_pink",
            baseString + "_purple",
            baseString + "_red",
            baseString + "_silver",
            baseString + "_white",
            baseString + "_yellow")

        val baseStringStar = "neotech:blockMiniatureStar"
        ModelBakery.addVariantName(GameRegistry.findItem(Reference.MOD_ID, "blockMiniatureStar"),
            baseStringStar + "_black",
            baseStringStar + "_blue",
            baseStringStar + "_brown",
            baseStringStar + "_cyan",
            baseStringStar + "_gray",
            baseStringStar + "_green",
            baseStringStar + "_light_blue",
            baseStringStar + "_lime",
            baseStringStar + "_magenta",
            baseStringStar + "_orange",
            baseStringStar + "_pink",
            baseStringStar + "_purple",
            baseStringStar + "_red",
            baseStringStar + "_silver",
            baseStringStar + "_white",
            baseStringStar + "_yellow")
    }

    /**
     * Called during the init phase of the mod loading
     *
     * Now that the items and such are loaded, use this chance to use them
     */
    override def init() = {
        ModelFactory.register()
        MetalManager.registerModels()
        ItemRenderManager.registerItemRenderer()

        Minecraft.getMinecraft.getRenderItem.getItemModelMesher.getModelManager.getBlockModelShapes.registerBuiltInBlocks(BlockManager.flushableChest)
        ClientRegistry.bindTileEntitySpecialRenderer(classOf[TileFlushableChest], new TileFlushableChestRenderer[TileFlushableChest])

        ClientRegistry.bindTileEntitySpecialRenderer(classOf[TileTank], new TileTankFluidRenderer)

        ClientRegistry.bindTileEntitySpecialRenderer(classOf[AbstractMachine], new TileMachineIORenderer)

        ClientRegistry.bindTileEntitySpecialRenderer(classOf[ItemInterfacePipe], new ItemResourceEntityRenderer)
        ClientRegistry.bindTileEntitySpecialRenderer(classOf[EnergyInterfacePipe], new EnergyResourceEntityRenderer)
        ClientRegistry.bindTileEntitySpecialRenderer(classOf[FluidInterfacePipe], new FluidResourceEntityRenderer)
        ClientRegistry.bindTileEntitySpecialRenderer(classOf[TileDimStorage], new TileDimStorageRenderer)
        ClientRegistry.bindTileEntitySpecialRenderer(classOf[TileMobStand], new MobStandEntityRenderer[TileMobStand])
        ClientRegistry.bindTileEntitySpecialRenderer(classOf[TileAttractor], new AttractorEnderEyeRenderer)
    }

    /**
     * Called during the postInit phase of the mod loading
     *
     * Usually used to close things opened to load and check for conditions
     */
    override def postInit() = {
        MinecraftForge.EVENT_BUS.register(RenderingEvents)
        Minecraft.getMinecraft.getResourceManager.asInstanceOf[IReloadableResourceManager].registerReloadListener(RenderingEvents)
    }
}
