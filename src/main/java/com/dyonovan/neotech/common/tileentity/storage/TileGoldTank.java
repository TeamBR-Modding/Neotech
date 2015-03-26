package com.dyonovan.neotech.common.tileentity.storage;

import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidTank;

public class TileGoldTank extends TileIronTank {
    @Override
    public void setTank() {
        this.tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 16);
    }
}
