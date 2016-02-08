package com.dyonovan.neotech.pipes.entities;

import com.dyonovan.neotech.client.renderers.TileRenderHelper;
import com.google.common.primitives.SignedBytes;
import com.teambr.bookshelf.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
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
 * @since August 15, 2015
 */
public class ItemResourceEntity extends ResourceEntity<ItemStack> {

    @SideOnly(Side.CLIENT)
    private RenderEntityItem itemRenderer;

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
    public ItemResourceEntity(ItemStack toMove, double x, double y, double z, double momentum, BlockPos sender, BlockPos senderTile, BlockPos receiver, World theWorld) {
        super(toMove, x, y, z, momentum, sender, senderTile, receiver, theWorld);
    }

    @Override
    public void onDropInWorld() {
        if (!world.isRemote && resource != null) {
            EntityItem item = new EntityItem(world, xPos, yPos, zPos);
            item.setEntityItemStack(resource);
            world.spawnEntityInWorld(item);
        }
        isDead = true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderResource(float tickPartial) {
        if(this.resource != null) {
            GlStateManager.pushMatrix();
            GlStateManager.pushAttrib();

            RenderManager manager = Minecraft.getMinecraft().getRenderManager();
            GL11.glTranslated(xPos - manager.renderPosX, yPos - manager.renderPosY, zPos - manager.renderPosZ);

            try {
                if (itemRenderer == null) {
                    itemRenderer = new RenderEntityItem(Minecraft.getMinecraft().getRenderManager(), Minecraft.getMinecraft().getRenderItem()) {
                        @Override
                        public int func_177078_a(ItemStack stack) {
                            return SignedBytes.saturatedCast(Math.min(stack.stackSize / 32, 15) + 1);
                        }

                        @Override
                        public boolean shouldBob() {
                            return false;
                        }

                        @Override
                        public boolean shouldSpreadItems() {
                            return false;
                        }
                    };
                }
                EntityItem itemStack = new EntityItem(Minecraft.getMinecraft().theWorld);
                itemStack.hoverStart = 0f;
                    itemStack.setEntityItemStack(resource != null ? resource.copy() : new ItemStack(Blocks.air));
                GlStateManager.scale(0.75, 0.75, 0.75);
                GlStateManager.rotate((float) (360.0 * (double) (System.currentTimeMillis() & 0x3FFFL) / (double) 0x3FFFL), 0.0F, 1.0F, 0.0F);
                if(itemStack.getEntityItem() != null)
                    itemRenderer.doRender(itemStack, 0, -0.25, 0, 0, 0);
            } catch (NullPointerException ignored) {
                GlStateManager.popMatrix();
                Minecraft.getMinecraft().renderEngine.getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
            }//Sometimes it tries to render after its gone, just to be safe
            finally {
                GlStateManager.popAttrib();
                GlStateManager.popMatrix();
            }
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
        tag.setLong("Destination", destination.toLong());
        tag.setLong("From", from.toLong());
        tag.setLong("FromTile", fromTileLocation.toLong());
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        if(resource == null)
            resource = new ItemStack(Blocks.air);
        resource.readFromNBT(tag);
        xPos = tag.getDouble("X");
        yPos = tag.getDouble("Y");
        zPos = tag.getDouble("Z");
        nextSpeed = tag.getDouble("Speed");
        destination = BlockPos.fromLong(tag.getLong("Destination"));
        from = BlockPos.fromLong(tag.getLong("From"));
        fromTileLocation = BlockPos.fromLong(tag.getLong("FromTile"));
        pathQueue = new Stack<>();
    }
}
