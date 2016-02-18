package com.dyonovan.neotech.common.items.traits

import cofh.api.energy.IEnergyContainerItem
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.NBTTagCompound

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 2/17/2016
  */
trait ItemBattery extends Item with IEnergyContainerItem {

    setMaxDamage(16)

    protected var capacity: Int = 0
    protected var maxReceive: Int = 0
    protected var maxExtract: Int = 0

    override def receiveEnergy(stack: ItemStack, maxReceive: Int, simulate: Boolean): Int = {
        if (!stack.hasTagCompound) {
            stack.setTagCompound(new NBTTagCompound)
        }
        var energy: Int = stack.getTagCompound.getInteger("Energy")
        val energyReceived: Int = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive))
        if (!simulate) {
            energy += energyReceived
            stack.getTagCompound.setInteger("Energy", energy)
            updateDamage(stack)
        }
        energyReceived
    }

    override def extractEnergy(stack: ItemStack, maxExtract: Int, simulate: Boolean): Int = {
        if (stack.getTagCompound == null || !stack.getTagCompound.hasKey("Energy")) {
            return 0
        }
        var energy = stack.getTagCompound.getInteger("Energy")
        val energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract))

        if (!simulate) {
            energy -= energyExtracted
            stack.getTagCompound.setInteger("Energy", energy)
            updateDamage(stack)
        }
        energyExtracted
    }

    override def getEnergyStored(stack: ItemStack): Int = {
        if (stack.getTagCompound == null || !stack.getTagCompound.hasKey("Energy"))
            return 0
        stack.getTagCompound.getInteger("Energy")
    }

    override def getMaxEnergyStored(stack: ItemStack): Int = capacity

    private def updateDamage(stack: ItemStack): Unit = {
        val r = getEnergyStored(stack).toFloat / getMaxEnergyStored(stack)
        val res = 16 - Math.round(r * 16)
        stack.setItemDamage(res)
    }
}
