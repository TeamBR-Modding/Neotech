package com.teambrmodding.neotech.api.jei.fluidGenerator;

import com.teambr.bookshelf.util.ClientUtils;
import com.teambrmodding.neotech.api.jei.NeotechJEIPlugin;
import com.teambrmodding.neotech.lib.Reference;
import com.teambrmodding.neotech.managers.RecipeManager;
import com.teambrmodding.neotech.registries.FluidFuelRecipeHandler;
import com.teambrmodding.neotech.registries.recipes.AbstractRecipe;
import com.teambrmodding.neotech.registries.recipes.FluidFuelRecipe;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

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
public class JEIFluidGeneratorCategory implements IRecipeCategory<JEIFluidGeneratorRecipeWrapper> {

    // Display
    private ResourceLocation backgroundResource = new ResourceLocation(Reference.MOD_ID, "textures/gui/jei/fluidFuel.png");
    private IDrawableAnimated flame;
    private IDrawableAnimated powerBar;

    /*******************************************************************************************************************
     * Constructor                                                                                                     *
     *******************************************************************************************************************/

    /**
     * Constructor
     */

    public JEIFluidGeneratorCategory() {
        IDrawableStatic progressArrowDrawable = NeotechJEIPlugin.jeiHelpers.getGuiHelper().createDrawable(backgroundResource, 170, 0, 14, 14);
        flame = NeotechJEIPlugin.jeiHelpers.getGuiHelper().createAnimatedDrawable(progressArrowDrawable, 200, IDrawableAnimated.StartDirection.TOP, true);

        IDrawableStatic powerBarDrawable = NeotechJEIPlugin.jeiHelpers.getGuiHelper().createDrawable(backgroundResource, 170, 14, 16, 62);
        powerBar = NeotechJEIPlugin.jeiHelpers.getGuiHelper().createAnimatedDrawable(powerBarDrawable, 300, IDrawableAnimated.StartDirection.BOTTOM, false);
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
        return NeotechJEIPlugin.FLUID_GEN_UUID;
    }

    /**
     * Used to display the title
     * @return The translated title
     */
    @Override
    public String getTitle() {
        return ClientUtils.translate("tile.neotech:fluidGenerator.name");
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
        flame.draw(minecraft, 77, 24);
        powerBar.draw(minecraft, 13, 9);
    }

    @Override
    public void drawAnimations(Minecraft minecraft) {
        // Deprecated, moved to drawExtras
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, JEIFluidGeneratorRecipeWrapper recipeWrapper) {
        // Deprecated
    }

    /**
     * Actually exposes the recipe to JEI
     * @param recipeLayout The object to hold al the info
     * @param recipeWrapper The recipe, not always needed as all ingredients are added to ingredients
     * @param ingredients What holds the ingredients
     */
    @Override
    public void setRecipe(IRecipeLayout recipeLayout, JEIFluidGeneratorRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiFluidStackGroup fluidStackGroup = recipeLayout.getFluidStacks();
        IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();

        // Load
        fluidStackGroup.init(0, true, 141, 9, 16, 62, 2000, false, null);
        itemStackGroup.init(0, true, 119, 8);
        itemStackGroup.init(1, false, 119, 54);

        // Set to layout
        recipeLayout.getFluidStacks().set(0, ingredients.getInputs(FluidStack.class).get(0));

        List<ItemStack> filledStacks = new ArrayList<>();
        List<ItemStack> emptyStacks = new ArrayList<>();
        for(Item item : Item.REGISTRY) {
            ItemStack stack = new ItemStack(item);
            IFluidHandler provider = FluidUtil.getFluidHandler(stack);
            if(provider != null) {
                int amount = provider.fill(ingredients.getInputs(FluidStack.class).get(0).get(0), false);
                if(amount > 0) {
                    emptyStacks.add(stack.copy());
                    provider.fill(ingredients.getInputs(FluidStack.class).get(0).get(0), true);
                    filledStacks.add(stack);
                }
            }
        }
        recipeLayout.getItemStacks().set(0, filledStacks);
        recipeLayout.getItemStacks().set(1, emptyStacks);
    }

    /*******************************************************************************************************************
     * Class Methods                                                                                                   *
     *******************************************************************************************************************/

    /**
     * Used to generate a list of all recipes for this category
     * @return The completed list of recipes
     */
    public static java.util.List<JEIFluidGeneratorRecipeWrapper> buildRecipeList() {
        ArrayList<JEIFluidGeneratorRecipeWrapper> recipes = new ArrayList<>();
        FluidFuelRecipeHandler fluidFuelRecipeHandler = RecipeManager.getHandler(RecipeManager.RecipeType.FLUID_FUELS);
        for(FluidFuelRecipe recipe : fluidFuelRecipeHandler.recipes) {
            FluidStack fluid = AbstractRecipe.getFluidStackFromString(recipe.fluidStackInput);
            recipes.add(new JEIFluidGeneratorRecipeWrapper(fluid, recipe.burnTime, recipe.burnRate));
        }
        return recipes;
    }
}
