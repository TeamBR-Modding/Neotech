package com.teambrmodding.neotech.common.items;

import com.teambrmodding.neotech.common.tiles.traits.IUpgradeItem;
import com.teambrmodding.neotech.managers.CapabilityLoadManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/15/2017
 */
public class UpgradeItem extends BaseItem implements IUpgradeItem, ICapabilityProvider {
    private String id;
    private ENUM_UPGRADE_CATEGORY category;
    private int multiplier;
    private boolean stackAware;

    /**
     * Creates the upgrade item
     * @param name         The item name, also doubles as the upgrade item id
     * @param category     The upgrade item category
     * @param stackSize    The max stack size
     * @param multiplier   The upgrade multiplier
     * @param stackAware   Count stack size in multiplier
     */
    public UpgradeItem(String name, ENUM_UPGRADE_CATEGORY category, int stackSize, int multiplier, boolean stackAware) {
        super(name, stackSize);
        this.id = name;
        this.category = category;
        this.multiplier = multiplier;
        this.stackAware = stackAware;
    }

    /**
     * Called from ItemStack.setItem, will hold extra data for the life of this ItemStack.
     * Can be retrieved from stack.getCapabilities()
     * The NBT can be null if this is not called from readNBT or if the item the stack is
     * changing FROM is different then this item, or the previous item had no capabilities.
     *
     * This is called BEFORE the stacks item is set so you can use stack.getItem() to see the OLD item.
     * Remember that getItem CAN return null.
     *
     * @param stack The ItemStack
     * @param nbt   NBT of this item serialized, or null.
     * @return A holder instance associated with this ItemStack where you can hold capabilities for the life of this item.
     */
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
        return this;
    }

    /*******************************************************************************************************************
     * IUpgradeItem                                                                                                    *
     *******************************************************************************************************************/

    /**
     * Get the id of this upgrade item
     *
     * @return A unique ID, should be a static variable somewhere
     */
    @Override
    public String getID() {
        return id;
    }

    /**
     * Used to get what category this upgrade is, this allows for tiered upgrades, only use if tiered
     *
     * @return Category based of standard set, use NONE if not needed
     */
    @Override
    public ENUM_UPGRADE_CATEGORY getCategory() {
        return category;
    }

    /**
     * Specify the multiplier for this object. Used commonly with tiered objects
     *
     * @param stack The stack this object is in, to access stack size etc.
     * @return The multiplier for this object, machines can use differently
     */
    @Override
    public int getMultiplier(ItemStack stack) {
        return multiplier * (stackAware ? stack.stackSize : 1);
    }

    /*******************************************************************************************************************
     * ICapabilityProvider                                                                                             *
     *******************************************************************************************************************/

    /**
     * Determines if this object has support for the capability in question on the specific side.
     * The return value of this MIGHT change during runtime if this object gains or looses support
     * for a capability.
     *
     * Example:
     * A Pipe getting a cover placed on one side causing it loose the Inventory attachment function for that side.
     *
     * This is a light weight version of getCapability, intended for metadata uses.
     *
     * @param capability The capability to check
     * @param facing     The Side to check from:
     *                   CAN BE NULL. Null is defined to represent 'internal' or 'self'
     * @return True if this object supports the capability.
     */
    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY;
    }
    /**
     * Retrieves the handler for the capability requested on the specific side.
     * The return value CAN be null if the object does not support the capability.
     * The return value CAN be the same for multiple faces.
     *
     * @param capability The capability to check
     * @param facing     The Side to check from:
     *                   CAN BE NULL. Null is defined to represent 'internal' or 'self'
     * @return True if this object supports the capability.
     */
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability != null && capability == CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY)
            return (T) this;
        return null;
    }
}
