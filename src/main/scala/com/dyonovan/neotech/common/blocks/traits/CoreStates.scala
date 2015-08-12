package com.dyonovan.neotech.common.blocks.traits

import com.dyonovan.neotech.common.tiles.AbstractTile
import com.teambr.bookshelf.common.blocks.properties.PropertyRotation
import net.minecraft.block.Block
import net.minecraft.block.properties.{IProperty, PropertyBool}
import net.minecraft.block.state.{BlockState, IBlockState}
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.{MathHelper, BlockPos, EnumFacing, EnumWorldBlockLayer}
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.property.{ExtendedBlockState, IUnlistedProperty}
import net.minecraftforge.fml.relauncher.{SideOnly, Side}

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

  lazy val PROPERTY_ACTIVE = PropertyBool.create("IsActive")

  /**
   * Called when the block is placed, we check which way the player is facing and put our value as the opposite of that
   */
  override def onBlockPlaced(world : World, blockPos : BlockPos, facing : EnumFacing, hitX : Float, hitY : Float, hitZ : Float, meta : Int, placer : EntityLivingBase) : IBlockState = {
    val playerFacingDirection = if (placer == null) 0 else MathHelper.floor_double((placer.rotationYaw / 90.0F) + 0.5D) & 3
    val enumFacing = EnumFacing.getHorizontal(playerFacingDirection).getOpposite
    this.getDefaultState.withProperty(PropertyRotation.FOUR_WAY.getProperty, enumFacing)
  }

  /**
   * Used to say what our block state is
   */
  override def createBlockState() : BlockState = {
    val listed : Array[IProperty] = Array(PropertyRotation.FOUR_WAY.getProperty, PROPERTY_ACTIVE)
    val unlisted = new Array[IUnlistedProperty[_]](0)
    new ExtendedBlockState(this, listed, unlisted)
  }

  override def getExtendedState(state : IBlockState, world : IBlockAccess, pos : BlockPos) : IBlockState = {
    world.getTileEntity(pos) match {
      case core : AbstractTile =>
        state.withProperty (PropertyRotation.FOUR_WAY.getProperty, world.getBlockState (pos).getValue (PropertyRotation.FOUR_WAY.getProperty).asInstanceOf[EnumFacing] )
          .withProperty(PROPERTY_ACTIVE, core.isBurning.asInstanceOf[java.lang.Boolean])
      case _ =>
        state.withProperty (PropertyRotation.FOUR_WAY.getProperty, world.getBlockState (pos).getValue (PropertyRotation.FOUR_WAY.getProperty).asInstanceOf[EnumFacing] )
          .withProperty(PROPERTY_ACTIVE, true)
    }
  }

  /**
   * Used to convert the meta to state
   * @param meta The meta
   * @return
   */
  override def getStateFromMeta(meta : Int) : IBlockState = {
    getDefaultState.withProperty(PropertyRotation.FOUR_WAY.getProperty, EnumFacing.getFront(meta & 5)).withProperty(PROPERTY_ACTIVE, if((Integer.valueOf(meta & 15) >> 2) == 1) true else false)
  }

  /**
   * Called to convert state from meta
   * @param state The state
   * @return
   */
  override def getMetaFromState(state : IBlockState) = {
    val b0 : Byte = 0
    var i : Int = b0 | state.getValue(PropertyRotation.FOUR_WAY.getProperty).asInstanceOf[EnumFacing].getIndex
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
