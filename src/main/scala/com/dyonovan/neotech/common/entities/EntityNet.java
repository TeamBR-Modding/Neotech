package com.dyonovan.neotech.common.entities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since 2/12/2016
 */
public class EntityNet extends EntityThrowable {

    public EntityNet(World world) {
        super(world);
        renderDistanceWeight = 10.0D;
        dataWatcher.addObjectByDataType(13, 5);
    }

    public EntityNet(World world, double x, double y, double z, ItemStack stack) {
        super(world, x, y, z);
        renderDistanceWeight = 10.0D;
        dataWatcher.addObject(13, stack);
    }

    public EntityNet(World world, EntityLivingBase shooter, ItemStack stack) {
        super(world, shooter);
        renderDistanceWeight = 10.0D;
        dataWatcher.addObject(13, stack);

    }

    @Override
    protected void onImpact(MovingObjectPosition p_70184_1_) {

    }
}
