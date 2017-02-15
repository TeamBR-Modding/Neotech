package com.teambrmodding.neotech.api.jei.crucible;

import com.teambr.bookshelf.api.jei.drawables.GuiComponentArrowJEI;
import com.teambr.bookshelf.api.jei.drawables.GuiComponentBox;
import com.teambr.bookshelf.api.jei.drawables.GuiComponentPowerBarJEI;
import com.teambr.bookshelf.api.jei.drawables.SlotDrawable;
import com.teambr.bookshelf.helper.LogHelper;
import com.teambrmodding.neotech.api.jei.NeotechJEIPlugin;
import com.teambrmodding.neotech.lib.Reference;
import com.teambrmodding.neotech.managers.RecipeManager;
import com.teambrmodding.neotech.registries.CrucibleRecipe;
import com.teambrmodding.neotech.registries.CrucibleRecipeHandler;
import com.teambrmodding.neotech.utils.ClientUtils;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

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
public class JEICrucibleRecipeCategory implements IRecipeCategory<JEICrucibleRecipeWrapper> {

    // Variables
    private ResourceLocation backgroundResource = new ResourceLocation(Reference.MOD_ID(), "textures/gui/jei/jei.png");
    private SlotDrawable inputSlot              = new SlotDrawable(56, 17, false);
    private GuiComponentArrowJEI progressArrow  = new GuiComponentArrowJEI(81, 17, NeotechJEIPlugin.jeiHelpers);
    private GuiComponentPowerBarJEI powerBar    = new GuiComponentPowerBarJEI(14, 0, 18, 60, new Color(255, 0, 0), NeotechJEIPlugin.jeiHelpers);
    private GuiComponentBox tank                = new GuiComponentBox(115, 0, 50, 60);

    /*******************************************************************************************************************
     * Constructor                                                                                                     *
     *******************************************************************************************************************/

    public JEICrucibleRecipeCategory() {
        powerBar.addColor(new Color(255, 150, 0));
        powerBar.addColor(new Color(255, 255, 0));
    }

    /*******************************************************************************************************************
     * IRecipeCategory                                                                                                 *
     *******************************************************************************************************************/

    /**
     * The unique string for this category
     * @return The string
     */
    @Override
    public String getUid() {
        return NeotechJEIPlugin.CRUCIBLE_UUID;
    }

    /**
     * Used to display the title
     * @return The translated title
     */
    @Override
    public String getTitle() {
        return ClientUtils.translate("tile.neotech:electricCrucible.name");
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


    @Override
    public void drawExtras(Minecraft minecraft) {
        // Draw Backgrounds
        inputSlot.draw(minecraft);
        tank.draw(minecraft);

        // Draw Animations
        progressArrow.draw(minecraft);
        powerBar.draw(minecraft, 0, 0);
    }

    @Override
    public void drawAnimations(Minecraft minecraft) {
        // Deprecated, moved to drawExtras
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, JEICrucibleRecipeWrapper recipeWrapper) {
        // Deprecated
    }

    /**
     * Actually exposes the recipe to JEI
     * @param recipeLayout The object to hold al the info
     * @param recipeWrapper The recipe, not always needed as all ingredients are added to ingredients
     * @param ingredients What holds the ingredients
     */
    @Override
    public void setRecipe(IRecipeLayout recipeLayout, JEICrucibleRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup itemStackGroup  = recipeLayout.getItemStacks();
        IGuiFluidStackGroup fluidStackGroup = recipeLayout.getFluidStacks();

        // Init components
        itemStackGroup.init(0, true, 56, 17);
        fluidStackGroup.init(0, false, 116, 0, 48, 59, 2000, false, null);

        // Fill layout
        recipeLayout.getItemStacks().set(0,  ingredients.getInputs(ItemStack.class).get(0));
        recipeLayout.getFluidStacks().set(0, ingredients.getOutputs(FluidStack.class).get(0));
    }

    /*******************************************************************************************************************
     * Class Methods                                                                                                   *
     *******************************************************************************************************************/

    /**
     * Used to generate a list of all recipes for this category
     * @return The completed list of recipes
     */
    public static java.util.List<JEICrucibleRecipeWrapper> buildRecipeList() {
        ArrayList<JEICrucibleRecipeWrapper> recipes = new ArrayList<>();
        CrucibleRecipeHandler crucibleRecipeHandler = (CrucibleRecipeHandler) RecipeManager.getHandler("crucible").get();
        for(CrucibleRecipe recipe : crucibleRecipeHandler.recipes()) {
            java.util.List<ItemStack> inputList = OreDictionary.getOres(recipe.ore());
            FluidStack output = recipe.getFluidStackFromString(recipe.output());
            if(output != null) {
                if(!inputList.isEmpty()) {
                    recipes.add(new JEICrucibleRecipeWrapper(inputList, output));
                } else {
                    ItemStack input = recipe.getItemStackFromString(recipe.input());
                    if(input != null) {
                        recipes.add(new JEICrucibleRecipeWrapper(Collections.singletonList(input), output));
                    } else LogHelper.severe("[NeoTech] Crucible json is corrupt! Please delete and recreate!");
                }
            } else LogHelper.severe("[NeoTech] Crucible json is corrupt! Please delete and recreate!");
        }
        return recipes;
    }
}
