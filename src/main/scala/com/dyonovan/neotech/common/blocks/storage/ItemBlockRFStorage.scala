package com.dyonovan.neotech.common.blocks.storage

import cofh.api.energy.IEnergyContainerItem
import com.dyonovan.neotech.managers.BlockManager
import com.teambr.bookshelf.client.gui.GuiColor
import net.minecraft.block.Block
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{ItemStack, Item, ItemBlock}
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 16, 2015
 */
class ItemBlockRFStorage(block: Block) extends ItemBlock(block) with IEnergyContainerItem {

    setNoRepair()
    setMaxStackSize(1)
    setMaxDamage(16)
    setHasSubtypes(true)

    @SideOnly(Side.CLIENT)
    override def getSubItems(item: Item, tab: CreativeTabs, subItems: java.util.List[_]): Unit = {
        var is = new ItemStack(this)
        setEnergy(is, getEnergyInfo._2)
        subItems.asInstanceOf[java.util.List[ItemStack]].add(is)

        if (getEnergyInfo._1 != 4) {
            is = new ItemStack(this)
            setEnergy(is, 0)
            subItems.asInstanceOf[java.util.List[ItemStack]].add(is)
        }
    }

    @SideOnly(Side.CLIENT)
    override def addInformation(stack: ItemStack, player: EntityPlayer, list: java.util.List[_], boolean: Boolean): Unit = {
        if (stack.hasTagCompound) {
            if (stack.getTagCompound.getInteger("Energy") != 0) {
                list.asInstanceOf[java.util.List[String]].add(GuiColor.ORANGE + (stack.getTagCompound.getInteger("Energy") + "/" + getEnergyInfo._2 + " RF"))
            } else list.asInstanceOf[java.util.List[String]].add(GuiColor.RED + "0/" + getEnergyInfo._2 + " RF")
        } else list.asInstanceOf[java.util.List[String]].add(GuiColor.RED + "0/" + getEnergyInfo._2 + " RF")
    }

    private def setEnergy(container: ItemStack, energy: Int): Unit = {
        var tag = new NBTTagCompound
        if (container.getTagCompound != null)
            tag = container.getTagCompound
        tag.setInteger("Energy", energy)

        container.setTagCompound(tag)
        updateDamage(container)
    }

    private def updateDamage(stack: ItemStack): Unit = {
        val r = getEnergyStored(stack).toFloat / getMaxEnergyStored(stack)
        val res = 16 - (r * 16).toInt
        stack.setItemDamage(res)
    }

    override def extractEnergy(container: ItemStack, maxExtract: Int, simulate: Boolean): Int = {
        var energy = container.getTagCompound.getInteger("Energy")
        val energyExtracted = Math.min(energy, Math.min(getEnergyInfo._3, maxExtract))

        if (!simulate) {
            energy -= energyExtracted
            setEnergy(container, energy)
        }
        energyExtracted
    }

    override def getEnergyStored(container: ItemStack): Int = container.getTagCompound.getInteger("Energy")

    override def getMaxEnergyStored(container: ItemStack): Int = getEnergyInfo._2

    override def receiveEnergy(container: ItemStack, maxReceive: Int, simulate: Boolean): Int = {
        var energy = container.getTagCompound.getInteger("Energy")
        val energyReceived = Math.min(getEnergyInfo._2 - energy, Math.min(getEnergyInfo._3, maxReceive))

        if (!simulate) {
            energy += energyReceived
            setEnergy(container, energy)
        }
        energyReceived
    }

    private def getEnergyInfo: (Int, Int, Int) = {
        block match {
            case BlockManager.basicRFStorage => (1, 25000, 200)
            case BlockManager.advancedRFStorage => (2, 1000000, 1000)
            case BlockManager.eliteRFStorage => (3, 10000000, 10000)
            case BlockManager.creativeRFStorage => (4, 100000000, 100000)
            case _ => (1, 25000, 200)
        }
    }
}
