package com.dyonovan.neotech.pipes.types

import com.dyonovan.neotech.pipes.collections.WorldPipes
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis pauljoda
  * @since August 16, 2015
  *
  * This is the base pipe and interface for all pipes. Every pipe should extend this
  */
trait SimplePipe extends TileEntity {
    WorldPipes.notifyPipes()

    override def invalidate() = WorldPipes.notifyPipes()

    /**
      * Used as a simple check to see if the pipe can connect. At it's most basic, it just checks if the tile in that
      * direction is a pipe. This is mainly used for path finding but also on the renderer
      *
      * @param facing The direction from this block
      * @return
      */
    def canConnect(facing: EnumFacing): Boolean = {
        //Safety Check
        if(getWorld == null || getPos == null)
            return false

        // Check for multi-part
        if(hasIntersect(facing))
            return false

        if (getWorld.getTileEntity(getPos) == null) return false
        getWorld.getTileEntity(getPos) match {
            case advanced : AdvancedPipe if advanced.isDisabled(facing) => return false
            case _ =>
        }
        getWorld.getTileEntity(getPos.offset(facing)) match {
            case advanced: AdvancedPipe => !advanced.isDisabled(facing.getOpposite) && !advanced.hasIntersect(facing.getOpposite)
            case pipe: SimplePipe => !pipe.hasIntersect(facing.getOpposite)
            case _ => true
        }
    }

    def hasIntersect(facing : EnumFacing) : Boolean = {
        //Safety Check
        if(getWorld == null || getPos == null)
            return true

        getWorld.getTileEntity(getPos) match {
           /* case tileContainer: IMicroblockTile =>
                if(tileContainer.getMicroblockContainer != null && tileContainer.getMicroblockContainer.getPartContainer != null) {
                    val parts = tileContainer.getMicroblockContainer.getParts
                    for(part <- parts) {
                        // Check for hollow
                        if(part.getContainer.getPartInSlot(PartSlot.getFaceSlot(facing)) != null) {
                            part.getContainer.getPartInSlot(PartSlot.getFaceSlot(facing)) match {
                                case microblock: IFaceMicroblock =>
                                    if (!microblock.isFaceHollow)
                                        return true
                                case _ =>
                            }
                        }
                    }

                    // Occlusion Check
                    if(!OcclusionHelper.occlusionTest(parts, getAxisForFace(facing)))
                        return true

                }*/
            case _ => return false
        }
        false
    }

    lazy val AxisUp    = new AxisAlignedBB(5 / 16F, 11 / 16F, 5 / 16F,
        11 / 16F, 1.0F, 11 / 16F)
    lazy val AxisDown  = new AxisAlignedBB(5 / 16F, 0F, 5 / 16F,
        11 / 16F, 5 / 16F, 11 / 16F)
    lazy val AxisSouth = new AxisAlignedBB(5 / 16F, 5 / 16F, 11 / 16F,
        11 / 16F, 11 / 16F, 1F)
    lazy val AxisNorth = new AxisAlignedBB(5 / 16F, 5 / 16F, 0F,
        11 / 16F, 11 / 16F, 5 / 16F)
    lazy val AxisEast  = new AxisAlignedBB(11 / 16F, 5 / 16F, 5 / 16F,
        1F, 11 / 16F, 11 / 16F)
    lazy val AxisWest  = new AxisAlignedBB(0F, 5 / 16F, 5 / 16F,
        5 / 16F, 11 / 16F, 11 / 16F)
    lazy val AxisSelf  = new AxisAlignedBB(5 / 16F, 5 / 16F, 5 / 16F,
        11 / 16F, 11 / 16F, 11 / 16F)

    def getAxisForFace(facing : EnumFacing) : AxisAlignedBB = {
        facing match {
            case EnumFacing.UP    => AxisUp
            case EnumFacing.DOWN  => AxisDown
            case EnumFacing.NORTH => AxisNorth
            case EnumFacing.SOUTH => AxisSouth
            case EnumFacing.EAST  => AxisEast
            case EnumFacing.WEST  => AxisWest
            case _ => AxisSelf
        }
    }

    /**
      * Sometimes we need to know if the connection is more than just a pipe. Usually an inventory of some sort.
      * This is used primarily on the renderer to render the block on the pipe
      *
      * @param facing The direction from this block
      * @return
      */
    def isSpecialConnection(facing : EnumFacing) : Boolean = !getWorld.getTileEntity(getPos.offset(facing)).isInstanceOf[SimplePipe]

    /**
      * Convert the position to a long format
      *
      * @return
      */
    def getPosAsLong: Long = getPos.toLong

    /**
      * Called when this pipe is broken
      */
    def onPipeBroken() : Unit = {}
}
