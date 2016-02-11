package com.dyonovan.neotech.pipes.entities;

import com.dyonovan.neotech.pipes.types.InterfacePipe;
import com.dyonovan.neotech.pipes.types.SimplePipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * This file was created for NeoTech
 *
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
    public R resource;

    //The World
    protected World world;

    //Lets the network know we need some help, we won't do anything until it handles things
    public boolean isDead = false;

    //The position of the entity
    public double xPos;
    public double yPos;
    public double zPos;

    public double prevX;
    public double prevY;
    public double prevZ;

    //How fast to move
    protected double speed;
    public double nextSpeed;

    //The path and destination
    public Stack<Vec3> pathQueue;
    public Vec3 currentTarget;
    public BlockPos fromTileLocation;
    public BlockPos destinationPipe;
    public BlockPos destinationTile;

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
    public ResourceEntity(R toMove, double x, double y, double z, double momentum, BlockPos senderTile, BlockPos rPipe, BlockPos rTile, World theWorld) {
        resource = toMove;
        xPos = prevX = x;
        yPos = prevY = y;
        zPos = prevZ = z;
        speed = nextSpeed = momentum;

        fromTileLocation = new BlockPos(senderTile);
        destinationPipe = new BlockPos(rPipe);
        destinationTile = new BlockPos(rTile);
        world = theWorld;

        pathQueue = new Stack<>();
    }

    public void setWorld(World newWorld) {
        this.world = newWorld;
    }

    /**
     * Used to update the resource. You must call this from the tick handler, it will not call itself
     */
    public void updateEntity() {
        if(resource == null)
            isDead = true;
        if(!isDead && world != null) {
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
        else {
            findPathToDestination();
            if(pathQueue != null && !pathQueue.isEmpty())
                currentTarget = pathQueue.pop();
            else
                currentTarget = null;
        }
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
                int xMod = Integer.signum(Double.compare(xPos, currentTarget.xCoord));
                int yMod = Integer.signum(Double.compare(yPos, currentTarget.yCoord));
                int zMod = Integer.signum(Double.compare(zPos, currentTarget.zCoord));

                prevX = xPos;
                prevY = yPos;
                prevZ = zPos;

                if (xMod != 0) {
                    double distance = (Math.abs(xPos - currentTarget.xCoord));
                    distanceToTravel -= Math.min(speed, distance);
                    xPos += Math.min(speed, distance) * -xMod;
                } else if (yMod != 0) {
                    double distance = (Math.abs(yPos - currentTarget.yCoord));
                    distanceToTravel -= Math.min(speed, distance);
                    yPos += Math.min(speed, distance) * -yMod;
                } else if (zMod != 0) {
                    double distance = (Math.abs(zPos - currentTarget.zCoord));
                    distanceToTravel -= Math.min(speed, distance);
                    zPos += Math.min(speed, distance) * -zMod;
                }

                if(world.getTileEntity(new BlockPos(xPos, yPos, zPos)) == null) {
                    isDead = true;
                    return;
                }

                //If we have entered the new pipe, then we should update
                if (xPos == currentTarget.xCoord && yPos == currentTarget.yCoord && zPos == currentTarget.zCoord) {
                    if (onPipeEntered())
                        nextTarget();
                }
            }
        }
    }

    /**
     * Called when there is no pipe or something went wrong. You should drop the resources in the world here
     */
    abstract public void onDropInWorld();

    /**
     * Called when the resource enters a new pipe. Will drop in world if there is no pipe
     * @return True if into a new pipe
     */
    public boolean onPipeEntered() {
        if(world.getTileEntity(new BlockPos(currentTarget.xCoord, currentTarget.yCoord, currentTarget.zCoord)) instanceof SimplePipe) {
            SimplePipe pipe = (SimplePipe) world.getTileEntity(new BlockPos(currentTarget.xCoord, currentTarget.yCoord, currentTarget.zCoord));
            pipe.onResourceEnteredPipe(this);
            return true;
        } else if(new BlockPos(currentTarget.xCoord, currentTarget.yCoord, currentTarget.zCoord).toLong() == fromTileLocation.toLong()) {
            return true;
        } else if(new BlockPos(currentTarget.xCoord, currentTarget.yCoord, currentTarget.zCoord).toLong() == destinationTile.toLong()) {
            BlockPos tilePos = new BlockPos(currentTarget.xCoord, currentTarget.yCoord, currentTarget.zCoord);
            for(EnumFacing dir : EnumFacing.values()) {
                if(tilePos.offset(dir).toLong() == destinationPipe.toLong()) {
                    if(world.getTileEntity(destinationPipe) != null)
                        //noinspection unchecked
                        ((InterfacePipe)world.getTileEntity(destinationPipe)).tryInsertResource(this, dir.getOpposite());
                }
            }
            return true;
        } else {
            isDead = true;
            return false;
        }
    }

    /**
     * Used to render the resource
     * @param tickPartial The tick partial time
     */
    @SideOnly(Side.CLIENT)
    abstract public void renderResource(float tickPartial);

    /**
     * Used to pass to the client relevant information. ONLY USE WHAT IS NEEDED TO RENDER
     */
    abstract public void writeToNBT(NBTTagCompound tag);

    /**
     * Used to pass to the client relevant information. ONLY USE WHAT IS NEEDED TO RENDER
     */
    abstract public void readFromNBT(NBTTagCompound tag);

    /**
     * Finds the path to the registered destination
     */
    public boolean findPathToDestination() {
        if(world != null && destinationTile != null && isValidBlockToTravel(new BlockPos(xPos, yPos, zPos))) {
            TileEntity currentTile = world.getTileEntity(new BlockPos(xPos, yPos, zPos));

            HashMap<Long, Integer> distance = new HashMap<>();
            HashMap<Long, BlockPos> parent = new HashMap<>();

            distance.put(currentTile.getPos().toLong(), 0); //Distance from source to source
            parent.put(currentTile.getPos().toLong(), null); //Previous pipe in best path

            Queue<BlockPos> queue = new LinkedList<>(); //Build the queue
            queue.add(BlockPos.fromLong(currentTile.getPos().toLong())); //Add to the queue

            while(!queue.isEmpty()) {
                BlockPos thisPos = queue.poll();
                if (world.getTileEntity(thisPos) instanceof SimplePipe) {
                    SimplePipe thisPipe = (SimplePipe) world.getTileEntity(thisPos);

                    //Add all children
                    if (thisPipe != null) {
                        for (EnumFacing facing : EnumFacing.values()) {
                            if (thisPipe.canConnect(facing)) {
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

                                    if (otherPos.toLong() == destinationTile.toLong()) { //Found it!
                                        pathQueue.clear();
                                        BlockPos u = destinationTile;
                                        while (parent.get(u.toLong()) != null) {
                                            pathQueue.push(new Vec3(u.getX() + 0.5, u.getY() + 0.5, u.getZ() + 0.5));
                                            u = parent.get(u.toLong());
                                        }
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    for (EnumFacing facing : EnumFacing.values()) {
                        if (thisPos.offset(facing) instanceof SimplePipe) {
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

                                if (otherPos.toLong() == destinationTile.toLong()) { //Found it!
                                    pathQueue.clear();
                                    BlockPos u = destinationTile;
                                    while (parent.get(u.toLong()) != null) {
                                        pathQueue.push(new Vec3(u.getX() + 0.5, u.getY() + 0.5, u.getZ() + 0.5));
                                        u = parent.get(u.toLong());
                                    }
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
            isDead = true;
        }
        return false;
    }

    public boolean isValidBlockToTravel(BlockPos pos) {
        return world.getTileEntity(pos) instanceof SimplePipe ||
                pos.toLong() == destinationTile.toLong() || pos.toLong() == fromTileLocation.toLong();
    }

    /**
     * Sets the speed of this resource
     *
     * DO NOT SET TO ANYTHING 0 OR BELOW.
     * @param i New speed
     */
    public void setSpeed(double i) {
        nextSpeed = i;
    }

    /**
     * Used to add or remove speed from an entity
     *
     * If the speed goes below 0, it will set to a minimum of very slow
     * @param i Velocity change
     */
    public void applySpeed(double i) {
        nextSpeed += i;
        if(nextSpeed <= 0)
            nextSpeed = 0.001;
    }
}
