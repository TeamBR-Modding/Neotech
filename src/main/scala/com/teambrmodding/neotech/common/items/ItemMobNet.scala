package com.teambrmodding.neotech.common.items

import com.teambr.bookshelf.client.gui.{GuiColor, GuiTextFormat}
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.{Entity, EntityList}
import net.minecraft.item.ItemStack
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.{EnumActionResult, ActionResult, EnumHand, EnumFacing}
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
  * @since 2/12/2016
  */
object ItemMobNet {
    final val instance = new ItemMobNet
}

class ItemMobNet extends BaseItem("mobNet", 16) {
    override def onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer, hand: EnumHand) : ActionResult[ItemStack] = {
    if (stack.hasTagCompound && !world.isRemote) {
            val mop = rayTrace(world, player, false)
            if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
                val entity = EntityList.createEntityByName(stack.getTagCompound.getString("type"), world)
                if (entity != null) {
                    entity.readFromNBT(stack.getTagCompound)
                    entity.setPosition(mop.hitVec.xCoord, mop.hitVec.yCoord, mop.hitVec.zCoord)
                    offsetInDir(entity, mop.sideHit)
                    if (stack.hasDisplayName)
                        entity.setCustomNameTag(stack.getDisplayName)
                    world.spawnEntityInWorld(entity)
                }
                stack.setTagCompound(null)
                entity match {
                    case mob: EntityMob =>
                        mob.setAttackTarget(player)
                    case _ =>
                }
            }
        }
        new ActionResult[ItemStack](EnumActionResult.PASS, stack)
    }

    def offsetInDir(entity : Entity, dir : EnumFacing) = {
        dir match {
            case EnumFacing.DOWN => entity.posY -= 1
            case EnumFacing.NORTH => entity.posZ += 1
            case EnumFacing.SOUTH => entity.posZ -= 1
            case EnumFacing.EAST => entity.posX -= 1
            case EnumFacing.WEST => entity.posX += 1
            case _ =>
        }
    }

    override def hasEffect(stack : ItemStack) = stack.hasTagCompound

    @SideOnly(Side.CLIENT)
    override def addInformation(stack: ItemStack, player: EntityPlayer, list: java.util.List[String], boolean: Boolean): Unit = {
        if (stack.hasTagCompound){
            list.add(GuiColor.GREEN + stack.getTagCompound.getString("type"))
        } else {
            list.add(GuiTextFormat.ITALICS + GuiColor.RED.toString + "Empty")
        }

    }
}
