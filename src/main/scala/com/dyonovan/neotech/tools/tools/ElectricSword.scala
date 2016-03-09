package com.dyonovan.neotech.tools.tools

import java.util

import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.managers.ItemManager
import com.dyonovan.neotech.tools.ToolHelper
import com.dyonovan.neotech.tools.ToolHelper.ToolType
import com.dyonovan.neotech.tools.ToolHelper.ToolType.ToolType
import com.dyonovan.neotech.tools.modifier.ModifierBaneOfArthropods.ItemModifierBaneOfArthropods
import com.dyonovan.neotech.tools.modifier.ModifierBeheading.ItemModifierBeheading
import com.dyonovan.neotech.tools.modifier.ModifierLooting.ItemModifierLooting
import com.dyonovan.neotech.tools.modifier.ModifierSharpness.ItemModifierSharpness
import com.dyonovan.neotech.tools.modifier.ModifierSmite.ItemModifierSmite
import com.dyonovan.neotech.tools.upgradeitems.BaseUpgradeItem
import com.dyonovan.neotech.utils.ClientUtils
import net.minecraft.block.Block
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.{ItemStack, ItemSword}
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraft.util.BlockPos
import net.minecraft.world.World

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
class ElectricSword extends ItemSword(ToolHelper.NEOTECH_TOOLS) with BaseElectricTool {

    setUnlocalizedName(Reference.MOD_ID + ":electricSword")

    /** *****************************************************************************************************************
      * *************************************** BaseElectricTool Functions **********************************************
      * *****************************************************************************************************************/

    /**
      * The tool name of this tool, should be all lower case and used when getting the tool info
      *
      * @return The tool name
      */
    override def getToolName: String = "sword"

    /**
      * Used by the model to get the base texture, this should be with no upgrades installed
      *
      * @return The texture location, eg "neotech:items/tools/sword/sword
      */
    override def getBaseTexture: String = ClientUtils.prefixResource("items/tools/sword/electricSword")

    /**
      * To prevent the stack from taking damage while hitting entities, we must override this method
      */
    override def hitEntity(stack: ItemStack, target: EntityLivingBase, attacker: EntityLivingBase): Boolean =
        extractEnergy(stack, 250, simulate = false) > 0

    /**
      * When the block is broken, apply AOE here
      */
    override def onBlockDestroyed(stack: ItemStack, worldIn: World, blockIn: Block,
                                  pos: BlockPos, playerIn: EntityLivingBase): Boolean = true

    /**
      * Called on tick, allows us to make sure things are installed
      */
    override def setDefaultTags(stack: ItemStack): Unit = {
        var tier = 1
        if (stack.hasTagCompound && stack.getTagCompound.hasKey("Tier"))
            tier = stack.getTagCompound.getInteger("Tier")
        val amount = getTierPower(tier)
        val tagCompound = new NBTTagCompound
        val tagList = new NBTTagList
        tagCompound.setTag(ToolHelper.ModifierListTag, tagList)
        tagCompound.setInteger("EnergyCapacity", amount._1)
        tagCompound.setInteger("MaxExtract", amount._2)
        tagCompound.setInteger("MaxReceive", amount._2)
        tagCompound.setInteger("Tier", tier)
        stack.setTagCompound(tagCompound)
    }

    /** *****************************************************************************************************************
      * ************************************** ThermalBinderItem Functions **********************************************
      * *****************************************************************************************************************/

    /**
      * What type of tool is this?
      *
      * @return
      */
    override def getToolType: ToolType = ToolType.Sword

    /**
      * The list of things that are accepted by this item
      */
    override def acceptableUpgrades: util.ArrayList[String] = new util.ArrayList[String](util.Arrays.asList(
        ItemManager.basicRFBattery.getUpgradeName,
        ItemManager.itemRegistry.get(classOf[ItemModifierSharpness]).asInstanceOf[BaseUpgradeItem].getUpgradeName,
        ItemManager.itemRegistry.get(classOf[ItemModifierSmite]).asInstanceOf[BaseUpgradeItem].getUpgradeName,
        ItemManager.itemRegistry.get(classOf[ItemModifierBeheading]).asInstanceOf[BaseUpgradeItem].getUpgradeName,
        ItemManager.itemRegistry.get(classOf[ItemModifierBaneOfArthropods]).asInstanceOf[BaseUpgradeItem].getUpgradeName,
        ItemManager.itemRegistry.get(classOf[ItemModifierLooting]).asInstanceOf[BaseUpgradeItem].getUpgradeName
    ))

    /**
      * Used to get the upgrade count on this item, mainly used in the motherboard to determine how long to cook
      */
    override def getUpgradeCount(stack: ItemStack): Int = {
        if (stack.hasTagCompound && stack.getTagCompound.hasKey(ToolHelper.ModifierListTag)) {
            val tagList = stack.getTagCompound.getTagList(ToolHelper.ModifierListTag, 10)
            var count = 0
            for (x <- 0 until tagList.tagCount())
                count += tagList.getCompoundTagAt(x).getInteger("ModifierLevel")
            return count
        }
        0
    }
}
