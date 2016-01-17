package com.dyonovan.neotech.common.tiles.machines

import com.dyonovan.neotech.collections.UpgradeBoard
import com.dyonovan.neotech.common.tiles.AbstractMachine
import com.dyonovan.neotech.managers.ItemManager
import net.minecraft.inventory.Container
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 21, 2015
 */
class TileThermalBinder extends AbstractMachine {

    override def initialSize: Int = 6

    final val INPUT1 = 0
    final val INPUT2 = 1
    final val INPUT3 = 2
    final val INPUT4 = 3
    final val MB_INPUT = 4
    final val MB_OUTPUT = 5

    var count = 0

    def build(): Unit = {
        getStackInSlot(MB_INPUT).getItem match {
            case ItemManager.upgradeMBEmpty if hasSlotUpgrades =>
                val tag = writeToMB()
                val newMB = new ItemStack(ItemManager.upgradeMBFull)
                newMB.setTagCompound(tag)
                setInventorySlotContents(MB_OUTPUT, newMB)
                setInventorySlotContents(MB_INPUT, null)
            case ItemManager.upgradeMBFull if !hasSlotUpgrades =>
                val mb = UpgradeBoard.getBoardFromStack(getStackInSlot(MB_INPUT))
                var slot = 0
                if (mb.hasControl) {
                    setInventorySlotContents(slot, new ItemStack(ItemManager.upgradeControl, 1))
                    slot += 1
                }
                if (mb.hasExpansion) {
                    setInventorySlotContents(slot, new ItemStack(ItemManager.upgradeExpansion, 1))
                    slot += 1
                }
                if (mb.getHardDriveCount > 0) {
                    setInventorySlotContents(slot, new ItemStack(ItemManager.upgradeHardDrive, mb.getHardDriveCount))
                    slot += 1
                }
                if (mb.getProcessorCount > 0) {
                    setInventorySlotContents(slot, new ItemStack(ItemManager.upgradeProcessor, mb.getProcessorCount))
                    slot += 1
                }
                setInventorySlotContents(MB_INPUT, null)
                setInventorySlotContents(MB_OUTPUT, new ItemStack(ItemManager.upgradeMBEmpty, 1))
            case _ =>
        }
    }

    override def doWork(): Unit = {
        if(getStackInSlot(0) == null) {
            reset()
        }
        if (this.values.burnTime > 0) {
            this.values.burnTime = values.burnTime - 1
            val actual = energy.extractEnergy(count * 100, true)
            if (actual < count * 100)
                reset()
            else
                energy.extractEnergy(actual, false)
            worldObj.markBlockForUpdate(pos)
        } else if (this.values.currentItemBurnTime > 0 && this.values.burnTime <= 0) {
            build()
            reset()
        }
    }

    private def reset(): Unit = {
        values.burnTime = 0
        values.currentItemBurnTime = 0
        count = 0
        worldObj.markBlockForUpdate(pos)
    }

    def getCount: Int = {
        if (getStackInSlot(MB_INPUT) != null) {
            getStackInSlot(MB_INPUT).getItem match {
                case ItemManager.upgradeMBEmpty if hasSlotUpgrades =>
                    var count = 0
                    for (i <- 0 to 3) {
                        if (getStackInSlot(i) != null)
                            count += getStackInSlot(i).stackSize
                    }
                    return count
                case ItemManager.upgradeMBFull if !hasSlotUpgrades =>
                    val mb = UpgradeBoard.getBoardFromStack(getStackInSlot(MB_INPUT))
                    if (mb != null) {
                        var count = mb.getHardDriveCount + mb.getProcessorCount
                        if (mb.hasControl) count += 1
                        if (mb.hasExpansion) count += 1
                        return count
                    }
                case _ =>
            }
        }
        0
    }

    private def writeToMB(): NBTTagCompound = {
        val tag = new NBTTagCompound
        for (i <- 0 to 3) {
            if (getStackInSlot(i) != null) {
                getStackInSlot(i).getItem match {
                    case item: ItemManager.upgradeControl.type =>
                        if (!tag.getBoolean("Control")) {
                            tag.setBoolean("Control", true)
                            setInventorySlotContents(i, null)
                        }
                    case item: ItemManager.upgradeExpansion.type =>
                        if (!tag.getBoolean("Expansion")) {
                            tag.setBoolean("Expansion", true)
                            setInventorySlotContents(i, null)
                        }
                    case item: ItemManager.upgradeHardDrive.type =>
                        val countTag = tag.getInteger("HardDrive")
                        if (countTag + getStackInSlot(i).stackSize <= 8) {
                            tag.setInteger("HardDrive", countTag + getStackInSlot(i).stackSize)
                            setInventorySlotContents(i, null)
                        } else if (countTag + getStackInSlot(i).stackSize > 8) {
                            tag.setInteger("HardDrive", 8)
                            getStackInSlot(i).stackSize -= 8 - countTag
                        }
                    case item: ItemManager.upgradeProcessor.type =>
                        val countTag = tag.getInteger("Processor")
                        if (countTag + getStackInSlot(i).stackSize <= 8) {
                            tag.setInteger("Processor", countTag + getStackInSlot(i).stackSize)
                            setInventorySlotContents(i, null)
                        } else if (countTag + getStackInSlot(i).stackSize > 8) {
                            tag.setInteger("Processor", 8)
                            getStackInSlot(i).stackSize -= 8 - countTag
                        }
                }
            }
        }
        tag
    }

    def hasSlotUpgrades: Boolean = {
        for (i <- 0 to 3) {
            if (getStackInSlot(i) != null) return true
        }
        false
    }

    @SideOnly(Side.CLIENT) override def getCookProgressScaled(scaleVal: Int): Int =
        (((this.values.currentItemBurnTime - this.values.burnTime) * scaleVal) / Math.max(20 * 20 * count, 0.001)).toInt

    override def spawnActiveParticles(x: Double, y: Double, z: Double): Unit = {}

    /**
     * Get the output of the recipe
     * @param stack The input
     * @return The output
     */
    override def recipe(stack: ItemStack): ItemStack = null

    /**
     * Used to output the redstone single from this structure
     *
     * Use a range from 0 - 16.
     *
     * 0 Usually means that there is nothing in the tile, so take that for lowest level. Like the generator has no energy while
     * 16 is usually the flip side of that. Output 16 when it is totally full and not less
     *
     * @return int range 0 - 16
     */
    override def getRedstoneOutput: Int = Container.calcRedstoneFromInventory(this)

    override def isItemValidForSlot(slot: Int, stack: ItemStack): Boolean = {
        val item = stack.getItem
        if (slot >= 0 && slot <= 3)
            item.isInstanceOf[ItemManager.upgradeControl.type] || item.isInstanceOf[ItemManager.upgradeExpansion.type] ||
                    item.isInstanceOf[ItemManager.upgradeHardDrive.type] || item.isInstanceOf[ItemManager.upgradeProcessor.type]
        else if (slot == 4) item.isInstanceOf[ItemManager.upgradeMBEmpty.type] || item.isInstanceOf[ItemManager.upgradeMBFull.type]
        else false
    }

    override def canExtractItem(index: Int, stack: ItemStack, direction: EnumFacing): Boolean = index == 5

    override def canInsertItem(slot: Int, itemStackIn: ItemStack, direction: EnumFacing): Boolean = {
        isItemValidForSlot(slot, itemStackIn)
    }

    override def writeToNBT(tag: NBTTagCompound): Unit = {
        super.writeToNBT(tag)
        tag.setInteger("Count", count)
    }

    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super.readFromNBT(tag)
        count = tag.getInteger("Count")
    }
}
