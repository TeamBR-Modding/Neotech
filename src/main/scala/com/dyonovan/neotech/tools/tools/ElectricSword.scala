package com.dyonovan.neotech.tools.tools

import java.util

import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.managers.ItemManager
import com.dyonovan.neotech.tools.ToolHelper
import com.dyonovan.neotech.tools.ToolHelper.ToolType
import com.dyonovan.neotech.tools.ToolHelper.ToolType.ToolType
import com.dyonovan.neotech.tools.upgradeitems.UpgradeItemManager
import com.dyonovan.neotech.utils.ClientUtils
import net.minecraft.block.Block
import net.minecraft.entity.{EntityLivingBase, Entity}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{ItemStack, ItemSword}
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraft.util.BlockPos
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
  * @since 2/24/2016
  */
class ElectricSword extends ItemSword(ToolHelper.NEOTECH) with BaseElectricTool {

    setUnlocalizedName(Reference.MOD_ID + ":electricSword")

    override def getToolName: String = "sword"
    override def getBaseTexture: String = ClientUtils.prefixResource("items/tools/sword/electricSword", doLowerCase = false)

    override def hitEntity(stack: ItemStack, target: EntityLivingBase, attacker: EntityLivingBase) : Boolean = {
        extractEnergy(stack, 250, simulate = false) > 0
    }

    override def onBlockDestroyed(stack: ItemStack, worldIn: World, blockIn: Block,
                                  pos: BlockPos, playerIn: EntityLivingBase) : Boolean = true

    override def onUpdate(stack: ItemStack, worldIn: World, entityIn: Entity, itemSlot: Int, isSelected: Boolean): Unit = {
        if(!stack.hasTagCompound) {
            val tagCompound = new NBTTagCompound
            val tagList = new NBTTagList
            tagCompound.setTag(ToolHelper.ModifierListTag, tagList)
            tagCompound.setInteger("EnergyCapacity", 25000)
            stack.setTagCompound(tagCompound)
        }
        capacity = stack.getTagCompound.getInteger("EnergyCapacity")
    }

    override def onCreated(stack: ItemStack, worldIn: World, player: EntityPlayer): Unit = {
        if (stack.hasTagCompound) {
            if (stack.getTagCompound.hasKey("Energy"))
                updateDamage(stack)
        } else {
            // Set empty modifier list
            val tagCompound = new NBTTagCompound
            val tagList = new NBTTagList
            tagCompound.setTag(ToolHelper.ModifierListTag, tagList)
            stack.setTagCompound(tagCompound)
        }
    }

    /**
      * The list of things that are accepted by this item
      */
    override def acceptableUpgrades: util.ArrayList[String] = new util.ArrayList[String](util.Arrays.asList(
        UpgradeItemManager.upgradeSharpness.getUpgradeName, UpgradeItemManager.upgradeSmite.getUpgradeName,
        ItemManager.basicRFBattery.getUpgradeName
    ))

    /**
      * What type of tool is this?
      *
      * @return
      */
    override def getToolType: ToolType = ToolType.Sword

    /**
      * Used to get the upgrade count on this item, mainly used in the motherboard to determine how long to cook
      *
      * @param stack
      * @return
      */
    override def getUpgradeCount(stack: ItemStack): Int = {
        if(stack.hasTagCompound && stack.getTagCompound.hasKey(ToolHelper.ModifierListTag)) {
            val tagList = stack.getTagCompound.getTagList(ToolHelper.ModifierListTag, 10)
            var count = 0
            for(x <- 0 until tagList.tagCount())
                count += tagList.getCompoundTagAt(x).getInteger("ModifierLevel")
            return count
        }
        0
    }

    @SideOnly(Side.CLIENT)
    override def addInformation(stack: ItemStack, player: EntityPlayer, list: java.util.List[String], boolean: Boolean): Unit = {
        if(stack.hasTagCompound)
            list.add(ClientUtils.formatNumber(getEnergyStored(stack)) + " / " + ClientUtils.formatNumber(getMaxEnergyStored(stack)) + " RF")
        list.add("")
        list.add("Upgrades: " + ToolHelper.getCurrentUpgradeCount(stack) + " / " + getMaximumUpgradeCount(stack))
        for(string <- ToolHelper.getToolTipForDisplay(stack))
            list.add(string)
    }
}
