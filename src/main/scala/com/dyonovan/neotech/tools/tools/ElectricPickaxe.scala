package com.dyonovan.neotech.tools.tools

import java.util

import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.managers.ItemManager
import com.dyonovan.neotech.tools.{ToolHelper, UpgradeItemManager}
import com.dyonovan.neotech.tools.ToolHelper.ToolType
import com.dyonovan.neotech.tools.ToolHelper.ToolType.ToolType
import com.dyonovan.neotech.tools.modifier.ModifierAOE._
import com.dyonovan.neotech.tools.modifier.{ModifierAOE, ModifierMiningLevel, ModifierMiningSpeed, ModifierShovel}
import com.dyonovan.neotech.utils.ClientUtils
import gnu.trove.map.hash.THashMap
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{ItemPickaxe, ItemStack}
import net.minecraft.util.{BlockPos, MovingObjectPosition}
import net.minecraft.world.World
import net.minecraftforge.common.ForgeHooks

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


    setUnlocalizedName(Reference.MOD_ID + ":electricPickaxe")

    /*******************************************************************************************************************
      **************************************** BaseElectricTool Functions **********************************************
      ******************************************************************************************************************/

    /**
      * The tool name of this tool, should be all lower case and used when getting the tool info
      *
      * @return The tool name
      */
    override def getToolName: String = "pickaxe"

    /**
      * Used by the model to get the base texture, this should be with no upgrades installed
      *
      * @return The texture location, eg "neotech:items/tools/sword/sword
      */
    override def getBaseTexture: String = ClientUtils.prefixResource("items/tools/pickaxe/electricPickaxe")

    /**
      * This will allow us to add more tool classes based on the stack
      *
      * @param stack The stack in
      * @return The list of effective tools
      */
    override def getToolClasses(stack: ItemStack): java.util.Set[String] = {
        val defaults = new THashMap[String, Integer]()
        defaults.put("pickaxe", ModifierMiningLevel.getMiningLevel(stack))
        if(ModifierShovel.hasShovelUpgrade(stack))
            defaults.put("shovel", ModifierMiningLevel.getMiningLevel(stack))
        defaults.keySet()
    }

    /*******************************************************************************************************************
      *************************************** ThermalBinderItem Functions **********************************************
      ******************************************************************************************************************/

    /**
      * What type of tool is this?
      *
      * @return
      */
    override def getToolType: ToolType = ToolType.Pickaxe

    /**
      * The list of things that are accepted by this item
      */
    override def acceptableUpgrades: util.ArrayList[String] = new util.ArrayList[String](util.Arrays.asList(
        UpgradeItemManager.upgradeMiningLevel2.getUpgradeName, UpgradeItemManager.upgradeMiningLevel3.getUpgradeName,
        UpgradeItemManager.upgradeMiningLevel4.getUpgradeName, UpgradeItemManager.upgradeSilkTouch.getUpgradeName,
        UpgradeItemManager.upgradeFortune.getUpgradeName, UpgradeItemManager.upgradeMiningSpeed.getUpgradeName,
        UpgradeItemManager.upgradeAOE.getUpgradeName, ItemManager.basicRFBattery.getUpgradeName,
        UpgradeItemManager.upgradeShovel.getUpgradeName
    ))

    /*******************************************************************************************************************
      ****************************************** Item/Tool Functions ***************************************************
      ******************************************************************************************************************/

    /**
      * Get the harvest level of this item
      */
    override def getHarvestLevel(stack: ItemStack, toolClass: String): Int =
        if(super.getHarvestLevel(stack, toolClass) != -1) ModifierMiningLevel.getMiningLevel(stack) else super.getHarvestLevel(stack, toolClass)

    /**
      * Get the dig speed
      */
    override def getDigSpeed(stack: ItemStack, state: IBlockState): Float =
        if(super.getDigSpeed(stack, state) > 1.0F) ModifierMiningSpeed.getMiningSpeed(stack) else super.getDigSpeed(stack, state)

    /**
      * Lets us set creative able to break, also checks if has power
      */
    override def onBlockStartBreak(stack: ItemStack, pos: BlockPos, player: EntityPlayer): Boolean = {
        if (!player.capabilities.isCreativeMode)
            return getEnergyStored(stack) < RF_COST(stack)
        else if (player.capabilities.isCreativeMode) {
            val world = player.worldObj
            val mop = getMovingObjectPositionFromPlayer(world, player.asInstanceOf[EntityPlayer], false)
            if (mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                val blockList = ToolHelper.getBlockList(ModifierAOE.getAOELevel(stack), mop, player.asInstanceOf[EntityPlayer], world, stack)
                for (b <- 0 until blockList.size) {
                    val newPos = blockList.get(b)
                    val block = world.getBlockState(newPos).getBlock
                    world.setBlockToAir(newPos)
                    if(newPos != pos)
                        world.playAuxSFX(2001, newPos, Block.getIdFromBlock(block))
                }
            }
        }
        false
    }

    /**
      * When the block is broken, apply AOE here
      */
    override def onBlockDestroyed(stack: ItemStack, world: World, block: Block, pos: BlockPos, player: EntityLivingBase): Boolean = {
        if (ModifierAOE.getAOELevel(stack) > 0 && player.isInstanceOf[EntityPlayer] && ModifierAOE.isAOEActive(stack)) {
            val mop = getMovingObjectPositionFromPlayer(world, player.asInstanceOf[EntityPlayer], false)
            if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                val blockList = ToolHelper.getBlockList(ModifierAOE.getAOELevel(stack), mop, player.asInstanceOf[EntityPlayer], world, stack)
                for (b <- 0 until blockList.size) {
                    val newPos = blockList.get(b)
                    val block = world.getBlockState(newPos).getBlock
                    if (ForgeHooks.isToolEffective(world, newPos, stack) && block.canHarvestBlock(world, newPos, player.asInstanceOf[EntityPlayer]) || player.asInstanceOf[EntityPlayer].capabilities.isCreativeMode) {
                        if (!player.asInstanceOf[EntityPlayer].capabilities.isCreativeMode)
                            block.harvestBlock(world, player.asInstanceOf[EntityPlayer], newPos, block.getDefaultState, world.getTileEntity(newPos))
                        world.setBlockToAir(newPos)
                        if (newPos != pos)
                            world.playAuxSFX(2001, newPos, Block.getIdFromBlock(block))
                    }
                    rfCost(player.asInstanceOf[EntityPlayer], stack)
                }
            }
        } else rfCost(player.asInstanceOf[EntityPlayer], stack)
        true
    }

    /***
      * Set AOE to true
      */
    override def onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack = {
        if (ModifierAOE.getAOELevel(stack) > 0 && player.isSneaking) {
            val tag = ModifierAOE.getModifierTagFromStack(stack)
            if (tag != null && tag.hasKey(ACTIVE)) {
                tag.setBoolean(ACTIVE, !tag.getBoolean(ACTIVE))
                overrideModifierTag(stack, tag)
            }
        }
        stack
    }
}
