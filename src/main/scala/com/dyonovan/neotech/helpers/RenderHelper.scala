package com.dyonovan.neotech.helpers

import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.{DefaultVertexFormats, VertexFormat, VertexFormatElement}
import net.minecraft.entity.Entity
import org.lwjgl.opengl.GL11

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis pauljoda
 * @since August 17, 2015
 */
object RenderHelper {

    val POSITION_TEX_NORMALF = new VertexFormat()
    val NORMAL_3F = new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.NORMAL, 3)
    POSITION_TEX_NORMALF.addElement(DefaultVertexFormats.POSITION_3F)
    POSITION_TEX_NORMALF.addElement(DefaultVertexFormats.TEX_2F)
    POSITION_TEX_NORMALF.addElement(NORMAL_3F)

    /***
      * Used to draw a 3d cube, provide opposite corners
      * @param x1 First X Position
      * @param y1 First Y Position
      * @param z1 First Z Position
      * @param x2 Second X Position
      * @param y2 Second Y Position
      * @param z2 Second Z Position
      * @param u Min U
      * @param v Min V
      * @param u1 Max U
      * @param v1 Max V
      */
    def renderCubeWithTexture(x1 : Double, y1 : Double, z1 : Double, x2 : Double, y2 : Double, z2 : Double, u : Double, v : Double, u1 : Double, v1 : Double): Unit = {
        val tes = Tessellator.getInstance().getWorldRenderer

        tes.begin(GL11.GL_QUADS, POSITION_TEX_NORMALF)
        tes.pos(x1, y1, z1).tex(u, v).normal(0, -1, 0).endVertex()
        tes.pos(x1, y2, z1).tex(u, v1).normal(0, -1, 0).endVertex()
        tes.pos(x2, y2, z1).tex(u1, v1).normal(0, -1, 0).endVertex()
        tes.pos(x2, y1, z1).tex(u1, v).normal(0, -1, 0).endVertex()
        Tessellator.getInstance().draw()

        tes.begin(GL11.GL_QUADS, POSITION_TEX_NORMALF)
        tes.pos(x1, y1, z2).tex(u, v).normal(0, -1, 0).endVertex()
        tes.pos(x2, y1, z2).tex(u, v1).normal(0, -1, 0).endVertex()
        tes.pos(x2, y2, z2).tex(u1, v1).normal(0, -1, 0).endVertex()
        tes.pos(x1, y2, z2).tex(u1, v).normal(0, -1, 0).endVertex()
        Tessellator.getInstance().draw()

        tes.begin(GL11.GL_QUADS, POSITION_TEX_NORMALF)
        tes.pos(x1, y1, z1).tex(u, v).normal(0, -1, 0).endVertex()
        tes.pos(x1, y1, z2).tex(u, v1).normal(0, -1, 0).endVertex()
        tes.pos(x1, y2, z2).tex(u1, v1).normal(0, -1, 0).endVertex()
        tes.pos(x1, y2, z1).tex(u1, v).normal(0, -1, 0).endVertex()
        Tessellator.getInstance().draw()

        tes.begin(GL11.GL_QUADS, POSITION_TEX_NORMALF)
        tes.pos(x2, y1, z1).tex(u, v).normal(0, -1, 0).endVertex()
        tes.pos(x2, y2, z1).tex(u, v1).normal(0, -1, 0).endVertex()
        tes.pos(x2, y2, z2).tex(u1, v1).normal(0, -1, 0).endVertex()
        tes.pos(x2, y1, z2).tex(u1, v).normal(0, -1, 0).endVertex()
        Tessellator.getInstance().draw()

        tes.begin(GL11.GL_QUADS, POSITION_TEX_NORMALF)
        tes.pos(x1, y1, z1).tex(u, v).normal(0, -1, 0).endVertex()
        tes.pos(x2, y1, z1).tex(u, v1).normal(0, -1, 0).endVertex()
        tes.pos(x2, y1, z2).tex(u1, v1).normal(0, -1, 0).endVertex()
        tes.pos(x1, y1, z2).tex(u1, v).normal(0, -1, 0).endVertex()
        Tessellator.getInstance().draw()

        tes.begin(GL11.GL_QUADS, POSITION_TEX_NORMALF)
        tes.pos(x1, y2, z1).tex(u, v).normal(0, -1, 0).endVertex()
        tes.pos(x1, y2, z2).tex(u, v1).normal(0, -1, 0).endVertex()
        tes.pos(x2, y2, z2).tex(u1, v1).normal(0, -1, 0).endVertex()
        tes.pos(x2, y2, z1).tex(u1, v).normal(0, -1, 0).endVertex()
        Tessellator.getInstance().draw()
    }

    /***
      * Sets up the renderer for a Billboard effect (always facing the player)
      * Used to simulate a 3d ish icon with a 2d sprite
      * @param entity The Entity to Billboard to (usually the player)
      */
    def setupBillboard(entity : Entity) {
        GL11.glRotatef(-entity.rotationYaw, 0, 1, 0)
        GL11.glRotatef(entity.rotationPitch, 1, 0, 0)
    }
}
