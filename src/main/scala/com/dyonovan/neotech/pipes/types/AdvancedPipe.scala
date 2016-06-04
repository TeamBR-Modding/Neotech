package com.dyonovan.neotech.pipes.types

import com.dyonovan.neotech.collections.{EnumInputOutputMode, InputOutput}
import com.dyonovan.neotech.common.blocks.traits.Upgradeable
import com.dyonovan.neotech.pipes.collections.{Filter, WorldPipes}
import com.teambr.bookshelf.common.tiles.traits.{RedstoneAware, Syncable}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
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
    val IO_FIELD_ID = 2
    val FREQUENCY = 3
    val FILTER = 4

    val FILTER_MATCH_TAG = 0
    val FILTER_MATCH_DAMAGE = 1
    val FILTER_MATCH_ORE = 2
    val FILTER_BLACKLIST = 3
}

abstract class AdvancedPipe extends TileEntity with SimplePipe with Syncable with Upgradeable with RedstoneAware
         with Filter with InputOutput {

    override def resetIO() : Unit = {
        for(dir <- EnumFacing.values()) {
            sideModes.put(dir, EnumInputOutputMode.INPUT_ALL)
        }
    }

    /**
      * Add all modes you want, in order, here
      */
    override def setupValidModes() : Unit = {
        validModes += EnumInputOutputMode.INPUT_ALL
        validModes += EnumInputOutputMode.OUTPUT_ALL
        validModes += EnumInputOutputMode.ALL_MODES
        validModes += EnumInputOutputMode.DISABLED
    }

    /**
      * Add all modes you want, in order, here
      */
    def addValidModes() : Unit = {} // We aren't using the defaults, use above

    //Variables
    var mode : Int = 0 //Used to set mode (Round Robin etc) only used in extract pipe
    var redstone : Int = 0
    var frequency : Int = 0
    var reRender = false

    /**
      * Used to tell if this pipe is allowed to connect
      *
      * @param facing The direction from this block
      * @return Can connect
      */
    def canConnectExtract(facing: EnumFacing): Boolean = canOutputFromSide(facing) && super.canConnect(facing)

    /**
      * Can this pipe connect
      *
      * @param facing The direction from this block
      * @return
      */
    def canConnectSink(facing: EnumFacing): Boolean = canInputFromSide(facing) && super.canConnect(facing)

    /**
      * This is mainly used to sending info to the client so it knows what to render. It will also be used to save on world
      * exit. You should only be saving the things needed for those instances.
      *
      * @param tag
      */
    override def writeToNBT(tag : NBTTagCompound) : NBTTagCompound = {
        super[Upgradeable].writeToNBT(tag)
        super[Filter].writeToNBT(tag)
        super[InputOutput].writeToNBT(tag)
        super[Syncable].writeToNBT(tag)
        tag.setInteger("mode", mode)
        tag.setInteger("redstone", redstone)
        tag.setInteger("frequency", frequency)
        tag.setBoolean("ReRender", reRender)
        reRender = false
        tag
    }

    /**
      * Receives the data from the server. Will not be full info needed for the resources.
      *
      * If you are on the server side, you must set the resource world object to the worldObj. Additional info may be
      * required.
      *
      * Note, if you do forget to set the world, the onServerTick method will try to save it. But for safety, just add it
 *
      * @param tag
      */
    override def readFromNBT(tag : NBTTagCompound) : Unit = {
        super[Upgradeable].readFromNBT(tag)
        super[Filter].readFromNBT(tag)
        super[InputOutput].readFromNBT(tag)
        super[Syncable].readFromNBT(tag)
        mode = tag.getInteger("mode")
        redstone = tag.getInteger("redstone")
        frequency = tag.getInteger("frequency")
        if(tag.hasKey("ReRender") && tag.getBoolean("ReRender") && getWorld != null)
            worldObj.markBlockRangeForRenderUpdate(getPos, getPos)
    }

    /**
      * Used to identify the packet that will get called on update
      *
      * @return The packet to send
      */
    /*override def getDescriptionPacket: SPacketUpdateTileEntity = {
        val tag = new NBTTagCompound
        this.writeToNBT(tag)
        getMicroblockContainer.getPartContainer.writeDescription(tag)
        new SPacketUpdateTileEntity(getPos, 1, tag)
    }*/

    /**
      * Called when a packet is received
      *
      * @param net The manager sending
      * @param pkt The packet received
      */
    /*override def onDataPacket(net : NetworkManager, pkt : SPacketUpdateTileEntity) = {
        this.readFromNBT(pkt.getNbtCompound)
        getMicroblockContainer.getPartContainer.readDescription(pkt.getNbtCompound)
    }*/

    /**
      * Used to mark for update
      */
    override def markDirty() : Unit = {}

    /**
      * Called when the board is removed, reset to default values
      */
    override def resetValues(): Unit = {
        mode = 0
        redstone = 0
        frequency = 0
        worldObj.setBlockState(getPos, worldObj.getBlockState(pos), 6)
    }

    @SideOnly(Side.CLIENT)
    def getGUIHeight : Int = {
        var baseHeight = 71
        if(getUpgradeBoard != null && getUpgradeBoard.hasControl)
            baseHeight += 60
        if(getUpgradeBoard != null && getUpgradeBoard.hasExpansion)
            baseHeight += 60
        baseHeight
    }

    override def setVariable(id : Int, value : Double) = {
        id match {
            case AdvancedPipe.REDSTONE_FIELD_ID => redstone = value.toInt; worldObj.setBlockState(getPos, worldObj.getBlockState(pos), 6)
            case AdvancedPipe.MODE_FIELD_ID => mode = value.toInt; worldObj.setBlockState(getPos, worldObj.getBlockState(pos), 6)
            case AdvancedPipe.IO_FIELD_ID =>
                toggleMode(EnumFacing.getFront(value.toInt))
                reRender = true
                worldObj.notifyBlockUpdate(getPos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3)
                WorldPipes.notifyPipes()
            case AdvancedPipe.FREQUENCY =>
                frequency = value.toInt
                worldObj.notifyBlockUpdate(getPos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 6)
                WorldPipes.notifyPipes()
            case AdvancedPipe.FILTER =>
                value.toInt match {
                    case AdvancedPipe.FILTER_MATCH_TAG => matchTag = !matchTag
                    case AdvancedPipe.FILTER_MATCH_DAMAGE => matchDamage = !matchDamage
                    case AdvancedPipe.FILTER_MATCH_ORE => matchOreDict = !matchOreDict
                    case AdvancedPipe.FILTER_BLACKLIST => blackList = !blackList
                    case _ =>
                }
                worldObj.notifyBlockUpdate(getPos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3)
                WorldPipes.notifyPipes()
            case _ =>
        }
    }

    override def getVariable(id : Int) : Double = {
        id match {
            case AdvancedPipe.REDSTONE_FIELD_ID => redstone
            case AdvancedPipe.MODE_FIELD_ID => mode
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
