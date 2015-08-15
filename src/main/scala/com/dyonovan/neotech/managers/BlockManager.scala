package com.dyonovan.neotech.managers

import com.dyonovan.neotech.common.blocks.machines.BlockMachine
import com.dyonovan.neotech.common.blocks.ore.BlockOre
import com.dyonovan.neotech.common.tiles.machines.{TileElectricCrusher, TileElectricFurnace, TileFurnaceGenerator}
import com.dyonovan.neotech.pipes.blocks.BlockPipe
import com.dyonovan.neotech.pipes.tiles.TilePipe
import com.dyonovan.neotech.registries.PowerAdvantageRegistry
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fml.common.Loader
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
    val furnaceGenerator = new BlockMachine("furnaceGenerator", classOf[TileFurnaceGenerator])

    //ores
    val oreCopper = new BlockOre("oreCopper", 1)
    val blockCopper = new BlockOre("blockCopper", 1)
    val oreTin = new BlockOre("oreTin", 1)
    val blockTin = new BlockOre("blockTin", 1)

    //Pipes
    val pipeItemBasic = new BlockPipe("pipeItemBasic", Material.rock, classOf[TilePipe])


    def preInit(): Unit = {
        //Machines
        registerBlock(electricFurnace, "electricFurnace", classOf[TileElectricFurnace], powerAcceptor = true)
        registerBlock(electricCrusher, "electricCrusher", classOf[TileElectricCrusher], powerAcceptor = true)
        registerBlock(furnaceGenerator, "furnaceGenerator", classOf[TileFurnaceGenerator])

        //Ores
        registerBlock(oreCopper, "oreCopper", null, "oreCopper", powerAcceptor = false)
        registerBlock(oreTin, "oreTin", null, "oreTin", powerAcceptor = false)
        registerBlock(blockCopper, "blockCopper", null, "blockCopper", powerAcceptor = false)
        registerBlock(blockTin, "blockTin", null, "blockTin", powerAcceptor = false)

        //Pipes
        registerBlock(pipeItemBasic, "pipeItemBasic", classOf[TilePipe])
    }

    /**
     * Helper method for registering block
     *
     * @param block      The block to register
     * @param name       The name to register the block to
     * @param tileEntity The tile entity, null if none
     * @param oreDict    The ore dict tag, should it be needed
     */
    def registerBlock(block: Block, name: String, tileEntity: Class[_ <: TileEntity], oreDict: String,
                      powerAcceptor: Boolean) : Unit = {
        GameRegistry.registerBlock(block, name)
        if (tileEntity != null)
            GameRegistry.registerTileEntity(tileEntity, name)
        if (oreDict != null)
            OreDictionary.registerOre(oreDict, block)

        if (Loader.isModLoaded("poweradvantage") && powerAcceptor) {
            PowerAdvantageRegistry.registerPA(block)
        }
    }

    /**
     * No ore dict helper method
     *
     * @param block      The block to add
     * @param name       The name
     * @param tileEntity The tile
     */
    def registerBlock(block: Block, name: String, tileEntity: Class[_ <: TileEntity]) : Unit = {
        registerBlock(block, name, tileEntity, null, powerAcceptor = false)
    }

    def registerBlock(block: Block, name: String, tileEntity: Class[_ <: TileEntity], powerAcceptor: Boolean) : Unit = {
        registerBlock(block, name, tileEntity, null, powerAcceptor)
    }


}
