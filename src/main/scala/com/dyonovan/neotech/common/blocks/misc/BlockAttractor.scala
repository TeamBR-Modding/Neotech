package com.dyonovan.neotech.common.blocks.misc

import com.dyonovan.neotech.client.gui.misc.GuiAttractor
import com.dyonovan.neotech.common.blocks.BaseBlock
import com.dyonovan.neotech.common.container.misc.ContainerAttractor
import com.dyonovan.neotech.common.tiles.misc.TileAttractor
import com.teambr.bookshelf.common.tiles.traits.OpensGui
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.state.{BlockState, IBlockState}
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraft.world.World
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

    override def getRenderType: Int = 3

    override def isOpaqueCube : Boolean = false

    @SideOnly(Side.CLIENT)
    override def isTranslucent : Boolean = true

    override def isFullCube = false

    override def onBlockPlaced(world: World, pos: BlockPos, facing: EnumFacing, hitX : Float, hitY : Float, hitZ : Float, meta : Int, placer : EntityLivingBase) : IBlockState = {
        getDefaultState.withProperty(BlockAttractor.DIR, facing)
    }

    override def createBlockState: BlockState = {
        new BlockState(this, BlockAttractor.DIR)
    }

    override def getStateFromMeta(meta: Int): IBlockState = {
        getDefaultState.withProperty(BlockAttractor.DIR, EnumFacing.getFront(meta))
    }

    override def getMetaFromState(state: IBlockState): Int = {
        state.getValue(BlockAttractor.DIR).ordinal()
    }

    override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        new ContainerAttractor(player.inventory, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileAttractor])
    }

    override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        new GuiAttractor(player.inventory, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileAttractor])
    }
}
