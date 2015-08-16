package com.dyonovan.neotech.pipes.blocks

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.client.modelfactory.ModelFactory
import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.pipes.network.ItemResourceEntity
import com.dyonovan.neotech.pipes.tiles.{BasePipe, IPipe}
import com.dyonovan.neotech.pipes.world.WorldTicker
import com.teambr.bookshelf.common.blocks.properties.TileAwareState
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{ EnumFacing, AxisAlignedBB, BlockPos, EnumWorldBlockLayer }
import net.minecraft.world.{ IBlockAccess, World }
import net.minecraftforge.fml.relauncher.{ Side, SideOnly }

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis pauljoda
 * @since August 14, 2015
 */
class BlockPipe(val name : String, mat : Material, tileClass : Class[_ <: BasePipe]) extends BlockContainer(mat) {

    //Constructor
    setUnlocalizedName(Reference.MOD_ID + ":" + name)
    setCreativeTab(NeoTech.tabPipes)
    setHardness(1.5F)
    setLightOpacity(0)
    ModelFactory.INSTANCE.pipeRegistry += this

    override def onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
        val item = new ItemResourceEntity(new ItemStack(Items.coal), pos.getX + 0.5, pos.getY + 0.5, pos.getZ + 0.5, 0.05, pos, pos.north(5).east(2), worldIn)
        WorldTicker.INSTANCE.addResourceToWorld(worldIn, item)
        true
    }

    override def getExtendedState(state : IBlockState, world : IBlockAccess, pos : BlockPos) : IBlockState = {
        world.getTileEntity(pos) match {
            case tile : TileEntity => new TileAwareState(tile, world.getBlockState(pos).getBlock)
            case _ => state
        }
    }

    override def setBlockBoundsBasedOnState(worldIn : IBlockAccess, pos : BlockPos) {
        var x1 = 0.25F
        var x2 = 1.0F - x1
        var y1 = 0.25F
        var y2 = 1.0F - y1
        var z1 = 0.25F
        var z2 = 1.0F - z1
        if(isCableConnected(worldIn, pos, EnumFacing.WEST)) {
            x1 = 0.0F
        }

        if(isCableConnected(worldIn, pos, EnumFacing.EAST)) {
            x2 = 1.0F
        }

        if(isCableConnected(worldIn, pos, EnumFacing.NORTH)) {
            z1 = 0.0F
        }

        if(isCableConnected(worldIn, pos, EnumFacing.SOUTH)) {
            z2 = 1.0F
        }

        if(isCableConnected(worldIn, pos, EnumFacing.DOWN)) {
            y1 = 0.0F
        }

        if(isCableConnected(worldIn, pos, EnumFacing.UP)) {
            y2 = 1.0F
        }

        this.setBlockBounds(x1, y1, z1, x2, y2, z2)
    }

    def isCableConnected(world: IBlockAccess, pos: BlockPos, facing: EnumFacing) : Boolean = {
        world.getTileEntity(pos).asInstanceOf[IPipe].canConnect(facing)
    }

    override def addCollisionBoxesToList(worldIn : World, pos : BlockPos, state : IBlockState, mask : AxisAlignedBB, list : java.util.List[_], collidingEntity : Entity) {
        this.setBlockBoundsBasedOnState(worldIn, pos)
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity)
    }

    override def getRenderType : Int = 3

    override def isOpaqueCube : Boolean = false

    @SideOnly(Side.CLIENT)
    override def isTranslucent : Boolean = true

    override def isFullCube : Boolean = false

    @SideOnly(Side.CLIENT)
    override def getBlockLayer : EnumWorldBlockLayer = EnumWorldBlockLayer.CUTOUT

    override def createNewTileEntity(worldIn : World, meta : Int) : TileEntity = tileClass.newInstance()
}
