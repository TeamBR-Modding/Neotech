package com.dyonovan.neotech.managers

import com.dyonovan.neotech.common.blocks.machines.BlockMachine
import com.dyonovan.neotech.common.blocks.ore.BlockOre
import com.dyonovan.neotech.common.blocks.storage.{ItemBlockTank, BlockTank, ItemBlockRFStorage, BlockRFStorage}
import com.dyonovan.neotech.common.tiles.machines.{TileElectricCrusher, TileElectricFurnace, TileFurnaceGenerator}
import com.dyonovan.neotech.common.tiles.storage.{TileTank, TileRFStorage}
import com.dyonovan.neotech.pipes.blocks.BlockPipe
import com.dyonovan.neotech.pipes.tiles.energy.{EnergySinkPipe, EnergyExtractionPipe}
import com.dyonovan.neotech.pipes.tiles.fluid.{FluidSinkPipe, FluidExtractionPipe}
import com.dyonovan.neotech.pipes.tiles.item.{ItemSinkPipe, ItemExtractionPipe}
import com.dyonovan.neotech.pipes.tiles.structure.StructurePipe
import com.dyonovan.neotech.registries.PowerAdvantageRegistry
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.item.ItemBlock
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
    val pipeBasicStructure = new BlockPipe("pipeStructure", Material.rock, classOf[StructurePipe])
    
    val pipeItemSource = new BlockPipe("pipeItemBasicSource", Material.rock, classOf[ItemExtractionPipe])
    val pipeItemSink = new BlockPipe("pipeItemBasicSink", Material.rock, classOf[ItemSinkPipe])

    val pipeEnergySource = new BlockPipe("pipeEnergyBasicSource", Material.rock, classOf[EnergyExtractionPipe])
    val pipeEnergySink = new BlockPipe("pipeEnergyBasicSink", Material.rock, classOf[EnergySinkPipe])

    val pipeFluidSource = new BlockPipe("pipeFluidBasicSource", Material.rock, classOf[FluidExtractionPipe])
    val pipeFluidSink = new BlockPipe("pipeFluidBasicSink", Material.rock, classOf[FluidSinkPipe])

    //RF Storage
    val basicRFStorage = new BlockRFStorage("basicRFStorage", 1)
    val advancedRFStorage = new BlockRFStorage("advancedRFStorage", 2)
    val eliteRFStorage = new BlockRFStorage("eliteRFStorage", 3)
    val creativeRFStorage = new BlockRFStorage("creativeRFStorage", 4)

    //Tanks
    val ironTank = new BlockTank("ironTank", 1)
    val goldTank = new BlockTank("goldTank", 2)
    val diamondTank = new BlockTank("diamondTank", 3)
    val creativeTank = new BlockTank("creativeTank", 4)

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
        registerBlock(pipeBasicStructure, "pipeStructure", classOf[StructurePipe])

        registerBlock(pipeItemSource, "pipeItemBasicSource", classOf[ItemExtractionPipe])
        registerBlock(pipeItemSink, "pipeItemBasicSink", classOf[ItemSinkPipe])

        registerBlock(pipeEnergySource, "pipeEnergyBasicSource", classOf[EnergyExtractionPipe])
        registerBlock(pipeEnergySink, "pipeEnergyBasicSink", classOf[EnergySinkPipe])

        registerBlock(pipeFluidSource, "pipeFluidBasicSource", classOf[FluidExtractionPipe])
        registerBlock(pipeFluidSink, "pipeFluidBasicSink", classOf[FluidSinkPipe])

        //RF Storage
        registerBlock(basicRFStorage, "basicRFStorage", classOf[TileRFStorage], powerAcceptor = true, classOf[ItemBlockRFStorage])
        registerBlock(advancedRFStorage, "advancedRFStorage", classOf[TileRFStorage], powerAcceptor = true, classOf[ItemBlockRFStorage])
        registerBlock(eliteRFStorage, "eliteRFStorage", classOf[TileRFStorage], powerAcceptor = true, classOf[ItemBlockRFStorage])
        registerBlock(creativeRFStorage, "creativeRFStorage", classOf[TileRFStorage], powerAcceptor = true, classOf[ItemBlockRFStorage])

        //Tanks
        registerBlock(ironTank, "ironTank", classOf[TileTank], powerAcceptor = false, classOf[ItemBlockTank])
        registerBlock(goldTank, "goldTank", classOf[TileTank], powerAcceptor = false, classOf[ItemBlockTank])
        registerBlock(diamondTank, "diamondTank", classOf[TileTank], powerAcceptor = false, classOf[ItemBlockTank])
        registerBlock(creativeTank, "creativeTank", classOf[TileTank], powerAcceptor = false, classOf[ItemBlockTank])
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

    def registerBlock(block: Block, name: String, tileEntity: Class[_ <: TileEntity], powerAcceptor: Boolean,
                      itemBlock: Class[_ <: ItemBlock]) : Unit = {
        GameRegistry.registerBlock(block, itemBlock, name)
        if (tileEntity != null)
            GameRegistry.registerTileEntity(tileEntity, name)

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
