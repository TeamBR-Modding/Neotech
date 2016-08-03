package com.dyonovan.neotech.tools.tools

import java.util

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.tools.ToolHelper
import com.dyonovan.neotech.tools.modifier.ModifierMiningLevel
import com.dyonovan.neotech.tools.upgradeitems.ThermalBinderItem
import com.teambr.bookshelf.common.items.traits.{ItemBattery, ItemModelProvider}
import com.teambr.bookshelf.loadables.CreatesTextures
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{EnumRarity, Item, ItemStack}
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import scala.collection.mutable.ArrayBuffer

/**
  * This file was created for Bookshelf API
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 2/21/2016
  */
trait BaseElectricTool extends Item with ItemBattery with ThermalBinderItem with ItemModelProvider with CreatesTextures {

    setCreativeTab(NeoTech.tabTools)
    setMaxStackSize(1)

    /**
      * The tool name of this tool, should be all lower case and used when getting the tool info
      *
      * @return The tool name
      */
    def getToolName   : String

    /**
      * Used by the model to get the base texture, this should be with no upgrades installed
      *
      * @return The texture location, eg "neotech:items/tools/sword/sword
      */
    def getBaseTexture : String

    /**
      * Defines if this should be rendered as a tool
      *
      * @return Is a tool
      */
    override def isTool = true

    /**
      * Used to get a list of textures to render, order is important
      *
      * @return
      */
    override def getTextures(stack: ItemStack) : java.util.List[String] = {
        val list = new util.ArrayList[String]()
        list.add(getBaseTexture)

        if(stack.getItem.isInstanceOf[ElectricPickaxe])
            list.add("neotech:items/tools/pickaxe/miningLevel1")

        val modifierList = ToolHelper.getModifierTagList(stack)
        if(modifierList != null) {
            for(x <- 0 until modifierList.tagCount()) {
                val tagCompound = modifierList.getCompoundTagAt(x)
                if(!tagCompound.hasKey("Active") || (tagCompound.hasKey("Active") && tagCompound.getBoolean("Active"))) {
                    list.add(tagCompound.getString("TextureLocation"))
                }
            }
        }

        list
    }

    def getTexturesToStitch : ArrayBuffer[String] =  ArrayBuffer(
        "neotech:items/tools/sword/electricSword", "neotech:items/tools/pickaxe/electricPickaxe",
        "neotech:items/tools/pickaxe/miningLevel1", "neotech:items/tools/pickaxe/miningLevel2",
        "neotech:items/tools/pickaxe/miningLevel3", "neotech:items/tools/pickaxe/miningLevel4",
        "neotech:items/tools/pickaxe/miningSpeed", "neotech:items/tools/pickaxe/silkTouch",
        "neotech:items/tools/pickaxe/fortune", "neotech:items/tools/pickaxe/aoe",
        "neotech:items/tools/pickaxe/shovelPick", "neotech:items/tools/pickaxe/lighting",
        "neotech:items/tools/sword/sharpness", "neotech:items/tools/sword/smite",
        "neotech:items/tools/sword/beheading", "neotech:items/tools/sword/spiderBane",
        "neotech:items/tools/sword/looting")

    /*******************************************************************************************************************
      ****************************************** Item/Tool Functions ***************************************************
      ******************************************************************************************************************/
    /**
      * Used to define how much an operation costs
      *
      * @return How much energy to drain
      */
    def RF_COST(stack : ItemStack) : Int = 50

    /**
      * Extracts the energy, if in creative it won't
      *
      * @param player The player in
      * @param stack The stack
      */
    def rfCost(player: EntityPlayer, stack: ItemStack): Unit = {
        if (!player.capabilities.isCreativeMode) {
            extractEnergy(stack, RF_COST(stack), simulate = false)
            updateDamage(stack)
        }
    }

    /**
      * We want this name to be special
      *
      * @param stack The stack
      * @return The rarity of the item
      */
    override def getRarity(stack: ItemStack): EnumRarity = EnumRarity.RARE

    /**
      * Keeps this from being enchanted
      */
    override def isBookEnchantable(stack: ItemStack, book: ItemStack): Boolean = false

    override def setDefaultTags(stack: ItemStack): Unit = {
        var tagList: NBTTagList = new NBTTagList
        val tag = new NBTTagCompound
        if (stack.hasTagCompound) {
            tagList = stack.getTagCompound.getTagList(ToolHelper.ModifierListTag, 10) // Load the modifier list
            if(tagList != null && ModifierMiningLevel.getModifierTagFromStack(stack) == null) // Does not already exist
                tagList.appendTag(ModifierMiningLevel.writeToNBT(new NBTTagCompound, stack, 1)) // Put mining level one in
            tag.setTag(ToolHelper.ModifierListTag, tagList) // Send back the tag
        } else {
            // Set empty modifier list
            tagList.appendTag(ModifierMiningLevel.writeToNBT(new NBTTagCompound, stack, 1))
            tag.setTag(ToolHelper.ModifierListTag, tagList)

        }
        var tier = 1
        if (stack.hasTagCompound && stack.getTagCompound.hasKey("Tier"))
            tier = stack.getTagCompound.getInteger("Tier")
        val amount = getTierPower(tier)
        tag.setInteger("EnergyCapacity", amount._1)
        tag.setInteger("MaxExtract", amount._2)
        tag.setInteger("MaxReceive", amount._2)
        tag.setInteger("Tier", tier)
        stack.setTagCompound(tag)
    }

    /**
      * This will allow us to add more tool classes based on the stack
      *
      * @param stack The stack in
      * @return The list of effective tools
      */
    override def getToolClasses(stack: ItemStack): java.util.Set[String] =
        super.getToolClasses(stack)

    /**
      * To prevent the stack from taking damage while hitting entities, we must override this method
      */
    override def hitEntity(stack: ItemStack, target: EntityLivingBase, attacker: EntityLivingBase) : Boolean =
        extractEnergy(stack, RF_COST(stack), simulate = false) > 0

    def getTierPower(t: Int): (Int, Int) = {
        t match {
            case 1 => (25000, 250)
            case 2 => (100000, 1000)
            case 3 => (1000000, 10000)
            case _ => (25000, 250)
        }
    }

    /**
      * Disable MC Effect
      */
    @SideOnly(Side.CLIENT)
    override def hasEffect(stack: ItemStack): Boolean = false

    /*******************************************************************************************************************
      *********************************************** Misc Functions ***************************************************
      ******************************************************************************************************************/

    @SideOnly(Side.CLIENT)
    override def addInformation(stack: ItemStack, player: EntityPlayer, list: java.util.List[String], boolean: Boolean): Unit = {
        for(string <- ToolHelper.getToolTipForDisplay(stack))
            list.add(string)
    }
}
