package com.dyonovan.neotech.common.pipe.item;

import com.dyonovan.neotech.common.pipe.Pipe;
import com.dyonovan.neotech.common.pipe.storage.ItemBuffer;
import com.dyonovan.neotech.common.tileentity.InventoryTile;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class PipeBasicItem extends Pipe<ItemBuffer> {

    protected boolean extractMode;

    @Override
    public void setBuffer() {
        buffer = new ItemBuffer();
        extractMode = false;
    }

    @Override
    public void initBuffers() {
        InventoryTile[] built = new InventoryTile[6];
        for(int i = 0; i < 6; i++)
            built[i] = new InventoryTile(1);
        buffer.setBuffers(built);
        buffer.setParentPipe(this);
    }

    @Override
    public int getMaximumTransferRate() {
        return 1;
    }

    @Override
    public boolean isPipeConnected(BlockPos pos, EnumFacing facing) {
        TileEntity te = worldObj.getTileEntity(pos);
        return canTileReceive(te);
    }

    @Override
    public boolean canTileReceive(TileEntity tile) {
        return tile instanceof IInventory || tile instanceof PipeBasicItem;
    }

    @Override
    public boolean canTileProvide(TileEntity tile) {
        return tile instanceof IInventory;
    }

    @Override
    public void giveToTile(TileEntity tile, EnumFacing face) {
        if(tile instanceof PipeBasicItem) {
            PipeBasicItem other = (PipeBasicItem)tile;
            other.buffer.acceptResource(getMaximumTransferRate(), face.getOpposite(), buffer.removeResource(getMaximumTransferRate(), face, false), false);
        } else if(tile instanceof IInventory && buffer.getStorageForFace(face).getStackInSlot(0) != null && !extractMode) {
            IInventory otherInv = (IInventory)tile;
            for(int i = 0; i < otherInv.getSizeInventory(); i++) {
                if(otherInv.getStackInSlot(i) == null) {
                    otherInv.setInventorySlotContents(i, buffer.removeResource(getMaximumTransferRate(), face, false));
                    return;
                } else {
                    if(otherInv.getStackInSlot(i).getItem() == buffer.getStorageForFace(face).getStackInSlot(0).getItem() &&
                            otherInv.getStackInSlot(i).getItemDamage() == buffer.getStorageForFace(face).getStackInSlot(0).getItemDamage() &&
                            !otherInv.getStackInSlot(i).hasTagCompound()) {
                        if(otherInv.getStackInSlot(i).stackSize + buffer.getStorageForFace(face).getStackInSlot(0).stackSize <= otherInv.getStackInSlot(i).getMaxStackSize()) {
                            otherInv.getStackInSlot(i).stackSize += buffer.removeResource(getMaximumTransferRate(), face, false).stackSize;
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void extractFromTile(TileEntity tile, EnumFacing face) {
       if(extractMode) {
            if(tile instanceof IInventory) {
                IInventory otherInv = (IInventory)tile;
                for(int i = 0; i < otherInv.getSizeInventory(); i++) {
                    if(otherInv.getStackInSlot(i) != null) {
                        if(otherInv.getStackInSlot(i).stackSize <= getMaximumTransferRate()) {
                            ItemStack mover = otherInv.getStackInSlot(i).copy();
                            otherInv.setInventorySlotContents(i, null);
                            buffer.acceptResource(getMaximumTransferRate(), face, mover, false);
                            return;
                        } else {
                            ItemStack mover = otherInv.getStackInSlot(i).copy();
                            mover.stackSize = getMaximumTransferRate();
                            ItemStack removed = buffer.acceptResource(getMaximumTransferRate(), face, mover, false);
                            otherInv.getStackInSlot(i).stackSize -= removed != null ? removed.stackSize : 0;
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getOperationDelay() {
        return 20;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setBoolean("ExtractMode", extractMode);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        extractMode = nbt.getBoolean("ExtractMode");
    }

    public boolean getExtractModeActive() {
        return extractMode;
    }

    public void setExtractMode(boolean newValue) {
        extractMode = newValue;
        worldObj.markBlockForUpdate(pos);
    }

    public boolean hasItemInPipe() {
        for(EnumFacing enumFacing : EnumFacing.values()) {
            if(buffer.getStorageForFace(enumFacing).getStackInSlot(0) != null)
                return true;
        }
        return false;
    }
}
