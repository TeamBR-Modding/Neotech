package com.teambrmodding.neotech.api.jei.centrifuge;

import com.teambr.bookshelf.helper.LogHelper;
import com.teambr.bookshelf.util.ClientUtils;
import com.teambrmodding.neotech.api.jei.NeotechJEIPlugin;
import com.teambrmodding.neotech.lib.Reference;
import com.teambrmodding.neotech.managers.RecipeManager;
import com.teambrmodding.neotech.registries.CentrifugeRecipeHandler;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;

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
public class JEICentrifugeRecipeCategory implements IRecipeCategory<JEICentrifugeRecipeWrapper> {

    // Display
    private ResourceLocation backgroundResource = new ResourceLocation(Reference.MOD_ID, "textures/gui/jei/jei.png");
    private IDrawableAnimated progressArrow;
    private IDrawableAnimated powerBar;

    /*******************************************************************************************************************
     * Constructor                                                                                                     *
     *******************************************************************************************************************/

    /**
     * Constructor
     */
    public JEICentrifugeRecipeCategory() {
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
        return NeotechJEIPlugin.CENTRIFUGE_UUID;
    }

    /**
     * Used to display the title
     * @return The translated title
     */
    @Override
    public String getTitle() {
        return ClientUtils.translate("tile.neotech:centrifuge.name");
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
        progressArrow.draw(minecraft, 92, 31);
        powerBar.draw(minecraft, 13, 8);
    }

    @Override
    public void drawAnimations(Minecraft minecraft) {
        // Deprecated, moved to drawExtras
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, JEICentrifugeRecipeWrapper recipeWrapper) {
        // Deprecated
    }

    /**
     * Actually exposes the recipe to JEI
     * @param recipeLayout The object to hold al the info
     * @param recipeWrapper The recipe, not always needed as all ingredients are added to ingredients
     * @param ingredients What holds the ingredients
     */
    @Override
    public void setRecipe(IRecipeLayout recipeLayout, JEICentrifugeRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiFluidStackGroup fluidStackGroup = recipeLayout.getFluidStacks();

        // Load the fluids
        fluidStackGroup.init(0, true,  37,  8, 49, 62, 2000, false, null);
        fluidStackGroup.init(1, false, 121, 8, 16, 62, 2000, false, null);
        fluidStackGroup.init(2, false, 142, 8, 16, 62, 2000, false, null);

        // Set into layout
        recipeLayout.getFluidStacks().set(0, ingredients.getInputs(FluidStack.class).get(0));
        recipeLayout.getFluidStacks().set(1, ingredients.getOutputs(FluidStack.class).get(0));
        recipeLayout.getFluidStacks().set(2, ingredients.getOutputs(FluidStack.class).get(1));
    }

    /*******************************************************************************************************************
     * Class Methods                                                                                                   *
     *******************************************************************************************************************/

    /**
     * Used to generate a list of all recipes for this category
     * @return The completed list of recipes
     */
    public static java.util.List<JEICentrifugeRecipeWrapper> buildRecipeList() {
        ArrayList<JEICentrifugeRecipeWrapper> recipes = new ArrayList<>();
        CentrifugeRecipeHandler centrifugeRecipeHandler = RecipeManager.getHandler(RecipeManager.RecipeType.CENTRIFUGE);
        for(CentrifugeRecipeHandler.CentrifugeRecipe recipe : centrifugeRecipeHandler.recipes) {
            FluidStack fluidInput = recipe.getFluidStackFromString(recipe.fluidStackInput);
            FluidStack fluidOutputOne = recipe.getFluidStackFromString(recipe.fluidStackOutputOne);
            FluidStack fluidOutputTwo   = recipe.getFluidStackFromString(recipe.fluidStackOutputTwo);
            if(fluidInput != null && fluidOutputOne != null && fluidOutputTwo != null)
                recipes.add(new JEICentrifugeRecipeWrapper(fluidInput, fluidOutputOne, fluidOutputTwo));
            else
                LogHelper.logger.error("[Neotech] Centrifuge Recipe json is corrupt! Please delete and run again.");
        }

        return recipes;
    }
}
