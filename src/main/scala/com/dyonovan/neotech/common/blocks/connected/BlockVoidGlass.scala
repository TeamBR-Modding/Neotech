package com.dyonovan.neotech.common.blocks.connected

import com.dyonovan.neotech.utils.ClientUtils
import com.teambr.bookshelf.client.gui.GuiTextFormat
import com.teambr.bookshelf.traits.HasToolTip
import net.minecraft.block.material.Material

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis "pauljoda"
  * @since 2/27/2016
  */
class BlockVoidGlass extends BlockConnected("voidGlass", Material.glass) with HasToolTip {
    setLightOpacity(1000)

    override def isClear: Boolean = true
    override def getToolTip(): List[String] = List(GuiTextFormat.ITALICS + ClientUtils.translate("neotech.voidGlass.tip"))
}
