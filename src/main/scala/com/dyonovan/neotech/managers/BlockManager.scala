package com.dyonovan.neotech.managers

import com.dyonovan.neotech.common.blocks.machines.{BlockSolarPanel, BlockMachine, BlockGrinder}
import com.dyonovan.neotech.common.blocks.misc._
import com.dyonovan.neotech.common.blocks.ore.BlockOre
import com.dyonovan.neotech.common.blocks.storage._
import com.dyonovan.neotech.common.tiles.machines._
import com.dyonovan.neotech.common.tiles.machines.generators.{TileSolarPanel, TileFurnaceGenerator, TileFluidGenerator}
import com.dyonovan.neotech.common.tiles.machines.operators.{TreeFarm, TilePump}
import com.dyonovan.neotech.common.tiles.machines.processors.{TileThermalBinder, TileElectricFurnace, TileElectricCrusher}
import com.dyonovan.neotech.common.tiles.misc._
import com.dyonovan.neotech.common.tiles.storage.{TileDimStorage, TileFlushableChest, TileTank, TileRFStorage}
import com.dyonovan.neotech.pipes.blocks.{ItemBlockColored, BlockPipeSpecial, BlockPipe}
import com.dyonovan.neotech.pipes.tiles.energy.EnergyInterfacePipe
import com.dyonovan.neotech.pipes.tiles.fluid.FluidInterfacePipe
import com.dyonovan.neotech.pipes.tiles.item.ItemInterfacePipe
import com.dyonovan.neotech.pipes.tiles.structure.StructurePipe
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.item.{ItemBlock, ItemStack, EnumDyeColor}
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

    val pump = new BlockMachine("pump", classOf[TilePump], fourWayRotation = false)
    val treeFarm = new BlockMachine("treeFarm", classOf[TreeFarm], fourWayRotation = false)
    val mechanicalPipe = new BlockMechanicalPipe("mechanicalPipe")

    //ores
    val oreCopper = new BlockOre("oreCopper", 1)
    val blockCopper = new BlockOre("blockCopper", 1)
    val oreTin = new BlockOre("oreTin", 1)
    val blockTin = new BlockOre("blockTin", 1)
    val blockBronze = new BlockOre("blockBronze", 1)

    //Pipes
    val pipeBasicStructure = new BlockPipe("pipeStructure", Material.glass, true, classOf[StructurePipe])
    val pipeBasicSpeedStructure = new BlockPipe("pipeBasicSpeedStructure", Material.rock, false, classOf[StructurePipe])
    val pipeAdvancedSpeedStructure = new BlockPipe("pipeAdvancedSpeedStructure", Material.rock, false, classOf[StructurePipe])
    val pipeEliteSpeedStructure = new BlockPipe("pipeEliteSpeedStructure", Material.rock, false, classOf[StructurePipe])
    val pipeItemInterface = new BlockPipeSpecial("pipeItemBasicInterface", Material.rock, classOf[ItemInterfacePipe])
    val pipeEnergyInterface = new BlockPipeSpecial("pipeEnergyBasicInterface", Material.rock, classOf[EnergyInterfacePipe])
    val pipeFluidInterface = new BlockPipeSpecial("pipeFluidBasicInterface", Material.rock, classOf[FluidInterfacePipe])

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

    //Solar Panels
    val solarPanelT1 = new BlockSolarPanel("solarPanelT1", 1)
    val solarPanelT2 = new BlockSolarPanel("solarPanelT2", 2)
    val solarPanelT3 = new BlockSolarPanel("solarPanelT3", 3)

    //Misc
    val blockCrafter = new BlockCrafter("blockCrafter", classOf[TileCrafter])
    val blockMiniatureSun = new BlockMiniatureSun("blockMiniatureSun", classOf[TileFertilizer])
    val blockMiniatureStar = new BlockStar("blockMiniatureStar")
    val playerPlate = new BlockPlayerPlate
    val chunkLoader = new BlockChunkLoader
    val flushableChest = new BlockFlushableChest
    val dimStorage = new BlockDimStorage("dimStorage")
    val creativeDimStorage = new BlockCreativeDimStorage
    val redstoneClock = new BlockRedstoneClock

    def preInit(): Unit = {
        //Machines
        registerBlock(grinder, "grinder", classOf[TileGrinder])

        registerBlock(electricFurnace, "electricFurnace", classOf[TileElectricFurnace])
        registerBlock(electricCrusher, "electricCrusher", classOf[TileElectricCrusher])
        registerBlock(furnaceGenerator, "furnaceGenerator", classOf[TileFurnaceGenerator])
        registerBlock(fluidGenerator, "fluidGenerator", classOf[TileFluidGenerator])
        registerBlock(thermalBinder, "thermalBinder", classOf[TileThermalBinder])

        registerBlock(pump, "pump", classOf[TilePump])
        registerBlock(treeFarm, "treeFarm", classOf[TreeFarm])
        registerBlock(mechanicalPipe, "mechanicalPipe", null)

        //Ores
        registerBlock(oreCopper, "oreCopper", null, "oreCopper")
        registerBlock(oreTin, "oreTin", null, "oreTin")
        registerBlock(blockCopper, "blockCopper", null, "blockCopper")
        registerBlock(blockTin, "blockTin", null, "blockTin")
        registerBlock(blockBronze, "blockBronze", null, "blockBronze")

        //Pipes
        registerBlock(pipeBasicStructure, "pipeStructure", classOf[StructurePipe], classOf[ItemBlockColored])
        for(color <- EnumDyeColor.values())
            OreDictionary.registerOre("pipeStructure", new ItemStack(pipeBasicStructure, 1, color.getMetadata))
        registerBlock(pipeBasicSpeedStructure, "pipeBasicSpeedStructure", classOf[StructurePipe])
        registerBlock(pipeAdvancedSpeedStructure, "pipeAdvancedSpeedStructure", classOf[StructurePipe])
        registerBlock(pipeEliteSpeedStructure, "pipeEliteSpeedStructure", classOf[StructurePipe])
        registerBlock(pipeItemInterface, "pipeItemBasicInterface", classOf[ItemInterfacePipe])
        registerBlock(pipeEnergyInterface, "pipeEnergyBasicInterface", classOf[EnergyInterfacePipe])
        registerBlock(pipeFluidInterface, "pipeFluidBasicInterface", classOf[FluidInterfacePipe])

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

        //Solar Panels
        registerBlock(solarPanelT1, "solarPanelT1", classOf[TileSolarPanel])
        registerBlock(solarPanelT2, "solarPanelT2", classOf[TileSolarPanel])
        registerBlock(solarPanelT3, "solarPanelT3", classOf[TileSolarPanel])

        //misc
        registerBlock(blockCrafter, "blockCrafter", classOf[TileCrafter])
        registerBlock(blockMiniatureSun, "blockMiniatureSun", classOf[TileFertilizer])
        registerBlock(blockMiniatureStar, "blockMiniatureStar", classOf[TileStar], classOf[ItemBlockColored])
        for(color <- EnumDyeColor.values())
            OreDictionary.registerOre("blockMiniatureStar", new ItemStack(blockMiniatureStar, 1, color.getMetadata))
        registerBlock(playerPlate, "playerPlate", null)
        registerBlock(chunkLoader, "chunkLoader", classOf[TileChunkLoader])
        registerBlock(flushableChest, "flushableChest", classOf[TileFlushableChest])
        registerBlock(dimStorage, "dimStorage", classOf[TileDimStorage], classOf[ItemBlockDimStorage])
        registerBlock(redstoneClock, "redstoneClock", classOf[TileRedstoneClock])
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
