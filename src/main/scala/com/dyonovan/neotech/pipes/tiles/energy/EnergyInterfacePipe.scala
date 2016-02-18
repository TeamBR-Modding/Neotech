package com.dyonovan.neotech.pipes.tiles.energy

import java.util

import cofh.api.energy.{EnergyStorage, IEnergyProvider, IEnergyReceiver}
import com.dyonovan.neotech.pipes.entities.{EnergyResourceEntity, ResourceEntity}
import com.dyonovan.neotech.pipes.types.{InterfacePipe, SimplePipe}
import com.teambr.bookshelf.client.gui.{GuiColor, GuiTextFormat}
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraft.util.{BlockPos, EnumFacing, StatCollector}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis pauljoda
  * @since August 17, 2015
  */
class EnergyInterfacePipe extends InterfacePipe[EnergyStorage, EnergyResourceEntity] {

    override def getDescription : String = {
        GuiColor.YELLOW +  "" + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.energyInterfacePipe.name") + ":\n" +
                GuiColor.WHITE + StatCollector.translateToLocal("neotech.energyInterfacePipe.desc") + "\n\n" +
                GuiColor.GREEN + GuiTextFormat.BOLD + GuiTextFormat.UNDERLINE + StatCollector.translateToLocal("neotech.text.upgrades") + ":\n" + GuiTextFormat.RESET +
                GuiColor.YELLOW + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.text.processors") + ":\n" +
                GuiColor.WHITE + StatCollector.translateToLocal("neotech.energyInterfacePipe.processorUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.text.hardDrives") + ":\n" +
                GuiColor.WHITE + StatCollector.translateToLocal("neotech.energyInterfacePipe.hardDriveUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.text.control") + ":\n" +
                GuiColor.WHITE + StatCollector.translateToLocal("neotech.energyInterfacePipe.controlUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.text.expansion") + ":\n" +
                GuiColor.WHITE +  StatCollector.translateToLocal("neotech.energyInterfacePipe.expansionUpgrade.desc")
    }

/*******************************************************************************************************************
  ************************************** Extraction Methods ********************************************************
  ******************************************************************************************************************/

    mode = 2

    override def canConnect(facing: EnumFacing): Boolean =
        if(super.canConnect(facing))
            getWorld.getTileEntity(pos.offset(facing)) match {
                case energy : IEnergyReceiver => energy.canConnectEnergy(facing.getOpposite)
                case energy : IEnergyProvider => energy.canConnectEnergy(facing.getOpposite)
                case pipe : SimplePipe => true
                case _ => false
            }
        else
            super.canConnect(facing)

    /**
      * This is the speed to extract from. You should be calling this when building your resources to send.
      *
      * This is included as a reminder to the child to have variable speeds
      *
      * @return
      */
    override def getSpeed: Double = {
        if(getUpgradeBoard != null && getUpgradeBoard.getProcessorCount > 0)
            getUpgradeBoard.getProcessorCount * 0.5
        else
            0.5
    }
    /**
      * Used to specify how much RF, check for upgrades here
      *
      * @return
      */
    def getMaxRFDrain : Int = {
        var rate = 2000
        if(getUpgradeBoard != null && getUpgradeBoard.getHardDriveCount > 0)
            rate *= (getUpgradeBoard.getHardDriveCount * 4)
        rate
    }

    /**
      * Get how many ticks to 'cooldown' between operations.
      *
      * @return 20 = 1 second
      */
    override def getDelay: Int = {
        if(getUpgradeBoard != null && getUpgradeBoard.getProcessorCount > 0)
            20 - getUpgradeBoard.getProcessorCount * 2
        else
            20
    }
    /**
      * This is what is actually called to the child class. Here you should call your extractResources or whatever you want
      * this pipe to do on its action phase. The parent will not automatically call extract
      *
      * This is useful if you wish to set different modes and call different path finding
      */
    override def doExtraction(): Unit = {
        tryExtractResources()
    }

    /**
      * The first step in moving things. You should call this from doExtraction. This is an outside method so you can
      * have additional functions to the pipe besides just extracting. For example, a pipe that pulls items in the world
      */
    override def tryExtractResources(): Unit = {
        for(dir <- EnumFacing.values()) {
            if (canConnectExtract(dir)) {
                getWorld.getTileEntity(pos.offset(dir)) match {
                    case provider: IEnergyProvider =>
                        if (provider.getEnergyStored(dir.getOpposite) > 0) {
                            val tempStorage = new EnergyStorage(getMaxRFDrain)
                            tempStorage.setEnergyStored(provider.extractEnergy(dir.getOpposite, getMaxRFDrain, true))
                            val energyResourceEntity = new EnergyResourceEntity(tempStorage,
                                pos.getX + 0.5, pos.getY + 0.5, pos.getZ + 0.5, getSpeed,
                                pos.offset(dir), pos.north(), pos.north(), getWorld)
                            if (extractOnMode(energyResourceEntity, simulate = true)) {
                                provider.extractEnergy(dir.getOpposite, tempStorage.getEnergyStored, false)
                                extractOnMode(nextResource, simulate = false)
                                return
                            }
                        }
                    case _ =>
                }
            }
        }
    }

