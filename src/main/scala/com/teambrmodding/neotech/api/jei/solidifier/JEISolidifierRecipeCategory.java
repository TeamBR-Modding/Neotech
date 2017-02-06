package com.teambrmodding.neotech.api.jei.solidifier;

import com.teambr.bookshelf.api.jei.drawables.GuiComponentArrowJEI;
import com.teambr.bookshelf.api.jei.drawables.GuiComponentBox;
import com.teambr.bookshelf.api.jei.drawables.GuiComponentPowerBarJEI;
import com.teambr.bookshelf.api.jei.drawables.SlotDrawable;
import com.teambrmodding.neotech.api.jei.NeotechJEIPlugin;
import com.teambrmodding.neotech.lib.Reference;
import com.teambrmodding.neotech.managers.RecipeManager;
import com.teambrmodding.neotech.registries.SolidifierRecipe;
import com.teambrmodding.neotech.registries.SolidifierRecipeHandler;
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
public class JEISolidifierRecipeCategory implements IRecipeCategory<JEISolidifierRecipeWrapper> {

    // Variables
    private ResourceLocation backgroundResource = new ResourceLocation(Reference.MOD_ID(), "textures/gui/jei/jei.png");
    private GuiComponentArrowJEI progressArrow  = new GuiComponentArrowJEI(97, 17, NeotechJEIPlugin.jeiHelpers);
    private GuiComponentPowerBarJEI powerBar    = new GuiComponentPowerBarJEI(14, 0, 18, 60, new Color(255, 0, 0), NeotechJEIPlugin.jeiHelpers);
    private GuiComponentBox inputTank           = new GuiComponentBox(35, 0, 50, 60);
    private SlotDrawable slotOutput             = new SlotDrawable(133, 17, false);

    /*******************************************************************************************************************
     * Constructor                                                                                                     *
     *******************************************************************************************************************/

    /**
     * Constructor, we want to add the colors to our powerBar here
     */
    public JEISolidifierRecipeCategory() {
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
        inputTank.draw(minecraft);
        slotOutput.draw(minecraft);

        // Draw Animations
        progressArrow.draw(minecraft);
        powerBar.draw(minecraft, 0, 0);
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
        fluidStackGroup.init(0, true, 36, 0, 48, 59, 2000, false, null);
        itemStackGroup.init(0, false, 133, 17);

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
        SolidifierRecipeHandler centrifugeRecipeHandler = (SolidifierRecipeHandler) RecipeManager.getHandler("solidifier").get();
        for(SolidifierRecipe recipe : centrifugeRecipeHandler.recipes()) {
            int mode;
            int amount = recipe.getFluidFromString(recipe.input()).amount;
            switch (amount) {
                case 1296 :
                    mode = 0;
                    break;
                case 144 :
                    mode = 1;
                    break;
                case 16 :
                    mode = 2;
                    break;
                default :
                    mode = -1;

            }
            recipes.add(new JEISolidifierRecipeWrapper(recipe.getFluidFromString(recipe.input()), recipe.getItemStackFromString(recipe.output()), mode));
        }
        return recipes;
    }
}
