package com.teambrmodding.neotech.api.jei.fluidGenerator;

import com.teambrmodding.neotech.api.jei.NeotechJEIPlugin;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.awt.*;

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
public class JEIFluidGeneratorRecipeWrapper extends BlankRecipeWrapper {

    // Variables
    private FluidStack fluid;
    private int burnTime;

    private IDrawableAnimated flame;

    /*******************************************************************************************************************
     * Constructor                                                                                                     *
     *******************************************************************************************************************/

    public JEIFluidGeneratorRecipeWrapper(FluidStack input, int time) {
        fluid = input;
        burnTime = time;

        ResourceLocation furnaceBackgroundLocation = new ResourceLocation("minecraft", "textures/gui/container/furnace.png");
        IDrawableStatic flameDrawable = NeotechJEIPlugin.jeiHelpers.getGuiHelper().createDrawable(furnaceBackgroundLocation, 176, 0, 14, 14);
        this.flame = NeotechJEIPlugin.jeiHelpers.getGuiHelper().createAnimatedDrawable(flameDrawable, 100, IDrawableAnimated.StartDirection.TOP, true);
    }

    /*******************************************************************************************************************
     * Helper Methods                                                                                                  *
     *******************************************************************************************************************/

    /**
     * Used to make sure this recipe has been created correctly
     * @return True if recipe can be displayed
     */
    public boolean isValid() {
        return burnTime > 0 && fluid != null;
    }

    /*******************************************************************************************************************
     * BlankRecipeWrapper                                                                                              *
     *******************************************************************************************************************/

    /**
     * Exposes the ingredients to JEI for display
     * @param ingredients The ingredients object
     */
    @Override
    public void getIngredients(IIngredients ingredients) {
        // Set fluid
        ingredients.setInput(FluidStack.class, fluid);
    }

    /**
     * Draw the burn time
     */
    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        flame.draw(minecraft, 40, 10);
        minecraft.fontRendererObj.drawString(String.valueOf(burnTime) + " ticks", 58, 17, Color.gray.getRGB());
    }
}
