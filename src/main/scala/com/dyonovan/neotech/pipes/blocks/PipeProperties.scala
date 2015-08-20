package com.dyonovan.neotech.pipes.blocks

import net.minecraft.block.properties.{PropertyBool, PropertyEnum}
import net.minecraft.item.EnumDyeColor

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis pauljoda
 * @since August 19, 2015
 */
object PipeProperties {
    lazy val COLOR = PropertyEnum.create("color", classOf[EnumDyeColor])
    lazy val UP = PropertyBool.create("up")
    lazy val DOWN = PropertyBool.create("down")
    lazy val NORTH = PropertyBool.create("north")
    lazy val SOUTH = PropertyBool.create("south")
    lazy val EAST = PropertyBool.create("east")
    lazy val WEST = PropertyBool.create("west")
}
