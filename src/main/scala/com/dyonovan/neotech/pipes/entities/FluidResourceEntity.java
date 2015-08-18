package com.dyonovan.neotech.pipes.entities;

import com.dyonovan.neotech.helpers.RenderHelper;
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
     * @param sender
     * @param receiver
     * @param theWorld
     */
    public FluidResourceEntity(FluidTank toMove, double x, double y, double z, double momentum, BlockPos sender, BlockPos receiver, World theWorld) {
        super(toMove, x, y, z, momentum, sender, receiver, theWorld);
    }

    @Override
    public void onDropInWorld() {
        //Since we are just fluid, lets not do anything for now.
    }

    @Override
    public void renderResource(float tickPartial) {
        if(resource != null && resource.getFluid() != null) {
            GlStateManager.pushMatrix();
            GlStateManager.pushAttrib();

            RenderManager manager = Minecraft.getMinecraft().getRenderManager();
            GL11.glPushMatrix();
            GL11.glTranslated(xPos - manager.renderPosX, yPos - manager.renderPosY, zPos - manager.renderPosZ);

            RenderUtils.bindMinecraftBlockSheet();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableLighting();

            TextureAtlasSprite fluidIcon = resource.getFluid().getFluid().getIcon();
            RenderHelper.renderCubeWithTexture(-0.2, -0.2, -0.2, 0.2, 0, 0.2, fluidIcon.getMinU(), fluidIcon.getMinV(), fluidIcon.getMaxU(), fluidIcon.getMaxV());
            GL11.glPopMatrix();

            GL11.glTranslated(prevX - manager.renderPosX, prevY - manager.renderPosY, prevZ - manager.renderPosZ);
            RenderHelper.renderCubeWithTexture(-0.19, -0.19, -0.19, 0.19, -0.1, 0.19, fluidIcon.getMinU(), fluidIcon.getMinV(), fluidIcon.getMaxU(), fluidIcon.getMaxV());

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
        tag.setDouble("PX", prevX);
        tag.setDouble("PY", prevY);
        tag.setDouble("PZ", prevZ);
        tag.setDouble("Speed", speed);
        tag.setLong("Destination", destination.toLong());
        tag.setLong("From", from.toLong());
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        if(resource == null)
            resource = new FluidTank(0);
        resource.readFromNBT(tag);
        xPos = tag.getDouble("X");
        yPos = tag.getDouble("Y");
        zPos = tag.getDouble("Z");
        prevX = tag.getDouble("PX");
        prevY = tag.getDouble("PY");
        prevZ = tag.getDouble("PZ");
        nextSpeed = tag.getDouble("Speed");
        destination = BlockPos.fromLong(tag.getLong("Destination"));
        from = BlockPos.fromLong(tag.getLong("From"));
        pathQueue = new Stack<>();
    }
}