package com.teambrmodding.neotech.api.jei.solidifier;

import com.teambr.bookshelf.api.jei.drawables.GuiComponentItemStackButtonJEI;
import com.teambrmodding.neotech.common.tiles.machines.processors.TileSolidifier;
import com.teambrmodding.neotech.managers.MetalManager;
import com.teambrmodding.neotech.utils.ClientUtils;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

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
public class JEISolidifierRecipeWrapper extends BlankRecipeWrapper {

    // Variables
    private FluidStack input;
    private ItemStack output;
    private int mode;

    private GuiComponentItemStackButtonJEI modeButton;

    /*******************************************************************************************************************
     * Constructor                                                                                                     *
     *******************************************************************************************************************/

    /**
     * Recipe Wrapper
     * @param in The
     * @param out
     * @param solidifyMode
     */
    public JEISolidifierRecipeWrapper(FluidStack in, ItemStack out, int solidifyMode) {
        input = in;
        output = out;
        mode = solidifyMode;

        ItemStack displayStack = new ItemStack(Blocks.IRON_BLOCK);
        switch (mode) {
            case 1 :
                displayStack = new ItemStack(Items.IRON_INGOT);
                break;
            case 2 :
                displayStack = new ItemStack(MetalManager.getMetal("iron").get().nugget().get());
                break;
            default:
        }
        modeButton = new GuiComponentItemStackButtonJEI(97, 37, displayStack);
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
        modeButton.draw(minecraft);
    }

    /**
     * Add mode tool tip
     */
    @Nullable
    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        if(mouseX > 97 && mouseX < 117 && mouseY > 37 && mouseY < 57) {
            String tipString;
            switch (mode) {
                case 0 :
                    tipString = ClientUtils.translate("neotech.text.blockMode");
                    break;
                case 1 :
                    tipString = ClientUtils.translate("neotech.text.ingotMode");
                    break;
                case 2 :
                    tipString = ClientUtils.translate("neotech.text.nuggetMode");
                    break;
                default :
                    tipString = ClientUtils.translate("neotech.text.blockMode");
            }
            return Collections.singletonList(tipString);
        }
        return super.getTooltipStrings(mouseX, mouseY);
    }
}
