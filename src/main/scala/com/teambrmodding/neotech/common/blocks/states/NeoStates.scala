package com.teambrmodding.neotech.common.blocks.states

import net.minecraft.block.properties.PropertyInteger

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
object NeoStates {
    lazy val ON_BLOCK  = PropertyInteger.create("attached_side", 0, 6)
}
