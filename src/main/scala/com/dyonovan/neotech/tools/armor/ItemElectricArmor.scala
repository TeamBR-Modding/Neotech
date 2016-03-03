package com.dyonovan.neotech.tools.armor

import java.util

import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.managers.ItemManager
import com.dyonovan.neotech.tools.ToolHelper.ToolType
import com.dyonovan.neotech.tools.ToolHelper.ToolType.ToolType
import com.dyonovan.neotech.tools.modifier.{ModifierGlide, ModifierFallResist, ModifierJetpack}
import com.dyonovan.neotech.tools.tools.BaseElectricTool
import com.dyonovan.neotech.tools.{ToolHelper, UpgradeItemManager}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{ItemArmor, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraftforge.fml.client.FMLClientHandler

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
        ItemArmor(ToolHelper.NEOTECH_ARMOR, index, armorType) with BaseElectricTool {

    setUnlocalizedName(Reference.MOD_ID + ":" + name)
    setNoRepair()

    /*******************************************************************************************************************
      * Item Battery                                                                                                   *
      ******************************************************************************************************************/

    override def setDefaultTags(stack: ItemStack): Unit = {
        var energy = 0
        if (stack.hasTagCompound && stack.getTagCompound.hasKey("Energy"))
            energy = stack.getTagCompound.getInteger("Energy")
        val tag = new NBTTagCompound
        tag.setInteger("Energy", energy)
        tag.setInteger("EnergyCapacity", 2500)
        tag.setInteger("MaxExtract", 200)
        tag.setInteger("MaxReceive", 200)
        stack.setTagCompound(tag)
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
            val energyExtracted = Math.min(energy, Math.min(stack.getTagCompound.getInteger("MaxExtract"), 250))
            energy -= energyExtracted
            stack.getTagCompound.setInteger("Energy", energy)
        }

        val scaled = getEnergyStored(stack).toFloat / getMaxEnergyStored(stack)
        var toSet = 16 - Math.round(scaled * 16)
        if (scaled < 1 && toSet == 0)
            toSet = 1
        else if(toSet == 16)
            toSet = 15
        super.setDamage(stack, toSet)
    }

    /*******************************************************************************************************************
      * Armor                                                                                                          *
      ******************************************************************************************************************/

    override def onArmorTick(world: World, player: EntityPlayer, itemStack: ItemStack): Unit = {
        if(!world.isRemote) {
            if(ModifierFallResist.hasFallResist(itemStack))
                player.fallDistance = 0
        } else {
            if(ModifierFallResist.hasFallResist(itemStack))
                player.fallDistance = 0

            if (FMLClientHandler.instance().getClient.gameSettings.keyBindJump.isKeyDown &&
                    ModifierJetpack.hasJetpack(itemStack) && getEnergyStored(itemStack) > RF_COST(itemStack)) {
                player.motionY += 0.2
                if (!player.capabilities.isCreativeMode) {
                    extractEnergy(itemStack, 1, simulate = false)
                    updateDamage(itemStack)
                }
            }
        }

        if (ModifierGlide.hasGlide(itemStack) && player.motionY < -0.1 && player.isSneaking) {
            var horizontalSpeed: Double = 0
            var verticalSpeed: Double = 0

            horizontalSpeed = 0.2
            verticalSpeed = 0.4

            player.motionY *= verticalSpeed
            player.motionX += Math.cos(Math.toRadians(player.rotationYawHead + 90)) * horizontalSpeed
            player.motionZ += Math.sin(Math.toRadians(player.rotationYawHead + 90)) * horizontalSpeed
            player.fallDistance = 0F
        }
    }

    /*******************************************************************************************************************
      * Thermal Binder Item                                                                                            *
      ******************************************************************************************************************/

    /**
      * The list of things that are accepted by this item
      */
    override def acceptableUpgrades: util.ArrayList[String] = {
        val list = new util.ArrayList[String]()
        armorType match {
            case 1 =>
                list.add(UpgradeItemManager.upgradeJetpack.getUpgradeName)
                list.add(UpgradeItemManager.upgradeGlide.getUpgradeName)
            case 3 =>
                list.add(UpgradeItemManager.upgradeFallResist.getUpgradeName)
            case _=>
        }
        list.add(ItemManager.basicRFBattery.getUpgradeName)
        list
    }

    /**
      * What type of tool is this?
      *
      * @return
      */
    override def getToolType: ToolType = ToolType.ARMOR

    /**
      * The tool name of this tool, should be all lower case and used when getting the tool info
      *
      * @return The tool name
      */
    override def getToolName: String = "armor"

    /**
      * Used by the model to get the base texture, this should be with no upgrades installed
      *
      * @return The texture location, eg "neotech:items/tools/sword/sword
      */
    override def getBaseTexture: String = ""
}
