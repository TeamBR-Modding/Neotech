package com.dyonovan.neotech.common.pipe.item;

import com.dyonovan.neotech.common.pipe.Pipe;
import com.dyonovan.neotech.common.pipe.storage.ItemBuffer;
import com.dyonovan.neotech.common.tileentity.InventoryTile;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
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
        buffer.setBuffers(new InventoryTile[]{new InventoryTile(1)});
    }

    @Override
    public int getMaximumTransferRate() {
        return 1;
    }

    @Override
    public boolean isPipeConnected(BlockPos pos, EnumFacing facing) {
        TileEntity tile = worldObj.getTileEntity(pos);
        return (tile instanceof IInventory || tile instanceof PipeBasicItem);
    }

    @Override
    public boolean canTileReceive(TileEntity tile) {
        return (tile instanceof IInventory || tile instanceof PipeBasicItem) && buffer.removeResource(getMaximumTransferRate(), EnumFacing.EAST, true) != null;
    }

    @Override
    public boolean canTileProvide(TileEntity tile) {
        return tile instanceof IInventory;
    }

    @Override
    public void giveToTile(TileEntity tile, EnumFacing face) {
        if(face == buffer.receivedDirection)
            return;
        if(tile instanceof PipeBasicItem) {
            PipeBasicItem otherPipe = (PipeBasicItem)tile;
            if(otherPipe.buffer.getStorageForFace(face).getStackInSlot(0) == null) {
                otherPipe.buffer.acceptResource(1, face.getOpposite(), buffer.removeResource(1, face, false), false);
            }
        } else if(tile instanceof IInventory) {
            IInventory inventory = (IInventory)tile;
            for(int i = 0; i < inventory.getSizeInventory(); i++) {
                if(inventory.getStackInSlot(i) == null) {
                    inventory.setInventorySlotContents(i, buffer.removeResource(1, face, false));
                } else if(inventory.getStackInSlot(i).getItem() == buffer.getStorageForFace(face).getStackInSlot(0).getItem() &&
                        inventory.getStackInSlot(i).getItemDamage() == buffer.getStorageForFace(face).getStackInSlot(0).getItemDamage() &&
                        inventory.getStackInSlot(i).getMaxStackSize() < buffer.getStorageForFace(face).getStackInSlot(0).stackSize + getMaximumTransferRate() &&
                        !inventory.getStackInSlot(i).hasTagCompound()) {
                    inventory.getStackInSlot(i).stackSize += buffer.removeResource(getMaximumTransferRate(), face, false).stackSize;
                }
            }
        }
    }

    @Override
    public void extractFromTile(TileEntity tile, EnumFacing face) {
        if(extractMode) {
            if(tile instanceof IInventory) {
                IInventory inventory = (IInventory)tile;
                for(int i = 0; i < inventory.getSizeInventory(); i++) {
                    if(inventory.getStackInSlot(i) != null) {
                        if(inventory.getStackInSlot(i).stackSize <= getMaximumTransferRate()) {
                            buffer.acceptResource(getMaximumTransferRate(), face.getOpposite(), inventory.getStackInSlot(i).copy(), false);
                            inventory.setInventorySlotContents(i, null);
                        }
                        else if(inventory.getStackInSlot(i).stackSize > getMaximumTransferRate()) {
                            ItemStack mover = new ItemStack(inventory.getStackInSlot(i).getItem(), getMaximumTransferRate(), inventory.getStackInSlot(i).getItemDamage());
                            if(inventory.getStackInSlot(i).hasTagCompound())
                                mover.setTagCompound(inventory.getStackInSlot(i).getTagCompound());
                            buffer.acceptResource(getMaximumTransferRate(), face.getOpposite(), mover, false);
                            inventory.getStackInSlot(i).stackSize -= getMaximumTransferRate();
                        }
                    }
                }
            }
        }
    }

    public boolean getExtractModeActive() {
        return extractMode;
    }

    public void setExtractMode(boolean newValue) {
        extractMode = newValue;
    }
}
