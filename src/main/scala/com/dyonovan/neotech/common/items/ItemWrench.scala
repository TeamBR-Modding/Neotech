package com.dyonovan.neotech.common.items

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.{EnumFacing, BlockPos}
import net.minecraft.world.World

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/9/2016
  */
class ItemWrench extends BaseItem("wrench", 1) {
    override def isFull3D : Boolean = true
    override def onItemUseFirst(stack : ItemStack, player : EntityPlayer, world : World, pos : BlockPos, side : EnumFacing,
                                hitX : Float, hitY : Float, hitZ : Float): Boolean = {
        val block = world.getBlockState(pos).getBlock
        if (!player.isSneaking && block != Blocks.bed && block.rotateBlock(world, pos, side)) {
            player.swingItem()
            return !world.isRemote
        }
        false
    }

    override def doesSneakBypassUse(world : World, pos: BlockPos, player: EntityPlayer) : Boolean = true
}
