package com.dyonovan.neotech.tools.tools

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.tools.ToolHelper
import com.dyonovan.neotech.tools.modifier.ModifierMiningLevel
import com.dyonovan.neotech.tools.upgradeitems.ThermalBinderItem
import com.dyonovan.neotech.utils.ClientUtils
import com.teambr.bookshelf.common.items.traits.ItemBattery
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraft.world.World
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

trait BaseElectricTool extends ItemBattery with ThermalBinderItem {

    setCreativeTab(NeoTech.tabTools)
    setMaxStackSize(1)

    override var capacity: Int = 25000
    override var maxExtract: Int = 250
    override var maxReceive: Int = 250

    def getToolName   : String

    def getBaseTexture : String



    override def onUpdate(stack: ItemStack, worldIn: World, entityIn: Entity, itemSlot: Int, isSelected: Boolean): Unit = {
        if(!stack.hasTagCompound) {
            val tagCompound = new NBTTagCompound
            val tagList = new NBTTagList
            tagList.appendTag(ModifierMiningLevel.writeToNBT(new NBTTagCompound, stack, 1))
            tagCompound.setTag(ToolHelper.ModifierListTag, tagList)
            stack.setTagCompound(tagCompound)
        }
    }

    override def onCreated(stack: ItemStack, worldIn: World, player: EntityPlayer): Unit = {
        if (stack.hasTagCompound) {
            if (stack.getTagCompound.hasKey("Energy"))
                updateDamage(stack)
            if (stack.getTagCompound.hasKey("Tier")) {
                val power = getTierPower(stack.getTagCompound.getInteger("Tier"))
                capacity = power._1
                maxExtract = power._2
                maxReceive = power._2
            }
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
            stack.setTagCompound(tagCompound)
        }
    }

    /**
      * Defines amount of power each tier holds
      *
      * @param t Battery Tier
      * @return Tuple2(capacity, maxReceive)
      */
    def getTierPower(t: Int): (Int, Int) = {
        t match {
            case 1 => (25000, 200)
            case 2 => (100000, 1000)
            case 3 => (1000000, 10000)
            case _ => (0, 0)
        }
    }

    /**
      * Turn off default enchantment look for our tools as we will be using the MC Enchantment NBT Data tag
      */
    @SideOnly(Side.CLIENT)
    override def hasEffect(stack: ItemStack): Boolean = false

    @SideOnly(Side.CLIENT)
    override def addInformation(stack: ItemStack, player: EntityPlayer, list: java.util.List[String], boolean: Boolean): Unit = {
        list.add(ClientUtils.formatNumber(getEnergyStored(stack)) + " / " + ClientUtils.formatNumber(getMaxEnergyStored(stack)) + " RF")
        list.add("")
        list.add("Upgrades: " + ToolHelper.getCurrentUpgradeCount(stack) + " / " + getMaximumUpgradeCount(stack))
        for(string <- ToolHelper.getToolTipForDisplay(stack))
            list.add(string)
    }
}
