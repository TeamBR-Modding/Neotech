package com.teambrmodding.neotech.common.metals.items

import com.teambrmodding.neotech.Neotech
import com.teambrmodding.neotech.lib.Reference
import net.minecraft.item.{ItemStack, Item}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/17/2016
  */
class ItemMetal(name: String, color : Int, maxStackSize: Int) extends Item {
    setCreativeTab(Neotech.tabMetals)
    setMaxStackSize(maxStackSize)
    setUnlocalizedName(Reference.MOD_ID + ":" + name)

    def getName: String = name

    def getColorFromItemStack(stack: ItemStack) : Int = color
}