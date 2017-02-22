package com.teambrmodding.neotech.registries.recipes;

import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

/**
* This file was created for NeoTech
* <p>
* NeoTech is licensed under the
* Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
* http://creativecommons.org/licenses/by-nc-sa/4.0/
*
* @author Paul Davis - pauljoda
* @since 2/22/2017
*/
public class CrusherRecipe extends AbstractRecipe<ItemStack, Pair<Pair<ItemStack, ItemStack>, Integer>> {
    public String inputItemStack, outputItemStack, outputSecondary;
    public int secondaryOutputPercentChance;

    /**
     * Creates the recipe
     */
    public CrusherRecipe(String inputItemStack, String outputItemStack, String outputSecondary,
                         int secondaryOutputPercentChance) {
        this.inputItemStack = inputItemStack;
        this.outputItemStack = outputItemStack;
        this.outputSecondary = outputSecondary;
        this.secondaryOutputPercentChance = secondaryOutputPercentChance;
    }

    /***************************************************************************************************************
     * AbstractRecipe                                                                                              *
     ***************************************************************************************************************/

    /**
     * Used to get the output of this recipe
     *
     * @param input The input object
     * @return The output object
     */
    @Nullable
    @Override
    public Pair<Pair<ItemStack, ItemStack>, Integer> getOutput(ItemStack input) {
        if(input == null) // Safety Check
            return null;

        if(isValidInput(input))
            return Pair.of(Pair.of(getItemStackFromString(outputItemStack), getItemStackFromString(outputSecondary)), secondaryOutputPercentChance);

        return null;
    }

    /**
     * Is the input valid for an output
     *
     * @param input The input object
     * @return True if there is an output
     */
    @Override
    public boolean isValidInput(ItemStack input) {
        return isItemStackValidForRecipeStack(inputItemStack, input);
    }
}
