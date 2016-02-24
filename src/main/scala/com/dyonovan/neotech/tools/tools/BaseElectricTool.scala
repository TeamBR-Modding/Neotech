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
            tagCompound.setInteger("EnergyCapacity", 25000)
            stack.setTagCompound(tagCompound)
        }
        capacity = stack.getTagCompound.getInteger("EnergyCapacity")
    }

    override def onCreated(stack: ItemStack, worldIn: World, player: EntityPlayer): Unit = {
        if (stack.hasTagCompound) {
            if (stack.getTagCompound.hasKey("Energy"))
                updateDamage(stack)
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
      * Adds energy to a container item. Returns the quantity of energy that was accepted.
      * This should always return 0 if the item cannot be externally charged.
      *
      * @param stack ItemStack to be charged.
      * @param maxReceive Maximum amount of energy to be sent into the item.
      * @param simulate If TRUE, the charge will only be simulated.
      * @return Amount of energy that was (or would have been, if simulated) received by the item.
      */
    override def receiveEnergy(stack: ItemStack, maxReceive: Int, simulate: Boolean): Int = {
        if (!stack.hasTagCompound) {
            stack.setTagCompound(new NBTTagCompound)
        }
        var energy: Int = stack.getTagCompound.getInteger("Energy")
        val energyReceived: Int = Math.min(stack.getTagCompound.getInteger("EnergyCapacity") - energy, Math.min(this.maxReceive, maxReceive))
        if (!simulate) {
            energy += energyReceived
            stack.getTagCompound.setInteger("Energy", energy)
            updateDamage(stack)
        }
        energyReceived
    }

    /**
      * Get the max amount of energy that can be stored in the container item.
      */
    override def getMaxEnergyStored(stack: ItemStack): Int = stack.getTagCompound.getInteger("EnergyCapacity")

    /**
      * Turn off default enchantment look for our tools as we will be using the MC Enchantment NBT Data tag
      */
    @SideOnly(Side.CLIENT)
    override def hasEffect(stack: ItemStack): Boolean = false

    @SideOnly(Side.CLIENT)
    override def addInformation(stack: ItemStack, player: EntityPlayer, list: java.util.List[String], boolean: Boolean): Unit = {
        if(stack.hasTagCompound)
            list.add(ClientUtils.formatNumber(getEnergyStored(stack)) + " / " + ClientUtils.formatNumber(getMaxEnergyStored(stack)) + " RF")
        list.add("")
        list.add("Upgrades: " + (ToolHelper.getCurrentUpgradeCount(stack) - 1) + " / " + getMaximumUpgradeCount(stack))
        for(string <- ToolHelper.getToolTipForDisplay(stack))
            list.add(string)
    }
}
