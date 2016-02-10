package com.dyonovan.neotech.pipes.entities;

import com.teambr.bookshelf.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTank;
import org.lwjgl.opengl.GL11;

import java.util.Stack;

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis pauljoda
 * @since August 17, 2015
 */
public class FluidResourceEntity extends ResourceEntity<FluidTank> {

    /**
     * Stub for reading from server
     */
    public FluidResourceEntity() {}

    /**
     * Move an entity with momentum. Use this for most cases
     *
     * @param toMove   What you are moving
     * @param x        The X Position
     * @param y        The Y Position
     * @param z        The Z Position
     * @param momentum How fast to move
     * @param theWorld
     */
    public FluidResourceEntity(FluidTank toMove, double x, double y, double z, double momentum, BlockPos senderTile, BlockPos rPipe, BlockPos rTile, World theWorld) {
        super(toMove, x, y, z, momentum, senderTile, rPipe, rTile, theWorld);
    }

    @Override
    public void onDropInWorld() {
        isDead = true;
        //Since we are just fluid, lets not do anything for now.
    }

    @Override
    public void renderResource(float tickPartial) {
        if(resource != null && resource.getFluid() != null) {
            GlStateManager.pushMatrix();
            GlStateManager.pushAttrib();

            RenderManager manager = Minecraft.getMinecraft().getRenderManager();
            GlStateManager.translate(xPos - manager.renderPosX, yPos - manager.renderPosY, zPos - manager.renderPosZ);

            RenderUtils.bindMinecraftBlockSheet();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableLighting();

            TextureAtlasSprite fluidIcon =  Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(resource.getFluid().getFluid().getStill(resource.getFluid()).toString());
            RenderUtils.renderCubeWithTexture(-0.2, -0.2, -0.2, 0.2, 0.2, 0.2, fluidIcon.getMinU(), fluidIcon.getMinV(), fluidIcon.getMaxU(), fluidIcon.getMaxV());

            GlStateManager.enableLighting();
            RenderUtils.bindMinecraftBlockSheet();

            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GlStateManager.popAttrib();
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        if(resource != null)
            resource.writeToNBT(tag);
        tag.setDouble("X", xPos);
        tag.setDouble("Y", yPos);
        tag.setDouble("Z", zPos);
        tag.setDouble("Speed", speed);
        tag.setLong("DestinationPipe", destinationPipe.toLong());
        tag.setLong("DestinationTile", destinationTile.toLong());
        tag.setLong("FromTile", fromTileLocation.toLong());
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        if(resource == null)
            resource = new FluidTank(0);
        resource.readFromNBT(tag);
        xPos = tag.getDouble("X");
        yPos = tag.getDouble("Y");
        zPos = tag.getDouble("Z");
        nextSpeed = tag.getDouble("Speed");
        destinationPipe = BlockPos.fromLong(tag.getLong("DestinationPipe"));
        destinationTile = BlockPos.fromLong(tag.getLong("DestinationTile"));
        fromTileLocation = BlockPos.fromLong(tag.getLong("FromTile"));
        pathQueue = new Stack<>();
    }
}