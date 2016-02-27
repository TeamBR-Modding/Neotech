package com.dyonovan.neotech.common.blocks

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.lib.Reference
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.{AxisAlignedBB, BlockPos}
import net.minecraft.world.World

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
class BlockPhantomGlass extends Block(Material.glass) with BlockConnectedTextures {

    setCreativeTab(NeoTech.tabDecorations)
    setUnlocalizedName(Reference.MOD_ID + ":phantomGlass")

    /**
      * Define true if you are a clear texture
      *
      * @return
      */
    override def isClear: Boolean = true

    // Methods to move textures to lower class, handle others here
    override def NoCornersTextureLocation: String = "neotech:blocks/connected/phantomGlass/phantomGlass"
    override def CornersTextureLocation: String = "neotech:blocks/connected/phantomGlass/phantomGlass_corners"
    override def VerticalTextureLocation: String = "neotech:blocks/connected/phantomGlass/phantomGlass_vertical"
    override def AntiCornersTextureLocation: String = "neotech:blocks/connected/phantomGlass/phantomGlass_anti_corners"
    override def HorizontalTextureLocation: String = "neotech:blocks/connected/phantomGlass/phantomGlass_horizontal"

    override def addCollisionBoxesToList(worldIn: World, pos: BlockPos, state: IBlockState, mask: AxisAlignedBB,
                                            list: java.util.List[AxisAlignedBB], collidingEntity: Entity) : Unit = {
        if(collidingEntity.isInstanceOf[EntityPlayer] && !collidingEntity.isSneaking) { /* NO OP */ }
        else
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity)
    }
}
