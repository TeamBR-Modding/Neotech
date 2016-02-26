package com.dyonovan.neotech.tools.tools

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.tools.ToolHelper
import com.dyonovan.neotech.tools.modifier.ModifierAOE._
import com.dyonovan.neotech.tools.modifier.{ModifierAOE, ModifierMiningLevel}
import com.dyonovan.neotech.tools.upgradeitems.ThermalBinderItem
import com.teambr.bookshelf.common.items.traits.ItemBattery
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.item.{EnumRarity, Item, ItemStack}
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraft.world.World
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
trait BaseElectricTool extends Item with ItemBattery with ThermalBinderItem {

    setCreativeTab(NeoTech.tabTools)
    setMaxStackSize(1)

    override var capacity: Int = 25000
    override var maxExtract: Int = 250
    override var maxReceive: Int = 250

    /**
      * The tool name of this tool, should be all lower case and used when getting the tool info
      *
      * @return The tool name
      */
    def getToolName   : String

    /**
      * Used by the model to get the base texture, this should be with no upgrades installed
      *
      * @return The texture location, eg "neotech:items/tools/sword/sword
      */
    def getBaseTexture : String

    /*******************************************************************************************************************
      ****************************************** Item/Tool Functions ***************************************************
      ******************************************************************************************************************/
    /**
      * Used to define how much an operation costs
      *
      * @return How much energy to drain
      */
    def RF_COST(stack : ItemStack) : Int = 250

    /**
      * Extracts the energy, if in creative it won't
      *
      * @param player The player in
      * @param stack The stack
      */
    def rfCost(player: EntityPlayer, stack: ItemStack): Unit = {
        if (!player.capabilities.isCreativeMode) {
            extractEnergy(stack, RF_COST(stack), simulate = false)
            updateDamage(stack)
        }
    }

    /**
      * We want this name to be special
      *
      * @param stack The stack
      * @return The rarity of the item
      */
    override def getRarity(stack: ItemStack): EnumRarity = EnumRarity.RARE

    /**
      * Keeps this from being enchanted
      */
    override def isBookEnchantable(stack: ItemStack, book: ItemStack): Boolean = false

    /**
      * Called on tick, allows us to make sure things are installed
      */
    override def onUpdate(stack: ItemStack, worldIn: World, entityIn: Entity, itemSlot: Int, isSelected: Boolean): Unit = {
        if(!stack.hasTagCompound) {
            val tagCompound = new NBTTagCompound
            val tagList = new NBTTagList
            tagList.appendTag(ModifierMiningLevel.writeToNBT(new NBTTagCompound, stack, 1))
            tagCompound.setTag(ToolHelper.ModifierListTag, tagList)
            tagCompound.setInteger("EnergyCapacity", 25000)
            tagCompound.setInteger("MaxExtract", 200)
            tagCompound.setInteger("MaxReceive", 200)
            stack.setTagCompound(tagCompound)
        }
    }

    /**
      * Called when the stack is created, we use this to set defaults
      */
    override def onCreated(stack: ItemStack, worldIn: World, player: EntityPlayer): Unit = {
        if (stack.hasTagCompound) {
            if (stack.getTagCompound.hasKey("Energy"))
                updateDamage(stack)
            val tag = stack.getTagCompound.getTagList(ToolHelper.ModifierListTag, 10) // Load the modifier list
            if(tag != null && ModifierMiningLevel.getModifierTagFromStack(stack) == null) // Does not already exist
                tag.appendTag(ModifierMiningLevel.writeToNBT(new NBTTagCompound, stack, 1)) // Put mining level one in
            stack.getTagCompound.setTag(ToolHelper.ModifierListTag, tag) // Send back the tag
        } else {
            // Set empty modifier list
            val tagCompound = new NBTTagCompound
            val tagList = new NBTTagList
            tagList.appendTag(ModifierMiningLevel.writeToNBT(new NBTTagCompound, stack, 1))
            tagCompound.setTag(ToolHelper.ModifierListTag, tagList)
            stack.setTagCompound(tagCompound)
        }
    }

    /**
      * This will allow us to add more tool classes based on the stack
      *
      * @param stack The stack in
      * @return The list of effective tools
      */
    override def getToolClasses(stack: ItemStack): java.util.Set[String] =
        super.getToolClasses(stack)

    /**
      * To prevent the stack from taking damage while hitting entities, we must override this method
      */
    override def hitEntity(stack: ItemStack, target: EntityLivingBase, attacker: EntityLivingBase) : Boolean =
        extractEnergy(stack, RF_COST(stack), simulate = false) > 0

    /**
      * Puts effect if AOE is active
      */
    @SideOnly(Side.CLIENT)
    override def hasEffect(stack: ItemStack): Boolean = {
        val tag = ModifierAOE.getModifierTagFromStack(stack)
        if (tag != null && tag.hasKey(ACTIVE))
            tag.getBoolean(ACTIVE)
        else
            false
    }

    /*******************************************************************************************************************
      ******************************************** Battery Functions ***************************************************
      ******************************************************************************************************************/

    /**
      * Adds energy to a container item. Returns the quantity of energy that was accepted.
      * This should always return 0 if the item cannot be externally charged.
      *
      * @param stack ItemStack to be charged.
      * @param maxReceive Maximum amount of energy to be sent into the item.
      * @param simulate If TRUE, the charge will only be simulated.
      * @return Amount of energy that was (or would have been, if simulated) received by the item.
      */
    override def receiveEnergy(stack: ItemStack, maxReceive: Int, simulate: Boolean): Int = {
        if (!stack.hasTagCompound) {
            stack.setTagCompound(new NBTTagCompound)
        }
        var energy: Int = stack.getTagCompound.getInteger("Energy")
        val energyReceived: Int = Math.min(stack.getTagCompound.getInteger("EnergyCapacity") - energy,
            Math.min(stack.getTagCompound.getInteger("MaxReceive"), maxReceive))
        if (!simulate) {
            energy += energyReceived
            stack.getTagCompound.setInteger("Energy", energy)
            updateDamage(stack)
        }
        energyReceived
    }

    /**
      * Get the max amount of energy that can be stored in the container item.
      */
    override def getMaxEnergyStored(stack: ItemStack): Int =
        stack.getTagCompound.getInteger("EnergyCapacity")

    /*******************************************************************************************************************
      *********************************************** Misc Functions ***************************************************
      ******************************************************************************************************************/

    @SideOnly(Side.CLIENT)
    override def addInformation(stack: ItemStack, player: EntityPlayer, list: java.util.List[String], boolean: Boolean): Unit = {
        for(string <- ToolHelper.getToolTipForDisplay(stack))
            list.add(string)
    }
}