    /**
      * This is called when we fail to send a resource. You should put the resource back where you found it or
      * add it to the world
      *
      * @param resource
      */
    override def returnResource(resource: EnergyResourceEntity): Unit = {
        resource.isDead = true
    }

    override def writeToNBT(tag : NBTTagCompound) : Unit = {
        super.writeToNBT(tag)
        super[TileEntity].writeToNBT(tag)
        tag.setInteger("SizeResources", resources.size())
        val resourceList = new NBTTagList
        for(i <- 0 until resources.size()) {
            val resourceTag = new NBTTagCompound
            resources.get(i).writeToNBT(resourceTag)
            resourceList.appendTag(resourceTag)
        }
        tag.setTag("Resources", resourceList)
    }

    override def readFromNBT(tag : NBTTagCompound) : Unit = {
        super.readFromNBT(tag)
        super[TileEntity].readFromNBT(tag)
        val resourceList = tag.getTagList("Resources", 10)
        resources = new util.ArrayList[EnergyResourceEntity]()
        for(i <- 0 until resourceList.tagCount()) {
            val resourceTag = resourceList.getCompoundTagAt(i)
            val item = new EnergyResourceEntity()
            item.readFromNBT(resourceTag)
            if(worldObj != null && !worldObj.isRemote) {
                item.setWorld(worldObj)
            }
            resources.add(item)
        }
    }

    /**
      * Called when the board is removed, reset to default values
      */
    override def resetValues(): Unit = {
        super.resetValues()
        mode = 2
        worldObj.markBlockForUpdate(pos)
    }

    /*******************************************************************************************************************
      *************************************** Insertion Methods ********************************************************
      ******************************************************************************************************************/

    /**
      * Used to check if this pipe can accept a resource
      *
      * You should not actually change anything, all simulation
      *
      * @param resourceEntity
      * @return
      */
    override def willAcceptResource(resourceEntity: ResourceEntity[_], tilePos : BlockPos): Boolean = {
        if(resourceEntity == null || !resourceEntity.isInstanceOf[EnergyResourceEntity] || resourceEntity.resource == null || !super.willAcceptResource(resourceEntity, tilePos))
            return false

        val resource = resourceEntity.asInstanceOf[EnergyResourceEntity]

        //Try and insert the energy
        for(dir <- EnumFacing.values()) {
            if (pos.offset(dir).toLong == tilePos.toLong && canConnectSink(dir) && tilePos.toLong != resource.fromTileLocation.toLong) {
                worldObj.getTileEntity(tilePos) match {
                    case receiver: IEnergyReceiver =>
                        val usedEnergy = receiver.receiveEnergy(dir.getOpposite, resource.resource.getEnergyStored, true)
                        if (usedEnergy > 0) {
                            return true
                        }
                    case _ =>
                }
            }
        }
        false
    }

    /**
      * Called when the resource has found its target and is actually sending, change resource size here
      *
      * @param resource
      */
    override def resourceBeingExtracted(resource: EnergyResourceEntity): Unit = {
        val tilePos = resource.destinationTile
        for(dir <- EnumFacing.values()) {
            if (pos.offset(dir).toLong == tilePos.toLong && canConnectSink(dir) && tilePos.toLong != resource.fromTileLocation.toLong) {
                worldObj.getTileEntity(tilePos) match {
                    case receiver: IEnergyReceiver =>
                        val usedEnergy = receiver.receiveEnergy(dir.getOpposite, resource.resource.getEnergyStored, true)
                        if (usedEnergy > 0) {
                            resource.resource.setEnergyStored(usedEnergy)
                            return
                        }
                    case _ =>
                }
            }
        }
    }

    /**
      * Used to get a list of what tiles are attached that can accept resources. Don't worry about if full or not,
      * just if this pipe interfaces with the tile add it here
      *
      * @return A list of the tiles that are valid sinks
      */
    override def getAttachedSinks: util.List[Long] = {
        val returnList = new util.ArrayList[Long]()
        for(dir <- EnumFacing.values()) {
            if (canConnectSink(dir)) {
                worldObj.getTileEntity(pos.offset(dir)) match {
                    case receiver: IEnergyReceiver => returnList.add(pos.offset(dir).toLong)
                    case _ =>
                }
            }
        }
        returnList
    }

    /**
      * Try and insert the resource into an inventory.
      *
      * It is pretty good practice to send the resource back if you can't remove all of it
      *
      * @param resource
      */
    override def tryInsertResource(resource: EnergyResourceEntity, dir : EnumFacing): Unit = {
        if (resource == null || resource.resource == null)
            return

        if (canConnectSink(dir)) {
            worldObj.getTileEntity(pos.offset(dir)) match {
                case receiver: IEnergyReceiver if !resource.isDead =>
                    receiver.receiveEnergy(dir.getOpposite, resource.resource.extractEnergy(resource.resource.getEnergyStored, false), false)
                    if (resource.resource.getEnergyStored <= 0)
                        resource.isDead = true
                case _ =>
            }

            //If we couldn't fill, move back to source
            if (!resource.isDead) {
                resource.isDead = true
            }
        }
    }

    override def getPipeTypeID: Int = 0
}
