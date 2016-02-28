package com.dyonovan.neotech.pipes.tiles.fluid

import java.util

import com.dyonovan.neotech.pipes.types.{InterfacePipe, SimplePipe}
import com.teambr.bookshelf.client.gui.{GuiColor, GuiTextFormat}
import net.minecraft.nbt.NBTTagCompound
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
class FluidInterfacePipe extends InterfacePipe[IFluidHandler, FluidStack] {

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
                    case tank: IFluidHandler if tank.getTankInfo(dir.getOpposite) != null
                            && tank.getTankInfo(dir.getOpposite)(0).fluid != null =>
                        if (findSourceOnMode(tank.getTankInfo(dir.getOpposite)(0).fluid, pos.offset(dir))) {
                            if (foundSource != null) {
                                val amount = foundSource._1.fill(foundSource._2,
                                    tank.drain(dir.getOpposite, getMaxFluidDrain, false), false)
                                if(amount > 0)
                                    foundSource._1.fill(foundSource._2, tank.drain(dir.getOpposite, amount, true), true)
                                foundSource = null
                                return
                            }
                        }

                    case _ =>
                }
            }
        }
    }

    override def writeToNBT(tag : NBTTagCompound) : Unit = {
        super.writeToNBT(tag)
        super[TileEntity].writeToNBT(tag)
    }

    override def readFromNBT(tag : NBTTagCompound) : Unit = {
        super.readFromNBT(tag)
        super[TileEntity].readFromNBT(tag)
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
                if (tank.fill(facing, fluid, false) >= 100)
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
