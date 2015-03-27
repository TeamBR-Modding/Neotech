package com.dyonovan.neotech.common.pipe;

import com.dyonovan.neotech.common.pipe.storage.IPipeBuffer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import scala.actors.threadpool.Arrays;

import java.util.Collections;
import java.util.List;

public abstract class Pipe<T extends IPipeBuffer> extends TileEntity implements IUpdatePlayerListBox {
    protected T buffer;
    protected int coolDown;

    public abstract void setBuffer();

    public abstract void initBuffers();

    public abstract int getMaximumTransferRate();

    public abstract boolean isPipeConnected(BlockPos pos, EnumFacing facing);

    public abstract boolean canTileReceive(TileEntity tile);

    public abstract boolean canTileProvide(TileEntity tile);

    public abstract void giveToTile(TileEntity tile, EnumFacing face);

    public abstract void extractFromTile(TileEntity tile, EnumFacing face);

    public Pipe() {
        setBuffer();
        initBuffers();
        coolDown = 0;
    }

    @Override
    public void update() {
        if(worldObj.isRemote) return;
        coolDown--;
        if(coolDown <= 0) {
            buffer.update();
            List<EnumFacing> dirs = Arrays.asList(EnumFacing.values());
            Collections.shuffle(dirs);
            for (EnumFacing face : dirs) {
                //Extract
                if (buffer.canBufferExtract(buffer.getStorageForFace(face), face) &&
                        isPipeConnected(pos.offset(face), face) &&
                        canTileProvide(worldObj.getTileEntity(pos.offset(face)))) {
                    extractFromTile(worldObj.getTileEntity(pos.offset(face)), face);
                }

                //Transfer
                if (buffer.canBufferSend(buffer.getStorageForFace(face), face) &&
                        isPipeConnected(pos.offset(face), face) &&
                        canTileReceive(worldObj.getTileEntity(pos.offset(face)))) {
                    giveToTile(worldObj.getTileEntity(pos.offset(face)), face);
                }
            }
            coolDown = getOperationDelay();
        }
    }

    /**
     * How long between operations
     * @return How long (in ticks)
     */
    public int getOperationDelay() {
        return 0;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        buffer.writeToNBT(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        buffer.readFromNBT(nbt);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return new S35PacketUpdateTileEntity(this.pos, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }
}
