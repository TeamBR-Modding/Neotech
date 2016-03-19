package com.dyonovan.neotech.common.blocks.misc

import com.dyonovan.neotech.client.gui.misc.GuiAttractor
import com.dyonovan.neotech.common.blocks.BaseBlock
import com.dyonovan.neotech.common.container.misc.ContainerAttractor
import com.dyonovan.neotech.common.tiles.misc.TileAttractor
import com.teambr.bookshelf.common.tiles.traits.OpensGui
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.state.{BlockStateContainer, IBlockState}
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
  * Created by Dyonovan on 2/14/2016.
  */
object BlockAttractor {
    lazy val DIR = PropertyDirection.create("attached_side")
}

class BlockAttractor extends BaseBlock(Material.portal, "blockAttractor", classOf[TileAttractor]) with OpensGui {

    setDefaultState(this.blockState.getBaseState
            .withProperty(BlockAttractor.DIR, EnumFacing.UP))

    override def getRenderType(state: IBlockState): Int = 3

    override def isOpaqueCube(state: IBlockState) : Boolean = false

    @SideOnly(Side.CLIENT)
    override def isTranslucent(state: IBlockState) : Boolean = true

    override def isFullCube(state: IBlockState) = false

    override def onBlockPlaced(world: World, pos: BlockPos, facing: EnumFacing, hitX : Float, hitY : Float, hitZ : Float, meta : Int, placer : EntityLivingBase) : IBlockState = {
        getDefaultState.withProperty(BlockAttractor.DIR, facing)
    }

    override def createBlockState: BlockStateContainer = {
        new BlockStateContainer(this, BlockAttractor.DIR)
    }

    override def getStateFromMeta(meta: Int): IBlockState = {
        getDefaultState.withProperty(BlockAttractor.DIR, EnumFacing.getFront(meta))
    }

    override def getMetaFromState(state: IBlockState): Int = {
        state.getValue(BlockAttractor.DIR).ordinal()
    }

    override def setBlockBoundsBasedOnState(worldIn : IBlockAccess, pos : BlockPos): Unit = {
        var minX = 5 / 16F
        var minY = 5 / 16F
        var minZ = 5 / 16F
        var maxX = 11 / 16F
        var maxY = 11 / 16F
        var maxZ = 11 / 16F

        val state = worldIn.getBlockState(pos)
        val side = state.getValue(BlockAttractor.DIR)

        side match {
            case EnumFacing.UP      => minY = 0F
            case EnumFacing.DOWN    => maxY = 1F
            case EnumFacing.EAST    => minX = 0F
            case EnumFacing.WEST    => maxX = 1F
            case EnumFacing.NORTH   => maxZ = 1F
            case EnumFacing.SOUTH   => minZ = 0F
            case _ =>
        }

        setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ)
    }

    override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        new ContainerAttractor(player.inventory, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileAttractor])
    }

    override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        new GuiAttractor(player.inventory, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileAttractor])
    }
}
