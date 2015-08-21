package com.dyonovan.neotech.managers

import com.dyonovan.neotech.common.blocks.machines.BlockMachine
import com.dyonovan.neotech.common.blocks.misc.BlockCrafter
import com.dyonovan.neotech.common.blocks.ore.BlockOre
import com.dyonovan.neotech.common.blocks.storage.{BlockRFStorage, BlockTank, ItemBlockRFStorage, ItemBlockTank}
import com.dyonovan.neotech.common.tiles.machines._
import com.dyonovan.neotech.common.tiles.misc.TileCrafter
import com.dyonovan.neotech.common.tiles.storage.{TileRFStorage, TileTank}
import com.dyonovan.neotech.pipes.blocks.{BlockPipe, BlockPipeSpecial, ItemBlockPipe}
import com.dyonovan.neotech.pipes.tiles.energy.{EnergyExtractionPipe, EnergySinkPipe}
import com.dyonovan.neotech.pipes.tiles.fluid.{FluidExtractionPipe, FluidSinkPipe}
import com.dyonovan.neotech.pipes.tiles.item.{ItemExtractionPipe, ItemSinkPipe}
import com.dyonovan.neotech.pipes.tiles.structure.StructurePipe
import com.dyonovan.neotech.registries.PowerAdvantageRegistry
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.item.{EnumDyeColor, ItemBlock, ItemStack}
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

    def preInit(): Unit = {
        //Machines
        registerBlock(electricFurnace, "electricFurnace", classOf[TileElectricFurnace], powerAcceptor = true)
        registerBlock(electricCrusher, "electricCrusher", classOf[TileElectricCrusher], powerAcceptor = true)
        registerBlock(furnaceGenerator, "furnaceGenerator", classOf[TileFurnaceGenerator])
        registerBlock(fluidGenerator, "fluidGenerator", classOf[TileFluidGenerator])

        registerBlock(thermalBinder, "thermalBinder", classOf[TileThermalBinder])

        //Ores
        registerBlock(oreCopper, "oreCopper", null, "oreCopper", powerAcceptor = false)
        registerBlock(oreTin, "oreTin", null, "oreTin", powerAcceptor = false)
        registerBlock(blockCopper, "blockCopper", null, "blockCopper", powerAcceptor = false)
        registerBlock(blockTin, "blockTin", null, "blockTin", powerAcceptor = false)

        //Pipes
        registerBlock(pipeBasicStructure, "pipeStructure", classOf[StructurePipe], powerAcceptor = false, classOf[ItemBlockPipe])
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
        registerBlock(basicRFStorage, "basicRFStorage", classOf[TileRFStorage], powerAcceptor = true, classOf[ItemBlockRFStorage])
        registerBlock(advancedRFStorage, "advancedRFStorage", classOf[TileRFStorage], powerAcceptor = true, classOf[ItemBlockRFStorage])
        registerBlock(eliteRFStorage, "eliteRFStorage", classOf[TileRFStorage], powerAcceptor = true, classOf[ItemBlockRFStorage])
        registerBlock(creativeRFStorage, "creativeRFStorage", classOf[TileRFStorage], powerAcceptor = true, classOf[ItemBlockRFStorage])

        //Tanks
        registerBlock(ironTank, "ironTank", classOf[TileTank], powerAcceptor = false, classOf[ItemBlockTank])
        registerBlock(goldTank, "goldTank", classOf[TileTank], powerAcceptor = false, classOf[ItemBlockTank])
        registerBlock(diamondTank, "diamondTank", classOf[TileTank], powerAcceptor = false, classOf[ItemBlockTank])
        registerBlock(creativeTank, "creativeTank", classOf[TileTank], powerAcceptor = false, classOf[ItemBlockTank])

        //misc
        registerBlock(blockCrafter, "blockCrafter", classOf[TileCrafter])
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
