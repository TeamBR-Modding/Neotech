package com.teambrmodding.neotech.common.fluids;

import com.teambr.bookshelf.util.ClientUtils;
import com.teambrmodding.neotech.lib.Reference;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidClassic;

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
public class BaseFluidBlock extends BlockFluidClassic {
    private BaseFluid fluid;

    /**
     * Creates the fluid block
     * @param fluid The associated fluid
     */
    public BaseFluidBlock(BaseFluid fluid) {
        super(fluid, Material.WATER);
        this.fluid = fluid;
        setRegistryName(new ResourceLocation(Reference.MOD_ID, "fluid." + fluid.getName()));
        setUnlocalizedName(Reference.MOD_ID + ":" + fluid.getUnlocalizedName());
    }

    /**
     * Gets the color of this fluid block
     * @return The block color
     */
    public int getBlockColor() {
        return fluid.getColor();
    }
}
