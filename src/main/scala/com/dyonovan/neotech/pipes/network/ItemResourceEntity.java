package com.dyonovan.neotech.pipes.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;

/**
 * This file was created for NeoTech
 * <p/>
 * NeoTech is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis pauljoda
 * @since August 15, 2015
 */
public class ItemResourceEntity extends ResourceEntity<ItemStack> {

    /**
     * Stub for reading from server
     */
    public ItemResourceEntity() {}

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
    public ItemResourceEntity(ItemStack toMove, double x, double y, double z, double momentum, BlockPos sender, BlockPos receiver, World theWorld) {
        super(toMove, x, y, z, momentum, sender, receiver, theWorld);
    }

    @Override
    public void onDropInWorld() {
        if (!world.isRemote) {
            EntityItem item = new EntityItem(world, xPos, yPos, zPos);
            item.setEntityItemStack(resource);
            world.spawnEntityInWorld(item);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, resource);
        buf.writeDouble(xPos);
        buf.writeDouble(yPos);
        buf.writeDouble(zPos);
    }

    @Override
    public ResourceEntity fromBytes(ByteBuf buf) {
        return new ItemResourceEntity(ByteBufUtils.readItemStack(buf),
                buf.readDouble(), buf.readDouble(), buf.readDouble(), 0, new BlockPos(0, 0, 0), new BlockPos(0, 0, 0), null);
    }

    @Override
    public void renderResource(float tickPartial) {
        Minecraft.getMinecraft().theWorld.spawnParticle(EnumParticleTypes.REDSTONE, xPos, yPos, zPos, 100, 0, 0);
    }
}
