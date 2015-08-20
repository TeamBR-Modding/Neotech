package com.dyonovan.neotech.common.items

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.lib.Reference
import net.minecraft.item.Item

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 20, 2015
 */
class BaseUpgradeItem(name: String, maxStackSize: Int, creative: Boolean) extends Item {

    if (creative)
        setCreativeTab(NeoTech.tabNeoTech)
    setMaxStackSize(maxStackSize)
    setUnlocalizedName(Reference.MOD_ID + ":" + name)

    def getName: String = { name }
}
