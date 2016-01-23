package com.dyonovan.neotech.pipes.types

import com.dyonovan.neotech.pipes.entities.ResourceEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis pauljoda
  * @since August 16, 2015
  */
trait SinkPipe[T, R <: ResourceEntity[T]] extends AdvancedPipe {

    /**
      * Try and insert the resource into an inventory.
      *
      * It is pretty good practice to send the resource back if you can't remove all of it
      * @param resource
      */
    def tryInsertResource(resource : R)

    /**
      * Can this pipe connect
      * @param facing The direction from this block
      * @return
      */
    override def canConnect(facing: EnumFacing): Boolean = connections.get(facing.ordinal()) && super.canConnect(facing)

    /**
      * Used to check if this pipe can accept a resource
      *
      * You should not actually change anything, all simulation
      * @param resource
      * @return
      */
    def willAcceptResource(resource: ResourceEntity[_]) : Boolean = {
        if(getUpgradeBoard != null && getUpgradeBoard.hasControl) {
            if(redstone == -1 && isPowered)
                return false
            if(redstone == 1 && !isPowered)
                return false
        }
        true
    }

    /**
      * Called when the resource enters this pipe. If it is meant for us, we will try and insert
      * @param resource
      */
    override def onResourceEnteredPipe(resource: ResourceEntity[_]): Unit = {
        resource match {
            case matchedResource: R if resource.destination == getPos => tryInsertResource(matchedResource)
            case _ =>
        }
    }

    @SideOnly(Side.CLIENT)
    override def getGUIHeight : Int = {
        var baseHeight = 41
        if(getUpgradeBoard != null && getUpgradeBoard.hasControl)
            baseHeight += 60
        if(getUpgradeBoard != null && getUpgradeBoard.hasExpansion)
            baseHeight += 30
        baseHeight
    }
}
