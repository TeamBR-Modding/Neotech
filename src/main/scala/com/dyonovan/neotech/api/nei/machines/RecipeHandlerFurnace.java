package com.dyonovan.neotech.api.nei.machines;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.dyonovan.neotech.client.gui.machines.GuiElectricFurnace;
import com.dyonovan.neotech.lib.Reference;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.StatCollector;

import java.awt.*;
import java.util.Collections;
import java.util.Map;

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 17, 2015
 */
public class RecipeHandlerFurnace extends TemplateRecipeHandler {

    /**
     * Used to hold the smelting recipe
     */
    public class SmeltingPair extends CachedRecipe {

        public PositionedStack input; //Input stack
        public PositionedStack output; //Output stack

        public SmeltingPair(ItemStack input, ItemStack output) {
            this.input = new PositionedStack(input, 51, 24);
            this.output = new PositionedStack(output, 110, 24);
        }

        /**
         * Will look at the oreDict and cycle the same tags through
         */
        @Override
        public java.util.List<PositionedStack> getIngredients() {
            return getCycledIngredients(cycleticks / 48, Collections.singletonList(this.input));
        }

        @Override
        public PositionedStack getResult() {
            return this.output;
        }
    }

    /**
     * Draw the arrow and flame
     */
    @Override
    public void drawExtras(int recipe) {
        //this.drawProgressBar(77, 41, 176, 0, 14, 14, 48, 7);
        this.drawProgressBar(74, 23, 176, 14, 24, 16, 48, 0);
    }

    /**
     * Sets an area to click to see recipes
     */
    @Override
    public void loadTransferRects() {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(74, 23, 24, 18), "furnace"));
    }

    /**
     * The Gui textures
     * @return String for resource location
     */
    @Override
    public String getGuiTexture() {
        return Reference.MOD_ID() + ":textures/gui/nei/furnace.png";
    }

    /**
     * GUI we are mimicing
     */
    @Override
    public Class<? extends GuiContainer> getGuiClass() { return GuiElectricFurnace.class; }

    /**
     * The localize name for the tab
     */
    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal("neotech.nei.furnacerecipes");
    }

    @Override
    public TemplateRecipeHandler newInstance() {
        return super.newInstance();
    }

    /**
     * Loads the crafting using the results
     * @param outputID ID
     * @param results Checking from results
     */
    @Override
    public void loadCraftingRecipes(String outputID, Object... results) {
        if (outputID.equals("furnace") && this.getClass() == RecipeHandlerFurnace.class) {
            Map recipes = FurnaceRecipes.instance().getSmeltingList();

            for (Object o : recipes.entrySet()) {
                Map.Entry recipe = (Map.Entry) o;
                this.arecipes.add(new SmeltingPair((ItemStack) recipe.getKey(), (ItemStack) recipe.getValue()));
            }
        } else {
            super.loadCraftingRecipes(outputID, results);
        }
    }

    /**
     * Checks for crafting using our handler
     * @param result The stack we want to see how is made
     */
    @Override
    public void loadCraftingRecipes(ItemStack result) {
        Map recipes = FurnaceRecipes.instance().getSmeltingList();

        for (Object o : recipes.entrySet()) {
            Map.Entry recipe = (Map.Entry) o;
            if (NEIServerUtils.areStacksSameType((ItemStack) recipe.getValue(), result)) {
                this.arecipes.add(new SmeltingPair((ItemStack) recipe.getKey(), (ItemStack) recipe.getValue()));
            }
        }
    }

    /**
     * Used to find out the usage of blocks
     * @param inputId Our ID
     * @param ingredients Inputs, check if our handler makes something out of it
     */
    @Override
    public void loadUsageRecipes(String inputId, Object... ingredients) {
        if(inputId.equals("furnace") && this.getClass() == RecipeHandlerFurnace.class) {
            this.loadCraftingRecipes("furnace");
        } else {
            super.loadUsageRecipes(inputId, ingredients);
        }

    }

    /**
     * Used to find the usage of a single stack
     * @param ingredient The thing to see if we use it
     */
    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        Map recipes = FurnaceRecipes.instance().getSmeltingList();

        for (Object o : recipes.entrySet()) {
            Map.Entry recipe = (Map.Entry) o;
            if (NEIServerUtils.areStacksSameTypeCrafting((ItemStack) recipe.getKey(), ingredient)) {
                SmeltingPair arecipe = new SmeltingPair((ItemStack) recipe.getKey(), (ItemStack) recipe.getValue());
                arecipe.setIngredientPermutation(Collections.singletonList(arecipe.input), ingredient);
                this.arecipes.add(arecipe);
                return; //Found our match
            }
        }
    }
}