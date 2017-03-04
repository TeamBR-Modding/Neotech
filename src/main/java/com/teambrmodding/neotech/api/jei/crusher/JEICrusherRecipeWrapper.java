package com.teambrmodding.neotech.api.jei.crusher;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/5/2017
 */
public class JEICrusherRecipeWrapper extends BlankRecipeWrapper {

    // Variables
    private ItemStack input;
    private ItemStack output;
    private ItemStack optionalSecondary;
    private String    secondaryChance;

    /*******************************************************************************************************************
     * Constructor                                                                                                     *
     *******************************************************************************************************************/

    /**
     * Recipe Wrapper
     * @param in The input
     * @param out The output
     * @param secondary Secondary Output (can be null)
     * @param percentChance Percent chance of Secondary output (can ben null)
     */
    public JEICrusherRecipeWrapper(ItemStack in, ItemStack out, @Nullable ItemStack secondary, @Nullable String percentChance) {
        input = in;
        output = out;
        optionalSecondary = secondary;
        secondaryChance = percentChance;
    }

    /*******************************************************************************************************************
     * Helper Methods                                                                                                  *
     *******************************************************************************************************************/

    /**
     * Used to make sure this recipe has been created correctly
     * @return True if recipe can be displayed
     */
    public boolean isValid() {
        return input != null && output != null;
    }

    /*******************************************************************************************************************
     * BlankRecipeWrapper                                                                                              *
     *******************************************************************************************************************/

    /**
     * Exposes the ingredients to JEI for display
     * @param ingredients The ingredients object
     */
    @Override
    public void getIngredients(IIngredients ingredients) {
        // Set inputs
        ingredients.setInput(ItemStack.class, input);

        // Build output list
        ArrayList<ItemStack> outputs = new ArrayList<>();
        outputs.add(output);
        if(optionalSecondary != null)
            outputs.add(optionalSecondary);
        ingredients.setOutputs(ItemStack.class, outputs);
    }

    /**
     * Used to draw output chance
     */
    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        if(!optionalSecondary.isEmpty()) {
            minecraft.fontRendererObj.drawSplitString(secondaryChance + "%", 140, 55, 60, Color.gray.getRGB());
        }
    }
}
