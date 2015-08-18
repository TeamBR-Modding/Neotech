package com.dyonovan.neotech.pipes.tiles.fluid

import com.dyonovan.neotech.pipes.entities.{FluidResourceEntity, ResourceEntity}
import com.dyonovan.neotech.pipes.types.{SimplePipe, SinkPipe}
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraftforge.fluids.{FluidTank, IFluidHandler}

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
class FluidSinkPipe extends SinkPipe[FluidTank, FluidResourceEntity] {
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
                    if(tank.fill(dir.getOpposite, resource.resource.getFluid, false) > 0)
                        return true
                case _ =>
            }
        }
        false
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
}
