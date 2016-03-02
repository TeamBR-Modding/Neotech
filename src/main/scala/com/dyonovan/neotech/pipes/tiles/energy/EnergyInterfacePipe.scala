package com.dyonovan.neotech.pipes.tiles.energy

import java.util

import cofh.api.energy.{IEnergyProvider, IEnergyReceiver}
import com.dyonovan.neotech.pipes.types.{SimplePipe, AdvancedPipe, InterfacePipe}
import com.teambr.bookshelf.client.gui.{GuiColor, GuiTextFormat}
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
class EnergyInterfacePipe extends InterfacePipe[IEnergyReceiver, Integer] {

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
                case advanced: AdvancedPipe => !advanced.isDisabled(facing.getOpposite) && !advanced.hasIntersect(facing.getOpposite)
                case pipe: SimplePipe => !pipe.hasIntersect(facing.getOpposite)
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
                            if (findSourceOnMode(provider.getEnergyStored(dir.getOpposite), pos.offset(dir))) {
                                if(foundSource != null) {
                                    val amount = foundSource._1.receiveEnergy(foundSource._2,
                                        provider.extractEnergy(dir.getOpposite, getMaxRFDrain, true), true)
                                    if(amount > 0) {
                                        foundSource._1.receiveEnergy(foundSource._2,
                                            provider.extractEnergy(dir.getOpposite, amount, false), false)
                                    }
                                    foundSource = null
                                }
                            }
                        }
                    case _ =>
                }
            }
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
      * @param energy
      * @return
      */
    override def willAcceptResource(energy: Integer, tilePos : BlockPos, facing : EnumFacing): Boolean = {
        if(!super.willAcceptResource(energy, tilePos, facing))
            return false

        //Try and insert the energy
        worldObj.getTileEntity(tilePos) match {
            case receiver: IEnergyReceiver =>
                if (receiver.receiveEnergy(facing, energy, true) > 0)
                    return true
                false
            case _ => false
        }
    }

    /**
      * Used to get a list of what tiles are attached that can accept resources. Don't worry about if full or not,
      * just if this pipe interfaces with the tile add it here
      *
      * @return A list of the tiles that are valid sinks
      */
    override def getAttachedSinks: util.List[(Long, EnumFacing)] = {
        val returnList = new util.ArrayList[(Long, EnumFacing)]()
        for(dir <- EnumFacing.values()) {
            if (canConnectSink(dir)) {
                worldObj.getTileEntity(pos.offset(dir)) match {
                    case receiver: IEnergyReceiver => returnList.add((pos.offset(dir).toLong, dir.getOpposite))
                    case _ =>
                }
            }
        }
        returnList
    }

    override def getPipeTypeID: Int = 0
}
