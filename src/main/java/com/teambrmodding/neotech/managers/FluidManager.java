package com.teambrmodding.neotech.managers;

import com.teambrmodding.neotech.common.fluids.BaseFluid;
import com.teambrmodding.neotech.common.fluids.BaseFluidBlock;
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
    // Hydrogen
    public static BaseFluid hydrogen;
    public static BaseFluidBlock blockHydrogen;

    // Oxygen
    public static BaseFluid oxygen;
    public static BaseFluidBlock blockOxygen;

    /**
     * Loads the fluids
     */
    public static void preInit() {
        hydrogen = createFluidGas(0xFF0044BB, true, "hydrogen");
        blockHydrogen = registerFluidBlock(hydrogen, new BaseFluidBlock(hydrogen));

        oxygen = createFluidGas(0xFFDDDDDD, true, "oxygen");
        blockOxygen = registerFluidBlock(oxygen, new BaseFluidBlock(oxygen));
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
    public static BaseFluid createFluidGas(int color, boolean isGas, String name) {
        ResourceLocation stillIcon   = new ResourceLocation(Reference.MOD_ID, "blocks/metal_still");
        ResourceLocation flowingIcon = new ResourceLocation(Reference.MOD_ID, "blocks/metal_flow");

        BaseFluid fluid = new BaseFluid(color, isGas, name, stillIcon, flowingIcon);
        FluidRegistry.registerFluid(fluid);
        if(!FluidRegistry.getBucketFluids().contains(fluid))
            FluidRegistry.addBucketForFluid(fluid);

        return fluid;
    }

    /**
     * Assigns a fluid block to a fluid and registers the block
     * @param fluid The fluid
     * @param block The block
     * @return The registered block
     */
    public static BaseFluidBlock registerFluidBlock(Fluid fluid, BaseFluidBlock block) {
        if(fluid != null) {
            fluid.setBlock(block);
            BlockManager.registerBlock(block);
        }
        return block;
    }
}
