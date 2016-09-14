package com.teambrmodding.neotech.common.tiles.traits

import com.teambr.bookshelf.common.container.InventoryCallback
import com.teambr.bookshelf.common.tiles.traits.Inventory
import com.teambrmodding.neotech.common.tiles.traits.IUpgradeItem.ENUM_UPGRADE_CATEGORY
import com.teambrmodding.neotech.managers.{CapabilityLoadManager, ItemManager}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.items.IItemHandler

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/9/2016
  */
trait Upgradeable {
    lazy val upgradeInventory = new Inventory {
        override def initialSize: Int = 6

        /**
          * Used to define if an item is valid for a slot, we only want full MotherBoards
          *
          * @param index The slot id
          * @param stack The stack to check
          * @return True if you can put this there
          */
        override def isItemValidForSlot(index: Int, stack: ItemStack): Boolean = !hasUpgradeAlready(stack)

        addCallback(new InventoryCallback {
            override def onInventoryChanged(inventory: IItemHandler, slotNumber: Int): Unit = {
                if(inventory.getStackInSlot(slotNumber) == null)
                    resetValues()
                upgradeInventoryChanged(slotNumber)
            }
        })
    }

    /**
      * Called when upgrade inventory is changed
      */
    def upgradeInventoryChanged(slot: Int) = { }

    /**
      * Called when the inventory changes, reset relevant values here
      */
    def resetValues(): Unit = { }

