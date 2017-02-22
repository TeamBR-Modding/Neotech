package com.teambrmodding.neotech.api.jei.alloyer;

import com.teambr.bookshelf.helper.LogHelper;
import com.teambr.bookshelf.util.ClientUtils;
import com.teambrmodding.neotech.api.jei.NeotechJEIPlugin;
import com.teambrmodding.neotech.lib.Reference;
import com.teambrmodding.neotech.managers.RecipeManager;
import com.teambrmodding.neotech.registries.AbstractRecipe;
import com.teambrmodding.neotech.registries.AlloyerRecipeHandler;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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
public class JEIAlloyerRecipeCategory implements IRecipeCategory<JEIAlloyerRecipeWrapper> {

    // Display
    private ResourceLocation backgroundResource = new ResourceLocation(Reference.MOD_ID, "textures/gui/jei/electricAlloyer.png");
    private IDrawableAnimated progressArrow;
    private IDrawableAnimated powerBar;

    /*******************************************************************************************************************
     * Constructor                                                                                                     *
     *******************************************************************************************************************/

    /**
     * Constructor
     */

    public JEIAlloyerRecipeCategory() {
        IDrawableStatic progressArrowDrawable = NeotechJEIPlugin.jeiHelpers.getGuiHelper().createDrawable(backgroundResource, 169, 0, 23, 17);
        progressArrow = NeotechJEIPlugin.jeiHelpers.getGuiHelper().createAnimatedDrawable(progressArrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);

        IDrawableStatic powerBarDrawable = NeotechJEIPlugin.jeiHelpers.getGuiHelper().createDrawable(backgroundResource, 169, 17, 16, 62);
        powerBar = NeotechJEIPlugin.jeiHelpers.getGuiHelper().createAnimatedDrawable(powerBarDrawable, 300, IDrawableAnimated.StartDirection.TOP, true);
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
        return NeotechJEIPlugin.jeiHelpers.getGuiHelper().createDrawable(backgroundResource, 0, 0, 169, 80);
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
        progressArrow.draw(minecraft, 78, 32);
        powerBar.draw(minecraft, 12, 9);
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
        fluidStackGroup.init(0, true,  36, 9, 16, 62, 2000, false, null);
        fluidStackGroup.init(1, true,  57, 9, 16, 62, 2000, false, null);
        fluidStackGroup.init(2, false, 108, 9, 49, 62, 2000, false, null);

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
        AlloyerRecipeHandler alloyerRecipeHandler = RecipeManager.getHandler(RecipeManager.RecipeType.ALLOYER);
        for(AlloyerRecipeHandler.AlloyerRecipe recipe : alloyerRecipeHandler.recipes) {
            FluidStack fluidInputOne = AbstractRecipe.getFluidStackFromString(recipe.fluidStackOne);
            FluidStack fluidInputTwo = AbstractRecipe.getFluidStackFromString(recipe.fluidStackTwo);
            FluidStack fluidOutput   = AbstractRecipe.getFluidStackFromString(recipe.fluidStackOutput);
            if(fluidInputOne != null && fluidInputTwo != null && fluidOutput != null)
                recipes.add(new JEIAlloyerRecipeWrapper(fluidInputOne, fluidInputTwo, fluidOutput));
            else
                LogHelper.logger.error("[Neotech] Alloyer Recipe json is corrupt! Please delete and run again.");
        }

        return recipes;
    }
}
