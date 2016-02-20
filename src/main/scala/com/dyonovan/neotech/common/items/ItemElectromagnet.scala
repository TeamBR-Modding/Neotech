package com.dyonovan.neotech.common.items

import java.text.NumberFormat
import java.util
import java.util.Locale

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.lib.Reference
import com.teambr.bookshelf.client.gui.GuiColor
import com.teambr.bookshelf.common.items.traits.ItemBattery
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.entity.item.{EntityTNTPrimed, EntityXPOrb, EntityItem}
import net.minecraft.entity.monster.EntityCreeper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.{EntityLargeFireball, EntitySmallFireball, EntityArrow}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.{StatCollector, AxisAlignedBB, ChatComponentText}
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

    override var capacity: Int = 8000
    override var maxExtract: Int = 100
    override var maxReceive: Int = 100

    setMaxStackSize(1)
    setCreativeTab(NeoTech.tabNeoTech)
    setMaxStackSize(maxStackSize)
    setUnlocalizedName(Reference.MOD_ID + ":" + "electroMagnet")

    override def onCreated(stack: ItemStack, worldIn: World, player: EntityPlayer): Unit = {
        if (stack.hasTagCompound && stack.getTagCompound.hasKey("Energy"))
            updateDamage(stack)
        else {
            val nbt = new NBTTagCompound
            nbt.setInteger("Energy", 0)
            stack.setTagCompound(nbt)
        }
    }

    override def onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack = {
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
                    player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("neotech.text.magnetism") + ": " +
                            (if(!active) GuiColor.GREEN + StatCollector.translateToLocal("neotech.text.active")
                            else GuiColor.RED + StatCollector.translateToLocal("neotech.text.disabled"))))
            }
        }
        stack
    }

    override def onUpdate(stack: ItemStack, world: World, entityIn: Entity, itemSlot: Int, isSelected: Boolean): Unit = {
        entityIn match {
            case player: EntityPlayer =>
                if(stack.hasTagCompound) {
                    val energy = stack.getTagCompound.getInteger("Energy")
                    if(stack.getTagCompound.getBoolean("Active") && energy > 0){
                        val bb = AxisAlignedBB.fromBounds(
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

                                // Arrows are already moving, increase pull
                                if(entity.isInstanceOf[EntityArrow])
                                    speed += 0.2F

                                //Check X Position
                                if(entity.posX < player.posX)
                                    entity.motionX += speed
                                else
                                    entity.motionX -= speed

                                //Check Y Position
                                if(entity.posY < player.posY)
                                    entity.motionY += speed
                                else entity.motionY -= speed

                                //Check Z Position
                                if(entity.posZ < player.posZ)
                                    entity.motionZ += speed
                                else
                                    entity.motionZ -= speed
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
            list.add(GuiColor.WHITE + StatCollector.translateToLocal("neotech.text.magnetism"))
            list.add(if (active) GuiColor.GREEN + StatCollector.translateToLocal("neotech.text.active")
            else GuiColor.RED + StatCollector.translateToLocal("neotech.text.disabled"))
        }
        list.add(GuiColor.ORANGE + StatCollector.translateToLocal("neotech.text.redstoneFlux"))
        list.add(NumberFormat.getNumberInstance(Locale.forLanguageTag(Minecraft.getMinecraft.gameSettings.language))
                .format(getEnergyStored(stack)) +
                " / " +
                NumberFormat.getNumberInstance(Locale.forLanguageTag(Minecraft.getMinecraft.gameSettings.language))
                        .format(getMaxEnergyStored(stack)) + " RF")
    }
}
