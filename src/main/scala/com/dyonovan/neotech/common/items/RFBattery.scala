package com.dyonovan.neotech.common.items

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.tools.upgradeitems.BaseUpgradeItem
import com.dyonovan.neotech.utils.ClientUtils
import com.teambr.bookshelf.common.items.traits.ItemBattery
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
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
  * @since 2/17/2016
  */
class RFBattery(name: String, tier: Int) extends BaseUpgradeItem("battery", 1) with ItemBattery {

    setMaxStackSize(1)
    setCreativeTab(NeoTech.tabNeoTech)
    setMaxStackSize(maxStackSize)
    setUnlocalizedName(Reference.MOD_ID + ":" + name)

    val tierPower = getTierPower(tier)

    override var capacity: Int = tierPower._1
    override var maxExtract: Int = tierPower._2
    override var maxReceive: Int = tierPower._2

    override def onCreated(stack: ItemStack, worldIn: World, player: EntityPlayer): Unit = {
        if (stack.hasTagCompound && stack.getTagCompound.hasKey("Energy"))
            updateDamage(stack)
    }

    /**
      * Defines amount of power each tier holds
      *
      * @param t Battery Tier
      * @return Touple2(capacity, maxReceive)
      */
    def getTierPower(t: Int): (Int, Int) = {
        t match {
            case 1 => (25000, 200)
            case 2 => (100000, 1000)
            case 3 => (1000000, 10000)
            case _ => (0, 0)
        }
    }

    @SideOnly(Side.CLIENT)
    override def addInformation(stack: ItemStack, player: EntityPlayer, list: java.util.List[String], boolean: Boolean): Unit = {
        list.add(ClientUtils.formatNumber(getEnergyStored(stack)) + " / " + ClientUtils.formatNumber(getMaxEnergyStored(stack)) + " RF")
    }

    /**
      * Can this upgrade item allow more to be applied to the item
      *
      * @param stack The stack we want to apply to, get count from there
      * @param count The stack size of the input
      * @return True if there is space for the entire count
      */
    override def canAcceptLevel(stack: ItemStack, count: Int, name: String): Boolean = true

    /**
      * Use this to put information onto the stack, called when put onto the stack
      *
      * @param stack The stack to put onto
      * @return The tag passed
      */
    override def writeInfoToNBT(stack: ItemStack, tag: NBTTagCompound, writingStack : ItemStack): Unit = {
        stack.getTagCompound.setInteger("EnergyCapacity", capacity)
        stack.getTagCompound.setInteger("MaxReceive", maxReceive)
        stack.getTagCompound.setInteger("MaxExtract", maxExtract)
    }
}
