package com.dyonovan.neotech.managers

import java.util
import javax.annotation.Nullable

import com.dyonovan.neotech.client.ItemRenderManager
import com.dyonovan.neotech.common.metals.blocks.{BlockFluidMetal, BlockMetalOre}
import com.dyonovan.neotech.common.metals.fluids.FluidMetal
import com.dyonovan.neotech.common.metals.items.ItemMetal
import com.dyonovan.neotech.lib.Reference
import net.minecraft.client.renderer.ItemMeshDefinition
import net.minecraft.client.resources.model.ModelResourceLocation
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

    //Stores all registered metals
    lazy val metalRegistry = new util.HashMap[String, Metal]()

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
        registerMetal("copper", 1, 0xFFc27646, 0xFF4676c2)
        registerMetal("tin",    1, 0xFFebeced, 0xFFedeceb)
        registerMetal("bronze", 1, 0xFFdfa62b, 0xFF2ba6df, hasOre = false)
    }

    /**
      * Used to register a metal
      *
      * @param metalName The name of the metal eg. copper
      * @param miningLevel The mining level of the ore
      * @param color The color of the metal, set for rendering
      * @return
      */
    def registerMetal(metalName : String, miningLevel : Int, color : Int, colorBlock : Int, hasOre : Boolean = true) : Metal = {
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
            oreBlock = BlockManager.registerBlock(new BlockMetalOre(oreName, colorBlock, miningLevel),
                oreName, null, oreName).asInstanceOf[BlockMetalOre]
        }

        val blockName = "block" + metalName.charAt(0).toUpper + metalName.substring(1)

        val solidBlock = BlockManager.registerBlock(new BlockMetalOre(blockName, colorBlock, 1),
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

    class MoltenMetalItemMesh(name : String) extends ItemMeshDefinition {
        val model = new ModelResourceLocation(Reference.MOD_ID + ":" + name)
        override def getModelLocation(stack : ItemStack) = model
    }
}
