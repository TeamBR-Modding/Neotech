package com.dyonovan.jatm.collections;

import com.dyonovan.jatm.common.blocks.BlockBakeable;
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
    public BlockBakeable block;

    public DummyState(IBlockAccess w, BlockPos p, BlockBakeable blockBakeable) {
        this.blockAccess = w;
        this.pos = p;
        this.block = blockBakeable;
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

    public BlockBakeable getBlock() {
        return block;
    }
}