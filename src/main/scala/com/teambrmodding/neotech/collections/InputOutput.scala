package com.teambrmodding.neotech.collections

import java.util

import com.teambr.bookshelf.traits.NBTSavable
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks._


/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * Used as the base trait for all machines that automatically move things in and out, also handles disabled faces
  *
  * @author Paul Davis <pauljoda>
  * @since 1/24/2016
  */
trait InputOutput extends NBTSavable {

    // A list of the valid modes for this instance
    lazy val validModes = new ArrayBuffer[EnumInputOutputMode]()

    // Holds the current mode for each side
    val sideModes = new util.HashMap[EnumFacing, EnumInputOutputMode]()

    // Setup Modes
    setupValidModes()

    // Reset things to defaults
    resetIO()

    /**
      * Used to set the default modes for this, calls the helper function to the child
      */
    def setupValidModes(): Unit = {
        // Set default as the first, always
        validModes += EnumInputOutputMode.DEFAULT

        // Add in specific instance modes
        addValidModes()

        // Add disabled to the end
        validModes += EnumInputOutputMode.DISABLED
    }

    /**
      * Add all modes you want, in order, here
      */
    def addValidModes() : Unit

    /**
      * Toggles the mode to the next available mode in the list
      *
      * @param dir The face to toggle
      */
    def toggleMode(dir : EnumFacing): Unit = {
        // Loop to find the next value
        var nextMode : EnumInputOutputMode = null
        var selectNext = false

        breakable {
            for (mode <- validModes) {
                if (selectNext) {
                    nextMode = mode
                    break
                }
                if (mode == sideModes.get(dir))
                    selectNext = true
            }
        }

        // Means we need to loop back to the front
        if(nextMode == null)
            nextMode = validModes(0)

        // Update collection
        sideModes.put(dir, nextMode)
    }

    /**
      * Used to check if the side is set to a mode that allows output
      * @param dir The face to output from
      * @param isPrimary Is this a primary output or false for secondary
      * @return True if you can move
      */
    def canOutputFromSide(dir : EnumFacing, isPrimary : Boolean = true): Boolean = {
        if(isDisabled(dir))
            return false

        if(isPrimary)
            sideModes.get(dir) == EnumInputOutputMode.ALL_MODES || sideModes.get(dir) == EnumInputOutputMode.OUTPUT_ALL ||
                    sideModes.get(dir) == EnumInputOutputMode.OUTPUT_PRIMARY
        else
            sideModes.get(dir) == EnumInputOutputMode.ALL_MODES || sideModes.get(dir) == EnumInputOutputMode.OUTPUT_ALL ||
                    sideModes.get(dir) == EnumInputOutputMode.OUTPUT_SECONDARY
    }

    /**
      * Used to check if the side is set to a mode that allows input
      * @param dir The face to input from
      * @param isPrimary Is this a primary input or false for secondary
      * @return True if you can move
      */
    def canInputFromSide(dir : EnumFacing, isPrimary : Boolean = true): Boolean = {
        if(isDisabled(dir))
            return false

        if(isPrimary)
            sideModes.get(dir) == EnumInputOutputMode.ALL_MODES || sideModes.get(dir) == EnumInputOutputMode.INPUT_ALL ||
                    sideModes.get(dir) == EnumInputOutputMode.INPUT_PRIMARY
        else
            sideModes.get(dir) == EnumInputOutputMode.ALL_MODES || sideModes.get(dir) == EnumInputOutputMode.INPUT_ALL ||
                    sideModes.get(dir) == EnumInputOutputMode.INPUT_SECONDARY
    }

    /**
      * Used to check if the side has been set to disabled
      * @param dir The face
      * @return True if disabled
      */
    def isDisabled(dir : EnumFacing) : Boolean =
        sideModes.get(dir) == EnumInputOutputMode.DISABLED

    /**
      * Used to get the mode for a specific side, probably should use this as there are helper methods but
      * some instances may need this
      * @param dir The face
      * @return The mode for the side
      */
    def getModeForSide(dir : EnumFacing) : EnumInputOutputMode =
        sideModes.get(dir)

    /**
      * Get the string used to find the texture for this mode
      */
    def getDisplayIconForSide(dir : EnumFacing) : String = {
        getModeForSide(dir) match {
            case EnumInputOutputMode.INPUT_ALL =>
                "neotech:blocks/inputFace"
            case EnumInputOutputMode.INPUT_PRIMARY =>
                "neotech:blocks/inputFacePrimary"
            case EnumInputOutputMode.INPUT_SECONDARY =>
                "neotech:blocks/inputFaceSecondary"
            case EnumInputOutputMode.OUTPUT_ALL =>
                "neotech:blocks/outputFace"
            case EnumInputOutputMode.OUTPUT_PRIMARY =>
                "neotech:blocks/outputFacePrimary"
            case EnumInputOutputMode.OUTPUT_SECONDARY =>
                "neotech:blocks/outputFaceSecondary"
            case EnumInputOutputMode.ALL_MODES =>
                "neotech:blocks/inputOutputFace"
            case EnumInputOutputMode.DISABLED =>
                "neotech:blocks/disabled"
            case _ => null
        }
    }

    /**
      * Resets everything to default mode
      */
    def resetIO() : Unit = {
        for(dir <- EnumFacing.values())
            sideModes.put(dir, validModes(0))
    }

    /**
      * Read the info from the tag
      * @param tag The stored data
      */
    override def readFromNBT(tag : NBTTagCompound) = {
        for(side <- EnumFacing.values())
            sideModes.put(side, EnumInputOutputMode.getModeFromInt(tag.getInteger("Side: " + side.ordinal())))
    }

    /**
      * Write information to the tag
      * @param tag The data to write to
      */
    override def writeToNBT(tag : NBTTagCompound) : NBTTagCompound = {
        for(side <- EnumFacing.values())
            tag.setInteger("Side: " + side.ordinal(), sideModes.get(side).getIntValue)
        tag
    }
}
