package com.teambrmodding.neotech.api.jei.centrifuge;

import com.teambr.bookshelf.api.jei.drawables.GuiComponentArrowJEI;
import com.teambr.bookshelf.api.jei.drawables.GuiComponentBox;
import com.teambr.bookshelf.api.jei.drawables.GuiComponentPowerBarJEI;
import com.teambr.bookshelf.helper.LogHelper;
import com.teambrmodding.neotech.api.jei.NeotechJEIPlugin;
import com.teambrmodding.neotech.lib.Reference;
import com.teambrmodding.neotech.managers.RecipeManager;
import com.teambrmodding.neotech.registries.CentrifugeRecipe;
import com.teambrmodding.neotech.registries.CentrifugeRecipeHandler;
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
    private ResourceLocation backgroundResource = new ResourceLocation(Reference.MOD_ID(), "textures/gui/jei/jei.png");
    private GuiComponentArrowJEI progressArrow  = new GuiComponentArrowJEI(94, 17, NeotechJEIPlugin.jeiHelpers);
    private GuiComponentPowerBarJEI powerBar    = new GuiComponentPowerBarJEI(14, 0, 18, 60, new Color(255, 0, 0), NeotechJEIPlugin.jeiHelpers);

    // Tanks
    private GuiComponentBox tankInput     = new GuiComponentBox(38, 0, 50, 60);
    private GuiComponentBox tankOutputOne = new GuiComponentBox(125, 0, 18, 60);
    private GuiComponentBox tankOutputTwo = new GuiComponentBox(147, 0, 18, 60);

    /*******************************************************************************************************************
     * Constructor                                                                                                     *
     *******************************************************************************************************************/

    /**
     * Constructor, we want to add the colors to our powerBar here
     */
    public JEICentrifugeRecipeCategory() {
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
        tankInput.draw(minecraft);
        tankOutputOne.draw(minecraft);
        tankOutputTwo.draw(minecraft);

        // Draw Animations
        progressArrow.draw(minecraft);
        powerBar.draw(minecraft, 0, 0);
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
        fluidStackGroup.init(0, true,  39,  0, 48, 59, 2000, false, null);
        fluidStackGroup.init(1, false, 126, 0, 16, 59, 2000, false, null);
        fluidStackGroup.init(2, false, 148, 0, 16, 59, 2000, false, null);

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
        CentrifugeRecipeHandler centrifugeRecipeHandler = (CentrifugeRecipeHandler) RecipeManager.getHandler("centrifuge").get();
        for(CentrifugeRecipe recipe : centrifugeRecipeHandler.recipes()) {
            FluidStack fluidInput = recipe.getFluidFromString(recipe.fluidIn());
            FluidStack fluidOutputOne = recipe.getFluidFromString(recipe.fluidOne());
            FluidStack fluidOutputTwo   = recipe.getFluidFromString(recipe.fluidTwo());
            if(fluidInput != null && fluidOutputOne != null && fluidOutputTwo != null)
                recipes.add(new JEICentrifugeRecipeWrapper(fluidInput, fluidOutputOne, fluidOutputTwo));
            else
                LogHelper.severe("[Neotech] Centrifuge Recipe json is corrupt! Please delete and run again.");
        }

        return recipes;
    }
}
