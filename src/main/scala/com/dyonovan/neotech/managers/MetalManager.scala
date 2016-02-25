package com.dyonovan.neotech.managers

import java.util
import javax.annotation.Nullable

import com.dyonovan.neotech.client.ItemRenderManager
import com.dyonovan.neotech.common.metals.blocks.{BlockFluidMetal, BlockMetalOre}
import com.dyonovan.neotech.common.metals.fluids.FluidMetal
import com.dyonovan.neotech.common.metals.items.ItemMetal
import com.dyonovan.neotech.registries.ConfigRegistry
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
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

    // Stores all registered metals
    lazy val metalRegistry = new util.HashMap[String, Metal]()

    // Fluid Output Values
    lazy val BLOCK_MB  = 1296
    lazy val ORE_MB    = 432
    lazy val INGOT_MB  = 144
    lazy val DUST_MB   = 72
    lazy val NUGGET_MB = 16

    def registerModels() : Unit = {
        MinecraftForge.EVENT_BUS.register(this)
        val iterator = metalRegistry.keySet().iterator()
        while(iterator.hasNext) {
            val metal = metalRegistry.get(iterator.next())
            //Blocks
            if(metal.block.isDefined)
                ItemRenderManager.registerItem(Item.getItemFromBlock(metal.block.get))

            //Ore
            if(metal.oreBlock.isDefined)
                ItemRenderManager.registerItem(Item.getItemFromBlock(metal.oreBlock.get))

            //Ingots
            if(metal.ingot.isDefined)
                ItemRenderManager.registerItem(metal.ingot.get)

            //Dust
            if(metal.dust.isDefined)
                ItemRenderManager.registerItem(metal.dust.get)

            //Nugget
            if(metal.nugget.isDefined)
                ItemRenderManager.registerItem(metal.nugget.get)

            // Fluids
            if(metal.fluidBlock.isDefined)
                ItemRenderManager.registerItem(Item.getItemFromBlock(metal.fluidBlock.get))
        }
    }

    def registerDefaultMetals() : Unit = {
        registerMetal("copper", 1, 0xFFc27a49, 0xFFc27a49)
        registerMetal("dirtycopper", 1, 0xFFc27a49, 0xFFc27a49,
            hasDust = false, dirtyFluid = true, hasIngot = false, hasNugget = false, hasOre = false, hasSolidBlock = false)
        registerMetal("tin",    1, 0xFFe7eadd, 0xFFe7eadd)
        registerMetal("dirtytin", 1, 0xFFe7eadd, 0xFFe7eadd,
            hasDust = false, dirtyFluid = true, hasIngot = false, hasNugget = false, hasOre = false, hasSolidBlock = false)
        registerMetal("lead",   1, 0xFF796a78, 0xFF796a78)
        registerMetal("dirtylead", 2, 0xFF796a78, 0xFF796a78,
            hasDust = false, dirtyFluid = true, hasIngot = false, hasNugget = false, hasOre = false, hasSolidBlock = false)
        registerMetal("silver", 1, 0xFFc5d8de, 0xFFc5d8de)
        registerMetal("dirtysilver", 2, 0xFFc5d8de, 0xFFc5d8de,
            hasDust = false, dirtyFluid = true, hasIngot = false, hasNugget = false, hasOre = false, hasSolidBlock = false)
        registerMetal("bronze", 1, 0xFFcd9520, 0xFFcd9520, hasOre = false)
        registerMetal("steel",  1, 0xFF646464, 0xFF646464, hasOre = false)
        registerMetal("gold",   1, 0xFFdede00, 0xFFdede00, hasOre = false, hasSolidBlock = false, hasIngot = false, hasNugget = false)
        registerMetal("dirtygold", 1, 0xFFdede00, 0xFFdede00,
            hasDust = false, dirtyFluid = true, hasIngot = false, hasNugget = false, hasOre = false, hasSolidBlock = false)
        registerMetal("iron",   1, 0xFFd8d8d8, 0xFFb71b1b, hasOre = false, hasSolidBlock = false, hasIngot = false)
        registerMetal("dirtyiron", 1, 0xFFd8d8d8, 0xFFb71b1b,
            hasDust = false, dirtyFluid = true, hasIngot = false, hasNugget = false, hasOre = false, hasSolidBlock = false)
        registerMetal("carbon", 1, 0xFF202020, 0xFF202020,
            hasDust = false, dirtyFluid = true, hasIngot = false, hasNugget = false, hasOre = false, hasSolidBlock = false)
        registerMetal("obsidian", 1, 0xFF583962, 0xFF583962,
            hasDust = false, hasIngot = false, hasNugget = false, hasOre = false, hasSolidBlock = false)
    }

    /**
      * Used to register a metal
      *
      * @param metalName The name of the metal eg. copper
      * @param miningLevel The mining level of the ore
      * @param color The color of the metal, set for rendering
      * @return
      */
    def registerMetal(metalName : String, miningLevel : Int, color : Int, fluidColor : Int,
                      hasFluid : Boolean = true, dirtyFluid : Boolean = false, hasFluidBlock : Boolean = true,
                      hasOre : Boolean = true, hasSolidBlock : Boolean = true,
                      hasIngot : Boolean = true, hasDust : Boolean = true, hasNugget : Boolean = true) : Metal = {
        val metalNameBase = metalName.toLowerCase

        /***************************************************************************************************************
          ********************************************** Fluid *********************************************************
          **************************************************************************************************************/

        // Create the Fluid
        var fluid : Fluid = null
        if(hasFluid)
            fluid = createFluidMetal(fluidColor, dirtyFluid, metalNameBase, "neotech:blocks/metal")

        //Create the Fluid Block
        var fluidBlock : BlockFluidMetal = null
        if(hasFluidBlock)
            fluidBlock = registerFluidBlock(fluid, new BlockFluidMetal(fluid))

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

        var solidBlock : BlockMetalOre = null
        if(hasSolidBlock)
            solidBlock = BlockManager.registerBlock(new BlockMetalOre(blockName, color, 1),
                blockName, null, blockName).asInstanceOf[BlockMetalOre]

        /***************************************************************************************************************
          ********************************************** Items *********************************************************
          **************************************************************************************************************/

        val ingotName = "ingot" + metalName.charAt(0).toUpper + metalName.substring(1)

        var ingot : ItemMetal = null
        if(hasIngot)
            ingot = ItemManager.registerItem(new ItemMetal(ingotName, color, 64),
                ingotName, ingotName).asInstanceOf[ItemMetal]

        val dustName = "dust" + metalName.charAt(0).toUpper + metalName.substring(1)

        var dust : ItemMetal = null
        if(hasDust)
            dust = ItemManager.registerItem(new ItemMetal(dustName, color, 64),
                dustName, dustName).asInstanceOf[ItemMetal]

        val nuggetName = "nugget" + metalName.charAt(0).toUpper + metalName.substring(1)

        var nugget : ItemMetal = null
        if(hasNugget)
            nugget = ItemManager.registerItem(new ItemMetal(nuggetName, color, 64),
                nuggetName, nuggetName).asInstanceOf[ItemMetal]

        /***************************************************************************************************************
          ********************************************** Metal *********************************************************
          **************************************************************************************************************/

        val metal = new Metal(metalName, Option(fluid), Option(fluidBlock),
            Option(oreBlock), Option(solidBlock),
            Option(ingot), Option(dust), Option(nugget))

        metalRegistry.put(metalName.toLowerCase, metal)

        /***************************************************************************************************************
          ******************************************* Crafting *********************************************************
          **************************************************************************************************************/

        //Ore to ingot
        if(metal.oreBlock.isDefined && metal.ingot.isDefined) {
            GameRegistry.addSmelting(metal.oreBlock.get, new ItemStack(metal.ingot.get, 1), 0.5F)
        }

        //Dust to ingot
        if(metal.ingot.isDefined && metal.dust.isDefined) {
            GameRegistry.addSmelting(metal.dust.get, new ItemStack(metal.ingot.get, 1), 0.5F)
        }

        // Nugget - Ingot
        if(metal.ingot.isDefined && metal.nugget.isDefined) {
            GameRegistry.addShapelessRecipe(new ItemStack(metal.nugget.get, 9), metal.ingot.get)
            GameRegistry.addRecipe(new ItemStack(metal.ingot.get, 1),
                "III",
                "III",
                "III", 'I'.asInstanceOf[java.lang.Character], metal.nugget.get)
        }

        // Ingot - Block
        if(metal.ingot.isDefined && metal.block.isDefined) {
            GameRegistry.addShapelessRecipe(new ItemStack(metal.ingot.get, 9), metal.block.get)
            GameRegistry.addRecipe(new ItemStack(metal.block.get, 1),
                "III",
                "III",
                "III", 'I'.asInstanceOf[java.lang.Character], metal.ingot.get)
        }

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
      * @param iconLoc The texture base eg. neotech:blocks/metal
      * @return The fluid created, null if not registered
      */
    @Nullable
    def createFluidMetal(color : Int, dirtyMetal : Boolean, name: String, iconLoc: String): Fluid = {
        var texture = iconLoc
        if(dirtyMetal)
            texture += "_dirty"
        val still = new ResourceLocation(texture + "_still")
        val flowing = new ResourceLocation(texture + "_flow")

        var fluid : Fluid = new FluidMetal(color, name, still, flowing)
        if(ConfigRegistry.generateFluids) {
            if(!FluidRegistry.isFluidRegistered(fluid))
                FluidRegistry.registerFluid(fluid)
            else
                fluid = FluidRegistry.getFluid(name)
            if(!FluidRegistry.getBucketFluids.contains(fluid))
                FluidRegistry.addBucketForFluid(fluid)
            fluid
        } else FluidRegistry.getFluid(fluid.getName)
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
    class Metal(val oreDict : String, val fluid : Option[Fluid], val fluidBlock : Option[BlockFluidMetal],
                val oreBlock : Option[BlockMetalOre], val block : Option[BlockMetalOre], val ingot : Option[ItemMetal],
                val dust : Option[ItemMetal], val nugget : Option[ItemMetal])
}
