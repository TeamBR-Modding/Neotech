package com.dyonovan.neotech.common.items

import com.teambr.bookshelf.client.gui.GuiTextFormat
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.{EnumAction, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntityMobSpawner
import net.minecraft.util.{ActionResult, EnumActionResult, EnumHand}
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

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
class ItemSpawnerMover extends BaseItem("spawnerMover", 1) {

    override def onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer, hand: EnumHand): ActionResult[ItemStack] = {
        player.setActiveHand(hand)
        new ActionResult[ItemStack](EnumActionResult.SUCCESS, stack)
    }

    override def getItemUseAction(stack: ItemStack): EnumAction = {
        EnumAction.BOW
    }

    override def getMaxItemUseDuration(stack: ItemStack): Int = {
        7200
    }

    override def hasEffect(stack : ItemStack) = stack.hasTagCompound

    override def onPlayerStoppedUsing(stack: ItemStack, world: World, player: EntityLivingBase, timeLeft: Int): Unit = {
        if (timeLeft <= 7180 && player.isInstanceOf[EntityPlayer]) {
            val mop = getMovingObjectPositionFromPlayer(world, player.asInstanceOf[EntityPlayer], false)
            if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
                val pos = mop.getBlockPos
                if (!stack.hasTagCompound) {
                    val tile = world.getTileEntity(pos)
                    tile match {
                        case spawner: TileEntityMobSpawner =>
                            val tag = new NBTTagCompound
                            spawner.writeToNBT(tag)
                            stack.setTagCompound(tag)
                            world.setBlockToAir(pos)
                            world.setBlockState(pos, world.getBlockState(pos), 3)
                        case _ =>
                    }
                } else {
                    val newPos = pos.offset(mop.sideHit)
                    if (!world.isAirBlock(newPos)) return

                    val tag = stack.getTagCompound
                    tag.setInteger("x", newPos.getX)
                    tag.setInteger("y", newPos.getY)
                    tag.setInteger("z", newPos.getZ)
                    world.setBlockState(newPos, Blocks.mob_spawner.getDefaultState)
                    val tile = world.getTileEntity(newPos).asInstanceOf[TileEntityMobSpawner]
                    tile.readFromNBT(tag)
                    stack.setTagCompound(null)
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    override def addInformation(stack: ItemStack, player: EntityPlayer, list: java.util.List[String], boolean: Boolean): Unit = {
        if (stack.hasTagCompound) {
            list.add(GuiTextFormat.ITALICS + "Type: " + stack.getTagCompound.getString("EntityId"))
        }
    }
}
