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
    sealed trait IO_MODE { def name: String }
    case object DISABLED extends IO_MODE { val name = "DISABLED" }
    case object OUT_ONLY extends IO_MODE { val name = "OUT_ONLY" }
    case object OUT_FIRST_ONLY extends IO_MODE { val name = "OUTFIRSTONLY" }
    case object IN_ONLY extends IO_MODE { val name = "INONLY" }
    case object BOTH extends IO_MODE { val name = "BOTH" }

    def modeToInt(mode : IO_MODE): Int = {
        mode match {
            case DISABLED => 0
            case OUT_ONLY => 2
            case IN_ONLY => 1
            case BOTH => 3
            case _ => 0
        }
    }

    def modeFromInt(mode : Int) : IO_MODE = {
        mode match {
            case 0 => DISABLED
            case 2 => OUT_ONLY
            case 1 => IN_ONLY
            case 3 => BOTH
            case _ => DISABLED
        }
    }

    def getNextMode(mode : IO_MODE) : IO_MODE = {
        mode match {
            case DISABLED => IN_ONLY
            case OUT_ONLY => BOTH
            case IN_ONLY => OUT_ONLY
            case BOTH => DISABLED
            case _ => DISABLED
        }
    }

    def getDisplayNameForIOMode(mode : IO_MODE) : String = {
        mode match {
            case DISABLED => GuiColor.GRAY + "Disabled"
            case OUT_ONLY => GuiColor.ORANGE + "Output Only"
            case IN_ONLY => GuiColor.BLUE + "Input Only"
            case BOTH => GuiColor.BLUE + "Input " + GuiColor.WHITE + "and " + GuiColor.ORANGE + "Output"
            case _ => GuiColor.RED + "ERROR"
        }
    }

    def getColor(mode : IO_MODE) : Color = {
        mode match {
            case DISABLED => null
            case OUT_ONLY => new Color(255, 102, 0, 150)
            case IN_ONLY => new Color(0, 102, 255, 150)
            case BOTH => new Color(0, 153, 0, 150)
            case _ => null
        }
    }

    val sideModes = new util.HashMap[EnumFacing, IO_MODE]()
    resetIO()

    def toggleMode(dir : EnumFacing): Unit = {
        sideModes.put(dir, getNextMode(sideModes.get(dir)))
    }

    def canOutputFromSide(dir : EnumFacing): Boolean = {
        sideModes.get(dir) == OUT_ONLY || sideModes.get(dir) == BOTH
    }

    def canInputFromSide(dir : EnumFacing): Boolean = {
        sideModes.get(dir) == IN_ONLY || sideModes.get(dir) == BOTH
    }

    def canOutputFromSideNoRotate(dir : EnumFacing): Boolean = {
        sideModes.get(dir) == OUT_ONLY || sideModes.get(dir) == BOTH
    }

    def canInputFromSideNoRotate(dir : EnumFacing): Boolean = {
        sideModes.get(dir) == IN_ONLY || sideModes.get(dir) == BOTH
    }

    def isDisabled(dir : EnumFacing) : Boolean = {
        sideModes.get(dir) == DISABLED
    }

    def getModeForSide(dir : EnumFacing) : IO_MODE = {
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
