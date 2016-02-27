package com.dyonovan.neotech.common.blocks;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.Collection;

/**
 * This file was created for NeoTech
 * <p/>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis "pauljoda"
 * @since 2/26/2016
 */
public class ConnectedTexturesState implements IBlockState {
    public BlockPos pos;
    public IBlockAccess world;
    public BlockConnectedTextures block;
    public Block holder;

    public ConnectedTexturesState(BlockPos p, IBlockAccess w, BlockConnectedTextures b, Block h) {
        pos = p;
        world = w;
        block = b;
        holder = h;
    }

    @Override
    public Collection<IProperty> getPropertyNames() {
        return null;
    }

    @Override
    public <T extends Comparable<T>> T getValue(IProperty<T> property) {
        return null;
    }

    @Override
    public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V value) {
        return null;
    }

    @Override
    public <T extends Comparable<T>> IBlockState cycleProperty(IProperty<T> property) {
        return null;
    }

    @Override
    public ImmutableMap<IProperty, Comparable> getProperties() {
        return null;
    }

    @Override
    public Block getBlock() {
        return holder;
    }
}
