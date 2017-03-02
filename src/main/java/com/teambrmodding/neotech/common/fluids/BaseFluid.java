package com.teambrmodding.neotech.common.fluids;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/15/2017
 */
public class BaseFluid extends Fluid {
    private int color;

    /**
     * Creates the Fluid
     * @param fluidName The name
     * @param still     Still Texture
     * @param flowing   Flowing Texture
     */
    public BaseFluid(int color, boolean isGas, String fluidName, ResourceLocation still, ResourceLocation flowing) {
        super(fluidName, still, flowing);
        this.color = color;
        setGaseous(isGas);
    }

    /*******************************************************************************************************************
     * Fluid                                                                                                           *
     *******************************************************************************************************************/

    /**
     * Get the color for this fluid
     * @return The color
     */
    @Override
    public int getColor() {
        return color;
    }
}

