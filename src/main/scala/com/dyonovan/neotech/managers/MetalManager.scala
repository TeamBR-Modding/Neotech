package com.dyonovan.neotech.managers

import java.util
import javax.annotation.Nullable

import com.dyonovan.neotech.common.metals.blocks.{BlockFluidMetal, BlockMetalOre}
import com.dyonovan.neotech.common.metals.fluids.FluidMetal
import com.dyonovan.neotech.common.metals.items.ItemMetal
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.{Fluid, FluidRegistry}
import net.minecraftforge.fml.common.registry.GameRegistry

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * Class to handle creation of all metals, includes fluids, fluid blocks, blocks, ingots, nuggets, dusts and ores
  *
  * @author Paul Davis <pauljoda>
  * @since 2/17/2016
  */
object MetalManager {

    //Stores all registered metals
    lazy val metalRegistry = new util.HashMap[String, Metal]()

    def registerModels() : Unit = {}

    def registerDefaultMetals() : Unit = {
        registerMetal("copper", 1, 0xFFc27646)
        registerMetal("tin",    1, 0xFFebeced)
        registerMetal("bronze", 1, 0xFFdfa62b, hasOre = false)
    }

    /**
      * Used to register a metal
      *
      * @param metalName The name of the metal eg. copper
      * @param miningLevel The mining level of the ore
      * @param color The color of the metal, set for rendering
      * @return
      */
    def registerMetal(metalName : String, miningLevel : Int, color : Int, hasOre : Boolean = true) : Metal = {
        val metalNameBase = metalName.toLowerCase

        /***************************************************************************************************************
          ********************************************** Fluid *********************************************************
          **************************************************************************************************************/

        // Create the Fluid
        val fluid = createFluidMetal(color, metalNameBase, "neotech:blocks/metal")

        //Create the Fluid Block
        val fluidBlock = registerFluidBlock(fluid, new BlockFluidMetal(fluid))

        /***************************************************************************************************************
          ********************************************* Blocks *********************************************************
          **************************************************************************************************************/

        val oreName = "ore" + metalName.charAt(0).toUpper + metalName.substring(1)

        var oreBlock : BlockMetalOre = null
        if(hasOre) {
            oreBlock = BlockManager.registerBlock(new BlockMetalOre(oreName, color, miningLevel),
                oreName, null, oreName).asInstanceOf[BlockMetalOre]
        }

        val blockName = "block" + metalName.charAt(0).toUpper + metalName.substring(1)

        val solidBlock = BlockManager.registerBlock(new BlockMetalOre(blockName, color, 1),
            blockName, null, blockName).asInstanceOf[BlockMetalOre]

        /***************************************************************************************************************
          ********************************************** Items *********************************************************
          **************************************************************************************************************/

        val ingotName = "ingot" + metalName.charAt(0).toUpper + metalName.substring(1)

        val ingot = ItemManager.registerItem(new ItemMetal(ingotName, color, 64),
            ingotName, ingotName).asInstanceOf[ItemMetal]

        val dustName = "dust" + metalName.charAt(0).toUpper + metalName.substring(1)

        val dust = ItemManager.registerItem(new ItemMetal(dustName, color, 64),
            dustName, dustName).asInstanceOf[ItemMetal]

        val nuggetName = "nugget" + metalName.charAt(0).toUpper + metalName.substring(1)

        val nugget = ItemManager.registerItem(new ItemMetal(nuggetName, color, 64),
            nuggetName, nuggetName).asInstanceOf[ItemMetal]

        /***************************************************************************************************************
          ********************************************** Metal *********************************************************
          **************************************************************************************************************/

        val metal = new Metal(metalName, Option(fluid), Option(fluidBlock),
            Option(oreBlock), Option(solidBlock),
            Option(ingot), Option(dust), Option(nugget))

        metalRegistry.put(metalName.toLowerCase, metal)

        metal
    }

    /**
      * Get the metal from the registry
      *
      * @param name The name of the metal
      * @return The metal, null if not in registry
      */
    def getMetal(name : String) : Option[Metal] = {
        if(metalRegistry.containsKey(name.toLowerCase))
            Some(metalRegistry.get(name.toLowerCase))
        else
            None
    }

    /**
      * Used to create a fluid for this metal
      *
      * @param color The color of the fluid, format 0xAARRGGBB
      * @param name The name of the fluid eg. copper
      * @param texture The texture base eg. neotech:blocks/metal
      * @return The fluid created, null if not registered
      */
    @Nullable
    def createFluidMetal(color : Int, name: String, texture: String): FluidMetal = {
        val still = new ResourceLocation(texture + "_still")
        val flowing = new ResourceLocation(texture + "_flow")

        val fluid = new FluidMetal(color, name, still, flowing)
        if(FluidRegistry.getFluid(fluid.getName) == null) {
            FluidRegistry.registerFluid(fluid)
            FluidRegistry.addBucketForFluid(fluid)
            fluid
        } else null
    }

    /**
      * Registers the Fluid Block to the fluid
      *
      * @param fluid The fluid
      * @param block The fluid block
      * @return The block passed back
      */
    def registerFluidBlock(fluid: Fluid, block: BlockFluidMetal): BlockFluidMetal = {
        if(fluid != null) {
            fluid.setBlock(block)
            val fluidName = block.getFluid.getUnlocalizedName()
            block.setUnlocalizedName(fluidName)
            GameRegistry.registerBlock(block, fluidName)
        }
        block
    }

    /**
      * Used to hold all information about a metal, some elements may be null, always check
      *
      * @param oreDict The ore dict tag of the metal
      * @param fluid The fluid of the metal
      * @param fluidBlock The fluid block of the metal
      * @param oreBlock The Ore Block or the metal
      * @param block The solid block of the metal
      * @param ingot The ingot of the metal
      * @param dust The dust of the metal
      * @param nugget The nugget of the metal
      */
    class Metal(val oreDict : String, val fluid : Option[FluidMetal], val fluidBlock : Option[BlockFluidMetal],
                val oreBlock : Option[BlockMetalOre], val block : Option[BlockMetalOre], val ingot : Option[ItemMetal],
                val dust : Option[ItemMetal], val nugget : Option[ItemMetal])
}
