package com.teambrmodding.neotech.common.blocks.machines;

import com.teambrmodding.neotech.common.tiles.machines.operators.TileTreeFarm;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

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
public class BlockTreeFarm extends BlockMachine {
    /**
     * Main constructor for the block
     */
    public BlockTreeFarm(String name) {
        super(name, TileTreeFarm.class);
    }

    /*******************************************************************************************************************
     * Block Methods                                                                                                   *
     *******************************************************************************************************************/

    /**
     * Called when the block is placed
     * @param worldIn The world
     * @param pos The block position
     * @param state The block state
     * @param placer Who placed the block
     * @param stack The stack that was the block
     */
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {}

    /**
     * Called to display particles
     * @param stateIn The state
     * @param worldIn The world
     * @param pos The pos
     * @param rand An instance or Random
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {}

    /*******************************************************************************************************************
     * BlockState Methods                                                                                              *
     *******************************************************************************************************************/

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    /**
     * Creates the block state with our properties
     * @return The block state
     */
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this);
    }

    /**
     * We want to get the actual state, passes info to the model not present in meta
     *
     * Is listed deprecated in favor of logic in block state, but our state calls this
     *
     * @param state The incoming state
     * @param worldIn The world
     * @param pos The position
     * @return A state that represents the actual state, not just what was stored
     */
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return state;
    }
}
