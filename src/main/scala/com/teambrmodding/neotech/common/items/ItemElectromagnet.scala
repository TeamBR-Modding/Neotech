package com.teambrmodding.neotech.common.items

import java.text.NumberFormat
import java.util
import java.util.Locale

import com.teambrmodding.neotech.NeoTech
import com.teambrmodding.neotech.lib.Reference
import com.teambrmodding.neotech.registries.ConfigRegistry
import com.teambr.bookshelf.client.gui.GuiColor
import com.teambr.bookshelf.common.items.traits.ItemBattery
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.entity.item.{EntityItem, EntityTNTPrimed, EntityXPOrb}
import net.minecraft.entity.monster.EntityCreeper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.{EntityArrow, EntityLargeFireball, EntitySmallFireball}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.{AxisAlignedBB, Vec3d}
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.translation.I18n
import net.minecraft.util.{ActionResult, EnumActionResult, EnumHand}
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/19/2016
  */
class ItemElectromagnet extends ItemBattery {
    lazy val RANGE = 10D

    setMaxStackSize(1)
    setCreativeTab(NeoTech.tabNeoTech)
    setMaxStackSize(maxStackSize)
    setUnlocalizedName(Reference.MOD_ID + ":" + "electroMagnet")

    override def setDefaultTags(stack: ItemStack): Unit = {
        if (!stack.hasTagCompound) {
            val tag = new NBTTagCompound
            tag.setInteger("Energy", 0)
            tag.setInteger("EnergyCapacity", ConfigRegistry.totalRFEM)
            tag.setInteger("MaxExtract", 200)
            tag.setInteger("MaxReceive", 200)
            stack.setTagCompound(tag)
        }
    }

    override def onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer, hand: EnumHand) : ActionResult[ItemStack] = {
        if(player.isSneaking) {
            if(!stack.hasTagCompound) {
                val nbt = new NBTTagCompound
                nbt.setInteger("Energy", 0)
                nbt.setBoolean("Active", true)
                stack.setTagCompound(nbt)
            } else {
                val active = stack.getTagCompound.getBoolean("Active")
                stack.getTagCompound.setBoolean("Active", !active)
                if(!world.isRemote)
                    player.addChatComponentMessage(new TextComponentString(I18n.translateToLocal("neotech.text.magnetism") + ": " +
                            (if(!active) GuiColor.GREEN + I18n.translateToLocal("neotech.text.active")
                            else GuiColor.RED + I18n.translateToLocal("neotech.text.disabled"))))
            }
        }
        new ActionResult[ItemStack](EnumActionResult.SUCCESS, stack)
    }

    override def onUpdate(stack: ItemStack, world: World, entityIn: Entity, itemSlot: Int, isSelected: Boolean): Unit = {
        super.onUpdate(stack, world, entityIn, itemSlot, isSelected)
        entityIn match {
            case player: EntityPlayer =>
                if(stack.hasTagCompound) {
                    val energy = stack.getTagCompound.getInteger("Energy")
                    if(stack.getTagCompound.getBoolean("Active") && energy > 0){
                        val bb = new AxisAlignedBB(
                            player.posX - RANGE, player.posY - RANGE, player.posZ - RANGE,
                            player.posX + RANGE, player.posY + RANGE, player.posZ + RANGE)

                        val entities = new util.ArrayList[Entity]()

                        entities.addAll(world.getEntitiesWithinAABB(classOf[EntityItem], bb))
                        entities.addAll(world.getEntitiesWithinAABB(classOf[EntityXPOrb], bb))
                        entities.addAll(world.getEntitiesWithinAABB(classOf[EntityCreeper], bb))
                        entities.addAll(world.getEntitiesWithinAABB(classOf[EntityTNTPrimed], bb))

                        bb.expand(RANGE * 2, RANGE * 2, RANGE * 2)

                        entities.addAll(world.getEntitiesWithinAABB(classOf[EntityArrow], bb))
                        entities.addAll(world.getEntitiesWithinAABB(classOf[EntitySmallFireball], bb))
                        entities.addAll(world.getEntitiesWithinAABB(classOf[EntityLargeFireball], bb))

                        if(!entities.isEmpty) {
                            for(x <- entities.toArray) {
                                val entity = x.asInstanceOf[Entity]
                                var speed = 0.075F

                                var motionVector =
                                    new Vec3d(
                                        player.posX - entity.posX,
                                        player.posY - entity.posY,
                                        player.posZ - entity.posZ)
                                if(motionVector.lengthVector() > 1)
                                    motionVector = motionVector.normalize()

                                // Arrows are already moving, increase pull
                                if (entity.isInstanceOf[EntityArrow])
                                    speed += 0.2F

                                motionVector.xCoord * speed
                                motionVector.yCoord * speed
                                motionVector.zCoord * speed

                                entity.motionX = motionVector.xCoord
                                entity.motionY = motionVector.yCoord
                                entity.motionZ = motionVector.zCoord
                                entity.velocityChanged = true
                            }

                            //Moved stuff, drain
                            stack.getItem.asInstanceOf[ItemElectromagnet].extractEnergy(stack, 1, simulate = false)
                        }
                    }
                }
            case _ =>
        }
    }

    @SideOnly(Side.CLIENT)
    override def addInformation(stack: ItemStack, player: EntityPlayer, list: java.util.List[String], boolean: Boolean): Unit = {
        if(stack.hasTagCompound) {
            val active = stack.getTagCompound.getBoolean("Active")
            list.add(if (active) GuiColor.GREEN + I18n.translateToLocal("neotech.text.active")
            else GuiColor.RED + I18n.translateToLocal("neotech.text.disabled"))

            list.add(GuiColor.ORANGE + I18n.translateToLocal("neotech.text.redstoneFlux"))
            list.add(NumberFormat.getNumberInstance(Locale.forLanguageTag(Minecraft.getMinecraft.gameSettings.language))
                    .format(getEnergyStored(stack)) +
                    " / " +
                    NumberFormat.getNumberInstance(Locale.forLanguageTag(Minecraft.getMinecraft.gameSettings.language))
                            .format(getMaxEnergyStored(stack)) + " RF")
        }
    }


}
