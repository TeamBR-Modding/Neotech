package com.teambrmodding.neotech.api.jei.crusher;

import com.teambr.bookshelf.api.jei.drawables.GuiComponentArrowJEI;
import com.teambr.bookshelf.api.jei.drawables.GuiComponentPowerBarJEI;
import com.teambr.bookshelf.api.jei.drawables.SlotDrawable;
import com.teambr.bookshelf.client.gui.GuiColor;
import com.teambrmodding.neotech.api.jei.NeotechJEIPlugin;
import com.teambrmodding.neotech.lib.Reference;
import com.teambrmodding.neotech.managers.RecipeManager;
import com.teambrmodding.neotech.registries.CrusherRecipeHandler;
import com.teambrmodding.neotech.registries.CrusherRecipes;
import com.teambrmodding.neotech.utils.ClientUtils;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITooltipCallback;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/5/2017
 */
public class JEICrusherRecipeCategory implements IRecipeCategory<JEICrusherRecipeWrapper> {

    // Variables
    private ResourceLocation backgroundResource = new ResourceLocation(Reference.MOD_ID(), "textures/gui/jei/jei.png");
    private GuiComponentArrowJEI progressArrow  = new GuiComponentArrowJEI(59, 21, NeotechJEIPlugin.jeiHelpers);
    private GuiComponentPowerBarJEI powerBar    = new GuiComponentPowerBarJEI(3, 0, 18, 60, new Color(255, 0, 0), NeotechJEIPlugin.jeiHelpers);
    private SlotDrawable slotInput              = new SlotDrawable(31,  20, false);
    private SlotDrawable slotOutput             = new SlotDrawable(96,  20, true);
    private SlotDrawable slotOutputTwo          = new SlotDrawable(125, 20, true);

    /*******************************************************************************************************************
     * Constructor                                                                                                     *
     *******************************************************************************************************************/

    /**
     * Constructor, we want to add the colors to our powerBar here
     */
    public JEICrusherRecipeCategory() {
        powerBar.addColor(new Color(255, 150, 0));
        powerBar.addColor(new Color(255, 255, 0));
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
        return NeotechJEIPlugin.jeiHelpers.getGuiHelper().createDrawable(backgroundResource, 0, 0, 170, 60);
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
        // Draw Slots
        slotInput.draw(minecraft);
        slotOutput.draw(minecraft);
        slotOutputTwo.draw(minecraft);

        // Draw Animations
        progressArrow.draw(minecraft);
        powerBar.draw(minecraft, 0, 0);
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

        itemStackGroup.init(0, true,  31,  20);
        itemStackGroup.init(1, false, 96,  20);
        itemStackGroup.init(2, false, 125, 20);

        recipeLayout.getItemStacks().set(0, ingredients.getInputs(ItemStack.class).get(0));
        recipeLayout.getItemStacks().set(1, ingredients.getOutputs(ItemStack.class).get(0));
        if(ingredients.getOutputs(ItemStack.class).size() > 1) {
            recipeLayout.getItemStacks().set(2, ingredients.getOutputs(ItemStack.class).get(1));
            recipeLayout.getItemStacks().addTooltipCallback(new ITooltipCallback<ItemStack>() {
                @Override
                public void onTooltip(int slotIndex, boolean input, ItemStack ingredient, List<String> tooltip) {
                    if(slotIndex == 2) {
                        tooltip.add("" + GuiColor.RED + ClientUtils.translate("jei.text.requiresExpansion"));
                    }
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
        CrusherRecipeHandler crusherRecipeHandler = (CrusherRecipeHandler) RecipeManager.getHandler("crusher").get();
        for(CrusherRecipes recipe : crusherRecipeHandler.recipes()) {
            ItemStack input = recipe.getItemStackFromString(recipe.input());
            ItemStack output = recipe.getItemStackFromString(recipe.output());
            output.stackSize = recipe.qty();
            ItemStack outputTwo  = recipe.getItemStackFromString(recipe.outputSecondary());
            String chance = String.valueOf(recipe.percentChance());
            recipes.add(new JEICrusherRecipeWrapper(input, output, outputTwo, chance));
        }
        return recipes;
    }
}
