package com.dyonovan.neotech.client

import cofh.api.energy.EnergyStorage
import com.dyonovan.neotech.client.modelfactory.ModelFactory
import com.dyonovan.neotech.client.renderers.ExtractionPipeRenderer
import com.dyonovan.neotech.common.CommonProxy
import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.managers.ItemRenderManager
import com.dyonovan.neotech.pipes.entities.{EnergyResourceEntity, FluidResourceEntity, ItemResourceEntity}
import com.dyonovan.neotech.pipes.tiles.energy.EnergyExtractionPipe
import com.dyonovan.neotech.pipes.tiles.fluid.FluidExtractionPipe
import com.dyonovan.neotech.pipes.tiles.item.ItemExtractionPipe
import net.minecraft.client.resources.model.ModelBakery
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidTank
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

        ClientRegistry.bindTileEntitySpecialRenderer(classOf[ItemExtractionPipe], new ExtractionPipeRenderer[ItemStack, ItemResourceEntity, ItemExtractionPipe])
        ClientRegistry.bindTileEntitySpecialRenderer(classOf[EnergyExtractionPipe], new ExtractionPipeRenderer[EnergyStorage, EnergyResourceEntity, EnergyExtractionPipe])
        ClientRegistry.bindTileEntitySpecialRenderer(classOf[FluidExtractionPipe], new ExtractionPipeRenderer[FluidTank, FluidResourceEntity, FluidExtractionPipe])
    }

    /**
     * Called during the init phase of the mod loading
     *
     * Now that the items and such are loaded, use this chance to use them
     */
    override def init() = {
        ModelFactory.register()
        ItemRenderManager.registerItemRenderer()
    }

    /**
     * Called during the postInit phase of the mod loading
     *
     * Usually used to close things opened to load and check for conditions
     */
    override def postInit() = {}

}
