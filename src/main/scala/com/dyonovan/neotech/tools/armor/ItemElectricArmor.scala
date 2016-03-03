package com.dyonovan.neotech.tools.armor

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.tools.ToolHelper
import com.teambr.bookshelf.common.items.traits.ItemBattery
import net.minecraft.item.{ItemStack, ItemArmor}
import net.minecraft.nbt.NBTTagCompound

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
        tag.setInteger("EnergyCapacity", 2500)
        tag.setInteger("MaxExtract", 200)
        tag.setInteger("MaxReceive", 200)
        stack.setTagCompound(tag)
    }
}
