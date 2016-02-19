package com.dyonovan.neotech.pipes.tiles.fluid

import java.util

import com.dyonovan.neotech.pipes.entities.{FluidResourceEntity, ResourceEntity}
import com.dyonovan.neotech.pipes.types.{InterfacePipe, SimplePipe}
import com.teambr.bookshelf.client.gui.{GuiColor, GuiTextFormat}
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{BlockPos, EnumFacing, StatCollector}
import net.minecraftforge.fluids._

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
class FluidInterfacePipe extends InterfacePipe[FluidTank, FluidResourceEntity] {

    override def getDescription : String = {
        GuiColor.YELLOW +  "" + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.fluidInterfacePipe.name") + ":\n" +
                GuiColor.WHITE + StatCollector.translateToLocal("neotech.fluidInterfacePipe.desc") + "\n\n" +
                GuiColor.GREEN + GuiTextFormat.BOLD + GuiTextFormat.UNDERLINE + StatCollector.translateToLocal("neotech.text.upgrades") + ":\n" + GuiTextFormat.RESET +
                GuiColor.YELLOW + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.text.processors") + ":\n" +
                GuiColor.WHITE + StatCollector.translateToLocal("neotech.fluidInterfacePipe.processorUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.text.hardDrives") + ":\n" +
                GuiColor.WHITE + StatCollector.translateToLocal("neotech.fluidInterfacePipe.hardDriveUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.text.control") + ":\n" +
                GuiColor.WHITE + StatCollector.translateToLocal("neotech.energyInterfacePipe.controlUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + StatCollector.translateToLocal("neotech.text.expansion") + ":\n" +
                GuiColor.WHITE +  StatCollector.translateToLocal("neotech.energyInterfacePipe.expansionUpgrade.desc")
    }

    /*******************************************************************************************************************
      ************************************** Extraction Methods ********************************************************
      ******************************************************************************************************************/

    override def canConnect(facing: EnumFacing): Boolean =
        if(super.canConnect(facing))
            getWorld.getTileEntity(pos.offset(facing)) match {
                case inventory : IFluidHandler => true
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
            getUpgradeBoard.getProcessorCount * 0.05
        else
            0.05
    }
    /**
      * Used to specify how many mb to drain, check for upgrades here
      *
      * @return
      */
    def getMaxFluidDrain : Int = {
        if(getUpgradeBoard != null && getUpgradeBoard.getHardDriveCount > 0)
            getUpgradeBoard.getHardDriveCount * 2000
        else
            1000
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
                worldObj.getTileEntity(pos.offset(dir)) match {
                    case tank: IFluidHandler =>
                        val tempTank = new FluidTank(getMaxFluidDrain)
                        tempTank.fill(tank.drain(dir.getOpposite, getMaxFluidDrain, false), true)
                        if (extractOnMode(new FluidResourceEntity(tempTank,
                            pos.getX + 0.5, pos.getY + 0.5, pos.getZ + 0.5, getSpeed,
                            pos.offset(dir), pos.north(), pos.north(), worldObj), simulate = true)) {
                            tank.drain(dir.getOpposite, nextResource.resource.getFluid.amount, true)
                            nextResource.resource = tempTank
                            extractOnMode(nextResource, simulate = false)
                            return
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
    override def returnResource(resource: FluidResourceEntity): Unit = {
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
        resources = new util.ArrayList[FluidResourceEntity]()
        for(i <- 0 until resourceList.tagCount()) {
            val resourceTag = resourceList.getCompoundTagAt(i)
            val item = new FluidResourceEntity()
            item.readFromNBT(resourceTag)
            if(worldObj != null && !worldObj.isRemote) {
                item.setWorld(worldObj)
            }
            resources.add(item)
        }
    }

    /*******************************************************************************************************************
      *************************************** Insertion Methods ********************************************************
      ******************************************************************************************************************/

    val waitingQueue  = new util.ArrayList[FluidResourceEntity]()
    var tempTank : IFluidHandler = null


    /**
      * Used to check if this pipe can accept a resource
      *
      * You should not actually change anything, all simulation
      *
      * @param resourceEntity
      * @return
      */
    override def willAcceptResource(resourceEntity: ResourceEntity[_], tilePos : BlockPos): Boolean = {
        if(resourceEntity == null || !resourceEntity.isInstanceOf[FluidResourceEntity] || resourceEntity.resource == null || !super.willAcceptResource(resourceEntity, tilePos))
            return false

        val resource = resourceEntity.asInstanceOf[FluidResourceEntity]

        if(resource.resource.getFluid == null)
            return false

        //Try and insert the fluid
        for(dir <- EnumFacing.values()) {
            if (pos.offset(dir).toLong == tilePos.toLong && canConnectSink(dir) && tilePos.toLong != resource.fromTileLocation.toLong) {
                worldObj.getTileEntity(tilePos) match {
                    case tank: IFluidHandler =>
                        val otherTile = createTileAndSimulate(tank, dir, tilePos)
                        val filledAmount = otherTile.asInstanceOf[IFluidHandler].fill(dir.getOpposite, resource.resource.getFluid, false)
                        otherTile.invalidate()
                        if (filledAmount >= 100) {
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
    override def resourceBeingExtracted(resource: FluidResourceEntity): Unit = {
        val tilePos = resource.destinationTile
        for(dir <- EnumFacing.values()) {
            if (pos.offset(dir).toLong == tilePos.toLong && canConnectSink(dir) && tilePos.toLong != resource.fromTileLocation.toLong) {
                worldObj.getTileEntity(tilePos) match {
                    case tank: IFluidHandler =>
                        val otherTile = createTileAndSimulate(tank, dir, tilePos)
                        val filledAmount = otherTile.asInstanceOf[IFluidHandler].fill(dir.getOpposite, resource.resource.getFluid, false)
                        otherTile.invalidate()
                        if (filledAmount > 0) {
                            resource.resource.getFluid.amount = filledAmount
                            waitingQueue.add(resource)
                            return
                        }
                    case _ =>
                }
            }
        }
    }

    def createTileAndSimulate(otherTank : IFluidHandler, dir : EnumFacing, position : BlockPos): TileEntity = {
        var otherTile : TileEntity = null

        if(worldObj.getTileEntity(position) != null) {
            otherTile = worldObj.getBlockState(position).getBlock.createTileEntity(worldObj, worldObj.getBlockState(position))
            val otherTag = new NBTTagCompound
            worldObj.getTileEntity(position).writeToNBT(otherTag)
            otherTile.readFromNBT(otherTag)
            otherTile.setWorldObj(worldObj)
        }

        if (waitingQueue.isEmpty)
            otherTile
        else {
            val iterator = waitingQueue.iterator() //Remove deads
            while (iterator.hasNext) {
                if (iterator.next().isDead)
                    iterator.remove()
            }

            for(x <- 0 until waitingQueue.size) {
                otherTile.asInstanceOf[IFluidHandler].fill(dir, waitingQueue.get(x).resource.getFluid, true)
            }
            otherTile
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
                    case receiver: IFluidHandler => returnList.add(pos.offset(dir).toLong)
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
    override def tryInsertResource(resource: FluidResourceEntity, dir : EnumFacing): Unit = {
        if(resource == null || resource.resource == null)
            return

        //Try and insert the fluid
        if (canConnectSink(dir)) {
            worldObj.getTileEntity(pos.offset(dir)) match {
                case tank: IFluidHandler if !resource.isDead =>
                    resource.resource.drain(tank.fill(dir.getOpposite, resource.resource.getFluid, true), true)
                    if (resource.resource.getFluidAmount <= 0) {
                        resource.isDead = true
                        resource.resource.setFluid(null)
                        waitingQueue.remove(resource)
                    }
                case _ =>
            }
        }

        //If we couldn't fill, move back to source
        if(!resource.isDead) {
            resource.isDead = true
        }
    }

    var coolDownSink = 0
    override def onServerTick(): Unit = {
        super.onServerTick()
        coolDownSink -= 1
        if(coolDownSink < 0) {
            coolDownSink = 200
            if (!waitingQueue.isEmpty) {
                val iterator = waitingQueue.iterator()
                while (iterator.hasNext) {
                    if (iterator.next().isDead)
                        iterator.remove()
                }
            }
        }
    }

    override def getPipeTypeID: Int = 1
}
