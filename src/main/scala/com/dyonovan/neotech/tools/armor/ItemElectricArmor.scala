package com.dyonovan.neotech.tools.armor

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.tools.ToolHelper
import com.teambr.bookshelf.common.items.traits.ItemBattery
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{ItemArmor, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import org.lwjgl.input.Keyboard

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis "pauljoda"
  * @since 3/3/2016
  */
class ItemElectricArmor(name : String, index : Int, armorType : Int) extends
        ItemArmor(ToolHelper.NEOTECH_ARMOR, index, armorType) with ItemBattery {

    setUnlocalizedName(Reference.MOD_ID + ":" + name)
    setCreativeTab(NeoTech.tabTools)
    setNoRepair()

    override def setDefaultTags(stack: ItemStack): Unit = {
        val tag = new NBTTagCompound
        tag.setInteger("Energy", 0)
        tag.setInteger("EnergyCapacity", 2500)
        tag.setInteger("MaxExtract", 200)
        tag.setInteger("MaxReceive", 200)
        stack.setTagCompound(tag)
    }

    override def onArmorTick(world: World, player: EntityPlayer, itemStack: ItemStack): Unit = {
        if(world.isRemote) {
            if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                player.motionY += 0.2
            }
        }
    }

    /**
      * Prevent the armor from being broken
      *
      * @param stack The stack
      * @param damage Damage to apply
      */
    override def setDamage(stack: ItemStack, damage: Int) : Unit = {
        // Drain the energy
        if (stack.getTagCompound == null || !stack.getTagCompound.hasKey("Energy")) {
            var energy = stack.getTagCompound.getInteger("Energy")
            val energyExtracted = Math.min(energy, Math.min(stack.getTagCompound.getInteger("MaxExtract"), 1000))
            energy -= energyExtracted
            stack.getTagCompound.setInteger("Energy", energy)
        }

        val scaled = getEnergyStored(stack).toFloat / getMaxEnergyStored(stack)
        var toSet = 16 - Math.round(scaled * 16)
        if (scaled < 1 && toSet == 0)
            toSet = 1
        else if(toSet == 16)
            toSet = 15
        stack.setItemDamage(toSet)
    }
}