    /**
      * Checks the inventory for existing cases of a tiered upgrade, also checks for existing by ID
      * @param stack The stack to check
      * @return True if we have this already
      */
    def hasUpgradeAlready(stack : ItemStack): Boolean = {
        // Make sure this is acceptable to insert
        if(stack.hasCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null)) {
            // Cast to our data object
            val upgradeItem = stack.getCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null)
            // Check against slots
            for(x <- 0 until upgradeInventory.getSizeInventory) {
                // Grab item in slot
                val slottedItem = upgradeInventory.getStackInSlot(x)
                // If we have an item here, and for type cast security it is valid
                if(slottedItem != null && slottedItem.hasCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null)) {
                    // Cast to our data object
                    val slottedUpgrade = slottedItem.getCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null)
                    // Check for matching category, only on tiered objects, otherwise match by ID
                    slottedUpgrade.getCategory match {
                        case IUpgradeItem.ENUM_UPGRADE_CATEGORY.CPU =>
                            if(upgradeItem.getCategory == IUpgradeItem.ENUM_UPGRADE_CATEGORY.CPU)
                                return true
                        case IUpgradeItem.ENUM_UPGRADE_CATEGORY.HDD =>
                            if(upgradeItem.getCategory == IUpgradeItem.ENUM_UPGRADE_CATEGORY.HDD)
                                return true
                        case IUpgradeItem.ENUM_UPGRADE_CATEGORY.MEMORY =>
                            if(upgradeItem.getCategory == IUpgradeItem.ENUM_UPGRADE_CATEGORY.MEMORY)
                                return true
                        case IUpgradeItem.ENUM_UPGRADE_CATEGORY.PSU =>
                            if(upgradeItem.getCategory == IUpgradeItem.ENUM_UPGRADE_CATEGORY.PSU)
                                return true
                        case _ =>
                            if(upgradeItem.getID.equalsIgnoreCase(slottedUpgrade.getID))
                                return true
                    }
                }
            }
        }
        false
    }

    /**
      * Used to get the upgrade count by id
      * @param id The id of the upgrade
      * @return How many are present, 0 for none found
      */
    def getUpgradeCountByID(id : String) : Int = {
        // Cycle Inventory
        for(x <- 0 until upgradeInventory.getSizeInventory) {
            // Grab item in slot
            val slottedItem = upgradeInventory.getStackInSlot(x)
            // If we have an item here, and for type cast security it is valid
            if (slottedItem != null && slottedItem.hasCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null)) {
                // Cast to our data object
                val slottedUpgrade = slottedItem.getCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null)
                // If we find what we need, return the stack size
                if(slottedUpgrade.getID.equalsIgnoreCase(id))
                    return slottedItem.stackSize
            }
        }
        0
    }

    /**
      * Used to check if this upgrade exists by category
      * @param id The category
      * @return If the upgrade is present
      */
    def hasUpgradeByID(id : String) : Boolean = {
        // Cycle Inventory
        for(x <- 0 until upgradeInventory.getSizeInventory) {
            // Grab item in slot
            val slottedItem = upgradeInventory.getStackInSlot(x)
            // If we have an item here, and for type cast security it is valid
            if (slottedItem != null && slottedItem.hasCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null)) {
                // Cast to our data object
                val slottedUpgrade = slottedItem.getCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null)
                // If we find what we need, return the stack size
                if(slottedUpgrade.getID.equalsIgnoreCase(id))
                    return true
            }
        }
        false
    }

    /**
      * Used to get the upgrade count by category
      * @param category The category of the upgrade
      * @return How many are present, 0 for none found
      */
    def getUpgradeCountByCategory(category : IUpgradeItem.ENUM_UPGRADE_CATEGORY) : Int = {
        // Cycle Inventory
        for(x <- 0 until upgradeInventory.getSizeInventory) {
            // Grab item in slot
            val slottedItem = upgradeInventory.getStackInSlot(x)
            // If we have an item here, and for type cast security it is valid
            if (slottedItem != null && slottedItem.hasCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null)) {
                // Cast to our data object
                val slottedUpgrade = slottedItem.getCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null)
                // If we find what we need, return the stack size
                if(slottedUpgrade.getCategory == category)
                    return slottedItem.stackSize
            }
        }
        0
    }

    /**
      * Used to check if this upgrade exists by category
      * @param category The category
      * @return Shouldn't really be using this, categories are made for numbered upgrades
      */
    def hasUpgradeByCategory(category : IUpgradeItem.ENUM_UPGRADE_CATEGORY) : Boolean = {
        // Cycle Inventory
        for(x <- 0 until upgradeInventory.getSizeInventory) {
            // Grab item in slot
            val slottedItem = upgradeInventory.getStackInSlot(x)
            // If we have an item here, and for type cast security it is valid
            if (slottedItem != null && slottedItem.hasCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null)) {
                // Cast to our data object
                val slottedUpgrade = slottedItem.getCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null)
                // If we find what we need, return the stack size
                if(slottedUpgrade.getCategory == category)
                    return true
            }
        }
        false
    }

    /**
      * Get modifier for a category
      * @param category The category
      * @return The modifier to apply
      */
    def getModifierForCategory(category: ENUM_UPGRADE_CATEGORY) : Int = {
        // Cycle Inventory
        for(x <- 0 until upgradeInventory.getSizeInventory) {
            // Grab item in slot
            val slottedItem = upgradeInventory.getStackInSlot(x)
            // If we have an item here, and for type cast security it is valid
            if (slottedItem != null && slottedItem.hasCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null)) {
                // Cast to our data object
                val slottedUpgrade = slottedItem.getCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null)
                // If we find what we need, return the stack size
                if(slottedUpgrade.getCategory == category)
                    return slottedUpgrade.getMultiplier(slottedItem)
            }
        }
        1
    }

    /**
      * Used to get the upgrade count by id
      * @param id The id of the upgrade
      * @return How many are present, 0 for none found
      */
    def getModifierByID(id : String) : Int = {
        // Cycle Inventory
        for(x <- 0 until upgradeInventory.getSizeInventory) {
            // Grab item in slot
            val slottedItem = upgradeInventory.getStackInSlot(x)
            // If we have an item here, and for type cast security it is valid
            if (slottedItem != null && slottedItem.hasCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null)) {
                // Cast to our data object
                val slottedUpgrade = slottedItem.getCapability(CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY, null)
                // If we find what we need, return the stack size
                if(slottedUpgrade.getID.equalsIgnoreCase(id))
                    return slottedUpgrade.getMultiplier(slottedItem)
            }
        }
        1
    }

    /**
      * Used mainly by GUI to check for changes
      * @param inventory The inventory to compare against
      * @return True if something changed
      */
    def hasChangedFromLast(inventory : Inventory) : Boolean = {
        for(x <- 0 until upgradeInventory.initialSize) {
            if(!ItemStack.areItemStacksEqual(upgradeInventory.getStackInSlot(x), inventory.getStackInSlot(x)))
                return true
        }
        false
    }

    //NBT, must overwrite
    def readFromNBT(tag: NBTTagCompound): Unit = {
        upgradeInventory.readFromNBT(tag, "upgrade")
        if(upgradeInventory.getSizeInventory != this.upgradeInventory.initialSize) {
            upgradeInventory.inventoryContents.clear()
            upgradeInventory.inventoryContents.setSize(this.upgradeInventory.initialSize)
        }
    }

    def writeToNBT(tag : NBTTagCompound): NBTTagCompound = {
        upgradeInventory.writeToNBT(tag, "upgrade")
    }
}
