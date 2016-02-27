package com.dyonovan.neotech.common.blocks.connected

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.lib.Reference
import com.teambr.bookshelf.common.blocks.BlockConnectedTextures
import net.minecraft.block.Block
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
class BlockVoidGlass extends Block(Material.glass) with BlockConnectedTextures {

    setHardness(2.0F)
    setLightOpacity(1000)
    setCreativeTab(NeoTech.tabDecorations)
    setUnlocalizedName(Reference.MOD_ID + ":voidGlass")

    override def isClear: Boolean = true

    override def NoCornersTextureLocation: String =  "neotech:blocks/connected/voidGlass/voidGlass"
    override def AntiCornersTextureLocation: String =  "neotech:blocks/connected/voidGlass/voidGlass_anti_corners"
    override def CornersTextureLocation: String =  "neotech:blocks/connected/voidGlass/voidGlass_corners"
    override def HorizontalTextureLocation: String =  "neotech:blocks/connected/voidGlass/voidGlass_horizontal"
    override def VerticalTextureLocation: String =  "neotech:blocks/connected/voidGlass/voidGlass_vertical"
}
