package com.teambrmodding.neotech.api.jei.fluidGenerator;

import com.teambr.bookshelf.util.ClientUtils;
import com.teambr.bookshelf.util.EnergyUtils;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fluids.FluidStack;

import java.awt.*;

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
public class JEIFluidGeneratorRecipeWrapper extends BlankRecipeWrapper {

    // Variables
    private FluidStack fluid;
    private int burnTime, burnRate;


    /*******************************************************************************************************************
     * Constructor                                                                                                     *
     *******************************************************************************************************************/

    public JEIFluidGeneratorRecipeWrapper(FluidStack input, int time, int burnRate) {
        fluid = input;
        burnTime = time;
        this.burnRate = burnRate;
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
        minecraft.fontRendererObj.drawString(fluid.getLocalizedName(), 33, 0, Color.darkGray.getRGB());
        minecraft.fontRendererObj.drawString(EnergyUtils.getEnergyDisplay(burnRate) + "/tick", 33, 46, Color.darkGray.getRGB());
        minecraft.fontRendererObj.drawString(ClientUtils.formatNumber(burnTime) + " ticks", 33, 55, Color.darkGray.getRGB());
        minecraft.fontRendererObj.drawString(EnergyUtils.getEnergyDisplay(burnRate * burnTime), 33, 64, Color.darkGray.getRGB());
    }
}
