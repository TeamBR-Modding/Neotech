package com.teambrmodding.neotech.managers;

import com.teambrmodding.neotech.common.fluids.FluidBlockGas;
import com.teambrmodding.neotech.common.fluids.FluidGas;
import com.teambrmodding.neotech.lib.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/15/2017
 */
public class FluidManager {

    // Gasses
    public static FluidGas hydrogen;
    public static FluidBlockGas blockHydrogen;

    public static FluidGas oxygen;
    public static FluidBlockGas blockOxygen;

    /**
     * Loads the fluids
     */
    public static void preInit() {
        hydrogen = createFluidGas(0xFF0044BB, "hydrogen");
        blockHydrogen = registerFluidBlock(hydrogen, new FluidBlockGas(hydrogen));

        oxygen = createFluidGas(0xFFDDDDDD, "oxygen");
        blockOxygen = registerFluidBlock(oxygen, new FluidBlockGas(oxygen));
    }

    /*******************************************************************************************************************
     * Helper Methods                                                                                                  *
     *******************************************************************************************************************/

    /**
     * Creates a fluid gas object
     * @param color The color
     * @param name  The name
     * @return The registered object
     */
    public static FluidGas createFluidGas(int color, String name) {
        ResourceLocation stillIcon   = new ResourceLocation(Reference.MOD_ID, "blocks/metal_still");
        ResourceLocation flowingIcon = new ResourceLocation(Reference.MOD_ID, "blocks/metal_flow");

        FluidGas fluidGas = new FluidGas(color, name, stillIcon, flowingIcon);
        FluidRegistry.registerFluid(fluidGas);
        if(!FluidRegistry.getBucketFluids().contains(fluidGas))
            FluidRegistry.addBucketForFluid(fluidGas);

        return fluidGas;
    }

    /**
     * Assigns a fluid block to a fluid and registers the block
     * @param fluid The fluid
     * @param block The block
     * @return The registered block
     */
    public static FluidBlockGas registerFluidBlock(Fluid fluid, FluidBlockGas block) {
        if(fluid != null) {
            fluid.setBlock(block);
            BlockManager.registerBlock(block);
        }
        return block;
    }
}
