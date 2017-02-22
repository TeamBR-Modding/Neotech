package com.teambrmodding.neotech.api.jei.crusher;

import com.teambr.bookshelf.client.gui.GuiColor;
import com.teambr.bookshelf.util.ClientUtils;
import com.teambrmodding.neotech.api.jei.NeotechJEIPlugin;
import com.teambrmodding.neotech.lib.Reference;
import com.teambrmodding.neotech.managers.RecipeManager;
import com.teambrmodding.neotech.registries.recipes.AbstractRecipe;
import com.teambrmodding.neotech.registries.CrusherRecipeHandler;
import com.teambrmodding.neotech.registries.recipes.CrusherRecipe;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
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
public class JEICrusherRecipeCategory implements IRecipeCategory<JEICrusherRecipeWrapper> {

    // Variables
    private ResourceLocation backgroundResource = new ResourceLocation(Reference.MOD_ID, "textures/gui/jei/electricCrusher.png");
    private IDrawableAnimated progressArrow;
    private IDrawableAnimated powerBar;

    /*******************************************************************************************************************
     * Constructor                                                                                                     *
     *******************************************************************************************************************/

    /**
     * Constructor
     */
    public JEICrusherRecipeCategory() {
        IDrawableStatic progressArrowDrawable = NeotechJEIPlugin.jeiHelpers.getGuiHelper().createDrawable(backgroundResource, 170, 0, 23, 17);
        progressArrow = NeotechJEIPlugin.jeiHelpers.getGuiHelper().createAnimatedDrawable(progressArrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);

        IDrawableStatic powerBarDrawable = NeotechJEIPlugin.jeiHelpers.getGuiHelper().createDrawable(backgroundResource, 170, 17, 16, 62);
        powerBar = NeotechJEIPlugin.jeiHelpers.getGuiHelper().createAnimatedDrawable(powerBarDrawable, 300, IDrawableAnimated.StartDirection.TOP, true);
    }

    /*******************************************************************************************************************
     * IRecipeCategory                                                                                                 *
     *******************************************************************************************************************/

    /**
     * The unique string for this category
     * @return The centrifuge string
     */
    @Override
    public String getUid() {
        return NeotechJEIPlugin.CRUSHER_UUID;
    }

    /**
     * Used to display the title
     * @return The translated title
     */
    @Override
    public String getTitle() {
        return ClientUtils.translate("tile.neotech:electricCrusher.name");
    }

    /**
     * Set background, we just use a flat gray, might expand in future to a texture
     * @return A drawable made from our texture
     */
    @Override
    public IDrawable getBackground() {
        return NeotechJEIPlugin.jeiHelpers.getGuiHelper().createDrawable(backgroundResource, 0, 0, 170, 80);
    }

    /**
     * We will use the default one registered in the NeotechJEIPlugin class
     * @return Null, don't worry about it here
     */
    @Nullable
    @Override
    public IDrawable getIcon() {
        return null;
    }

    /**
     * The main draw call, display generic stuff here
     */
    @Override
    public void drawExtras(Minecraft minecraft) {
        // Draw Animations
        progressArrow.draw(minecraft, 76, 31);
        powerBar.draw(minecraft, 13, 9);
    }

    @Override
    public void drawAnimations(Minecraft minecraft) {
        // Deprecated, moved to drawExtras
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, JEICrusherRecipeWrapper recipeWrapper) {
        // Deprecated
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, final JEICrusherRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();

        itemStackGroup.init(0, true,  52,  31);
        itemStackGroup.init(1, false, 113,  31);
        itemStackGroup.init(2, false, 135, 31);

        recipeLayout.getItemStacks().set(0, ingredients.getInputs(ItemStack.class).get(0));
        recipeLayout.getItemStacks().set(1, ingredients.getOutputs(ItemStack.class).get(0));
        if(ingredients.getOutputs(ItemStack.class).size() > 1) {
            recipeLayout.getItemStacks().set(2, ingredients.getOutputs(ItemStack.class).get(1));
            recipeLayout.getItemStacks().addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
                if(slotIndex == 2) {
                    tooltip.add("" + GuiColor.RED + ClientUtils.translate("jei.text.requiresExpansion"));
                }
            });
        }
    }

    /*******************************************************************************************************************
     * Class Methods                                                                                                   *
     *******************************************************************************************************************/

    /**
     * Used to generate a list of all recipes for this category
     * @return The completed list of recipes
     */
    public static java.util.List<JEICrusherRecipeWrapper> buildRecipeList() {
        ArrayList<JEICrusherRecipeWrapper> recipes = new ArrayList<>();
        CrusherRecipeHandler crusherRecipeHandler = RecipeManager.getHandler(RecipeManager.RecipeType.CRUSHER);
        for(CrusherRecipe recipe : crusherRecipeHandler.recipes) {
            ItemStack input = AbstractRecipe.getItemStackFromStringForDisplay(recipe.inputItemStack);
            ItemStack output = AbstractRecipe.getItemStackFromString(recipe.outputItemStack);
            ItemStack outputTwo  = AbstractRecipe.getItemStackFromString(recipe.outputSecondary);
            String chance = String.valueOf(recipe.secondaryOutputPercentChance);
            recipes.add(new JEICrusherRecipeWrapper(input, output, outputTwo, chance));
        }
        return recipes;
    }
}
