package com.dyonovan.neotech.common.tiles.storage

import cofh.api.energy.{EnergyStorage, IEnergyHandler, IEnergyReceiver}
import com.teambr.bookshelf.api.waila.Waila
import com.teambr.bookshelf.client.gui.GuiColor
import com.teambr.bookshelf.common.tiles.traits.UpdatingTile
import mcp.mobius.waila.api.ITaggedList
import mcp.mobius.waila.api.ITaggedList.ITipList
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
class TileRFStorage extends TileEntity with IEnergyHandler with UpdatingTile with Waila {

    var tier = 0

    def this(t: Int) {
        this()
        tier = t
        initEnergy()
    }

    var energy: EnergyStorage = _

    def initEnergy(): Unit = {
        tier match {
            case 1 => energy = new EnergyStorage(25000, 200, 200)
            case 2 => energy = new EnergyStorage(1000000, 1000, 1000)
            case 3 => energy = new EnergyStorage(10000000, 10000, 10000)
            case 4 =>
                energy = new EnergyStorage(100000000, 100000, 100000)
                energy.setEnergyStored(energy.getMaxEnergyStored)
            case _ =>
        }
        if (worldObj != null)
            worldObj.markBlockForUpdate(pos)
    }

    def getTier: Int = tier

    override def onServerTick(): Unit = {
        //Transfer
        if (energy != null) {
            if (energy.getEnergyStored > 0) {
                for (i <- EnumFacing.values()) {
                    worldObj.getTileEntity(pos.offset(i)) match {
                        case tile: IEnergyReceiver =>
                            val want = tile.receiveEnergy(i.getOpposite, energy.getEnergyStored, true)
                            if (want > 0) {
                                val actual = extractEnergy(null, want, simulate = false)
                                tile.receiveEnergy(i.getOpposite, actual, false)
                                worldObj.markBlockForUpdate(pos)
                            }
                        case _ =>
                    }
                }
            }
        }
    }

    override def writeToNBT(tag: NBTTagCompound): Unit = {
        super[TileEntity].writeToNBT(tag)
        super[UpdatingTile].writeToNBT(tag)
        if (energy != null) {
            energy.writeToNBT(tag)
        }
        tag.setInteger("Tier", tier)
    }

    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super[TileEntity].readFromNBT(tag)
        super[UpdatingTile].readFromNBT(tag)
        if (tag.hasKey("Tier")) {
            tier = tag.getInteger("Tier")
            initEnergy()
            if (tier != 4 && energy != null)
                energy.readFromNBT(tag)
        }

    }

    /** *****************************************************************************************************************
      * *********************************************** Energy methods **************************************************
      * *****************************************************************************************************************/

    override def getEnergyStored(from: EnumFacing): Int = {
        if (energy != null)
            energy.getEnergyStored
        else 0
    }

    override def getMaxEnergyStored(from: EnumFacing): Int = {
        if (energy != null)
            energy.getMaxEnergyStored
        else 0
    }

    override def receiveEnergy(from: EnumFacing, maxReceive: Int, simulate: Boolean): Int = {
        if (energy != null) {
            if (tier == 4) return 0
            val actual = energy.receiveEnergy(maxReceive, simulate)
            if (worldObj != null)
                worldObj.markBlockForUpdate(pos)
            actual
        } else 0
    }

    override def extractEnergy(from: EnumFacing, maxExtract: Int, simulate: Boolean): Int = {
        if (energy != null) {
            var doSimulate = simulate
            if (tier == 4) doSimulate = true
            val actual = energy.extractEnergy(maxExtract, doSimulate)
            if (worldObj != null)
                worldObj.markBlockForUpdate(pos)
            actual
        } else 0
    }

    override def canConnectEnergy(from: EnumFacing): Boolean = true

    override def returnWailaBody(tipList: ITaggedList.ITipList): ITipList = {
        var color = ""
        if (getEnergyStored(null) > 0)
            color = GuiColor.GREEN.toString
        else
            color = GuiColor.RED.toString
        tipList.add(color + getEnergyStored(null) + "/" + getMaxEnergyStored(null) + " RF")
        tipList
    }
}
