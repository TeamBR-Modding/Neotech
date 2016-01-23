package com.dyonovan.neotech.client

import com.dyonovan.neotech.client.modelfactory.ModelFactory
import com.dyonovan.neotech.client.renderers.{TileFlushableChestRenderer, FluidResourceEntityRenderer, EnergyResourceEntityRenderer, ItemResourceEntityRenderer}
import com.dyonovan.neotech.common.CommonProxy
import com.dyonovan.neotech.common.tiles.misc.TileChunkLoader
import com.dyonovan.neotech.common.tiles.storage.TileFlushableChest
import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.managers.{BlockManager, ItemRenderManager}
import com.dyonovan.neotech.pipes.tiles.energy.EnergyExtractionPipe
import com.dyonovan.neotech.pipes.tiles.fluid.FluidExtractionPipe
import com.dyonovan.neotech.pipes.tiles.item.ItemExtractionPipe
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.model.ModelBakery
import net.minecraftforge.fml.client.registry.ClientRegistry
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
        //Setup sub items
        val basicStructurePipeItem = GameRegistry.findItem(Reference.MOD_ID, "pipeStructure")
        val baseString = "neotech:pipeStructure"
        ModelBakery.addVariantName(basicStructurePipeItem,
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
    }

    /**
     * Called during the init phase of the mod loading
     *
     * Now that the items and such are loaded, use this chance to use them
     */
    override def init() = {
        ModelFactory.register()
        ItemRenderManager.registerItemRenderer()

        Minecraft.getMinecraft.getRenderItem.getItemModelMesher.getModelManager.getBlockModelShapes.registerBuiltInBlocks(BlockManager.flushableChest)
        ClientRegistry.bindTileEntitySpecialRenderer(classOf[TileFlushableChest], new TileFlushableChestRenderer[TileFlushableChest])

        ClientRegistry.bindTileEntitySpecialRenderer(classOf[ItemExtractionPipe], new ItemResourceEntityRenderer)
        ClientRegistry.bindTileEntitySpecialRenderer(classOf[EnergyExtractionPipe], new EnergyResourceEntityRenderer)
        ClientRegistry.bindTileEntitySpecialRenderer(classOf[FluidExtractionPipe], new FluidResourceEntityRenderer)
    }

    /**
     * Called during the postInit phase of the mod loading
     *
     * Usually used to close things opened to load and check for conditions
     */
    override def postInit() = {}

}
