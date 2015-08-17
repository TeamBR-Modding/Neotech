package com.dyonovan.neotech.helpers

import net.minecraft.client.renderer.Tessellator

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
    def renderCubeWithTexture(x1 : Double, y1 : Double, z1 : Double, x2 : Double, y2 : Double, z2 : Double, u : Double, v : Double, u1 : Double, v1 : Double): Unit = {
        val tes = Tessellator.getInstance().getWorldRenderer
        tes.startDrawingQuads()
        tes.addVertexWithUV(x1, y1, z1, u, v)
        tes.addVertexWithUV(x1, y2, z1, u, v1)
        tes.addVertexWithUV(x2, y2, z1, u1, v1)
        tes.addVertexWithUV(x2, y1, z1, u1, v)

        tes.addVertexWithUV(x1, y1, z2, u, v)
        tes.addVertexWithUV(x2, y1, z2, u, v1)
        tes.addVertexWithUV(x2, y2, z2, u1, v1)
        tes.addVertexWithUV(x1, y2, z2, u1, v)

        tes.addVertexWithUV(x1, y1, z1, u, v)
        tes.addVertexWithUV(x1, y1, z2, u, v1)
        tes.addVertexWithUV(x1, y2, z2, u1, v1)
        tes.addVertexWithUV(x1, y2, z1, u1, v)

        tes.addVertexWithUV(x2, y1, z1, u, v)
        tes.addVertexWithUV(x2, y2, z1, u, v1)
        tes.addVertexWithUV(x2, y2, z2, u1, v1)
        tes.addVertexWithUV(x2, y1, z2, u1, v)

        tes.addVertexWithUV(x1, y1, z1, u, v)
        tes.addVertexWithUV(x2, y1, z1, u, v1)
        tes.addVertexWithUV(x2, y1, z2, u1, v1)
        tes.addVertexWithUV(x1, y1, z2, u1, v)

        tes.addVertexWithUV(x1, y2, z1, u, v)
        tes.addVertexWithUV(x1, y2, z2, u, v1)
        tes.addVertexWithUV(x2, y2, z2, u1, v1)
        tes.addVertexWithUV(x2, y2, z1, u1, v)

        Tessellator.getInstance().draw()
    }
}
