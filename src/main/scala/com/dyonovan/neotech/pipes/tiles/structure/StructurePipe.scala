package com.dyonovan.neotech.pipes.tiles.structure

import com.dyonovan.neotech.managers.BlockManager
import com.dyonovan.neotech.pipes.entities.ResourceEntity
import com.dyonovan.neotech.pipes.types.SimplePipe

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis pauljoda
 * @since August 15, 2015
 */
class StructurePipe extends SimplePipe {
    /**
     * Called when a resource enters this pipe. You can do cool stuff here. The special pipes use it to insert and send
     * back while the upgraded pipes apply a speed update.
     *
     * NOTE: If you are applying a speed update, either use the helper method or set nextSpeed. The resource will update to
     * the next speed
     * @param resource
     */
    override def onResourceEnteredPipe(resource: ResourceEntity[_]): Unit = {
        resource.applySpeed(getSpeedApplied)
    }

    def getSpeedApplied : Double = {
        worldObj.getBlockState(pos).getBlock match {
            case BlockManager.pipeBasicSpeedStructure => 0.02
            case _ => 0.0
        }
    }
}
