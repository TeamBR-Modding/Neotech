package com.teambrmodding.neotech.common.items

import com.teambrmodding.neotech.NeoTech
import com.teambrmodding.neotech.lib.Reference
import net.minecraft.item.Item

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 12, 2015
 */
class BaseItem(name: String, maxStackSize: Int) extends Item {

    setCreativeTab(NeoTech.tabNeoTech)
    setMaxStackSize(maxStackSize)
    setUnlocalizedName(Reference.MOD_ID + ":" + name)

    def getName: String = { name }
}
