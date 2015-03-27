package com.dyonovan.neotech.common.pipe.item;

import com.dyonovan.neotech.common.blocks.IExpellable;
import com.dyonovan.neotech.common.pipe.Pipe;
import com.dyonovan.neotech.common.pipe.storage.ItemBuffer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;

import java.util.Random;

public class PipeBasicItem extends Pipe<ItemBuffer> implements IInventory, IExpellable {


    @Override
    public void setBuffer() {
        buffer = new ItemBuffer();
    }

    @Override
    public void initBuffers() {
        buffer.setBuffers(null);
        buffer.setParentPipe(this);
    }

    @Override
    public int getMaximumTransferRate() {
        return 32;
    }

    @Override
    public boolean isPipeConnected(BlockPos pos, EnumFacing facing) {
        TileEntity tile = worldObj.getTileEntity(pos);
        return (buffer.canBufferSend(null, facing) || buffer.canBufferExtract(null, facing)) && (tile instanceof IInventory);
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
        if(buffer.removeResource(getMaximumTransferRate(), face, true) != null) {
            if (tile instanceof ISidedInventory) {
                ISidedInventory otherInv = (ISidedInventory) tile;
                for (int i : otherInv.getSlotsForFace(face.getOpposite())) {
                    if (otherInv.getStackInSlot(i) == null) {
                        otherInv.setInventorySlotContents(i, buffer.removeResource(getMaximumTransferRate(), face, false));
                        return;
                    } else if(otherInv.getStackInSlot(i).getItem() == buffer.removeResource(getMaximumTransferRate(), face, true).getItem() &&
                            otherInv.getStackInSlot(i).getItemDamage() == buffer.removeResource(getMaximumTransferRate(), face, true).getItemDamage() &&
                            otherInv.canInsertItem(i, buffer.removeResource(getMaximumTransferRate(), face, true), face.getOpposite()) &&
                            !otherInv.getStackInSlot(i).hasTagCompound()) {
                        if(otherInv.getStackInSlot(i).stackSize + buffer.removeResource(getMaximumTransferRate(), face, true).stackSize < otherInv.getStackInSlot(i).getMaxStackSize()) {
                            otherInv.getStackInSlot(i).stackSize += buffer.removeResource(getMaximumTransferRate(), face, false).stackSize;
                            return;
                        }
                    }
                }
            } else if (tile instanceof IInventory) {
                IInventory otherInv = (IInventory) tile;
                for(int i = 0; i < otherInv.getSizeInventory(); i++) {
                    if (otherInv.getStackInSlot(i) == null) {
                        otherInv.setInventorySlotContents(i, buffer.removeResource(getMaximumTransferRate(), face, false));
                        return;
                    } else if(otherInv.getStackInSlot(i).getItem() == buffer.removeResource(getMaximumTransferRate(), face, true).getItem() &&
                            otherInv.getStackInSlot(i).getItemDamage() == buffer.removeResource(getMaximumTransferRate(), face, true).getItemDamage() &&
                            !otherInv.getStackInSlot(i).hasTagCompound()) {
                        if(otherInv.getStackInSlot(i).stackSize + buffer.removeResource(getMaximumTransferRate(), face, true).stackSize <= otherInv.getStackInSlot(i).getMaxStackSize()) {
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
        if(tile instanceof ISidedInventory) {
            ISidedInventory otherInv = (ISidedInventory)tile;
            for(int i : otherInv.getSlotsForFace(face.getOpposite())) {
                if(otherInv.getStackInSlot(i) != null) {
                    buffer.acceptResource(getMaximumTransferRate(), face, otherInv.getStackInSlot(i), false);
                    if(otherInv.getStackInSlot(i).stackSize <= 0)
                        otherInv.setInventorySlotContents(i, null);
                    return;
                }
            }
        } else if (tile instanceof IInventory) {
            IInventory otherInv = (IInventory)tile;
            for(int i = 0; i < otherInv.getSizeInventory(); i++) {
                if(otherInv.getStackInSlot(i) != null) {
                    buffer.acceptResource(getMaximumTransferRate(), face, otherInv.getStackInSlot(i), false);
                    if(otherInv.getStackInSlot(i).stackSize <= 0)
                        otherInv.setInventorySlotContents(i, null);
                    return;
                }
            }
        }
    }

    @Override
    public int getOperationDelay() {
        return 20;
    }

    public void toggleExtractMode(EnumFacing face) {
        if(buffer.canBufferExtract(null, face))
            buffer.setCanExtract(face, false);
        else
            buffer.setCanExtract(face, true);

        if(buffer.canBufferSend(null, face))
            buffer.setCanInsert(face, false);
        else
            buffer.setCanInsert(face, true);
    }

    public boolean hasItemsInPipe() {
        for(ItemStack stack : buffer.inventory.getValues()) {
            if(stack != null)
                return true;
        }
        return false;
    }

    @Override
    public int getSizeInventory() {
        return buffer.inventory.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return buffer.inventory.getStackInSlot(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack itemstack = getStackInSlot(index);
        if(itemstack != null) {
            if(itemstack.stackSize <= count) {
                setInventorySlotContents(index, null);
            }
            itemstack = itemstack.splitStack(count);
        }
        worldObj.markBlockForUpdate(this.getPos());
        return itemstack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        ItemStack stack = getStackInSlot(index);
        if (stack != null) {
            setInventorySlotContents(index, null);
        }
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        buffer.inventory.setStackInSlot(stack, index);
    }

    @Override
    public int getInventoryStackLimit() {
        return getMaximumTransferRate();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return false;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {

    }

    @Override
    public String getCommandSenderName() {
        return null;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public IChatComponent getDisplayName() {
        return null;
    }

    @Override
    public void expelItems() {
        for (ItemStack stack : buffer.inventory.getValues()) {
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
