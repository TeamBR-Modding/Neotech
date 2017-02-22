package com.teambrmodding.neotech.api.jei.solidifier;

import com.teambr.bookshelf.util.ClientUtils;
import com.teambrmodding.neotech.api.jei.NeotechJEIPlugin;
import com.teambrmodding.neotech.lib.Reference;
import com.teambrmodding.neotech.managers.RecipeManager;
import com.teambrmodding.neotech.registries.SolidifierRecipeHandler;
import com.teambrmodding.neotech.registries.recipes.AbstractRecipe;
import com.teambrmodding.neotech.registries.recipes.SolidifierRecipe;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

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
public class JEISolidifierRecipeCategory implements IRecipeCategory<JEISolidifierRecipeWrapper> {

    // Variables
    private ResourceLocation backgroundResource = new ResourceLocation(Reference.MOD_ID, "textures/gui/jei/electricSolidifier.png");
    private IDrawableAnimated progressArrow;
    private IDrawableAnimated powerBar;
    private IDrawableStatic button;

    /*******************************************************************************************************************
     * Constructor                                                                                                     *
     *******************************************************************************************************************/

    /**
     * Constructor
     */
    public JEISolidifierRecipeCategory() {
        IDrawableStatic progressArrowDrawable = NeotechJEIPlugin.jeiHelpers.getGuiHelper().createDrawable(backgroundResource, 170, 0, 23, 17);
        progressArrow = NeotechJEIPlugin.jeiHelpers.getGuiHelper().createAnimatedDrawable(progressArrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);

        IDrawableStatic powerBarDrawable = NeotechJEIPlugin.jeiHelpers.getGuiHelper().createDrawable(backgroundResource, 170, 17, 16, 62);
        powerBar = NeotechJEIPlugin.jeiHelpers.getGuiHelper().createAnimatedDrawable(powerBarDrawable, 300, IDrawableAnimated.StartDirection.TOP, true);

        button = NeotechJEIPlugin.jeiHelpers.getGuiHelper().createDrawable(backgroundResource, 218, 0, 22, 22);
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
        return NeotechJEIPlugin.SOLIDIFIER_UUID;
    }

    /**
     * Used to display the title
     * @return The translated title
     */
    @Override
    public String getTitle() {
        return ClientUtils.translate("tile.neotech:electricSolidifier.name");
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
        progressArrow.draw(minecraft, 92, 32);
        powerBar.draw(minecraft, 13, 9);
        button.draw(minecraft, 93, 50);
    }

    @Override
    public void drawAnimations(Minecraft minecraft) {
        // Deprecated, moved to drawExtras
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, JEISolidifierRecipeWrapper recipeWrapper) {
        // Deprecated
    }

    /**
     * Actually exposes the recipe to JEI
     * @param recipeLayout The object to hold al the info
     * @param recipeWrapper The recipe, not always needed as all ingredients are added to ingredients
     * @param ingredients What holds the ingredients
     */
    @Override
    public void setRecipe(IRecipeLayout recipeLayout, JEISolidifierRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiFluidStackGroup fluidStackGroup = recipeLayout.getFluidStacks();
        IGuiItemStackGroup itemStackGroup   = recipeLayout.getItemStacks();

        // Init components
        fluidStackGroup.init(0, true, 37, 9, 49, 62, 2000, false, null);
        itemStackGroup.init(0, false, 129, 31);

        // Fill layout
        recipeLayout.getFluidStacks().set(0, ingredients.getInputs(FluidStack.class).get(0));
        recipeLayout.getItemStacks().set(0, ingredients.getOutputs(ItemStack.class).get(0));
    }

    /*******************************************************************************************************************
     * Class Methods                                                                                                   *
     *******************************************************************************************************************/

    /**
     * Used to generate a list of all recipes for this category
     * @return The completed list of recipes
     */
    public static java.util.List<JEISolidifierRecipeWrapper> buildRecipeList() {
        ArrayList<JEISolidifierRecipeWrapper> recipes = new ArrayList<>();
        SolidifierRecipeHandler centrifugeRecipeHandler = RecipeManager.getHandler(RecipeManager.RecipeType.SOLIDIFIER);
        for(SolidifierRecipe recipe : centrifugeRecipeHandler.recipes) {

            recipes.add(new JEISolidifierRecipeWrapper(AbstractRecipe.getFluidStackFromString(recipe.inputFluidStack),
                    AbstractRecipe.getItemStackFromString(recipe.outputItemStack), recipe.requiredMode));
        }
        return recipes;
    }
}
