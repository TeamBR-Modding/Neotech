package com.dyonovan.neotech.pipes.types

import com.dyonovan.neotech.common.blocks.traits.Upgradeable
import com.dyonovan.neotech.pipes.collections.{WorldPipes, Filter, ConnectedSides}
import com.teambr.bookshelf.common.tiles.traits.{RedstoneAware, Syncable}
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/22/2016
  */
object AdvancedPipe {
    //Set and Get field constants
    val REDSTONE_FIELD_ID = 0
    val MODE_FIELD_ID = 1
    val CONNECTIONS = 2
    val FREQUENCY = 3
    val FILTER = 4

    val FILTER_MATCH_TAG = 0
    val FILTER_MATCH_DAMAGE = 1
    val FILTER_MATCH_ORE = 2
    val FILTER_BLACKLIST = 3
}

trait AdvancedPipe extends Syncable with Upgradeable with RedstoneAware with SimplePipe with Filter {

    //Variables
    var mode : Int = 0 //Used to set mode (Round Robin etc) only used in extract pipe
    var redstone : Int = 0
    var frequency : Int = 0

    //Used to set connections
    val connections = new ConnectedSides

    /**
      * This is mainly used to sending info to the client so it knows what to render. It will also be used to save on world
      * exit. You should only be saving the things needed for those instances.
      *
      * @param tag
      */
    override def writeToNBT(tag : NBTTagCompound) : Unit = {
        super[Upgradeable].writeToNBT(tag)
        super[Filter].writeToNBT(tag)
        connections.writeToNBT(tag)
        tag.setInteger("mode", mode)
        tag.setInteger("redstone", redstone)
        tag.setInteger("frequency", frequency)
    }

    /**
      * Receives the data from the server. Will not be full info needed for the resources.
      *
      * If you are on the server side, you must set the resource world object to the worldObj. Additional info may be
      * required.
      *
      * Note, if you do forget to set the world, the onServerTick method will try to save it. But for safety, just add it
      * @param tag
      */
    override def readFromNBT(tag : NBTTagCompound) : Unit = {
        super[Upgradeable].readFromNBT(tag)
        super[Filter].readFromNBT(tag)
        connections.readFromNBT(tag)
        mode = tag.getInteger("mode")
        redstone = tag.getInteger("redstone")
        frequency = tag.getInteger("frequency")
    }

    /**
      * Used to mark for update
      */
    override def markDirty() : Unit = {
        super[Upgradeable].markDirty()
    }

    /**
      * Called when the board is removed, reset to default values
      */
    override def resetValues(): Unit = {
        mode = 0
        redstone = 0
        frequency = 0
        resetFilter()
        for(x <- connections.connections.indices)
            connections.set(x, value = true)
        getWorld.markBlockForUpdate(getPos)
    }

    @SideOnly(Side.CLIENT)
    def getGUIHeight : Int = {
        var baseHeight = 41
        if(getUpgradeBoard != null && getUpgradeBoard.hasControl)
            baseHeight += 90
        if(getUpgradeBoard != null && getUpgradeBoard.hasExpansion)
            baseHeight += 60
        baseHeight
    }

    override def setVariable(id : Int, value : Double) = {
        id match {
            case AdvancedPipe.REDSTONE_FIELD_ID => redstone = value.toInt
            case AdvancedPipe.MODE_FIELD_ID => mode = value.toInt
            case AdvancedPipe.CONNECTIONS =>
                connections.set(value.toInt, !connections.get(value.toInt))
                WorldPipes.notifyPipes()
                getWorld.markBlockRangeForRenderUpdate(getPos, getPos)
            case AdvancedPipe.FREQUENCY =>
                frequency = value.toInt
                WorldPipes.notifyPipes()
            case AdvancedPipe.FILTER =>
                value.toInt match {
                    case AdvancedPipe.FILTER_MATCH_TAG => matchTag = !matchTag
                    case AdvancedPipe.FILTER_MATCH_DAMAGE => matchDamage = !matchDamage
                    case AdvancedPipe.FILTER_MATCH_ORE => matchOreDict = !matchOreDict
                    case AdvancedPipe.FILTER_BLACKLIST => blackList = !blackList
                    case _ =>
                }
                WorldPipes.notifyPipes()
            case _ =>
        }
    }

    override def getVariable(id : Int) : Double = {
        id match {
            case AdvancedPipe.REDSTONE_FIELD_ID => redstone
            case AdvancedPipe.MODE_FIELD_ID => mode
            case AdvancedPipe.CONNECTIONS => 0.0
            case AdvancedPipe.FREQUENCY => frequency
            case _ => 0.0
        }
    }

    def moveRedstoneMode(mod : Int) : Unit = {
        redstone += mod
        if(redstone < -1)
            redstone = 1
        else if(redstone > 1)
            redstone = -1
    }

    def getRedstoneModeName : String = {
        redstone match {
            case -1 => "Low"
            case 0 => "Disabled"
            case 1 => "High"
            case _ => "Error"
        }
    }

    def setRedstoneMode(newMode : Int) : Unit = {
        this.redstone = newMode
    }

    def moveMode(mod : Int) : Unit = {
        mode += mod
        if(mode < 0)
            mode = 2
        else if(mode > 2)
            mode = 0
    }

    def getModeName : String = {
        mode match {
            case 0 => "First Available"
            case 1 => "Last Available"
            case 2 => "Round-Robin"
            case _ => "Error"
        }
    }

    def setMode(newMode : Int) : Unit = {
        this.mode = newMode
    }
}
