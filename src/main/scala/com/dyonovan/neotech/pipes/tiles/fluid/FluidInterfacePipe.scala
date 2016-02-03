package com.dyonovan.neotech.pipes.tiles.fluid

import java.util

import com.dyonovan.neotech.pipes.entities.{ResourceEntity, FluidResourceEntity}
import com.dyonovan.neotech.pipes.types.{InterfacePipe, SimplePipe}
import com.teambr.bookshelf.client.gui.{GuiTextFormat, GuiColor}
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraft.util.{StatCollector, BlockPos, EnumFacing}
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
                            pos, pos.offset(dir), pos.north(), worldObj), simulate = true)) {
                            tank.drain(dir.getOpposite, tempTank.getFluidAmount, true)
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
        if(resource.resource.getFluidAmount <= 0) {
            resource.isDead = true
        } else {
            resource.destination = new BlockPos(resource.from)
            resource.findPathToDestination()
        }
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
    override def willAcceptResource(resourceEntity: ResourceEntity[_], isSending : Boolean): Boolean = {
        if(resourceEntity == null || !resourceEntity.isInstanceOf[FluidResourceEntity] || resourceEntity.resource == null || !super.willAcceptResource(resourceEntity, isSending))
            return false

        val resource = resourceEntity.asInstanceOf[FluidResourceEntity]

        if(resource.resource.getFluid == null)
            return false

        //Try and insert the fluid
        for(dir <- EnumFacing.values()) {
            if (canConnectSink(dir) && pos.offset(dir) != resource.fromTileLocation) {
                worldObj.getTileEntity(pos.offset(dir)) match {
                    case tank: IFluidHandler =>
                        val filledAmount = test(tank, dir).fill(dir.getOpposite, resource.resource.getFluid, false)
                        if (filledAmount > 0) {
                            resource.resource.getFluid.amount = filledAmount
                            if(isSending)
                                waitingQueue.add(resource)
                            return true
                        }
                    case _ =>
                }
            }
        }
        false
    }

    def test(otherTank : IFluidHandler, dir : EnumFacing): IFluidHandler = {
        if (waitingQueue.isEmpty)
            otherTank
        else {
            val iterator = waitingQueue.iterator() //Remove deads
            while (iterator.hasNext) {
                if (iterator.next().isDead)
                    iterator.remove()
            }

            tempTank = new IFluidHandler {
                val tank = new FluidTank(if(otherTank.getTankInfo(dir)(0).fluid != null)otherTank.getTankInfo(dir)(0).fluid.copy() else null, otherTank.getTankInfo(dir)(0).capacity)
                override def drain(from: EnumFacing, resource: FluidStack, doDrain: Boolean): FluidStack = drain(from, resource, doDrain)
                override def drain(from: EnumFacing, maxDrain: Int, doDrain: Boolean): FluidStack = {
                    val fluidAmount = tank.drain(maxDrain, false)
                    if (fluidAmount != null && doDrain)
                        tank.drain(maxDrain, true)
                    fluidAmount
                }
                override def canFill(from: EnumFacing, fluid: Fluid): Boolean = otherTank.canFill(from, fluid)
                override def canDrain(from: EnumFacing, fluid: Fluid): Boolean = otherTank.canDrain(from, fluid)
                override def fill(from: EnumFacing, resource: FluidStack, doFill: Boolean): Int = {
                    if (canFill(from, resource.getFluid)) {
                        if (tank.fill(resource, false) > 0) {
                            val actual = tank.fill(resource, doFill)
                            return actual
                        }
                    }
                    0
                }
                override def getTankInfo(from: EnumFacing): Array[FluidTankInfo] = getTankInfo(from)
            }

            for(x <- 0 until waitingQueue.size) {
                tempTank.fill(dir, waitingQueue.get(x).resource.getFluid, true)
            }
            tempTank
        }
    }

    /**
      * Try and insert the resource into an inventory.
      *
      * It is pretty good practice to send the resource back if you can't remove all of it
      *
      * @param resource
      */
    override def tryInsertResource(resource: FluidResourceEntity): Unit = {
        if(resource == null || resource.resource == null)
            return

        //Try and insert the fluid
        for(dir <- EnumFacing.values()) {
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
}
