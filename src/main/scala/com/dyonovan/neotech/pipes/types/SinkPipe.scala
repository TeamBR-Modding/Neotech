package com.dyonovan.neotech.pipes.types

import com.dyonovan.neotech.pipes.entities.ResourceEntity

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
trait SinkPipe[T, R <: ResourceEntity[T]] extends SimplePipe {
    /**
     * Used to check if this pipe can accept a resource
     *
     * You should not actually change anything, all simulation
     * @param resource
     * @return
     */
    def willAcceptResource(resource: R) : Boolean

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

    /**
     * Try and insert the resource into an inventory.
     *
     * It is pretty good practice to send the resource back if you can't remove all of it
     * @param resource
     */
    def tryInsertResource(resource : R)
}
