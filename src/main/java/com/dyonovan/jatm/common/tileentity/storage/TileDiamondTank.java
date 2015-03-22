package com.dyonovan.jatm.common.tileentity.storage;

import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidTank;

public class TileDiamondTank extends TileIronTank {
    @Override
    public void setTank() {
        this.tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 64);
    }
}
