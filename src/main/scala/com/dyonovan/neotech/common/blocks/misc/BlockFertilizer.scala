package com.dyonovan.neotech.common.blocks.misc

import com.dyonovan.neotech.client.gui.misc.GuiFertilizer
import com.dyonovan.neotech.common.blocks.BaseBlock
import com.dyonovan.neotech.common.container.misc.ContainerFertilizer
import com.dyonovan.neotech.common.tiles.misc.TileFertilizer
import com.teambr.bookshelf.common.tiles.traits.OpensGui
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyInteger
import net.minecraft.block.state.{BlockState, IBlockState}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{AxisAlignedBB, BlockPos, EnumFacing}
import net.minecraft.world.{IBlockAccess, World}

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 22, 2015
 */
object BlockFertilizer {
    lazy val ON_BLOCK = PropertyInteger.create("attached_side", 0, 6)
}

class BlockFertilizer(name: String, tileEntity: Class[_ <: TileEntity]) extends
BaseBlock(Material.iron, name, tileEntity) with OpensGui {

    setLightLevel(1.0F)
    setDefaultState(this.blockState.getBaseState
            .withProperty(BlockFertilizer.ON_BLOCK, 6))

    override def isFullBlock: Boolean = false
    override def isFullCube : Boolean = false
    override def isOpaqueCube : Boolean = false

    override def onBlockPlaced(world: World, pos: BlockPos, facing: EnumFacing, hitX : Float, hitY : Float, hitZ : Float, meta : Int, placer : EntityLivingBase) : IBlockState = {
        var attachedSide = 6

        placer match {
            case player : EntityPlayer =>
                if(player.isSneaking)
                    return getDefaultState.withProperty(BlockFertilizer.ON_BLOCK, attachedSide)
            case _ =>
        }

        if (attachedSide == 6 && world.getBlockState(pos.offset(facing.getOpposite)) != null && world.getBlockState(pos.offset(facing.getOpposite)).getBlock.isSideSolid(world, pos.offset(facing.getOpposite), facing)) {
            attachedSide = facing.getOpposite.ordinal()
        }

        if(attachedSide == 6) {
            for (dir <- EnumFacing.values()) {
                if (attachedSide == 6 && world.getBlockState(pos.offset(dir)) != null && world.getBlockState(pos.offset(dir)).getBlock.isSideSolid(world, pos.offset(dir), dir.getOpposite))
                    attachedSide = dir.ordinal()
            }
        }

        getDefaultState.withProperty(BlockFertilizer.ON_BLOCK, attachedSide)
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    override def getStateFromMeta(meta: Int): IBlockState = {
        getDefaultState.withProperty(BlockFertilizer.ON_BLOCK, meta)
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    override def getMetaFromState(state: IBlockState): Int = {
        state.getValue(BlockFertilizer.ON_BLOCK).asInstanceOf[Int]
    }

    override def createBlockState: BlockState = {
        new BlockState(this, BlockFertilizer.ON_BLOCK)
    }

    override def setBlockBoundsBasedOnState(worldIn : IBlockAccess, pos : BlockPos): Unit = {
        if(worldIn.getBlockState(pos).getValue(BlockFertilizer.ON_BLOCK).asInstanceOf[Int] == 6) {
            this.setBlockBounds(6F / 16F, 6F / 16F, 6F / 16F, 10F / 16F, 10F / 16F, 10F / 16F)
        } else {
            EnumFacing.getFront(worldIn.getBlockState(pos).getValue(BlockFertilizer.ON_BLOCK).asInstanceOf[Int]) match {
                case EnumFacing.UP =>
                    this.setBlockBounds(6F / 16F, 12F / 16F, 6F / 16F, 10F / 16F, 16F / 16F, 10F / 16F)
                case EnumFacing.DOWN =>
                    this.setBlockBounds(6F / 16F, 0F / 16F, 6F / 16F, 10F / 16F, 4 / 16F, 10F / 16F)
                case EnumFacing.SOUTH =>
                    this.setBlockBounds(6F / 16F, 6F / 16F, 12F / 16F, 10F / 16F, 10F / 16F, 16F / 16F)
                case EnumFacing.NORTH =>
                    this.setBlockBounds(6F / 16F, 6F / 16F, 0F / 16F, 10F / 16F, 10F / 16F, 4F / 16F)
                case EnumFacing.EAST =>
                    this.setBlockBounds(12F / 16F, 6F / 16F, 6 / 16F, 16F / 16F, 10F / 16F, 10F / 16F)
                case EnumFacing.WEST =>
                    this.setBlockBounds(0F / 16F, 6F / 16F, 6F / 16F, 4F / 16F, 10F / 16F, 10F / 16F)
                case _ =>
                    this.setBlockBounds(6F / 16F, 6F / 16F, 0F / 16F, 10F / 16F, 10F / 16F, 10F / 16F)
            }
        }
    }

    def facingToInt(facing : EnumFacing) : Int = facing.ordinal()

    override def addCollisionBoxesToList(worldIn : World, pos : BlockPos, state : IBlockState, mask : AxisAlignedBB, list : java.util.List[_], collidingEntity : Entity) {
        this.setBlockBoundsBasedOnState(worldIn, pos)
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity)
    }

    override def getRenderType : Int = 3

    override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        new ContainerFertilizer(player.inventory, world.getTileEntity(new BlockPos(x, y, x)).asInstanceOf[TileFertilizer])
    }

    override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        new GuiFertilizer(player, world.getTileEntity(new BlockPos(x, y, x)).asInstanceOf[TileFertilizer])
    }
}
