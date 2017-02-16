package com.teambrmodding.neotech.common.items;

import com.teambrmodding.neotech.common.tiles.traits.IUpgradeItem;
import net.minecraft.item.ItemStack;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/15/2017
 */
public class UpgradeItem extends BaseItem implements IUpgradeItem {
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
}
