package com.teambrmodding.neotech.api.jei.crucible;

import com.teambr.bookshelf.helper.LogHelper;
import com.teambr.bookshelf.util.ClientUtils;
import com.teambrmodding.neotech.api.jei.NeotechJEIPlugin;
import com.teambrmodding.neotech.lib.Reference;
import com.teambrmodding.neotech.managers.RecipeManager;
import com.teambrmodding.neotech.registries.recipes.AbstractRecipe;
import com.teambrmodding.neotech.registries.CrucibleRecipeHandler;
import com.teambrmodding.neotech.registries.recipes.CrucibleRecipe;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;

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
public class JEICrucibleRecipeCategory implements IRecipeCategory<JEICrucibleRecipeWrapper> {

    // Variables
    private ResourceLocation backgroundResource = new ResourceLocation(Reference.MOD_ID, "textures/gui/jei/electricCrucible.png");
    private IDrawableAnimated progressArrow;
    private IDrawableAnimated powerBar;

    /*******************************************************************************************************************
     * Constructor                                                                                                     *
     *******************************************************************************************************************/

    public JEICrucibleRecipeCategory() {
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
        itemStackGroup.init(0, true, 52, 31);
        fluidStackGroup.init(0, false, 109, 9, 49, 62, 2000, false, null);

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
        CrucibleRecipeHandler crucibleRecipeHandler = RecipeManager.getHandler(RecipeManager.RecipeType.CRUCIBLE);
        for(CrucibleRecipe recipe : crucibleRecipeHandler.recipes) {
            java.util.List<ItemStack> inputList = OreDictionary.getOres(recipe.inputItemStack);
            FluidStack output = AbstractRecipe.getFluidStackFromString(recipe.outputFluidStack);
            if(output != null) {
                if(!inputList.isEmpty()) {
                    recipes.add(new JEICrucibleRecipeWrapper(inputList, output));
                } else {
                    ItemStack input = AbstractRecipe.getItemStackFromString(recipe.inputItemStack);
                    if(input != null) {
                        recipes.add(new JEICrucibleRecipeWrapper(Collections.singletonList(input), output));
                    } else LogHelper.logger.error("[NeoTech] Crucible json is corrupt! Please delete and recreate!");
                }
            } else LogHelper.logger.error("[NeoTech] Crucible json is corrupt! Please delete and recreate!");
        }
        return recipes;
    }
}
