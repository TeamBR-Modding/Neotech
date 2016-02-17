package com.dyonovan.neotech.common.entities;

import com.dyonovan.neotech.managers.ItemManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
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

    @SuppressWarnings("unused")
    public EntityNet(World world) {
        super(world);
        renderDistanceWeight = 10.0D;
    }

    @SuppressWarnings("unused")
    public EntityNet(World world, double x, double y, double z) {
        super(world, x, y, z);
        renderDistanceWeight = 10.0D;
    }

    public EntityNet(World world, EntityLivingBase shooter, double velocity) {
        super(world, shooter);
        renderDistanceWeight = 10.0D;
        this.setSize(0.5F, 0.5F);
        this.setLocationAndAngles(shooter.posX, shooter.posY + (double)shooter.getEyeHeight(), shooter.posZ, shooter.rotationYaw, shooter.rotationPitch);
        this.posX -= (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        this.posY -= 0.10000000149011612D;
        this.posZ -= (double)(MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.motionX = (double)(-MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
        this.motionZ = (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
        this.motionY = (double)(-MathHelper.sin(this.rotationPitch / 180.0F * (float)Math.PI));
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, (float)velocity * 1.5F, 1.0F);
    }

    @Override
    protected void onImpact(MovingObjectPosition mop) {
        ItemStack stack = new ItemStack(ItemManager.mobNet(), 1);
        if (mop != null && !worldObj.isRemote) {
            if (isValidEntity(mop.entityHit)) {
                NBTTagCompound tag = new NBTTagCompound();
                mop.entityHit.writeToNBT(tag);
                tag.setString("type", EntityList.getEntityString(mop.entityHit));
                mop.entityHit.setDead();
                stack.setTagCompound(tag);
                if (mop.entityHit.hasCustomName())
                    stack.setStackDisplayName(mop.entityHit.getCustomNameTag());
            }
            EntityItem item = new EntityItem(worldObj, mop.hitVec.xCoord, mop.hitVec.yCoord + 1, mop.hitVec.zCoord, stack);
            worldObj.spawnEntityInWorld(item);
        }
        this.setDead();
    }

    private boolean isValidEntity(Entity entity) {
        return entity instanceof EntityCreature ||
                entity instanceof EntitySlime ||
                entity instanceof EntityGhast ||
                entity instanceof EntityBat ||
                entity instanceof EntitySquid;
    }
}
