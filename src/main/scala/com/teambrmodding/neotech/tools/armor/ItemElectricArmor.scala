package com.teambrmodding.neotech.tools.armor

import java.util

import com.teambrmodding.neotech.events.PlayerUpdateEvent
import com.teambrmodding.neotech.lib.Reference
import com.teambrmodding.neotech.managers.ItemManager
import com.teambrmodding.neotech.network.{DrainEnergyPacketArmor, PacketDispatcher, ResetFallDistance, SpawnJetpackParticles}
import com.teambrmodding.neotech.tools.ToolHelper
import com.teambrmodding.neotech.tools.ToolHelper.ToolType
import com.teambrmodding.neotech.tools.ToolHelper.ToolType.ToolType
import com.teambrmodding.neotech.tools.modifier.ModifierFallResist.ItemModifierFallResist
import com.teambrmodding.neotech.tools.modifier.ModifierGlide.ItemModifierGlide
import com.teambrmodding.neotech.tools.modifier.ModifierHover.ItemModifierHover
import com.teambrmodding.neotech.tools.modifier.ModifierJetpack.ItemModifierJetpack
import com.teambrmodding.neotech.tools.modifier.ModifierNightVision.ItemModifierNightVision
import com.teambrmodding.neotech.tools.modifier.ModifierProtection.ItemModifierProtection
import com.teambrmodding.neotech.tools.modifier.ModifierSprinting.ItemModifierSprinting
import com.teambrmodding.neotech.tools.modifier._
import com.teambrmodding.neotech.tools.tools.BaseElectricTool
import com.teambrmodding.neotech.tools.upgradeitems.BaseUpgradeItem
import com.teambrmodding.neotech.utils.ClientUtils
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.MobEffects
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.{ItemArmor, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.potion.{Potion, PotionEffect}
import net.minecraft.world.World
import net.minecraftforge.fml.client.FMLClientHandler
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
class ItemElectricArmor(name : String, index : Int, armorType : EntityEquipmentSlot) extends
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
        var tier = 1
        if (stack.hasTagCompound && stack.getTagCompound.hasKey("Tier"))
            tier = stack.getTagCompound.getInteger("Tier")
        val amount = getTierPower(tier)
        val tag = new NBTTagCompound
        tag.setInteger("EnergyCapacity", amount._1)
        tag.setInteger("MaxExtract", amount._2)
        tag.setInteger("MaxReceive", amount._2)
        tag.setInteger("Tier", tier)
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
        if (stack.getTagCompound != null && stack.getTagCompound.hasKey("Energy")) {
            var energy = stack.getTagCompound.getInteger("Energy")
            val energyExtracted = Math.min(energy, Math.min(stack.getTagCompound.getInteger("MaxExtract"), if (damage > 0) damage else 0))
            energy -= energyExtracted
            stack.getTagCompound.setInteger("Energy", energy)
        }

        val scaled = getEnergyStored(stack).toFloat / getMaxEnergyStored(stack)
        val toSet = 16 - Math.round(scaled * 16)
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
            // Jetpack
            if(ModifierFallResist.hasFallResist(itemStack))
                player.fallDistance = 0

            if (FMLClientHandler.instance().getClient.gameSettings.keyBindJump.isKeyDown &&
                    ModifierJetpack.hasJetpack(itemStack) && getEnergyStored(itemStack) > RF_COST(itemStack)) {
                player.motionY += 0.2
                player.fallDistance = 0

                PacketDispatcher.net.sendToServer(new SpawnJetpackParticles(player))

                if (!player.capabilities.isCreativeMode) {
                    PacketDispatcher.net.sendToServer(new DrainEnergyPacketArmor(armorType.getIndex, 50))
                    if(player.motionY > -1)
                        PacketDispatcher.net.sendToServer(new ResetFallDistance)
                }
            }

            if(getEnergyStored(itemStack) > 1 &&
                    ClientUtils.isCtrlPressed && ModifierHover.hasHover(itemStack) && !player.onGround) {
                player.motionY = 0
                if (!player.capabilities.isCreativeMode) {
                    PacketDispatcher.net.sendToServer(new DrainEnergyPacketArmor(armorType.getIndex, 1))
                    if(player.motionY > -1)
                        PacketDispatcher.net.sendToServer(new ResetFallDistance)
                }
                PacketDispatcher.net.sendToServer(new SpawnJetpackParticles(player))
            }

            // Glider
            if (armorType == EntityEquipmentSlot.CHEST &&
                    getEnergyStored(itemStack) > 5 && ModifierGlide.hasGlide(itemStack) &&
                    Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
                val b0 = player.getDataManager.get(Entity.FLAGS).asInstanceOf[Byte].byteValue
                player.getDataManager.set(Entity.FLAGS, java.lang.Byte.valueOf((b0 | 1 << 7).toByte))
                if (!player.capabilities.isCreativeMode) {
                    PacketDispatcher.net.sendToServer(new DrainEnergyPacketArmor(armorType.getIndex, 1))
                    PacketDispatcher.net.sendToServer(new ResetFallDistance)
                }
            } else if(armorType == EntityEquipmentSlot.CHEST) {
                val b0 = player.getDataManager.get(Entity.FLAGS).asInstanceOf[Byte].byteValue
                player.getDataManager.set(Entity.FLAGS, java.lang.Byte.valueOf((b0 & ~(1 << 7)).toByte))
            }
        }

        // Night Vision
        if(getEnergyStored(itemStack) > 5 && itemStack.getItem == ItemManager.electricArmorHelmet &&
                ModifierNightVision.hasNightVision(itemStack)) {
            player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 220, 0, false, false))
            if (!player.capabilities.isCreativeMode && !world.isRemote) {
                extractEnergy(itemStack, 1, simulate = false)
                updateDamage(itemStack)
            }
        } else if(itemStack.getItem == ItemManager.electricArmorHelmet) {
            player.removePotionEffect(MobEffects.NIGHT_VISION)
        }

        // Sprinting
        if(getEnergyStored(itemStack) > 1 && itemStack.getItem == ItemManager.electricArmorLeggings &&
                ModifierSprinting.getSprintingLevel(itemStack) > 0) {
            if(world.isRemote) {
                PlayerUpdateEvent.dontChangeFOV = true
                PlayerUpdateEvent.previousFOV = Minecraft.getMinecraft.gameSettings.fovSetting
            }
            player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 20,
                ModifierSprinting.getSprintingLevel(itemStack) * 10 - 1, false, false))
            if (!player.capabilities.isCreativeMode && !world.isRemote) {
                extractEnergy(itemStack, 1, simulate = false)
                updateDamage(itemStack)
            }
        } else if(itemStack.getItem == ItemManager.electricArmorLeggings) {
            player.removePotionEffect(MobEffects.SPEED)
        }

        if(player.isElytraFlying)
            player.fallDistance = 0
    }

    def addPotionToPlayer(player : EntityPlayer, potion : Potion, amplifier : Int): Unit = {
        if(player.getActivePotionEffect(potion) == null)
            player.addPotionEffect(new PotionEffect(potion, 220, amplifier, true, false))
    }

    /*******************************************************************************************************************
      * Thermal Binder Item                                                                                            *
      ******************************************************************************************************************/

    /**
      * The list of things that are accepted by this item
      */
    override def acceptableUpgrades: util.ArrayList[String] = {
        val list = new util.ArrayList[String]()
        3 - armorType.getIndex match {
            case 0 =>
                list.add(ItemManager.itemRegistry.get(classOf[ItemModifierNightVision]).asInstanceOf[BaseUpgradeItem].getUpgradeName)
            case 1 =>
                list.add(ItemManager.itemRegistry.get(classOf[ItemModifierJetpack]).asInstanceOf[BaseUpgradeItem].getUpgradeName)
                list.add(ItemManager.itemRegistry.get(classOf[ItemModifierGlide]).asInstanceOf[BaseUpgradeItem].getUpgradeName)
                list.add(ItemManager.itemRegistry.get(classOf[ItemModifierHover]).asInstanceOf[BaseUpgradeItem].getUpgradeName)
            case 2 =>
                list.add(ItemManager.itemRegistry.get(classOf[ItemModifierSprinting]).asInstanceOf[BaseUpgradeItem].getUpgradeName)
            case 3 =>
                list.add(ItemManager.itemRegistry.get(classOf[ItemModifierFallResist]).asInstanceOf[BaseUpgradeItem].getUpgradeName)
            case _=>
        }
        list.add(ItemManager.basicRFBattery.getUpgradeName)
        list.add(ItemManager.itemRegistry.get(classOf[ItemModifierProtection]).asInstanceOf[BaseUpgradeItem].getUpgradeName)
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
