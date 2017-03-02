package com.teambrmodding.neotech.common.metals.fluids;

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
 * @since 2/16/2017
 */
public class FluidMetal extends Fluid {
    private int color;

    /**
     * Creates a metal fluid
     * @param color     The metal color
     * @param fluidName The name
     * @param still     Still icon
     * @param flowing   Flowing icon
     */
    public FluidMetal(int color, String fluidName, ResourceLocation still, ResourceLocation flowing) {
        super(fluidName, still, flowing);
        this.color = color;
        setLuminosity(10);
        setDensity(3000);
        setViscosity(6000);
        setTemperature(600);
    }

    /*******************************************************************************************************************
     * Fluid                                                                                                           *
     *******************************************************************************************************************/

    @Override
    public int getColor() {
        return color;
    }
}
