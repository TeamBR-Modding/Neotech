package com.dyonovan.neotech.collections

import java.awt.Color
import java.util

import com.teambr.bookshelf.client.gui.GuiColor
import com.teambr.bookshelf.traits.NBTSavable
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/24/2016
  */
trait InputOutput extends NBTSavable {
    sealed trait IOMODE { def name: String }
    case object DISABLED extends IOMODE { val name = "DISABLED" }
    case object OUTONLY extends IOMODE { val name = "OUTONLY" }
    case object INONLY extends IOMODE { val name = "INONLY" }
    case object BOTH extends IOMODE { val name = "BOTH" }

    def modeToInt(mode : IOMODE): Int = {
        mode match {
            case DISABLED => 0
            case OUTONLY => 2
            case INONLY => 1
            case BOTH => 3
            case _ => 0
        }
    }

    def modeFromInt(mode : Int) : IOMODE = {
        mode match {
            case 0 => DISABLED
            case 2 => OUTONLY
            case 1 => INONLY
            case 3 => BOTH
            case _ => DISABLED
        }
    }

    def getNextMode(mode : IOMODE) : IOMODE = {
        mode match {
            case DISABLED => INONLY
            case OUTONLY => BOTH
            case INONLY => OUTONLY
            case BOTH => DISABLED
            case _ => DISABLED
        }
    }

    def getDisplayNameForIOMode(mode : IOMODE) : String = {
        mode match {
            case DISABLED => GuiColor.GRAY + "Disabled"
            case OUTONLY => GuiColor.ORANGE + "Output Only"
            case INONLY => GuiColor.BLUE + "Input Only"
            case BOTH => GuiColor.BLUE + "Input " + GuiColor.WHITE + "and " + GuiColor.ORANGE + "Output"
            case _ => GuiColor.RED + "ERROR"
        }
    }

    def getUVForMode(mode : IOMODE) : (Int, Int) = {
        mode match {
            case DISABLED => (95, 239)
            case OUTONLY => (127, 239)
            case INONLY => (111, 239)
            case BOTH => (143, 239)
            case _ => (90, 239)
        }
    }

    def getColor(mode : IOMODE) : Color = {
        mode match {
            case DISABLED => null
            case OUTONLY => new Color(255, 102, 0, 150)
            case INONLY => new Color(0, 102, 255, 150)
            case BOTH => new Color(0, 153, 0, 150)
            case _ => null
        }
    }

    val sideModes = new util.HashMap[EnumFacing, IOMODE]()
    resetIO()

    def toggleMode(dir : EnumFacing): Unit = {
        sideModes.put(dir, getNextMode(sideModes.get(dir)))
    }

    def canOutputFromSide(dir : EnumFacing): Boolean = {
        sideModes.get(dir) == OUTONLY || sideModes.get(dir) == BOTH
    }

    def canInputFromSide(dir : EnumFacing): Boolean = {
        sideModes.get(dir) == INONLY || sideModes.get(dir) == BOTH
    }

    def canOutputFromSideNoRotate(dir : EnumFacing): Boolean = {
        sideModes.get(dir) == OUTONLY || sideModes.get(dir) == BOTH
    }

    def canInputFromSideNoRotate(dir : EnumFacing): Boolean = {
        sideModes.get(dir) == INONLY || sideModes.get(dir) == BOTH
    }

    def isDisabled(dir : EnumFacing) : Boolean = {
        sideModes.get(dir) == DISABLED
    }

    def getModeForSide(dir : EnumFacing) : IOMODE = {
        sideModes.get(dir)
    }

    def resetIO() : Unit = {
        for(dir <- EnumFacing.values()) {
            sideModes.put(dir, DISABLED)
        }
    }

    override def readFromNBT(tag : NBTTagCompound) = {
        for(side <- EnumFacing.values()) {
            sideModes.put(side, modeFromInt(tag.getInteger("Side: " + side.ordinal())))
        }
    }

    override def writeToNBT(tag : NBTTagCompound)= {
        for(side <- EnumFacing.values()) {
            tag.setInteger("Side: " + side.ordinal(), modeToInt(sideModes.get(side)))
        }
    }
}
