package com.dyonovan.neotech.common.tiles.machines

import com.dyonovan.neotech.registries.CrusherRecipeRegistry
import com.teambr.bookshelf.common.tiles.traits.{Inventory, UpdatingTile}
import com.teambr.bookshelf.network.PacketManager
import mezz.jei.network.PacketHandler
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/11/2016
  */
class TileGrinder extends UpdatingTile with Inventory {
    override def initialSize: Int = 7

    var progress : Int = 0
    val MAX_PROGRESS : Int = 15

    def activateGrinder(progressValue : Int, multiplier : Double): Unit = {
        updateCurrentItem()
        worldObj.markBlockForUpdate(pos)
        if(getStackInSlot(3) != null && hasOutputAvailable) {
            progress += (progressValue * multiplier).toInt
            if(progress >= MAX_PROGRESS) {
                progress = progress - MAX_PROGRESS
                grindItem()
                worldObj.markBlockForUpdate(pos)
                if(progress >= MAX_PROGRESS)
                    activateGrinder(0, 0)
            }
        } else
            progress = 0
    }

    def updateCurrentItem(): Unit = {
        if(getStackInSlot(3) == null) {
            for(x <- 0 until 3) { //Loop inputs
                if(getStackInSlot(x) != null) { //If we have found something...
                    setInventorySlotContents(3, getStackInSlot(x).copy()) //Move stack into middle
                    setInventorySlotContents(x, null) //Remove stack
                    return //Leave
                }
            }
        } else if(getStackInSlot(3).stackSize < getStackInSlot(3).getMaxStackSize) { //If we can fill the grinding
            for(x <- 0 until 3) { //Loop Inputs
                if(getStackInSlot(x) != null && getStackInSlot(x).getItem == getStackInSlot(3).getItem
                        && getStackInSlot(x).getItemDamage == getStackInSlot(3).getItemDamage) { //Must be same item, I'm assuming its just item and meta, no NBT
                val space = getStackInSlot(3).getMaxStackSize - getStackInSlot(3).stackSize
                    if(getStackInSlot(x).stackSize <= space) { //We will move entire stack
                        getStackInSlot(3).stackSize += getStackInSlot(x).stackSize //Add smaller stack
                        setInventorySlotContents(x, null) //Remove used stack
                    } else { //Move what you can
                        getStackInSlot(x).stackSize -= space //Remove what we need
                        getStackInSlot(3).stackSize += space //Add what we need
                        return //Stack full, exit
                    }
                }
            }
        }
    }

    def hasOutputAvailable : Boolean = {
        val output = CrusherRecipeRegistry.getOutput(getStackInSlot(3)).get._1
        for(x <- 4 until 7) {
            if(getStackInSlot(x) != null && getStackInSlot(x).stackSize <= getStackInSlot(x).getMaxStackSize &&
                    getStackInSlot(x).getItem == output.getItem &&
                    getStackInSlot(x).getItemDamage == output.getItemDamage &&
                    getStackInSlot(x).stackSize + output.stackSize <= getStackInSlot(x).getMaxStackSize) {
                return true
            }

        }
        for(x <- 4 until 7) { //We only want to do this is we have to, merge first if you can
            if (getStackInSlot(x) == null)
                return true
        }
        false
    }

    def grindItem() : Unit = {
        val output = CrusherRecipeRegistry.getOutput(getStackInSlot(3)).get._1
        for(x <- 4 until 7) {
            if(getStackInSlot(x) != null && getStackInSlot(x).stackSize <= getStackInSlot(x).getMaxStackSize &&
                    getStackInSlot(x).getItem == output.getItem &&
                    getStackInSlot(x).getItemDamage == output.getItemDamage &&
                    getStackInSlot(x).stackSize + output.stackSize <= getStackInSlot(x).getMaxStackSize) {
                getStackInSlot(3).stackSize -= 1
                getStackInSlot(x).stackSize += output.stackSize
                if(getStackInSlot(3).stackSize <= 0)
                    setInventorySlotContents(3, null)
                return
            }
        }
        for(x <- 4 until 7) { //We only want to do this is we have to, merge first if you can
            if (getStackInSlot(x) == null) {
                getStackInSlot(3).stackSize -= 1
                setInventorySlotContents(x, output.copy())
                if (getStackInSlot(3).stackSize <= 0)
                    setInventorySlotContents(3, null)
                return
            }
        }
    }

    override def isItemValidForSlot(index : Int, stack : ItemStack) : Boolean = {
        if(index < 3)
            CrusherRecipeRegistry.isItemValid(stack)
        else
            false
    }

    override def writeToNBT(tag : NBTTagCompound) : Unit = {
        super[TileEntity].writeToNBT(tag)
        super[Inventory].writeToNBT(tag)
        tag.setInteger("progress", progress)
    }

    override def readFromNBT(tag : NBTTagCompound) : Unit = {
        super[TileEntity].readFromNBT(tag)
        super[Inventory].readFromNBT(tag)
        progress = tag.getInteger("progress")
    }

    override def markDirty(): Unit = {
        super[TileEntity].markDirty()
    }
}
