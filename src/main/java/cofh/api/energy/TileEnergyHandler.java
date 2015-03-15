package cofh.api.energy;

import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * Reference implementation of {@link IEnergyHandler}. Use/extend this or implement your own.
 *
 * @author King Lemming
 *
 */
public class TileEnergyHandler extends TileEntity implements IEnergyHandler {

	protected EnergyStorage storage = new EnergyStorage(32000);

	@Override
	public void readFromNBT(NBTTagCompound nbt) {

		super.readFromNBT(nbt);
		storage.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {

		super.writeToNBT(nbt);
		storage.writeToNBT(nbt);
	}

	/* IEnergyConnection */
	@Override
	public boolean canConnectEnergy(EnumFaceDirection from) {

		return true;
	}

	/* IEnergyReceiver */
	@Override
	public int receiveEnergy(EnumFaceDirection from, int maxReceive, boolean simulate) {

		return storage.receiveEnergy(maxReceive, simulate);
	}

	/* IEnergyProvider */
	@Override
	public int extractEnergy(EnumFaceDirection from, int maxExtract, boolean simulate) {

		return storage.extractEnergy(maxExtract, simulate);
	}

	/* IEnergyReceiver and IEnergyProvider */
	@Override
	public int getEnergyStored(EnumFaceDirection from) {

		return storage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFaceDirection from) {

		return storage.getMaxEnergyStored();
	}

}
