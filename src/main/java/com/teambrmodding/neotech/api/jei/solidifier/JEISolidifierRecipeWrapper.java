package com.teambrmodding.neotech.api.jei.solidifier;

import com.teambr.bookshelf.helper.GuiHelper;
import com.teambr.bookshelf.util.ClientUtils;
import com.teambrmodding.neotech.managers.MetalManager;
import com.teambrmodding.neotech.registries.SolidifierRecipeHandler;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.Collections;
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
public class JEISolidifierRecipeWrapper extends BlankRecipeWrapper {

    // Variables
    private FluidStack input;
    private ItemStack output;
    private SolidifierRecipeHandler.SolidifierMode currentMode;

    /*******************************************************************************************************************
     * Constructor                                                                                                     *
     *******************************************************************************************************************/

    /**
     * Recipe Wrapper
     * @param in The
     * @param out
     * @param solidifyMode
     */
    public JEISolidifierRecipeWrapper(FluidStack in, ItemStack out, SolidifierRecipeHandler.SolidifierMode solidifyMode) {
        input = in;
        output = out;
        currentMode = solidifyMode;
    }

    /*******************************************************************************************************************
     * Helper Methods                                                                                                  *
     *******************************************************************************************************************/

    /**
     * Used to make sure this recipe has been created correctly
     * @return True if recipe can be displayed
     */
    public boolean isValid() {
        return input != null && output != null;
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
        // Set input
        ingredients.setInput(FluidStack.class, input);

        // Set output
        ingredients.setOutput(ItemStack.class, output);
    }

    /**
     * Used to draw button
     */
    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();

        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(currentMode.getDisplayStack(), 96, 53);

        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    /**
     * Add mode tool tip
     */
    @Nullable
    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        if(mouseX > 96 && mouseX < 117 && mouseY > 53 && mouseY < 62) {
            return Collections.singletonList(currentMode.getDisplayName());
        }
        return super.getTooltipStrings(mouseX, mouseY);
    }
}
