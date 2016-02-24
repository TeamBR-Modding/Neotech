package com.dyonovan.neotech.tools.tools

import java.util

import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.managers.ItemManager
import com.dyonovan.neotech.tools.ToolHelper
import com.dyonovan.neotech.tools.ToolHelper.ToolType
import com.dyonovan.neotech.tools.ToolHelper.ToolType.ToolType
import com.dyonovan.neotech.tools.modifier.{ModifierAOE, ModifierMiningLevel, ModifierMiningSpeed}
import com.dyonovan.neotech.tools.upgradeitems.UpgradeItemManager
import com.dyonovan.neotech.utils.ClientUtils
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{ItemPickaxe, ItemStack}
import net.minecraft.util.{BlockPos, MovingObjectPosition}
import net.minecraft.world.World

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
class ElectricPickaxe extends ItemPickaxe(ToolHelper.NEOTECH) with BaseElectricTool {

    lazy val RF_PER_BLOCK = 250

    setUnlocalizedName(Reference.MOD_ID + ":electricPickaxe")

    override def onBlockDestroyed(stack: ItemStack, world: World, block: Block, pos: BlockPos, player: EntityLivingBase): Boolean = {
        if (ModifierAOE.getAOELevel(stack) > 0 && player.isInstanceOf[EntityPlayer]) {
            val mop = stack.getItem.asInstanceOf[BaseElectricTool].getMovingObjectPositionFromPlayer(world, player.asInstanceOf[EntityPlayer], useLiquids = false)
            if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                val blockList = ToolHelper.getBlockList(ModifierAOE.getAOELevel(stack), mop, pos, player.asInstanceOf[EntityPlayer])
                for (b <- 0 until blockList.size) {
                    val newPos = blockList.get(b)
                    val block = world.getBlockState(newPos).getBlock
                    if (!block.isAir(world, newPos)) {
                        if (block.canHarvestBlock(world, newPos, player.asInstanceOf[EntityPlayer])) {
                            block.harvestBlock(world, player.asInstanceOf[EntityPlayer], newPos, block.getDefaultState, world.getTileEntity(newPos))
                            world.setBlockToAir(newPos)
                            rfCost(player.asInstanceOf[EntityPlayer], stack)
                        }
                    }
                }
            }
        } else rfCost(player.asInstanceOf[EntityPlayer], stack)
        true
    }

    def rfCost(player: EntityPlayer, stack: ItemStack): Unit = {
        if (!player.capabilities.isCreativeMode) {
            extractEnergy(stack, RF_PER_BLOCK, simulate = false)
            updateDamage(stack)
        }
    }

    override def onBlockStartBreak(stack: ItemStack, pos: BlockPos, player: EntityPlayer): Boolean = {
        if (!player.capabilities.isCreativeMode)
            getEnergyStored(stack) < RF_PER_BLOCK
        else true
    }

    override def getHarvestLevel(stack: ItemStack, toolClass: String): Int = {
        ModifierMiningLevel.getMiningLevel(stack)
    }

    override def getDigSpeed(stack: ItemStack, state: IBlockState): Float = {
        ModifierMiningSpeed.getMiningSpeed(stack)
    }

    override def getToolName: String = "pickaxe"

    override def getToolType: ToolType = ToolType.Pickaxe

    override def getBaseTexture: String = ClientUtils.prefixResource("items/tools/pickaxe/electricPickaxe", doLowerCase = false)

    override def acceptableUpgrades: util.ArrayList[String] = new util.ArrayList[String](util.Arrays.asList(
        UpgradeItemManager.upgradeMiningLevel.getUpgradeName, UpgradeItemManager.upgradeSilkTouch.getUpgradeName,
        UpgradeItemManager.upgradeFortune.getUpgradeName, UpgradeItemManager.upgradeMiningSpeed.getUpgradeName,
        UpgradeItemManager.upgradeAOE.getUpgradeName, ItemManager.basicRFBattery.getUpgradeName
    ))

    /**
      * Used to get the upgrade count on this item, mainly used in the motherboard to determine how long to cook
      *
      * @param stack ItemStack
      * @return
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
