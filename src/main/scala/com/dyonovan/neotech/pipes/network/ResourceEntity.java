package com.dyonovan.neotech.pipes.network;

import net.minecraft.util.BlockPos;

import java.util.Queue;

/**
 * This file was created for NeoTech
 * <p/>
 * NeoTech is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis pauljoda
 * @since August 15, 2015
 *
 * Used as a class to hold what will be in the pipes. This is responsible for the movement of the resource.
 */
abstract public class ResourceEntity<R> {
    //What are we moving
    protected R resource;

    //The position of the entity
    protected double xPos;
    protected double yPos;
    protected double zPos;

    //How fast to move
    protected double speed;

    //The path and destination
    protected Queue<BlockPos> pathQueue;
    protected BlockPos destination;

    /**
     * Don't use this unless you just want an object. Shouldn't be needed for actually adding in world
     * @param toMove What we should be moving
     */
    public ResourceEntity(R toMove) {
        this(toMove, 0, 0, 0);
    }

    /**
     * This will create a entity with no momentum, you might want that to do anything
     * @param toMove What we are moving
     * @param x The X Position
     * @param y The Y Position
     * @param z The Z Position
     */
    public ResourceEntity(R toMove, double x, double y, double z) {
       this(toMove, x, y, z, 0);
    }

    /**
     * Move an entity with momentum. Use this for most cases
     * @param toMove What you are moving
     * @param x The X Position
     * @param y The Y Position
     * @param z The Z Position
     * @param momentum How fast to move
     */
    public ResourceEntity(R toMove, double x, double y, double z, double momentum) {
        resource = toMove;
        xPos = x;
        yPos = y;
        zPos = z;
        speed = momentum;
    }

    public void updateEntity() {

    }
}
