package com.dyonovan.neotech.common.items

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntityMobSpawner
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraft.world.World

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 2/2/2016
  */
class ItemSpawnerMover extends BaseItem("spawnerMover", 1){

    override def onItemUse(stack: ItemStack, player: EntityPlayer, world: World, pos: BlockPos, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
        if (!stack.hasTagCompound) {
            val tile = world.getTileEntity(pos)
            tile match {
                case spawner: TileEntityMobSpawner =>
                    val tag = new NBTTagCompound
                    spawner.writeToNBT(tag)
                    stack.setTagCompound(tag)
                    world.setBlockToAir(pos)
                    world.markBlockForUpdate(pos)
                case _ =>
            }
        } else {
            val newPos = pos.offset(side)
            if (!world.isAirBlock(newPos)) return false

            val tag = stack.getTagCompound
            tag.setInteger("x", newPos.getX)
            tag.setInteger("y", newPos.getY)
            tag.setInteger("z", newPos.getZ)
            world.setBlockState(newPos, Blocks.mob_spawner.getDefaultState)
            val tile = world.getTileEntity(newPos).asInstanceOf[TileEntityMobSpawner]
            tile.readFromNBT(tag)
            stack.setTagCompound(null)
        }
        false
    }

}
