package com.dyonovan.neotech.common.tiles.misc

import com.dyonovan.neotech.common.blocks.misc.BlockAttractor
import com.teambr.bookshelf.common.tiles.traits.InventorySided
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler

/**
  * Created by Dyonovan on 2/14/2016.
  */
class TileAttractor extends TileEntity with InventorySided {

    override def initialSize: Int = 4

    override def getSlotsForFace(side: EnumFacing): Array[Int] = {
        if (side == worldObj.getBlockState(pos).getValue(BlockAttractor.DIR).getOpposite)
            Array(0, 1, 2, 3)
        else Array()
    }

    override def hasCapability(capability: Capability[_], facing : EnumFacing) = {
        facing == worldObj.getBlockState(pos).getValue(BlockAttractor.DIR).getOpposite
    }

    override def getCapability[T](capability: Capability[T], facing: EnumFacing) : T = {
        if (facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == worldObj.getBlockState(pos).getValue(BlockAttractor.DIR).getOpposite) {
                facing match {
                    case EnumFacing.UP => return handlerTop.asInstanceOf[T]
                    case EnumFacing.DOWN => return handlerBottom.asInstanceOf[T]
                    case EnumFacing.WEST => return handlerWest.asInstanceOf[T]
                    case EnumFacing.EAST => return handlerEast.asInstanceOf[T]
                    case EnumFacing.NORTH => return handlerNorth.asInstanceOf[T]
                    case EnumFacing.SOUTH => return handlerSouth.asInstanceOf[T]
                    case _ => return handlerWest.asInstanceOf[T]
                }
            }
        }
        super.getCapability[T](capability, facing)
    }


    override def canExtractItem(index: Int, stack: ItemStack, direction: EnumFacing): Boolean = {
        direction == worldObj.getBlockState(pos).getValue(BlockAttractor.DIR).getOpposite
    }

    override def canInsertItem(slot: Int, itemStackIn: ItemStack, direction: EnumFacing): Boolean = false

    override def writeToNBT(tag: NBTTagCompound): Unit = {
        super[TileEntity].writeToNBT(tag)
        super[InventorySided].writeToNBT(tag)
    }

    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super[TileEntity].readFromNBT(tag)
        super[InventorySided].readFromNBT(tag)
    }
}
