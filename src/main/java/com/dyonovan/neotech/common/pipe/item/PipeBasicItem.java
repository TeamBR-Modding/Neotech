package com.dyonovan.neotech.common.pipe.item;

import com.dyonovan.neotech.common.blocks.IExpellable;
import com.dyonovan.neotech.common.pipe.Pipe;
import com.dyonovan.neotech.common.pipe.storage.ItemBuffer;
import com.dyonovan.neotech.common.tileentity.InventoryTile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.Random;

public class PipeBasicItem extends Pipe<ItemBuffer> implements IExpellable{

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
        } else if(tile instanceof ISidedInventory && buffer.getStorageForFace(face).getStackInSlot(0) != null && !extractMode) {
            ISidedInventory otherInv = (ISidedInventory)tile;
            for(int i : otherInv.getSlotsForFace(face.getOpposite())) {
                if(otherInv.getStackInSlot(i) == null) {
                    if(otherInv.canInsertItem(i, buffer.removeResource(getMaximumTransferRate(), face, true), face.getOpposite()))
                        otherInv.setInventorySlotContents(i, buffer.removeResource(getMaximumTransferRate(), face, false));
                    return;
                } else {
                    if(otherInv.getStackInSlot(i).getItem() == buffer.getStorageForFace(face).getStackInSlot(0).getItem() &&
                            otherInv.getStackInSlot(i).getItemDamage() == buffer.getStorageForFace(face).getStackInSlot(0).getItemDamage() &&
                            otherInv.canInsertItem(i, buffer.removeResource(getMaximumTransferRate(), face, true), face.getOpposite()) &&
                            !otherInv.getStackInSlot(i).hasTagCompound()) {
                        if(otherInv.getStackInSlot(i).stackSize + buffer.getStorageForFace(face).getStackInSlot(0).stackSize <= otherInv.getStackInSlot(i).getMaxStackSize()) {
                            otherInv.getStackInSlot(i).stackSize += buffer.removeResource(getMaximumTransferRate(), face, false).stackSize;
                            return;
                        }
                    }
                }
            }
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
            if(tile instanceof ISidedInventory) {
                ISidedInventory otherInv = (ISidedInventory)tile;
                for(int i : otherInv.getSlotsForFace(face.getOpposite())) {
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
            else if(tile instanceof IInventory) {
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

    @Override
    public void expelItems() {
        for (EnumFacing face : EnumFacing.values()) {
            for (ItemStack stack : buffer.getStorageForFace(face).getValues()) {
                if (stack != null) {
                    Random random = new Random();
                    EntityItem entityitem =
                            new EntityItem(worldObj,
                                    pos.getX() + random.nextFloat() * 0.8F + 0.1F,
                                    pos.getY() + random.nextFloat() * 0.8F + 0.1F,
                                    pos.getZ() + random.nextFloat() * 0.8F + 0.1F,
                                    stack
                            );
                    if (stack.hasTagCompound()) {
                        entityitem.getEntityItem().setTagCompound((NBTTagCompound) stack.getTagCompound().copy());
                    }
                    float f3 = 0.05F;
                    entityitem.motionX = (double) ((float) random.nextGaussian() * f3);
                    entityitem.motionY = (double) ((float) random.nextGaussian() * f3 + 0.2F);
                    entityitem.motionZ = (double) ((float) random.nextGaussian() * f3);
                    worldObj.spawnEntityInWorld(entityitem);
                }
            }
        }
    }
}
