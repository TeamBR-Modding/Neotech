package com.teambrmodding.neotech.api.jei.alloyer;

import com.teambr.bookshelf.api.jei.drawables.GuiComponentArrowJEI;
import com.teambr.bookshelf.api.jei.drawables.GuiComponentBox;
import com.teambr.bookshelf.api.jei.drawables.GuiComponentPowerBarJEI;
import com.teambr.bookshelf.helper.LogHelper;
import com.teambrmodding.neotech.api.jei.NeotechJEIPlugin;
import com.teambrmodding.neotech.lib.Reference;
import com.teambrmodding.neotech.managers.RecipeManager;
import com.teambrmodding.neotech.registries.AlloyerRecipe;
import com.teambrmodding.neotech.registries.AlloyerRecipeHandler;
import com.teambrmodding.neotech.utils.ClientUtils;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

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
public class JEIAlloyerRecipeCategory implements IRecipeCategory<JEIAlloyerRecipeWrapper> {

    // Display
    private ResourceLocation backgroundResource = new ResourceLocation(Reference.MOD_ID(), "textures/gui/jei/jei.png");
    private GuiComponentArrowJEI progressArrow = new GuiComponentArrowJEI(81, 17, NeotechJEIPlugin.jeiHelpers);
    private GuiComponentPowerBarJEI powerBar    = new GuiComponentPowerBarJEI(14, 0, 18, 60, new Color(255, 0, 0), NeotechJEIPlugin.jeiHelpers);

    // Tanks
    private GuiComponentBox tankInputOne = new GuiComponentBox(38, 0, 18, 60);
    private GuiComponentBox tankInputTwo = new GuiComponentBox(60, 0, 18, 60);
    private GuiComponentBox tankOutput   = new GuiComponentBox(115, 0, 50, 60);

    /*******************************************************************************************************************
     * Constructor                                                                                                     *
     *******************************************************************************************************************/

    /**
     * Constructor, we want to add the colors to our powerBar here
     */
    public JEIAlloyerRecipeCategory() {
        powerBar.addColor(new Color(255, 150, 0));
        powerBar.addColor(new Color(255, 255, 0));
    }

    /*******************************************************************************************************************
     * IRecipeCategory                                                                                                 *
     *******************************************************************************************************************/

    /**
     * Get the unique ID of this category
     * @return The alloyer string
     */
    @Override
    public String getUid() {
        return NeotechJEIPlugin.ALLOYER_UUID;
    }

    /**
     * Used to display the title
     * @return The translated title
     */
    @Override
    public String getTitle() {
        return ClientUtils.translate("tile.neotech:alloyer.name");
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
        // Draw Tank Backgrounds
        tankInputOne.draw(minecraft);
        tankInputTwo.draw(minecraft);
        tankOutput.draw(minecraft);

        // Draw Animations
        progressArrow.draw(minecraft);
        powerBar.draw(minecraft, 0, 0);
    }

    @Override
    public void drawAnimations(Minecraft minecraft) {
        // Deprecated, moved to drawExtras
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, JEIAlloyerRecipeWrapper recipeWrapper) {
        // Deprecated
    }

    /**
     * Actually exposes the recipe to JEI
     * @param recipeLayout The object to hold al the info
     * @param recipeWrapper The recipe, not always needed as all ingredients are added to ingredients
     * @param ingredients What holds the ingredients
     */
    @Override
    public void setRecipe(IRecipeLayout recipeLayout, JEIAlloyerRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiFluidStackGroup fluidStackGroup = recipeLayout.getFluidStacks();

        // Load the fluids
        fluidStackGroup.init(0, true,  39, 0, 16, 59, 2000, false, null);
        fluidStackGroup.init(1, true,  61, 0, 16, 59, 2000, false, null);
        fluidStackGroup.init(2, false, 116, 0, 48, 59, 2000, false, null);

        // Set into layout
        recipeLayout.getFluidStacks().set(0, ingredients.getInputs(FluidStack.class).get(0));
        recipeLayout.getFluidStacks().set(1, ingredients.getInputs(FluidStack.class).get(1));
        recipeLayout.getFluidStacks().set(2, ingredients.getOutputs(FluidStack.class).get(0));
    }


    /*******************************************************************************************************************
     * Class Methods                                                                                                   *
     *******************************************************************************************************************/

    /**
     * Used to generate a list of all recipes for this category
     * @return The completed list of recipes
     */
    public static List<JEIAlloyerRecipeWrapper> buildRecipeList() {
        ArrayList<JEIAlloyerRecipeWrapper> recipes = new ArrayList<>();
        AlloyerRecipeHandler alloyerRecipeHandler = (AlloyerRecipeHandler) RecipeManager.getHandler("alloyer").get();
        for(AlloyerRecipe recipe : alloyerRecipeHandler.recipes()) {
            FluidStack fluidInputOne = recipe.getFluidFromString(recipe.fluidOne());
            FluidStack fluidInputTwo = recipe.getFluidFromString(recipe.fluidTwo());
            FluidStack fluidOutput   = recipe.getFluidFromString(recipe.fluidOut());
            if(fluidInputOne != null && fluidInputTwo != null && fluidOutput != null)
                recipes.add(new JEIAlloyerRecipeWrapper(fluidInputOne, fluidInputTwo, fluidOutput));
            else
                LogHelper.severe("[Neotech] Alloyer Recipe json is corrupt! Please delete and run again.");
        }

        return recipes;
    }
}
