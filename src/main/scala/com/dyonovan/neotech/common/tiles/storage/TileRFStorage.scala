package com.dyonovan.neotech.common.tiles.storage

import cofh.api.energy.{EnergyStorage, IEnergyHandler, IEnergyReceiver}
import com.teambr.bookshelf.common.tiles.traits.UpdatingTile
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 15, 2015
 */
class TileRFStorage(tier: Int) extends TileEntity with IEnergyHandler with UpdatingTile {

    def this() = this(1)

    var energy: EnergyStorage = new EnergyStorage(25000, 200, 200)

    tier match {
        case 1 => energy = new EnergyStorage(25000, 200, 200)
        case 2 => energy = new EnergyStorage(1000000, 1000, 1000)
        case 3 => energy = new EnergyStorage(10000000, 10000, 10000)
        case 4 =>
            energy = new EnergyStorage(100000000, 100000, 100000)
            energy.setEnergyStored(energy.getMaxEnergyStored)
        case _ => energy = new EnergyStorage(25000, 200, 200)
    }

    def getTier: Int = tier

    override def onServerTick(): Unit = {
        //Transfer
        if (energy.getEnergyStored > 0) {
            for (i <- EnumFacing.values()) {
                worldObj.getTileEntity(pos.offset(i)) match {
                    case tile: IEnergyReceiver =>
                        val want = tile.receiveEnergy(i.getOpposite, energy.getEnergyStored, true)
                        if (want > 0) {
                            val actual = energy.extractEnergy(want, false)
                            tile.receiveEnergy(i.getOpposite, actual, false)
                            worldObj.markBlockForUpdate(pos)
                        }
                    case _ =>
                }
            }
        }
    }

    override def writeToNBT(tag: NBTTagCompound): Unit = {
        super[TileEntity].writeToNBT(tag)
        super[UpdatingTile].writeToNBT(tag)
        energy.writeToNBT(tag)
    }

    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super[TileEntity].readFromNBT(tag)
        super[UpdatingTile].readFromNBT(tag)
        energy.readFromNBT(tag)
    }

    /** *****************************************************************************************************************
      * *********************************************** Energy methods **************************************************
      * *****************************************************************************************************************/

    override def getEnergyStored(from: EnumFacing): Int = energy.getEnergyStored

    override def getMaxEnergyStored(from: EnumFacing): Int = energy.getMaxEnergyStored

    override def receiveEnergy(from: EnumFacing, maxReceive: Int, simulate: Boolean): Int = {
        if (tier == 4) return 0
        val actual = energy.receiveEnergy(maxReceive, simulate)
        if (worldObj != null)
            worldObj.markBlockForUpdate(pos)
        actual
    }

    override def extractEnergy(from: EnumFacing, maxExtract: Int, simulate: Boolean): Int = {
        var doSimulate = simulate
        if (tier == 4) doSimulate = true
        val actual = energy.extractEnergy(maxExtract, simulate)
        if (worldObj != null)
            worldObj.markBlockForUpdate(pos)
        actual
    }

    override def canConnectEnergy(from: EnumFacing): Boolean = true
}
