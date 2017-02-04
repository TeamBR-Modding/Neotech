package com.teambrmodding.neotech.managers

import com.teambrmodding.neotech.common.blocks.machines.{BlockMachine, BlockSolarPanel}
import com.teambrmodding.neotech.common.blocks.storage._
import com.teambrmodding.neotech.common.tiles.machines.generators.{TileFluidGenerator, TileFurnaceGenerator, TileSolarPanel}
import com.teambrmodding.neotech.common.tiles.machines.operators.TileTreeFarm
import com.teambrmodding.neotech.common.tiles.machines.processors._
import com.teambrmodding.neotech.common.tiles.storage.TileRFStorage
import com.teambrmodding.neotech.common.tiles.storage.tanks._
import net.minecraft.block.Block
import net.minecraft.item.ItemBlock
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
    val electricFurnace = new BlockMachine("electricFurnace", classOf[TileElectricFurnace])
    val electricCrusher = new BlockMachine("electricCrusher", classOf[TileElectricCrusher])
    val furnaceGenerator = new BlockMachine("furnaceGenerator", classOf[TileFurnaceGenerator])
    val fluidGenerator = new BlockMachine("fluidGenerator", classOf[TileFluidGenerator])
    val electricCrucible = new BlockMachine("electricCrucible", classOf[TileCrucible])
    val electricSolidifier = new BlockMachine("electricSolidifier", classOf[TileSolidifier])
    val electricAlloyer = new BlockMachine("alloyer", classOf[TileAlloyer])
    val electricCentrifuge = new BlockMachine("centrifuge", classOf[TileCentrifuge])

    val treeFarm = new BlockMachine("treeFarm", classOf[TileTreeFarm], fourWayRotation = false)

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
    val voidTank = new BlockTank("voidTank", 5)

    //Solar Panels
    val solarPanelT1 = new BlockSolarPanel("solarPanelT1", 1)
    val solarPanelT2 = new BlockSolarPanel("solarPanelT2", 2)
    val solarPanelT3 = new BlockSolarPanel("solarPanelT3", 3)

    def preInit(): Unit = {
        //Machines
        registerBlock(electricFurnace, "electricFurnace", classOf[TileElectricFurnace])
        registerBlock(electricCrusher, "electricCrusher", classOf[TileElectricCrusher])
        registerBlock(furnaceGenerator, "furnaceGenerator", classOf[TileFurnaceGenerator])
        registerBlock(fluidGenerator, "fluidGenerator", classOf[TileFluidGenerator])
        registerBlock(electricCrucible, "electricCrucible", classOf[TileCrucible])
        registerBlock(electricSolidifier, "electricSolidifier", classOf[TileSolidifier])
        registerBlock(electricAlloyer, "alloyer", classOf[TileAlloyer])
        registerBlock(electricCentrifuge, "centrifuge", classOf[TileCentrifuge])

        registerBlock(treeFarm, "treeFarm", classOf[TileTreeFarm])

        //RF Storage
        registerBlock(basicRFStorage, "basicRFStorage", classOf[TileRFStorage], classOf[ItemBlockRFStorage])
        registerBlock(advancedRFStorage, "advancedRFStorage", classOf[TileRFStorage], classOf[ItemBlockRFStorage])
        registerBlock(eliteRFStorage, "eliteRFStorage", classOf[TileRFStorage], classOf[ItemBlockRFStorage])
        registerBlock(creativeRFStorage, "creativeRFStorage", classOf[TileRFStorage], classOf[ItemBlockRFStorage])

        //Tanks
        registerBlock(ironTank, "ironTank", classOf[TileIronTank], classOf[ItemBlockTank])
        registerBlock(goldTank, "goldTank", classOf[TileGoldTank], classOf[ItemBlockTank])
        registerBlock(diamondTank, "diamondTank", classOf[TileDiamondTank], classOf[ItemBlockTank])
        registerBlock(creativeTank, "creativeTank", classOf[TileCreativeTank], classOf[ItemBlockTank])
        registerBlock(voidTank, "voidTank", classOf[TileVoidTank], classOf[ItemBlockTank])

        //Solar Panels
        registerBlock(solarPanelT1, "solarPanelT1", classOf[TileSolarPanel])
        registerBlock(solarPanelT2, "solarPanelT2", classOf[TileSolarPanel])
        registerBlock(solarPanelT3, "solarPanelT3", classOf[TileSolarPanel])
    }

    /**
      * Helper method for registering block
      *
      * @param block      The block to register
      * @param name       The name to register the block to
      * @param tileEntity The tile entity, null if none
      * @param oreDict    The ore dict tag, should it be needed
      */
    def registerBlock(block: Block, name: String, tileEntity: Class[_ <: TileEntity], oreDict: String) : Block = {
        GameRegistry.registerBlock(block, name)
        if (tileEntity != null)
            GameRegistry.registerTileEntity(tileEntity, name)
        if (oreDict != null)
            OreDictionary.registerOre(oreDict, block)
        block
    }

    def registerBlock(block: Block, name: String, tileEntity: Class[_ <: TileEntity], itemBlock: Class[_ <: ItemBlock]) : Block = {
        GameRegistry.registerBlock(block, itemBlock, name)
        if (tileEntity != null)
            GameRegistry.registerTileEntity(tileEntity, name)
        block
    }

    /**
      * No ore dict helper method
      *
      * @param block      The block to add
      * @param name       The name
      * @param tileEntity The tile
      */
    def registerBlock(block: Block, name: String, tileEntity: Class[_ <: TileEntity]) : Block = {
        val oreDict: String = null
        registerBlock(block, name, tileEntity, oreDict)
    }
}
