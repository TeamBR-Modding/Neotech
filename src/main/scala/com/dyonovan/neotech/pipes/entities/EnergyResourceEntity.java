package com.dyonovan.neotech.pipes.entities;

import cofh.api.energy.EnergyStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import java.util.Stack;

/**
 * This file was created for NeoTech
 * <p/>
 * NeoTech is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis pauljoda
 * @since August 17, 2015
 */
public class EnergyResourceEntity extends ResourceEntity<EnergyStorage> {

    /**
     * Stub for reading from server
     */
    public EnergyResourceEntity() {}

    /**
     * Move an entity with momentum. Use this for most cases
     *
     * @param toMove   What you are moving
     * @param x        The X Position
     * @param y        The Y Position
     * @param z        The Z Position
     * @param momentum How fast to move
     * @param sender
     * @param receiver
     * @param theWorld
     */
    public EnergyResourceEntity(EnergyStorage toMove, double x, double y, double z, double momentum, BlockPos sender, BlockPos receiver, World theWorld) {
        super(toMove, x, y, z, momentum, sender, receiver, theWorld);
    }

    @Override
    public void onDropInWorld() {
        //Since we are just energy, lets not do anything for now.
        //If we are evil, maybe we should spawn an explosion...
    }

    @Override
    public void renderResource(float tickPartial) {
        Minecraft.getMinecraft().theWorld.spawnParticle(EnumParticleTypes.REDSTONE, xPos, yPos, zPos, 0, 100, 0);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        if(resource != null)
            resource.writeToNBT(tag);
        tag.setDouble("X", xPos);
        tag.setDouble("Y", yPos);
        tag.setDouble("Z", zPos);
        tag.setDouble("Speed", speed);
        tag.setLong("Destination", destination.toLong());
        tag.setLong("From", from.toLong());
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        if(resource == null)
            resource = new EnergyStorage(0);
        resource.readFromNBT(tag);
        xPos = tag.getDouble("X");
        yPos = tag.getDouble("Y");
        zPos = tag.getDouble("Z");
        nextSpeed = tag.getDouble("Speed");
        destination = BlockPos.fromLong(tag.getLong("Destination"));
        from = BlockPos.fromLong(tag.getLong("From"));
        pathQueue = new Stack<>();
    }
}
