package com.dyonovan.neotech.tools.tools

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.tools.ToolHelper
import com.dyonovan.neotech.tools.modifier.ModifierMiningLevel
import com.dyonovan.neotech.tools.upgradeitems.ThermalBinderItem
import com.teambr.bookshelf.common.items.traits.ItemBattery
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{EnumRarity, Item, ItemStack}
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

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
trait BaseElectricTool extends Item with ItemBattery with ThermalBinderItem {

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

    /*******************************************************************************************************************
      ****************************************** Item/Tool Functions ***************************************************
      ******************************************************************************************************************/
    /**
      * Used to define how much an operation costs
      *
      * @return How much energy to drain
      */
    def RF_COST(stack : ItemStack) : Int = 250

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
        if (stack.hasTagCompound) {
            val tag = stack.getTagCompound.getTagList(ToolHelper.ModifierListTag, 10) // Load the modifier list
            if(tag != null && ModifierMiningLevel.getModifierTagFromStack(stack) == null) // Does not already exist
                tag.appendTag(ModifierMiningLevel.writeToNBT(new NBTTagCompound, stack, 1)) // Put mining level one in
            stack.getTagCompound.setTag(ToolHelper.ModifierListTag, tag) // Send back the tag
        } else {
            // Set empty modifier list
            val tagCompound = new NBTTagCompound
            val tagList = new NBTTagList
            tagList.appendTag(ModifierMiningLevel.writeToNBT(new NBTTagCompound, stack, 1))
            tagCompound.setTag(ToolHelper.ModifierListTag, tagList)

            val amount = getTierPower(stack.getT)
            tagCompound.setInteger("EnergyCapacity", 25000)
            tagCompound.setInteger("MaxExtract", 200)
            tagCompound.setInteger("MaxReceive", 200)
            stack.setTagCompound(tagCompound)
        }
        var tier = 1
        if (stack.hasTagCompound && stack.getTagCompound.hasKey("Tier"))
            tier = stack.getTagCompound.getInteger("Tier")
        val amount = getTierPower(tier)
        val tag = new NBTTagCompound
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
