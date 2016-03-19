package com.dyonovan.neotech.pipes.tiles.fluid

import java.util

import com.dyonovan.neotech.pipes.types.{SimplePipe, AdvancedPipe, InterfacePipe}
import com.teambr.bookshelf.client.gui.{GuiColor, GuiTextFormat}
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.translation.I18n
import net.minecraft.util.{EnumFacing}
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
class FluidInterfacePipe extends InterfacePipe[IFluidHandler, FluidStack] {

    override def getDescription : String = {
        GuiColor.YELLOW +  "" + GuiTextFormat.BOLD + I18n.translateToLocal("neotech.fluidInterfacePipe.name") + ":\n" +
                GuiColor.WHITE + I18n.translateToLocal("neotech.fluidInterfacePipe.desc") + "\n\n" +
                GuiColor.GREEN + GuiTextFormat.BOLD + GuiTextFormat.UNDERLINE + I18n.translateToLocal("neotech.text.upgrades") + ":\n" + GuiTextFormat.RESET +
                GuiColor.YELLOW + GuiTextFormat.BOLD + I18n.translateToLocal("neotech.text.processors") + ":\n" +
                GuiColor.WHITE + I18n.translateToLocal("neotech.fluidInterfacePipe.processorUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + I18n.translateToLocal("neotech.text.hardDrives") + ":\n" +
                GuiColor.WHITE + I18n.translateToLocal("neotech.fluidInterfacePipe.hardDriveUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + I18n.translateToLocal("neotech.text.control") + ":\n" +
                GuiColor.WHITE + I18n.translateToLocal("neotech.energyInterfacePipe.controlUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + I18n.translateToLocal("neotech.text.expansion") + ":\n" +
                GuiColor.WHITE +  I18n.translateToLocal("neotech.energyInterfacePipe.expansionUpgrade.desc")
    }

    /*******************************************************************************************************************
      ************************************** Extraction Methods ********************************************************
      ******************************************************************************************************************/

    override def canConnect(facing: EnumFacing): Boolean =
        if(super.canConnect(facing))
            getWorld.getTileEntity(pos.offset(facing)) match {
                case inventory : IFluidHandler => true
                case advanced: AdvancedPipe => !advanced.isDisabled(facing.getOpposite) && !advanced.hasIntersect(facing.getOpposite)
                case pipe: SimplePipe => !pipe.hasIntersect(facing.getOpposite)
                case _ => false
            }
        else
            super.canConnect(facing)

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
                    case tank: IFluidHandler if tank.getTankInfo(dir.getOpposite) != null =>
                        for(tankInfoSlot <- 0 until tank.getTankInfo(dir.getOpposite).length) {
                            if (tank.getTankInfo(dir.getOpposite)(tankInfoSlot).fluid != null) {
                                if (findSourceOnMode(tank.getTankInfo(dir.getOpposite)(tankInfoSlot).fluid.copy(), pos.offset(dir))) {
                                    if (foundSource != null &&
                                            tank.drain(dir.getOpposite, getMaxFluidDrain, false) != null &&
                                            foundSource._1.canFill(foundSource._2,
                                        tank.drain(dir.getOpposite, getMaxFluidDrain, false).getFluid)) {
                                        val amount = foundSource._1.fill(foundSource._2,
                                            tank.drain(dir.getOpposite, getMaxFluidDrain, false), false)
                                        if (amount > 0) {
                                            foundSource._1.fill(foundSource._2, tank.drain(dir.getOpposite, amount, true), true)
                                            foundSource = null
                                            return
                                        }
                                    }
                                }
                            }
                        }
                    case _ =>
                }
            }
        }
    }

    /*******************************************************************************************************************
      *************************************** Insertion Methods ********************************************************
      ******************************************************************************************************************/

    /**
      * Used to check if this pipe can accept a resource
      *
      * You should not actually change anything, all simulation
      *
      * @param fluid
      * @return
      */
    override def willAcceptResource(fluid: FluidStack, tilePos : BlockPos, facing : EnumFacing): Boolean = {
        if(fluid == null || !fluid.isInstanceOf[FluidStack] || !super.willAcceptResource(fluid, tilePos, facing))
            return false

        if(fluid.getFluid == null) // I can't use this!
            return false

        //Try and insert the fluid
        worldObj.getTileEntity(tilePos) match {
            case tank: IFluidHandler =>
                if (tank.canFill(facing, fluid.getFluid) && tank.fill(facing, fluid, false) >= 10)
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
                    case receiver: IFluidHandler => returnList.add((pos.offset(dir).toLong, dir.getOpposite))
                    case _ =>
                }
            }
        }
        returnList
    }

    override def getPipeTypeID: Int = 1
}
