package com.dyonovan.neotech.pipes.tiles.energy

import cofh.api.energy.{IEnergyReceiver, EnergyStorage, IEnergyProvider}
import com.dyonovan.neotech.pipes.entities.EnergyResourceEntity
import com.dyonovan.neotech.pipes.types.{ExtractionPipe, SimplePipe}
import net.minecraft.util.EnumFacing

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
class EnergyExtractionPipe extends ExtractionPipe[EnergyStorage, EnergyResourceEntity] {
    override def canConnect(facing: EnumFacing): Boolean =
        getWorld.getTileEntity(getPos.offset(facing)).isInstanceOf[SimplePipe] || getWorld.getTileEntity(pos.offset(facing)).isInstanceOf[IEnergyProvider]

    /**
     * This is the speed to extract from. You should be calling this when building your resources to send.
     *
     * This is included as a reminder to the child to have variable speeds
     * @return
     */
    override def getSpeed: Double = 0.5

    /**
     * Used to specify how much RF, check for upgrades here
     * @return
     */
    def getMaxRFDrain : Int = 100

    /**
     * Get how many ticks to 'cooldown' between operations.
     * @return 20 = 1 second
     */
    override def getDelay: Int = 20

    /**
     * This is called when we fail to send a resource. You should put the resource back where you found it or
     * add it to the world
     * @param resource
     */
    override def resourceReturned(resource: EnergyResourceEntity): Unit = {
        val tempStorage = new EnergyStorage(resource.resource.getMaxEnergyStored, resource.resource.getMaxReceive, resource.resource.getMaxExtract)
        tempStorage.setEnergyStored(resource.resource.getEnergyStored)

        for(dir <- EnumFacing.values()) {
            worldObj.getTileEntity(pos.offset(dir)) match {
                case receiver : IEnergyReceiver if tempStorage.getEnergyStored > 0 =>
                    receiver.receiveEnergy(dir.getOpposite, tempStorage.extractEnergy(tempStorage.getEnergyStored, false), false)
                case _ =>
            }
        }
    }

    /**
     * The first step in moving things. You should call this from doExtraction. This is an outside method so you can
     * have additional functions to the pipe besides just extracting. For example, a pipe that pulls items in the world
     */
    override def tryExtractResources(): Unit = ???

    /**
     * Called when the resource returns. You should try to put the resource somewhere or send it back from where it came
     * @param resource
     */
    override def tryInsertResource(resource: EnergyResourceEntity): Unit = ???

    /**
     * This is what is actually called to the child class. Here you should call your extractResources or whatever you want
     * this pipe to do on its action phase. The parent will not automatically call extract
     *
     * This is useful if you wish to set different modes and call different path finding
     */
    override def doExtraction(): Unit = ???
}
