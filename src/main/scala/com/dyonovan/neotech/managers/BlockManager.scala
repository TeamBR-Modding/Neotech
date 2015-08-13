package com.dyonovan.neotech.managers

import com.dyonovan.neotech.common.blocks.BlockMachine
import com.dyonovan.neotech.common.tiles.machines.{TileElectricCrusher, TileElectricFurnace}
import net.minecraft.block.Block
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.oredict.OreDictionary

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
object BlockManager {

    val electricFurnace = new BlockMachine("electricFurnace", classOf[TileElectricFurnace])
    val electricCrusher = new BlockMachine("electricCrusher", classOf[TileElectricCrusher])


    def preInit(): Unit = {
        registerBlock(electricFurnace, "electricFurnace", classOf[TileElectricFurnace])
        registerBlock(electricCrusher, "electricCrusher", classOf[TileElectricCrusher])
    }

    /**
     * Helper method for registering block
     *
     * @param block      The block to register
     * @param name       The name to register the block to
     * @param tileEntity The tile entity, null if none
     * @param oreDict    The ore dict tag, should it be needed
     */
    def registerBlock(block: Block, name: String, tileEntity: Class[_ <: TileEntity], oreDict: String) : Unit = {
        GameRegistry.registerBlock(block, name)
        if (tileEntity != null)
            GameRegistry.registerTileEntity(tileEntity, name)
        if (oreDict != null)
            OreDictionary.registerOre(oreDict, block)
    }

    /**
     * No ore dict helper method
     *
     * @param block      The block to add
     * @param name       The name
     * @param tileEntity The tile
     */
    def registerBlock(block: Block, name: String, tileEntity: Class[_ <: TileEntity]) : Unit = {
        registerBlock(block, name, tileEntity, null)
    }

}
