package com.teambrmodding.neotech.common.tiles.storage

import com.teambrmodding.neotech.common.container.storage.ContainerFlushableChest
import com.teambrmodding.neotech.managers.BlockManager
import com.teambr.bookshelf.common.tiles.traits.{Syncable, Inventory}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.{SoundCategory, EnumParticleTypes}

/**
  * Created by Dyonovan on 1/22/2016.
  */
class TileFlushableChest extends Syncable with Inventory {

    override def initialSize: Int = 27

    var prevLidAngle : Float = 0
    var lidAngle : Float = 0
    var numUsingPlayers : Int = 0
    var ticksSinceSync : Int = -1

    override def update(): Unit = {
        if(worldObj != null && !worldObj.isRemote && numUsingPlayers != 0 && (ticksSinceSync + pos.getX + pos.getY + pos.getZ) % 200 == 0) {
            numUsingPlayers = 0
            val f = 5.0F
            val playerList : java.util.List[EntityPlayer] = worldObj.getEntitiesWithinAABB[EntityPlayer](classOf[EntityPlayer], new AxisAlignedBB(pos.getX - f, pos.getY - f, pos.getZ - f, pos.getX + 1 + f, pos.getY + 1 + f, pos.getZ + 1 + f))

            for(x <- 0 until playerList.size()) {
                if (playerList.get(x).openContainer.isInstanceOf[ContainerFlushableChest])
                    numUsingPlayers += 1
            }
        }

        if(worldObj != null && ticksSinceSync < 0 && !worldObj.isRemote)
            worldObj.addBlockEvent(pos, BlockManager.flushableChest, 0, numUsingPlayers)

        ticksSinceSync += 1
        prevLidAngle = lidAngle
        val f = 0.1F
        if(numUsingPlayers > 0 && lidAngle == 0.0F)
            worldObj.playSound(null.asInstanceOf[EntityPlayer], pos, SoundEvents.BLOCK_CHEST_OPEN,
                SoundCategory.BLOCKS, 0.3F, 0.5F)
        if((numUsingPlayers == 0 && lidAngle > 0.0F) || (numUsingPlayers > 0 && lidAngle < 1.0F)) {
            val f1 = lidAngle
            if(numUsingPlayers > 0)
                lidAngle += f
            else
                lidAngle -= f

            if(lidAngle > 1.0F)
                lidAngle = 1.0F

            val f2 = 0.5F
            if(lidAngle < f2 && f1 > f2)
                worldObj.playSound(null.asInstanceOf[EntityPlayer], pos, SoundEvents.BLOCK_CHEST_CLOSE,
                    SoundCategory.BLOCKS, 0.3F, 0.5F)
            if(lidAngle < 0.0F)
                lidAngle = 0.0F
        }
    }

    override def receiveClientEvent(i : Int, j : Int) : Boolean = {
        numUsingPlayers = j
        true
    }

    override def openInventory(player : EntityPlayer) : Unit = {
        if(worldObj == null)
            return
        numUsingPlayers += 1
        worldObj.addBlockEvent(pos, BlockManager.flushableChest, 1, numUsingPlayers)
    }

    override def closeInventory(player : EntityPlayer) : Unit = {
        if(worldObj == null)
            return
        numUsingPlayers -= 1
        worldObj.addBlockEvent(pos, BlockManager.flushableChest, 1, numUsingPlayers)
    }

    override def markDirty(): Unit = {
        super[TileEntity].markDirty()
    }

    override def writeToNBT(tag: NBTTagCompound): NBTTagCompound = {
        super[Inventory].writeToNBT(tag)
        tag
    }

    override def readFromNBT(tag: NBTTagCompound): Unit = super[Inventory].readFromNBT(tag)

    override def clear() = {
        super.clear()
        if(worldObj != null && !worldObj.isRemote) {
            worldObj.playSound(null.asInstanceOf[EntityPlayer], pos, SoundEvents.BLOCK_LAVA_EXTINGUISH,
                SoundCategory.BLOCKS, 0.3F, 0.5F)
            sendValueToClient(0, 0)
        }
    }

    override def setVariable(id: Int, value: Double): Unit = {
        id match {
            case 0 =>
                if(worldObj != null && worldObj.isRemote) {
                    worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX + 0.5, pos.getY + 1, pos.getZ + 0.5, 0, 0, 0)
                    worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX + 0.3, pos.getY + 1, pos.getZ + 0.3, 0, 0, 0)
                    worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX + 0.7, pos.getY + 1, pos.getZ + 0.7, 0, 0, 0)
                    worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX + 0.3, pos.getY + 1, pos.getZ + 0.7, 0, 0, 0)
                    worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX + 0.7, pos.getY + 1, pos.getZ + 0.3, 0, 0, 0)
                }
            case _ =>
        }
    }

    override def getVariable(id: Int): Double = {0.0}
}
