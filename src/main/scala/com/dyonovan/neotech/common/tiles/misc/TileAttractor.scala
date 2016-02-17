package com.dyonovan.neotech.common.tiles.misc

import com.dyonovan.neotech.common.blocks.misc.BlockAttractor
import com.teambr.bookshelf.common.tiles.traits.{InventorySided, UpdatingTile}
import net.minecraft.entity.item.EntityItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.{AxisAlignedBB, EnumFacing}
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler

/**
  * Created by Dyonovan on 2/14/2016.
  */
class TileAttractor extends UpdatingTile with InventorySided {

    final val COOL_DOWN_NUM = 40
    override def initialSize: Int = 4

    var doItems = true
    var doEntities = false
    var radius = 1
    var coolDown = COOL_DOWN_NUM
    var itemList: java.util.List[EntityItem] = _

    override def onServerTick(): Unit = {
        if (coolDown > 0) {
            coolDown -= 1
            return
        }

        if (itemList == null && doItems) {
            val startPos = getPos.add(-radius, -(radius + 1), -radius).offset(worldObj.getBlockState(pos).getValue(BlockAttractor.DIR), radius)
            val endPos = getPos.add(radius, radius, radius).offset(worldObj.getBlockState(pos).getValue(BlockAttractor.DIR), radius)
            itemList = worldObj.getEntitiesWithinAABB(classOf[EntityItem], new AxisAlignedBB(startPos, endPos))
        }
        if (itemList != null && !itemList.isEmpty) {
            val entityItem = itemList.get(0)
            if (entityItem.getEntityItem != null) {
                val actual = tryInsert(entityItem.getEntityItem)
                if (actual.isEmpty) {
                    entityItem.setDead()
                    itemList.remove(0)
                } else entityItem.getEntityItem.stackSize = actual.get
                worldObj.markBlockForUpdate(pos)
            }
        }
        if (itemList != null && itemList.isEmpty) itemList = null
        coolDown = COOL_DOWN_NUM
    }

    private def tryInsert(stack: ItemStack): Option[Int] = {
        if (stack.stackSize > 0) {
            for (i <- 0 until getSizeInventory) {
                val done = insertItem(i, stack, simulate = false)
                if (done == null) return None
                stack.stackSize -= done.stackSize
            }
        }
        Some(stack.stackSize)
    }

    override def getSlotsForFace(side: EnumFacing): Array[Int] = {
        if (side == worldObj.getBlockState(pos).getValue(BlockAttractor.DIR).getOpposite)
            Array(0, 1, 2, 3)
        else Array()
    }

    override def hasCapability(capability: Capability[_], facing: EnumFacing) = {
        facing == worldObj.getBlockState(pos).getValue(BlockAttractor.DIR).getOpposite
    }

    override def getCapability[T](capability: Capability[T], facing: EnumFacing): T = {
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
