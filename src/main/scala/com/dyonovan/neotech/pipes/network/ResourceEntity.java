package com.dyonovan.neotech.pipes.network;

import com.dyonovan.neotech.pipes.tiles.IPipe;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

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

    //The World
    protected World world;

    //Lets the network know we need some help, we won't do anything until it handles things
    public boolean needsHelp = false;

    //The position of the entity
    public double xPos;
    public double yPos;
    public double zPos;

    //How fast to move
    public double speed;

    //The path and destination
    protected Queue<BlockPos> pathQueue;
    public BlockPos currentTarget;
    public BlockPos from;
    public BlockPos destination;

    /**
     * Move an entity with momentum. Use this for most cases
     * @param toMove What you are moving
     * @param x The X Position
     * @param y The Y Position
     * @param z The Z Position
     * @param momentum How fast to move
     */
    public ResourceEntity(R toMove, double x, double y, double z, double momentum, BlockPos sender, BlockPos receiver, World theWorld) {
        resource = toMove;
        xPos = x;
        yPos = y;
        zPos = z;
        speed = momentum;

        from = new BlockPos(sender);
        destination = new BlockPos(receiver);
        world = theWorld;
    }

    /**
     * Used to update the resource. You must call this from the tick handler, it will not call itself
     */
    public void updateEntity() {
        //Make sure we have a target
        if(currentTarget == null)
            nextTarget();

        if(currentTarget != null && !needsHelp) {
            travelPath();
        } else
            needsHelp = true;
    }

    /**
     * Move the current target to next in the queue. If no more exist, sets to null
     */
    public void nextTarget() {
        if(!pathQueue.isEmpty())
            currentTarget = pathQueue.poll();
        else
            currentTarget = null;
    }

    /**
     * Moves in the path towards the destination
     */
    public void travelPath() {
        //Because we may be moving through multiple pipes here
        double distanceToTravel = speed;

        while(distanceToTravel > 0 && currentTarget != null) {
            int xMod = Integer.signum(Double.compare(Math.floor(xPos), currentTarget.getX()));
            int yMod = Integer.signum(Double.compare(Math.floor(yPos), currentTarget.getY()));
            int zMod = Integer.signum(Double.compare(Math.floor(zPos), currentTarget.getZ()));

            if (xMod != 0) {
                double distance = (Math.abs(xPos - currentTarget.getX()));
                distanceToTravel -= distance;
                xPos +=  distance * xMod;
            }
            else if (yMod != 0) {
                double distance = (Math.abs(yPos - currentTarget.getY()));
                distanceToTravel -= distance;
                yPos += distance * yMod;
            }
            else if (zMod != 0) {
                double distance = (Math.abs(zPos - currentTarget.getZ()));
                distanceToTravel -= distance;
                zPos += distance* zMod;
            }

            //If we have entered the new pipe, then we should update
            if(Math.floor(xPos) == currentTarget.getX() && Math.floor(yPos) == currentTarget.getY() && Math.floor(zPos) == currentTarget.getZ()) {
                if(onPipeEntered())
                    nextTarget();
            }
        }
    }

    /**
     * Called when there is no pipe or something went wrong. You should drop the resources in the world here
     */
    abstract void onDropInWorld();

    /**
     * Called when the resource enters a new pipe. Will drop in world if there is no pipe
     * @return True if into a new pipe
     */
    public boolean onPipeEntered() {
        if(world.getTileEntity(currentTarget) instanceof IPipe) {
            IPipe pipe = (IPipe) world.getTileEntity(currentTarget);
            pipe.onResourceEnteredPipe(this);
            return true;
        } else {
            onDropInWorld();
            needsHelp = true;
            return false;
        }
    }

    abstract void renderResource(float tickPartial);
}
