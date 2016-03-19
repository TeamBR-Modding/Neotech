package com.dyonovan.neotech.common.items

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumActionResult, EnumHand, EnumFacing}
import net.minecraft.world.{IBlockAccess, World}

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

    override def onItemUse(stack: ItemStack, player: EntityPlayer, world: World, pos: BlockPos,
                           hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) : EnumActionResult = {
        val block = world.getBlockState(pos).getBlock
        if (!player.isSneaking && block != Blocks.bed && block.rotateBlock(world, pos, facing)) {
            player.swingArm(EnumHand.MAIN_HAND)
            return EnumActionResult.SUCCESS
        }
        EnumActionResult.PASS
    }

    override def doesSneakBypassUse(stack: ItemStack, world: IBlockAccess, pos: BlockPos, player: EntityPlayer) : Boolean = true
}
