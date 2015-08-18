package com.dyonovan.neotech.api.nei.machines;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.dyonovan.neotech.client.gui.machines.GuiElectricCrusher;
import com.dyonovan.neotech.collections.CrusherRecipes;
import com.dyonovan.neotech.lib.Reference;
import com.dyonovan.neotech.registries.CrusherRecipeRegistry;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
public class RecipeHandlerCrusher extends TemplateRecipeHandler {

    /**
     * The recipe for the crusher
     */
    public class CrushingPair extends CachedRecipe {

        public PositionedStack input; //The input stack
        public PositionedStack output; //The output stack

        public CrushingPair(ItemStack input, ItemStack output) {
            this.input = new PositionedStack(input, 51, 24);
            this.output = new PositionedStack(output, 110, 24);
        }

        @Override
        public List<PositionedStack> getIngredients() {
            return getCycledIngredients(cycleticks / 48, Collections.singletonList(this.input));
        }

        @Override
        public PositionedStack getResult() {
            return this.output;
        }
    }

    /**
     * Used to draw the arrows and such
     */
    @Override
    public void drawExtras(int recipe) {
        this.drawProgressBar(74, 23, 176, 14, 24, 16, 48, 0);
        this.drawProgressBar(17, 12, 176, 32, 12, 44, 48, 7);
    }

    /**
     * Define the texture
     */
    @Override
    public String getGuiTexture() {
        return Reference.MOD_ID() + ":textures/gui/nei/furnace.png";
    }

    /**
     * The area we want to define for the "Recipes" area
     */
    @Override
    public void loadTransferRects() {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(74, 23, 24, 18), "ecrusher"));
    }

    /**
     * The Gui this handler mimics
     */
    @Override
    public Class<? extends GuiContainer> getGuiClass() {
        return GuiElectricCrusher.class;
    }

    /**
     * Sets the display name
     */
    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal("neotech.nei.crusherrecipes");
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
        if (outputID.equals("ecrusher") && this.getClass() == RecipeHandlerCrusher.class) {
            ArrayList<CrusherRecipeRegistry.CrusherRecipesStack> recipes = CrusherRecipeRegistry.getRecipes();

            for (CrusherRecipeRegistry.CrusherRecipesStack recipe : recipes)
                this.arecipes.add(new CrushingPair(recipe.input(), recipe.output()));
        } else
            super.loadCraftingRecipes(outputID, results);
    }

    /**
     * Checks for crafting using our handler
     * @param result The stack we want to see how is made
     */
    @Override
    public void loadCraftingRecipes(ItemStack result) {
        ArrayList<CrusherRecipeRegistry.CrusherRecipesStack> recipes = CrusherRecipeRegistry.getRecipes();

        for (CrusherRecipeRegistry.CrusherRecipesStack recipe : recipes) {
            if (NEIServerUtils.areStacksSameType(recipe.output(), result))
                this.arecipes.add(new CrushingPair(recipe.input(), recipe.output()));
        }
    }

    /**
     * Used to find out the usage of blocks
     * @param input Our ID
     * @param ingredients Inputs, check if our handler makes something out of it
     */
    @Override
    public void loadUsageRecipes(String input, Object... ingredients) {
        if (input.equals("ecrusher") && this.getClass() == RecipeHandlerCrusher.class)
            this.loadCraftingRecipes("ecrusher");
        else
            super.loadUsageRecipes(input, ingredients);
    }

    /**
     * Used to find the usage of a single stack
     * @param ingredient The thing to see if we use it
     */
    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        ArrayList<CrusherRecipeRegistry.CrusherRecipesStack> recipes = CrusherRecipeRegistry.getRecipes();

        for (CrusherRecipeRegistry.CrusherRecipesStack recipe : recipes) {
            if (NEIServerUtils.areStacksSameTypeCrafting(recipe.input(), ingredient)) {
                CrushingPair arecipe = new CrushingPair(recipe.input(), recipe.output());
                arecipe.setIngredientPermutation(Collections.singletonList(arecipe.input), ingredient);
                this.arecipes.add(arecipe);
                return; //We should stop since we found our answer
            }
        }
    }
}