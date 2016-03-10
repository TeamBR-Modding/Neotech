package com.dyonovan.neotech.client;

import net.minecraft.util.MathHelper;
import org.lwjgl.util.glu.Sphere;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * Takes the base of what the normal GLU Sphere does but the GL_QUAD_STRIP texture locations are based off
 * the given UV coords.
 *
 * @author Paul Davis <pauljoda>
 * @since 3/9/2016
 */
public class TexturedSphere extends Sphere {

    private static float PI = (float) Math.PI;

    @Override
    protected float cos(float r) {
        return MathHelper.cos(r);
    }

    @Override
    protected float sin(float r) {
        return MathHelper.sin(r);
    }

    /**
     * Draws the sphere, will try to wrap the entire texture over the ball, not by panel
     * @param radius The radius of the ball
     * @param slices How many slices, this is from pole to pole and around (like an orange)
     * @param stacks How many stack, this is how many panels to add to each slice, you need at least 2
     * @param minU Texture MinU
     * @param maxU Texture MaxU
     * @param minV Texture MinV
     * @param maxV Texture MaxV
     */
    public void draw(float radius, int slices, int stacks, float minU, float maxU, float minV, float maxV) {
        float rho, drho, theta, dtheta;
        float x, y, z;
        float nsign;

        if (super.orientation == GLU_INSIDE) {
            nsign = -1.0f;
        } else {
            nsign = 1.0f;
        }

        drho = PI / stacks;
        dtheta = 2.0f * PI / slices;

        float stackUSize = (maxU - minU) / (slices + 1);
        float stackVSize = (maxV - minV) / (stacks + 1);

        // draw intermediate stacks as quad strips
        for (int i = 0; i < stacks; i++) {
            rho = i * drho;
            glBegin(GL_QUAD_STRIP);
            for (int j = 0; j <= slices; j++) {
                theta = (j == slices) ? 0.0f : j * dtheta;
                x = -sin(theta) * sin(rho);
                y = cos(theta) * sin(rho);
                z = nsign * cos(rho);

                glTexCoord2f(
                        (j % 2 == 0 ? minU + (j * stackUSize) + stackUSize: minU + (j * stackUSize) + stackUSize),
                        minV + (i * stackVSize));
                glNormal3f(x * nsign, y * nsign, z * nsign);
                glVertex3f(x * radius, y * radius, z * radius);

                x = -sin(theta) * sin(rho + drho);
                y = cos(theta) * sin(rho + drho);
                z = nsign * cos(rho + drho);

                glTexCoord2f(
                        (j % 2 == 0 ? minU + (j * stackUSize) + stackUSize : minU + (j * stackUSize) + stackUSize),
                        minV + (i * stackVSize) + stackVSize);
                glNormal3f(x * nsign, y * nsign, z * nsign);
                glVertex3f(x * radius, y * radius, z * radius);
            }
            glEnd();
        }
    }

    /**
     * Draws the sphere, will try to wrap the entire texture by panel
     * @param radius The radius of the ball
     * @param slices How many slices, this is from pole to pole and around (like an orange)
     * @param stacks How many stack, this is how many panels to add to each slice, you need at least 2
     * @param minU Texture MinU
     * @param maxU Texture MaxU
     * @param minV Texture MinV
     * @param maxV Texture MaxV
     */
    public void drawNoFit(float radius, int slices, int stacks, float minU, float maxU, float minV, float maxV) {
        float rho, drho, theta, dtheta;
        float x, y, z;
        float nsign;

        if (super.orientation == GLU_INSIDE) {
            nsign = -1.0f;
        } else {
            nsign = 1.0f;
        }

        drho = PI / stacks;
        dtheta = 2.0f * PI / slices;

        // draw intermediate stacks as quad strips
        for (int i = 0; i < stacks; i++) {
            rho = i * drho;
            glBegin(GL_QUAD_STRIP);
            for (int j = 0; j <= slices; j++) {
                theta = (j == slices) ? 0.0f : j * dtheta;
                x = -sin(theta) * sin(rho);
                y = cos(theta) * sin(rho);
                z = nsign * cos(rho);

                glTexCoord2f(j % 2 == 0 ? minU  : maxU, minV);
                glNormal3f(x * nsign, y * nsign, z * nsign);
                glVertex3f(x * radius, y * radius, z * radius);

                x = -sin(theta) * sin(rho + drho);
                y = cos(theta) * sin(rho + drho);
                z = nsign * cos(rho + drho);

                glTexCoord2f(j % 2 == 0 ? minU  : maxU, maxV);
                glNormal3f(x * nsign, y * nsign, z * nsign);
                glVertex3f(x * radius, y * radius, z * radius);
            }
            glEnd();
        }
    }
}
