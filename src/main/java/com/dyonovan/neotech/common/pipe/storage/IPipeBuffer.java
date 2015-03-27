package com.dyonovan.neotech.common.pipe.storage;

import com.dyonovan.neotech.common.pipe.Pipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public interface IPipeBuffer<T, R, P extends Pipe> {

    /**
     * Set the parent of this buffer
     * @param parent
     */
    public void setParentPipe(P parent);

    /**
     * Set the buffers to new value
     * @param array The buffer array
     */
    public void setBuffers(T[] array);

    /**
     * Get the array of buffers
     * @return The type of buffers
     */
    public T[] getBuffers();

    /**
     * Return the Buffer for the face
     * @param face Face for buffer
     * @return Storage buffer
     */
    public T getStorageForFace(EnumFacing face);

    /**
     * Can this face send resources
     * @param buffer The buffer to check
     * @return True if possible
     */
    public boolean canBufferSend(T buffer, EnumFacing face);

    /**
     * Is the buffer allowing extraction
     * @param buffer Face buffer to extract into
     * @return True is available
     */
    public boolean canBufferExtract(T buffer, EnumFacing face);

    /**
     * Accept Resource
     * @param maxAmount How Much
     * @param inputFace Which face
     * @param resource What resource to fill
     * @param simulate Should you fake it?
     * @return How much was filled
     */
    public R acceptResource(int maxAmount, EnumFacing inputFace, R resource, boolean simulate);

    /**
     * Remove Resource
     * @param maxAmount The maximum amount to remove
     * @param outputFace Output Face
     * @param simulate Fake it?
     * @return The resource to remove
     */
    public R removeResource(int maxAmount, EnumFacing outputFace, boolean simulate);

    /**
     * Update on tick
     */
    public void update();

    /**
     * Save data to tag
     */
    public void writeToNBT(NBTTagCompound tag);

    /**
     * Read data from tag
     */
    public void readFromNBT(NBTTagCompound tag);
}
