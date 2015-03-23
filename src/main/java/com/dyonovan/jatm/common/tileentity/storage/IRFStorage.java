package com.dyonovan.jatm.common.tileentity.storage;

import cofh.api.energy.EnergyStorage;
import net.minecraft.util.BlockPos;

public interface IRFStorage {

    public int getTier();

    public EnergyStorage getRF();

    public BlockPos getTilePos();
}
