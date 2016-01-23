package com.dyonovan.neotech.pipes.collections

import java.util

import com.dyonovan.neotech.pipes.types.ExtractionPipe

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/23/2016
  */
object WorldPipes {
    val pipes = new util.ArrayList[ExtractionPipe[_, _]]()

    def notifyPipes() = {
        for(x <- 0 until pipes.size())
            pipes.get(x).shouldRefreshCache = true
    }
}
