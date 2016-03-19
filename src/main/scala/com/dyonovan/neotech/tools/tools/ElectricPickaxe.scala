package com.dyonovan.neotech.tools.tools

import java.util

import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.managers.{BlockManager, ItemManager}
import com.dyonovan.neotech.tools.ToolHelper.ToolType
import com.dyonovan.neotech.tools.ToolHelper.ToolType.ToolType
import com.dyonovan.neotech.tools.modifier.ModifierAOE.ItemModifierAOE
import com.dyonovan.neotech.tools.modifier.ModifierFortune.ItemModifierFortune
import com.dyonovan.neotech.tools.modifier.ModifierLighting.ItemModifierLighting
import com.dyonovan.neotech.tools.modifier.ModifierMiningSpeed.ItemModifierMiningSpeed
import com.dyonovan.neotech.tools.modifier.ModifierShovel.ItemModifierShovel
import com.dyonovan.neotech.tools.modifier.ModifierSilkTouch.ItemModifierSilkTouch
import com.dyonovan.neotech.tools.modifier._
import com.dyonovan.neotech.tools.upgradeitems.BaseUpgradeItem
import com.dyonovan.neotech.tools.{ToolHelper, UpgradeItemManager}
import com.dyonovan.neotech.utils.ClientUtils
import gnu.trove.map.hash.THashMap
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.item.{ItemPickaxe, ItemStack}
import net.minecraft.util.{EnumActionResult, EnumHand, EnumFacing}
import net.minecraft.util.math.{RayTraceResult, BlockPos}
import net.minecraft.world.World

import scala.collection.JavaConversions._

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
class ElectricPickaxe extends ItemPickaxe(ToolHelper.NEOTECH_TOOLS) with BaseElectricTool {

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
        UpgradeItemManager.upgradeMiningLevel4.getUpgradeName, ItemManager.basicRFBattery.getUpgradeName,
        ItemManager.itemRegistry.get(classOf[ItemModifierSilkTouch]).asInstanceOf[BaseUpgradeItem].getUpgradeName,
        ItemManager.itemRegistry.get(classOf[ItemModifierFortune]).asInstanceOf[BaseUpgradeItem].getUpgradeName,
        ItemManager.itemRegistry.get(classOf[ItemModifierMiningSpeed]).asInstanceOf[BaseUpgradeItem].getUpgradeName,
        ItemManager.itemRegistry.get(classOf[ItemModifierAOE]).asInstanceOf[BaseUpgradeItem].getUpgradeName,
        ItemManager.itemRegistry.get(classOf[ItemModifierShovel]).asInstanceOf[BaseUpgradeItem].getUpgradeName,
        ItemManager.itemRegistry.get(classOf[ItemModifierLighting]).asInstanceOf[BaseUpgradeItem].getUpgradeName
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
    override def getStrVsBlock(stack: ItemStack, state: IBlockState): Float =
        if(super.getStrVsBlock(stack, state) > 1.0F) ModifierMiningSpeed.getMiningSpeed(stack) else super.getStrVsBlock(stack, state)

    /**
      * Lets us set creative able to break, also checks if has power
      */
    override def onBlockStartBreak(stack: ItemStack, pos: BlockPos, player: EntityPlayer): Boolean = {
        if (!player.capabilities.isCreativeMode)
            return getEnergyStored(stack) < RF_COST(stack)
        else if (player.capabilities.isCreativeMode) {
            val world = player.worldObj
            val mop = getMovingObjectPositionFromPlayer(world, player.asInstanceOf[EntityPlayer], false)
            if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
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
    override def onBlockDestroyed(stack: ItemStack, world: World, blockIn: IBlockState, pos: BlockPos, entityLiving: EntityLivingBase): Boolean = {
        entityLiving match {
            case player: EntityPlayer =>
                if (ModifierAOE.getAOELevel(stack) > 0 && player.isInstanceOf[EntityPlayer] && ModifierAOE.isAOEActive(stack)) {
                    val mop = getMovingObjectPositionFromPlayer(world, player.asInstanceOf[EntityPlayer], false)
                    if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
                        val blockList = ToolHelper.getBlockList(ModifierAOE.getAOELevel(stack),
                            mop, player.asInstanceOf[EntityPlayer], world, stack)
                        for (newPos <- blockList) {
                            val block = world.getBlockState(newPos).getBlock
                            if (block.canHarvestBlock(world, newPos, player.asInstanceOf[EntityPlayer]) && ToolHelper.isToolEffective(world, newPos, stack)
                                    || player.asInstanceOf[EntityPlayer].capabilities.isCreativeMode) {
                                if (!player.asInstanceOf[EntityPlayer].capabilities.isCreativeMode)
                                    block.harvestBlock(world, player.asInstanceOf[EntityPlayer],
                                        newPos, world.getBlockState(newPos), world.getTileEntity(newPos), stack)
                                world.setBlockToAir(newPos)
                                if (!world.isRemote && newPos != pos)
                                    world.playAuxSFX(2001, newPos, Block.getIdFromBlock(block))
                            }
                            rfCost(player.asInstanceOf[EntityPlayer], stack)
                        }
                    }
                } else rfCost(player.asInstanceOf[EntityPlayer], stack)
                true
            case _ => true
        }
    }

    override def onItemUse(stack: ItemStack, playerIn: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand,
                           facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) : EnumActionResult = {
        val position = pos.offset(facing)
        if(getEnergyStored(stack) > RF_COST(stack) &&
                ModifierLighting.hasLighting(stack) && ModifierLighting.isLightingActive(stack)
                && worldIn.getBlockState(position).getBlock.isAir(worldIn.getBlockState(position), worldIn, position)) {
           // worldIn.playSoundAtEntity(playerIn, "random.wood_click", 1.0F, 1.0F)
            worldIn.setBlockState(position, BlockManager.lightSource.getDefaultState)
            worldIn.setBlockState(position, worldIn.getBlockState(position), 3)
            rfCost(playerIn, stack)
            return EnumActionResult.SUCCESS
        }
        EnumActionResult.PASS
    }

    override def onUpdate(stack: ItemStack, worldIn: World, entityIn: Entity, itemSlot: Int, isSelected: Boolean): Unit = {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected)
        if(ModifierLighting.hasLighting(stack) && entityIn.isInstanceOf[EntityPlayer]) {
            val pos = new BlockPos(entityIn.posX.toInt, entityIn.posY.toInt, entityIn.posZ.toInt)
            if(getEnergyStored(stack) > RF_COST(stack) &&
                    worldIn.getLightBrightness(pos) < 0.5 &&
                    ModifierLighting.isLightingActive(stack) &&
                    worldIn.getBlockState(pos).getBlock.isAir(worldIn.getBlockState(pos), worldIn, pos) &&
                    worldIn.getBlockState(pos).getBlock != BlockManager.lightSource) {
                worldIn.setBlockState(pos, BlockManager.lightSource.getDefaultState, 3)
                rfCost(entityIn.asInstanceOf[EntityPlayer], stack)
            }
        }
    }
}
