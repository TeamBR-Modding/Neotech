package com.dyonovan.neotech.pipes.types

import com.dyonovan.neotech.pipes.network.ResourceEntity

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
    def willAcceptResource(resource: R) : Boolean

    override def onResourceEnteredPipe(resource: ResourceEntity[_]): Unit = {
        resource match {
            case matchedResource: R if resource.destination == getPos => tryInsertResource(matchedResource)
            case _ =>
        }
    }

    def tryInsertResource(resource : R)
}
