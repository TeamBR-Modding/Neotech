package com.dyonovan.neotech.common.items.tools

import com.dyonovan.neotech.NeoTech
import com.teambr.bookshelf.client.gui.GuiTextFormat
import com.teambr.bookshelf.common.items.traits.ItemBattery
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.common.util.EnumHelper
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
  * This file was created for Bookshelf API
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 2/21/2016
  */
object BaseElectricTool {
    val NEOTECH = EnumHelper.addToolMaterial("NEOTECH", 1, 1, 4.0F, 1.0F, 0)
}
trait BaseElectricTool extends ItemBattery {

    setCreativeTab(NeoTech.tabNeoTech)
    setMaxStackSize(1)

    override var capacity: Int = 25000
    override var maxExtract: Int = 250
    override var maxReceive: Int = 250

    override def onCreated(stack: ItemStack, worldIn: World, player: EntityPlayer): Unit = {
        if (stack.hasTagCompound) {
            if (stack.getTagCompound.hasKey("Energy"))
                updateDamage(stack)
            if (stack.getTagCompound.hasKey("Tier")) {
                val power = getTierPower(stack.getTagCompound.getInteger("Tier"))
                capacity = power._1
                maxExtract = power._2
                maxReceive = power._2
            }
            val tag = stack.getTagCompound
            tag.setInteger("Harvest", 1)
            stack.setTagCompound(tag)
        }
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

    def getName: String = { getUnlocalizedName }

    @SideOnly(Side.CLIENT)
    override def addInformation(stack: ItemStack, player: EntityPlayer, list: java.util.List[String], boolean: Boolean): Unit = {
        list.add(getEnergyStored(stack) + "/" + getMaxEnergyStored(stack) + " RF")
        var harvestLevel = 1
        if (stack.hasTagCompound && stack.getTagCompound.hasKey("Harvest"))
            harvestLevel = stack.getTagCompound.getInteger("Harvest")
        var strLevel = ""
        harvestLevel match {
            case 3 => strLevel = "Obsidian"
            case 2 => strLevel = "Redstone"
            case 1 => strLevel = "Stone"
            case _ =>
        }
        list.add("Mining Level: " + GuiTextFormat.ITALICS + strLevel)
    }
}
