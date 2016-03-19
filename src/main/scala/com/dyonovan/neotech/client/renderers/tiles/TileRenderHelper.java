package com.dyonovan.neotech.client.renderers.tiles;

import com.teambr.bookshelf.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.EnumSkyBlock;
import org.lwjgl.opengl.GL11;

/**
 * This file was created for NeoTech
 * <p/>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * Special Thanks to Prof. Mobius from Jabba & Mezz from JEI
 * @author Paul Davis <pauljoda>
 * @since 1/24/2016
 */
public abstract class TileRenderHelper<T extends TileEntity> extends TileEntitySpecialRenderer<T> {

    protected float scale = 1f/256f;
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");


    public class LocationDouble {
        double x;
        double y;
        double z;

        LocationDouble(double xPos, double yPos, double zPos) {
            x = xPos;
            y = yPos;
            z = zPos;
        }
    }

    protected Minecraft mc            = Minecraft.getMinecraft();
    protected RenderItem renderItem   = mc.getRenderItem();
    protected FontRenderer renderFont    = mc.fontRendererObj;

    protected static byte ALIGNLEFT = 0x00;
    protected static byte ALIGNCENTER = 0x01;
    protected static byte ALIGNRIGHT = 0x02;

    protected void setLight(TileEntity tileEntity, EnumFacing side){
        int ambientLight = tileEntity.getWorld().getLightFor(EnumSkyBlock.SKY, tileEntity.getPos());
        int var6 = ambientLight % 65536;
        int var7 = ambientLight / 65536;
        float var8 = 1.0F;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var6 * var8, var7 * var8);
    }

    protected void renderTextOnBlock(String renderString, EnumFacing side, EnumFacing orientation, LocationDouble location, float size, double posx, double posy, int color, byte align) {
        this.renderTextOnBlock(renderString, side, orientation, location, size, posx, posy, 0F, color, align);
    }

    protected void renderTextOnBlock(String renderString, EnumFacing side, EnumFacing orientation, LocationDouble location, float size, double posx, double posy, float angle, int color, byte align){
        if (renderString == null || renderString.equals("")) { return; }

        int stringWidth = this.renderFont.getStringWidth(renderString);

        GL11.glPushMatrix();

        this.alignRendering(side, orientation, location);
        this.moveRendering(size, posx, posy, -0.001);

        GL11.glRotatef(angle, 0.0f, 0.0f, 1.0f);

        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_LIGHTING);

        switch (align){
            case 0:
                this.renderFont.drawString(renderString, 0, 0, color);
                break;
            case 1:
                this.renderFont.drawString(renderString, -stringWidth / 2, 0, color);
                break;
            case 2:
                this.renderFont.drawString(renderString, -stringWidth, 0, color);
                break;
        }

        GL11.glDepthMask(true);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    protected void renderStackOnBlock(ItemStack stack, EnumFacing side, EnumFacing orientation, LocationDouble location, float size, double posx, double posy) {
        if (stack == null) { return; }

        int[][] savedGLState = saveGLState(new int[]{ GL11.GL_ALPHA_TEST, GL11.GL_LIGHTING });
        GL11.glPushMatrix();

        this.alignRendering(side, orientation, location);
        this.moveRendering(size, posx, posy, -0.001);

        this.renderItem.renderItemAndEffectIntoGUI(stack, 0, 0);
        this.renderItem.renderItemOverlayIntoGUI(renderFont, stack, 0, 0, null);

        GL11.glPopMatrix();
        restoreGlState(savedGLState);
    }

    protected void renderIconOnBlock(TextureAtlasSprite icon, int sheet, EnumFacing side, EnumFacing orientation, LocationDouble barrelPos, float size, double posx, double posy, double zdepth){
        if (icon == null) { return ; }

        int[][] savedGLState = modifyGLState(new int[]{ GL11.GL_LIGHTING }, new int[]{ GL11.GL_ALPHA_TEST });
        GL11.glPushMatrix();

        this.alignRendering(side, orientation, barrelPos);
        this.moveRendering(size, posx, posy, zdepth);

        if(sheet == 0)
            RenderUtils.bindMinecraftBlockSheet();
        else
            RenderUtils.bindMinecraftItemSheet();

        this.drawIcon(0, 0, icon, side);

        GL11.glPopMatrix();
        restoreGlState(savedGLState);
    }

    protected void alignRendering(EnumFacing side, EnumFacing orientation, LocationDouble position){
        GL11.glTranslated(position.x + 0.5F, position.y + 0.5F, position.z + 0.5F);     // We align the rendering on the center of the block
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(this.getRotationYForSide(side, orientation), 0.0F, 1.0F, 0.0F); // We rotate it so it face the right face
        GL11.glRotatef(this.getRotationXForSide(side), 1.0F, 0.0F, 0.0F);
        GL11.glTranslated(-0.5F, -0.5F, -0.5f);
    }

    protected void moveRendering(float size, double posX, double posY, double posz){
        GL11.glTranslated(0, 0, posz);
        GL11.glScalef(scale, scale, -0.0001f);			  // We flatten the rendering and scale it to the right size
        GL11.glTranslated(posX, posY, 0);		  // Finally, we translate the icon itself to the correct position
        GL11.glScalef(size, size, 1.0f);
    }

    protected void drawIcon(int posX, int posY, TextureAtlasSprite icon, EnumFacing side) {
        float minU = icon.getMinU();
        float minV = icon.getMinV();
        float maxU = icon.getMaxU();
        float maxV = icon.getMaxV();
        int sizeX = 16;
        int sizeY = 16;
        VertexBuffer tes = Tessellator.getInstance().getBuffer();

        tes.begin(GL11.GL_QUADS, RenderUtils.POSITION_TEX_NORMALF());
        tes.pos(posX, posY + sizeY, 0).tex(minU, maxV).normal(0, -1, 0).endVertex();
        tes.pos(posX + sizeX, posY + sizeY, 0).tex(maxU, maxV).normal(0, -1, 0).endVertex();
        tes.pos(posX + sizeX, posY, 0).tex(maxU, minV).normal(0, -1, 0).endVertex();
        tes.pos(posX, posY,     0).tex(minU, minV).normal(0, -1, 0).endVertex();
        Tessellator.getInstance().draw();
    }

    static final int orientRotation[] = {0,0,0,2,3,1,0};
    protected float getRotationYForSide(EnumFacing side, EnumFacing orientation){
        int sideRotation[] = {orientRotation[orientation.ordinal()],orientRotation[orientation.ordinal()],0,2,3,1};
        return sideRotation[side.ordinal()] * 90F;
    }

    static final int sideRotation[] = {1,3,0,0,0,0};
    protected float getRotationXForSide(EnumFacing side){
        return sideRotation[side.ordinal()] * 90F;
    }

    protected int[][] saveGLState(int[] bitsToSave) {
        if (bitsToSave == null) { return null; }

        int[][] savedGLState = new int[bitsToSave.length][2];
        int count = 0;

        for (int glBit : bitsToSave) {
            savedGLState[count][0] = glBit;
            savedGLState[count++][1] = GL11.glIsEnabled(glBit) ? 1: 0;
        }
        return savedGLState;
    }

    protected int[][] modifyGLState(int[] bitsToDisable, int[] bitsToEnable) {
        return modifyGLState(bitsToDisable, bitsToEnable, null);
    }

    protected int[][] modifyGLState(int[] bitsToDisable, int[] bitsToEnable, int[] bitsToSave) {
        if (bitsToDisable == null && bitsToEnable == null && bitsToSave == null) { return null; }

        int[][] savedGLState = new int[(bitsToDisable != null ? bitsToDisable.length: 0) + (bitsToEnable != null ? bitsToEnable.length: 0) + (bitsToSave != null ? bitsToSave.length: 0)][2];
        int count = 0;

        if (bitsToDisable != null) {
            for (int glBit : bitsToDisable) {
                savedGLState[count][0] = glBit;
                savedGLState[count++][1] = GL11.glIsEnabled(glBit) ? 1: 0;
                GL11.glDisable(glBit);
            }
        }
        if (bitsToEnable != null) {
            for (int glBit : bitsToEnable) {
                savedGLState[count][0] = glBit;
                savedGLState[count++][1] = GL11.glIsEnabled(glBit) ? 1: 0;
                GL11.glEnable(glBit);
            }
        }
        if (bitsToSave != null) {
            for (int glBit : bitsToSave) {
                savedGLState[count][0] = glBit;
                savedGLState[count++][1] = GL11.glIsEnabled(glBit) ? 1: 0;
            }
        }

        return savedGLState;
    }

    protected void restoreGlState(int[][] savedGLState) {
        if (savedGLState == null) { return; }

        for(int[] glBit : savedGLState) {
            if (glBit[1] == 1)
                GL11.glEnable(glBit[0]);
            else
                GL11.glDisable(glBit[0]);
        }
    }
}