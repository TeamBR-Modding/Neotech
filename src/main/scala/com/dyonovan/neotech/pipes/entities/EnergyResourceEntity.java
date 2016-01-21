package com.dyonovan.neotech.pipes.entities;

import cofh.api.energy.EnergyStorage;
import com.dyonovan.neotech.lib.Reference;
import com.teambr.bookshelf.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
        isDead = true;
        //Since we are just energy, lets not do anything for now.
        //If we are evil, maybe we should spawn an explosion...
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderResource(float tickPartial) {
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();

        RenderManager manager = Minecraft.getMinecraft().getRenderManager();
        GL11.glTranslated(xPos - manager.renderPosX, yPos - manager.renderPosY, zPos - manager.renderPosZ);

        RenderUtils.bindTexture(new ResourceLocation(Reference.MOD_ID(), "textures/entity/energyEntity.png"));

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableLighting();

        //TODO Find another way to render power
        RenderUtils.setupBillboard(Minecraft.getMinecraft().thePlayer);

        WorldRenderer renderer = Tessellator.getInstance().getWorldRenderer();
        renderer.begin(GL11.GL_QUADS, RenderUtils.POSITION_TEX_NORMALF());
        renderer.pos(-0.2, -0.2, -0.2).tex(0, 0).normal(0, -1, 0).endVertex();
        renderer.pos(-0.2, 0.2, -0.2).tex(0, 1).normal(0, -1, 0).endVertex();
        renderer.pos(0.2, 0.2, -0.2).tex(1, 1).normal(0, -1, 0).endVertex();
        renderer.pos(0.2, -0.2, -0.2).tex(1, 0).normal(0, -1, 0).endVertex();
        Tessellator.getInstance().draw();

        GlStateManager.enableLighting();

        RenderUtils.bindMinecraftBlockSheet();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
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
