package com.dyonovan.neotech.common.blocks.storage

import com.dyonovan.neotech.common.blocks.BaseBlock
import com.dyonovan.neotech.common.tiles.storage.TileDimStorage
import com.teambr.bookshelf.common.blocks.properties.PropertyRotation
import com.teambr.bookshelf.common.blocks.traits.{DropsItems, KeepInventory}
import com.teambr.bookshelf.util.WorldUtils
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.{BlockState, IBlockState}
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.{MathHelper, EnumFacing, BlockPos}
import net.minecraft.world.World
import net.minecraftforge.common.property.{ExtendedBlockState, IUnlistedProperty}

/**
  * Created by Dyonovan on 1/23/2016.
  */
class BlockDimStorage extends BaseBlock(Material.iron, "dimStorage", classOf[TileDimStorage])
        with DropsItems { //todo shift click with wrench will break and keep inventory / Override Drops Items

    override def onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, facing: EnumFacing, f1: Float, f2: Float, f3: Float): Boolean = {
        val tile = world.getTileEntity(pos).asInstanceOf[TileDimStorage]

        if (player.getHeldItem != null) {
            val actual = tile.increaseQty(player.getHeldItem)
            if (actual > 0) player.getHeldItem.stackSize -= actual
        }
        true
    }

    override def onBlockClicked(world: World, pos: BlockPos, player: EntityPlayer): Unit = {
        val tile = world.getTileEntity(pos).asInstanceOf[TileDimStorage]

        if (tile.getStackInSlot(0) != null) {
            var actual = 0
            if (!player.isSneaking)
                actual = tile.decreaseQty(false)
            else actual = tile.decreaseQty(true)

            if (actual > 0) {
                player.inventory.addItemStackToInventory(
                    new ItemStack(tile.getStackInSlot(0).getItem, actual, tile.getStackInSlot(0).getItemDamage))
                tile.checkQty()
            }

        }
    }

    override def getRenderType : Int = 3

    override def rotateBlock(world : World, pos : BlockPos, side : EnumFacing) : Boolean = {
        if(side != EnumFacing.UP && side != EnumFacing.DOWN)
            world.setBlockState(pos, world.getBlockState(pos).withProperty(PropertyRotation.FOUR_WAY, side))
        else
            world.setBlockState(pos, world.getBlockState(pos).withProperty(PropertyRotation.FOUR_WAY, WorldUtils.rotateRight(world.getBlockState(pos).getValue(PropertyRotation.FOUR_WAY))))
        true
    }

    override def onBlockPlaced(world : World, blockPos : BlockPos, facing : EnumFacing, hitX : Float, hitY : Float, hitZ : Float, meta : Int, placer : EntityLivingBase) : IBlockState = {
        val playerFacingDirection = if (placer == null) 0 else MathHelper.floor_double((placer.rotationYaw / 90.0F) + 0.5D) & 3
        val enumFacing = EnumFacing.getHorizontal(playerFacingDirection).getOpposite
        this.getDefaultState.withProperty(PropertyRotation.FOUR_WAY, enumFacing)
    }

    /**
      * Used to say what our block state is
      */
    override def createBlockState() : BlockState = {
        val listed = new Array[IProperty[_]](1)
        listed(0) = PropertyRotation.FOUR_WAY
        val unlisted = new Array[IUnlistedProperty[_]](0)
        new ExtendedBlockState(this, listed, unlisted)
    }

    /**
      * Used to convert the meta to state
      *
      * @param meta The meta
      * @return
      */
    override def getStateFromMeta(meta : Int) : IBlockState = getDefaultState.withProperty(PropertyRotation.FOUR_WAY, EnumFacing.getFront(meta))

    /**
      * Called to convert state from meta
      *
      * @param state The state
      * @return
      */
    override def getMetaFromState(state : IBlockState) = state.getValue(PropertyRotation.FOUR_WAY).getIndex
}
