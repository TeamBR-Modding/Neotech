package com.teambrmodding.neotech.collections;

import com.teambr.bookshelf.util.ClientUtils;
import com.teambrmodding.neotech.managers.MetalManager;
import com.teambrmodding.neotech.registries.recipes.AbstractRecipe;
import net.minecraft.item.ItemStack;

/**
 * Enumeration to handle what mode to be in
 */
public enum SolidifierMode {
    BLOCK_MODE(MetalManager.BLOCK_MB, "blockIron", "neotech.text.blockMode"),
    INGOT_MODE(MetalManager.INGOT_MB, "ingotIron", "neotech.text.ingotMode"),
    NUGGET_MODE(MetalManager.NUGGET_MB, "nuggetIron", "neotech.text.nuggetMode");

    private String displayStack, displayName;
    private int requiredAmount;

    SolidifierMode(int requiredAmount, String displayStack, String displayName) {
        this.requiredAmount = requiredAmount;
        this.displayStack = displayStack;
        this.displayName = displayName;
    }

    /**
     * Gets the name to display translated
     *
     * @return The display name
     */
    public String getDisplayName() {
        return ClientUtils.translate(displayName);
    }

    /**
     * Get how many mb this mode requires
     *
     * @return The amount of mb
     */
    public int getRequiredAmount() {
        return requiredAmount;
    }

    /**
     * Used to get what display as the stack
     *
     * @return The display stack
     */
    public ItemStack getDisplayStack() {
        return AbstractRecipe.getItemStackFromString(displayStack);
    }

    /**
     * Get the next mode in the list, used for toggling and cycling options
     *
     * @return The next mode
     */
    public SolidifierMode getNextMode() {
        if (ordinal() + 1 < SolidifierMode.values().length)
            return SolidifierMode.values()[ordinal() + 1];
        else
            return SolidifierMode.values()[0];
    }
}
