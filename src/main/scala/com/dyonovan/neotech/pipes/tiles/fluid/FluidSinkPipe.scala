package com.dyonovan.neotech.pipes.tiles.fluid

import java.util

import com.dyonovan.neotech.pipes.entities.{FluidResourceEntity, ResourceEntity}
import com.dyonovan.neotech.pipes.types.{SimplePipe, SinkPipe}
import com.teambr.bookshelf.common.tiles.traits.UpdatingTile
import net.minecraft.util.{BlockPos, EnumFacing}
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
class FluidSinkPipe extends SinkPipe[FluidTank, FluidResourceEntity] with UpdatingTile {
    val waitingQueue  = new util.ArrayList[FluidResourceEntity]()

    override def canConnect(facing: EnumFacing): Boolean =
        getWorld.getTileEntity(getPos.offset(facing)).isInstanceOf[SimplePipe] || getWorld.getTileEntity(pos.offset(facing)).isInstanceOf[IFluidHandler]

    /**
      * Used to check if this pipe can accept a resource
      *
      * You should not actually change anything, all simulation
      * @param resourceEntity
      * @return
      */
    override def willAcceptResource(resourceEntity: ResourceEntity[_]): Boolean = {
        if(resourceEntity == null || !resourceEntity.isInstanceOf[FluidResourceEntity] || resourceEntity.resource == null)
            return false

        val resource = resourceEntity.asInstanceOf[FluidResourceEntity]

        if(resource.resource.getFluid == null)
            return false

        //Try and insert the fluid
        for(dir <- EnumFacing.values()) {
            worldObj.getTileEntity(pos.offset(dir)) match {
                case tank : IFluidHandler =>
                    if(test(tank, dir).fill(dir.getOpposite, resource.resource.getFluid, false) > 0) {
                        waitingQueue.add(resource)
                        return true
                    }
                case _ =>
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

            val tempTank = new IFluidHandler {
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
      * @param resource
      */
    override def tryInsertResource(resource: FluidResourceEntity): Unit = {
        if(resource == null || resource.resource == null)
            return

        //Try and insert the energy
        for(dir <- EnumFacing.values()) {
            worldObj.getTileEntity(pos.offset(dir)) match {
                case tank : IFluidHandler if !resource.isDead =>
                    resource.resource.drain(tank.fill(dir.getOpposite, resource.resource.getFluid, true), true)
                    if(resource.resource.getFluidAmount <= 0) {
                        resource.isDead = true
                        resource.resource.setFluid(null)
                        waitingQueue.remove(resource)
                    }
                case _ =>
            }
        }

        //If we couldn't fill, move back to source
        if(!resource.isDead) {
            val tempLocation = new BlockPos(resource.from)
            resource.from = new BlockPos(pos)
            resource.destination = new BlockPos(tempLocation)
            resource.findPathToDestination()
        }
    }

    var coolDown = 0
    override def onServerTick: Unit = {
        coolDown -= 1
        if(coolDown < 0) {
            coolDown = 200
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
