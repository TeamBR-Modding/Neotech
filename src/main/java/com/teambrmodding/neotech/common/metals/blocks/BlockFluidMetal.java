package com.teambrmodding.neotech.common.metals.blocks;

import com.teambrmodding.neotech.Neotech;
import com.teambrmodding.neotech.lib.Reference;
import net.minecraft.block.material.Material;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidClassic;
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
public class BlockFluidMetal extends BlockFluidClassic {

    /**
     * Creates the block
     * @param fluid The fluid to associate with
     */
    public BlockFluidMetal(Fluid fluid) {
        super(fluid, Material.LAVA);
        setRegistryName(new ResourceLocation(Reference.MOD_ID, "fluid." + fluid.getName()));
        setUnlocalizedName(Reference.MOD_ID + ":" + fluid.getUnlocalizedName());
        setCreativeTab(Neotech.tabMetals);
    }

    /**
     * Gets the color of this block
     * @return The block color
     */
    public int getBlockColor() {
        return definedFluid.getColor();
    }

    /*******************************************************************************************************************
     * Fluid                                                                                                           *
     *******************************************************************************************************************/

    /**
     * Chance that fire will spread and consume this block.
     * 300 being a 100% chance, 0, being a 0% chance.
     *
     * @param world The current world
     * @param pos   Block position in world
     * @param face  The face that the fire is coming from
     * @return A number ranging from 0 to 300 relating used to determine if the block will be consumed by fire
     */
    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 0;
    }

    /**
     * Called when fire is updating on a neighbor block.
     * The higher the number returned, the faster fire will spread around this block.
     *
     * @param world The current world
     * @param pos   Block position in world
     * @param face  The face that the fire is coming from
     * @return A number that is used to determine the speed of fire growth around the block
     */
    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 300;
    }
}
