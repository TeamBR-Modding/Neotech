package com.dyonovan.neotech.managers

import com.dyonovan.neotech.common.blocks.BlockChunkLoader
import com.dyonovan.neotech.common.blocks.machines.{BlockGrinder, BlockMachine}
import com.dyonovan.neotech.common.blocks.misc.{BlockPlayerPlate, BlockCrafter, BlockFertilizer}
import com.dyonovan.neotech.common.blocks.ore.BlockOre
import com.dyonovan.neotech.common.blocks.storage.{BlockRFStorage, BlockTank, ItemBlockRFStorage, ItemBlockTank}
import com.dyonovan.neotech.common.tiles.TileChunkLoader
import com.dyonovan.neotech.common.tiles.machines._
import com.dyonovan.neotech.common.tiles.misc.{TileCrafter, TileFertilizer}
import com.dyonovan.neotech.common.tiles.storage.{TileRFStorage, TileTank}
import com.dyonovan.neotech.pipes.blocks.{BlockPipe, BlockPipeSpecial, ItemBlockPipe}
import com.dyonovan.neotech.pipes.tiles.energy.{EnergyExtractionPipe, EnergySinkPipe}
import com.dyonovan.neotech.pipes.tiles.fluid.{FluidExtractionPipe, FluidSinkPipe}
import com.dyonovan.neotech.pipes.tiles.item.{ItemExtractionPipe, ItemSinkPipe}
import com.dyonovan.neotech.pipes.tiles.structure.StructurePipe
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.item.{EnumDyeColor, ItemBlock, ItemStack}
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

    //Machines
    val grinder = new BlockGrinder()

    val electricFurnace = new BlockMachine("electricFurnace", classOf[TileElectricFurnace])
    val electricCrusher = new BlockMachine("electricCrusher", classOf[TileElectricCrusher])
    val furnaceGenerator = new BlockMachine("furnaceGenerator", classOf[TileFurnaceGenerator])
    val fluidGenerator = new BlockMachine("fluidGenerator", classOf[TileFluidGenerator])
    val thermalBinder = new BlockMachine("thermalBinder", classOf[TileThermalBinder])

    //ores
    val oreCopper = new BlockOre("oreCopper", 1)
    val blockCopper = new BlockOre("blockCopper", 1)
    val oreTin = new BlockOre("oreTin", 1)
    val blockTin = new BlockOre("blockTin", 1)

    //Pipes
    val pipeBasicStructure = new BlockPipe("pipeStructure", Material.glass, true, classOf[StructurePipe])
    val pipeBasicSpeedStructure = new BlockPipe("pipeBasicSpeedStructure", Material.rock, false, classOf[StructurePipe])

    val pipeItemSource = new BlockPipeSpecial("pipeItemBasicSource", Material.rock, classOf[ItemExtractionPipe])
    val pipeItemSink = new BlockPipeSpecial("pipeItemBasicSink", Material.rock, classOf[ItemSinkPipe])

    val pipeEnergySource = new BlockPipeSpecial("pipeEnergyBasicSource", Material.rock, classOf[EnergyExtractionPipe])
    val pipeEnergySink = new BlockPipeSpecial("pipeEnergyBasicSink", Material.rock, classOf[EnergySinkPipe])

    val pipeFluidSource = new BlockPipeSpecial("pipeFluidBasicSource", Material.rock, classOf[FluidExtractionPipe])
    val pipeFluidSink = new BlockPipeSpecial("pipeFluidBasicSink", Material.rock, classOf[FluidSinkPipe])

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

    //Misc
    val blockCrafter = new BlockCrafter("blockCrafter", classOf[TileCrafter])
    val blockFertilizer = new BlockFertilizer("blockFertilizer", classOf[TileFertilizer])
    val playerPlate = new BlockPlayerPlate
    val chunkLoader = new BlockChunkLoader

    def preInit(): Unit = {
        //Machines
        registerBlock(grinder, "grinder", classOf[TileGrinder])

        registerBlock(electricFurnace, "electricFurnace", classOf[TileElectricFurnace])
        registerBlock(electricCrusher, "electricCrusher", classOf[TileElectricCrusher])
        registerBlock(furnaceGenerator, "furnaceGenerator", classOf[TileFurnaceGenerator])
        registerBlock(fluidGenerator, "fluidGenerator", classOf[TileFluidGenerator])
        registerBlock(thermalBinder, "thermalBinder", classOf[TileThermalBinder])

        //Ores
        registerBlock(oreCopper, "oreCopper", null, "oreCopper")
        registerBlock(oreTin, "oreTin", null, "oreTin")
        registerBlock(blockCopper, "blockCopper", null, "blockCopper")
        registerBlock(blockTin, "blockTin", null, "blockTin")

        //Pipes
        registerBlock(pipeBasicStructure, "pipeStructure", classOf[StructurePipe], classOf[ItemBlockPipe])
        for(color <- EnumDyeColor.values())
            OreDictionary.registerOre("pipeStructure", new ItemStack(pipeBasicStructure, 1, color.getMetadata))

        registerBlock(pipeBasicSpeedStructure, "pipeBasicSpeedStructure", classOf[StructurePipe])

        registerBlock(pipeItemSource, "pipeItemBasicSource", classOf[ItemExtractionPipe])
        registerBlock(pipeItemSink, "pipeItemBasicSink", classOf[ItemSinkPipe])

        registerBlock(pipeEnergySource, "pipeEnergyBasicSource", classOf[EnergyExtractionPipe])
        registerBlock(pipeEnergySink, "pipeEnergyBasicSink", classOf[EnergySinkPipe])

        registerBlock(pipeFluidSource, "pipeFluidBasicSource", classOf[FluidExtractionPipe])
        registerBlock(pipeFluidSink, "pipeFluidBasicSink", classOf[FluidSinkPipe])

        //RF Storage
        registerBlock(basicRFStorage, "basicRFStorage", classOf[TileRFStorage], classOf[ItemBlockRFStorage])
        registerBlock(advancedRFStorage, "advancedRFStorage", classOf[TileRFStorage], classOf[ItemBlockRFStorage])
        registerBlock(eliteRFStorage, "eliteRFStorage", classOf[TileRFStorage], classOf[ItemBlockRFStorage])
        registerBlock(creativeRFStorage, "creativeRFStorage", classOf[TileRFStorage], classOf[ItemBlockRFStorage])

        //Tanks
        registerBlock(ironTank, "ironTank", classOf[TileTank], classOf[ItemBlockTank])
        registerBlock(goldTank, "goldTank", classOf[TileTank], classOf[ItemBlockTank])
        registerBlock(diamondTank, "diamondTank", classOf[TileTank], classOf[ItemBlockTank])
        registerBlock(creativeTank, "creativeTank", classOf[TileTank], classOf[ItemBlockTank])

        //misc
        registerBlock(blockCrafter, "blockCrafter", classOf[TileCrafter])
        registerBlock(blockFertilizer, "blockFertilizer", classOf[TileFertilizer])
        registerBlock(playerPlate, "playerPlate", null)
        registerBlock(chunkLoader, "chunkLoader", classOf[TileChunkLoader])
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

    def registerBlock(block: Block, name: String, tileEntity: Class[_ <: TileEntity], itemBlock: Class[_ <: ItemBlock]) : Unit = {
        GameRegistry.registerBlock(block, itemBlock, name)
        if (tileEntity != null)
            GameRegistry.registerTileEntity(tileEntity, name)
    }

    /**
     * No ore dict helper method
     *
     * @param block      The block to add
     * @param name       The name
     * @param tileEntity The tile
     */
    def registerBlock(block: Block, name: String, tileEntity: Class[_ <: TileEntity]) : Unit = {
        val oreDict: String = null
        registerBlock(block, name, tileEntity, oreDict)
    }
}
