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
  * @since 2/28/2016
  */
abstract class BlockConnected(name : String, mat : Material) extends Block(mat) with BlockConnectedTextures {

        setCreativeTab(NeoTech.tabDecorations)
        setUnlocalizedName(Reference.MOD_ID + ":" + name)
        setHardness(2.0F)

        // Methods to move textures to lower class, handle others here
        override def NoCornersTextureLocation: String = "neotech:blocks/connected/" + name + "/" + name
        override def CornersTextureLocation: String = "neotech:blocks/connected/" + name + "/" + name + "_corners"
        override def VerticalTextureLocation: String = "neotech:blocks/connected/" + name + "/" + name + "_vertical"
        override def AntiCornersTextureLocation: String = "neotech:blocks/connected/" + name + "/" + name + "_anti_corners"
        override def HorizontalTextureLocation: String = "neotech:blocks/connected/" + name + "/" + name + "_horizontal"
}
