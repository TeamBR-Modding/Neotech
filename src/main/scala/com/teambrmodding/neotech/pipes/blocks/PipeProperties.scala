package com.teambrmodding.neotech.pipes.blocks

import net.minecraft.block.properties.{PropertyInteger, PropertyBool, PropertyEnum}
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
    lazy val COLOR  = PropertyEnum.create("color", classOf[EnumDyeColor])
    lazy val UP = PropertyBool.create("up")
    lazy val DOWN = PropertyBool.create("down")
    lazy val NORTH = PropertyBool.create("north")
    lazy val SOUTH = PropertyBool.create("south")
    lazy val EAST = PropertyBool.create("east")
    lazy val WEST = PropertyBool.create("west")
    lazy val SPECIAL_UP = PropertyInteger.create("up", 0, 2)
    lazy val SPECIAL_DOWN = PropertyInteger.create("down", 0, 2)
    lazy val SPECIAL_NORTH = PropertyInteger.create("north", 0, 2)
    lazy val SPECIAL_SOUTH = PropertyInteger.create("south", 0, 2)
    lazy val SPECIAL_EAST = PropertyInteger.create("east", 0, 2)
    lazy val SPECIAL_WEST = PropertyInteger.create("west", 0, 2)
}
