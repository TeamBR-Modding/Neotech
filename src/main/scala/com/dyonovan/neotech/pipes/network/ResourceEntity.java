package com.dyonovan.neotech.pipes.network;

import com.dyonovan.neotech.pipes.tiles.IPipe;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import javax.vecmath.Vector3d;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

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
    public boolean isDead = false;

    //The position of the entity
    public double xPos;
    public double yPos;
    public double zPos;

    //How fast to move
    protected double speed;
    public double nextSpeed;

    //The path and destination
    public Stack<BlockPos> pathQueue;
    public BlockPos currentTarget;
    public BlockPos from;
    public BlockPos destination;

    /**
     * Stub used in reading from server
     */
    public ResourceEntity() {}

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
        speed = nextSpeed = momentum;

        from = new BlockPos(sender);
        destination = new BlockPos(receiver);
        world = theWorld;

        pathQueue = new Stack<>();
        findPathToDestination();
    }

    /**
     * Used to update the resource. You must call this from the tick handler, it will not call itself
     */
    public void updateEntity() {
        if(!isDead) {
            if(!world.isRemote && world.getTileEntity(new BlockPos(xPos, yPos, zPos)) == null)
                isDead = true;
            //Make sure we have a target
            if (currentTarget == null)
                nextTarget();

            if (currentTarget != null) {
                travelPath();
            } else
                isDead = true;
        }
    }

    /**
     * Move the current target to next in the queue. If no more exist, sets to null
     */
    public void nextTarget() {
        if(pathQueue != null && !pathQueue.isEmpty())
            currentTarget = pathQueue.pop();
        else
            currentTarget = null;
    }

    /**
     * Moves in the path towards the destination
     */
    public void travelPath() {
        if (!world.isRemote) {
            //Because we may be moving through multiple pipes here
            speed = nextSpeed;
            double distanceToTravel = speed;

            while (distanceToTravel > 0 && currentTarget != null) {
                int xMod = Integer.signum(Double.compare(Math.floor(xPos), currentTarget.getX()));
                int yMod = Integer.signum(Double.compare(Math.floor(yPos), currentTarget.getY()));
                int zMod = Integer.signum(Double.compare(Math.floor(zPos), currentTarget.getZ()));

                double nextX = xPos;
                double nextY = yPos;
                double nextZ = zPos;

                if (xMod != 0) {
                    double distance = (Math.abs(Math.floor(xPos) - currentTarget.getX()));
                    distanceToTravel -= Math.min(speed, distance);
                    nextX += Math.min(speed, distance) * -xMod;
                } else if (yMod != 0) {
                    double distance = (Math.abs(Math.floor(yPos) - currentTarget.getY()));
                    distanceToTravel -= Math.min(speed, distance);
                    nextY += Math.min(speed, distance) * -yMod;
                } else if (zMod != 0) {
                    double distance = (Math.abs(Math.floor(zPos) - currentTarget.getZ()));
                    distanceToTravel -= Math.min(speed, distance);
                    nextZ += Math.min(speed, distance) * -zMod;
                }

                Vector3d newPos = checkIfOutOfPipe(nextX, nextY, nextZ);

                xPos = newPos.x;
                yPos = newPos.y;
                zPos = newPos.z;

                //If we have entered the new pipe, then we should update
                if (Math.floor(xPos) == currentTarget.getX() && Math.floor(yPos) == currentTarget.getY() && Math.floor(zPos) == currentTarget.getZ()) {
                    if (onPipeEntered())
                        nextTarget();
                }
            }
        }
    }

    private Vector3d checkIfOutOfPipe(double x, double y, double z) {
        double xDecimal = Math.abs(x % 1);
        double yDecimal = Math.abs(y % 1);
        double zDecimal = Math.abs(z % 1);
        double min = 0.3;
        double max = 0.7;
        if((xDecimal < min || xDecimal > max) && ((zDecimal < min || zDecimal > max) || (yDecimal < min || yDecimal > max)) || (zDecimal < min || zDecimal > max) && (yDecimal < min || yDecimal > max)) {
            if(xDecimal < 0.2 || xDecimal > 0.8)
                x = (Math.floor(x) + 0.5);
            if (zDecimal < 0.2 || zDecimal > 0.8)
                z = (Math.floor(z) + 0.5);
            if(yDecimal < 0.2 || yDecimal > 0.8)
                y = (Math.floor(y) + 0.5);
            return new Vector3d(x, y, z);
        }
        return new Vector3d(x, y, z);
    }

    /**
     * Called when there is no pipe or something went wrong. You should drop the resources in the world here
     */
    abstract public void onDropInWorld();

    /**
     * Used to send in the packet
     */
    abstract public void toBytes(ByteBuf buf);

    /**
     * Used to build from bytes
     */
    abstract public ResourceEntity fromBytes(ByteBuf buf);

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
            isDead = true;
            return false;
        }
    }

    abstract public void renderResource(float tickPartial);

    public boolean findPathToDestination() {
        if(world != null && world.getTileEntity(new BlockPos(xPos, yPos, zPos)) instanceof IPipe) {
            IPipe currentPipe = (IPipe) world.getTileEntity(new BlockPos(xPos, yPos, zPos));

            HashMap<Long, Integer> distance = new HashMap<>();
            HashMap<Long, BlockPos> parent = new HashMap<>();

            distance.put(currentPipe.getPosAsLong(), 0); //Distance from source to source
            parent.put(currentPipe.getPosAsLong(), null); //Previous pipe in best path

            Queue<BlockPos> queue = new LinkedList<>(); //Build the queue
            queue.add(BlockPos.fromLong(currentPipe.getPosAsLong())); //Add to the queue

            while(!queue.isEmpty()) {
                BlockPos thisPos = queue.poll();
                IPipe thisPipe = (IPipe) world.getTileEntity(thisPos);

                //Add all children
                if(thisPipe != null) {
                    for (EnumFacing facing : EnumFacing.values()) {
                        if(thisPipe.canConnect(facing)) {
                            BlockPos otherPos = thisPos.offset(facing);
                            if (distance.get(otherPos.toLong()) == null) {
                                queue.add(otherPos);
                                distance.put(otherPos.toLong(), Integer.MAX_VALUE); //Set distance to the max
                                parent.put(otherPos.toLong(), null); //Set no parent

                                int newDistance = (int) (distance.get(thisPos.toLong()) + thisPos.distanceSq(otherPos));
                                if (newDistance < distance.get(otherPos.toLong())) {
                                    distance.put(otherPos.toLong(), newDistance);
                                    parent.put(otherPos.toLong(), thisPos);
                                }

                                if (otherPos.toLong() == destination.toLong()) { //Found it!
                                    pathQueue.clear();
                                    BlockPos u = destination;
                                    while (parent.get(u.toLong()) != null) {
                                        pathQueue.push(u);
                                        u = parent.get(u.toLong());
                                    }
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
            onDropInWorld(); //Couldn't figure it out
            isDead = true;
        }
        return false;
    }
}
