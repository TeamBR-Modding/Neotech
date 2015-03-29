package com.dyonovan.neotech.common.tileentity.storage;

import cofh.api.energy.IEnergyReceiver;
import com.dyonovan.neotech.common.blocks.BlockBakeable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class TileCreativeRFStorage extends TileEliteRFStorage {

    private static final int RF_TOTAL_CREATIVE = 100000000;
    private static final int RF_TICK_CREATIVE = 100000;

    public TileCreativeRFStorage() {
        setInventory(3);
        setEnergyRF(RF_TOTAL_CREATIVE, RF_TICK_CREATIVE);
        energyRF.setEnergyStored(RF_TOTAL_CREATIVE);
    }

    @Override
    public void transferEnergy() {
        EnumFacing out = (EnumFacing) getWorld().getBlockState(this.pos).getValue(BlockBakeable.PROPERTY_FACING);
        TileEntity tile = getWorld().getTileEntity(this.pos.offset(out));
        if (tile instanceof IEnergyReceiver) {
            int avail = Math.min(energyRF.getMaxExtract(), energyRF.getEnergyStored());
            int amount = ((IEnergyReceiver) tile).receiveEnergy(out.getOpposite(), avail, true);
            ((IEnergyReceiver) tile).receiveEnergy(out.getOpposite(), amount, false);
        }
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[] {0, 1, 2};
    }
}
