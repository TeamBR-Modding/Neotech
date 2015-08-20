package com.dyonovan.neotech.common.blocks.traits

import com.dyonovan.neotech.common.tiles.AbstractMachine
import com.teambr.bookshelf.common.blocks.properties.PropertyRotation
import net.minecraft.block.Block
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.{BlockState, IBlockState}
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.{BlockPos, EnumFacing, EnumWorldBlockLayer, MathHelper}
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 11, 2015
 */
trait CoreStates extends Block {

    lazy val PROPERTY_ACTIVE = PropertyBool.create("isactive")

    /**
     * Called when the block is placed, we check which way the player is facing and put our value as the opposite of that
     */
    override def onBlockPlaced(world : World, blockPos : BlockPos, facing : EnumFacing, hitX : Float, hitY : Float, hitZ : Float, meta : Int, placer : EntityLivingBase) : IBlockState = {
        val playerFacingDirection = if (placer == null) 0 else MathHelper.floor_double((placer.rotationYaw / 90.0F) + 0.5D) & 3
        val enumFacing = EnumFacing.getHorizontal(playerFacingDirection).getOpposite
        this.getDefaultState.withProperty(PropertyRotation.FOUR_WAY, enumFacing).withProperty(PROPERTY_ACTIVE, false)
    }

    /**
     * Used to say what our block state is
     */
    override def createBlockState() : BlockState = {
        new BlockState(this, PropertyRotation.FOUR_WAY, PROPERTY_ACTIVE)
    }

    /**
     * Used to tell the actual state in world
     * @param state The state that is currently in
     * @param worldIn The world
     * @param pos The position
     * @return The new state, with relevant info added
     */
    override def getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos) : IBlockState = {
        worldIn.getTileEntity(pos) match {
            case tile : AbstractMachine =>
                state.withProperty(PROPERTY_ACTIVE, tile.values.burnTime > 0)
            case _ => state
        }
    }

    /**
     * Used to convert the meta to state
     * @param meta The meta
     * @return
     */
    override def getStateFromMeta(meta : Int) : IBlockState = {
        var facing = EnumFacing.getFront(meta & 5)
        if(facing.getAxis == EnumFacing.Axis.Y)
            facing = EnumFacing.NORTH
        getDefaultState.withProperty(PropertyRotation.FOUR_WAY, facing).withProperty(PROPERTY_ACTIVE, if((Integer.valueOf(meta & 15) >> 2) == 1) true else false)
    }

    /**
     * Called to convert state from meta
     * @param state The state
     * @return
     */
    override def getMetaFromState(state : IBlockState) = {
        var i : Int = state.getValue(PropertyRotation.FOUR_WAY).asInstanceOf[EnumFacing].ordinal()
        i |= (if(state.getValue(PROPERTY_ACTIVE).asInstanceOf[Boolean]) 1 else 0 ) << 2
        i
    }

    override def getRenderType : Int = 3

    override def isOpaqueCube : Boolean = false

    @SideOnly(Side.CLIENT)
    override def isTranslucent : Boolean = true

    @SideOnly(Side.CLIENT)
    override def getBlockLayer : EnumWorldBlockLayer = EnumWorldBlockLayer.CUTOUT
}
