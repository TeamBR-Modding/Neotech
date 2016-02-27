package com.dyonovan.neotech.common.blocks

import scala.collection.mutable.ArrayBuffer

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis "pauljoda"
  * @since 2/26/2016
  */
trait CreatesTextures {
    /**
      * Used to define the strings needed
      */
    def getTexturesToStitch: ArrayBuffer[String]
}
