package com.dyonovan.jatm.collections;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.Collection;

public class DummyState implements IBlockState {
    public IBlockAccess blockAccess;
    public BlockPos pos;

    public DummyState(IBlockAccess w, BlockPos p) {
        this.blockAccess = w;
        this.pos = p;
    }

    public Collection getPropertyNames() {
        return null;
    }

    public Comparable getValue(IProperty property) {
        return null;
    }

    public IBlockState withProperty(IProperty property, Comparable value) {
        return null;
    }

    public IBlockState cycleProperty(IProperty property) {
        return null;
    }

    public ImmutableMap getProperties() {
        return null;
    }

    public Block getBlock() {
        return null;
    }
}