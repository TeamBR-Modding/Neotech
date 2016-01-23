package com.dyonovan.neotech.common.blocks.storage

import com.dyonovan.neotech.client.gui.storage.GuiFlushableChest
import com.dyonovan.neotech.common.blocks.BaseBlock
import com.dyonovan.neotech.common.blocks.traits.CoreStates
import com.dyonovan.neotech.common.container.storage.ContainerFlushableChest
import com.dyonovan.neotech.common.tiles.storage.TileFlushableChest
import com.dyonovan.neotech.managers.ItemManager
import com.teambr.bookshelf.common.blocks.properties.PropertyRotation
import com.teambr.bookshelf.common.blocks.traits.DropsItems
import com.teambr.bookshelf.common.tiles.traits.OpensGui
import com.teambr.bookshelf.util.WorldUtils
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.{EnumFacing, BlockPos}
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 1/23/2016
  */
class BlockFlushableChest extends BaseBlock(Material.iron, "flushableChest", classOf[TileFlushableChest])
  with CoreStates with DropsItems with OpensGui{

    this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F)

    override def onNeighborBlockChange(world: World, pos: BlockPos, state: IBlockState, block: Block): Unit = {
        if (!world.isRemote) {
            if (world.isBlockPowered(pos)) {
                world.getTileEntity(pos).asInstanceOf[TileFlushableChest].clear()
                world.markBlockForUpdate(pos)
            }
        }
    }

    override def isOpaqueCube: Boolean = false

    override def isFullCube: Boolean = false

    override def getRenderType: Int = 2

    override def rotateBlock(world : World, pos : BlockPos, side : EnumFacing) : Boolean = {
        if(side != EnumFacing.UP && side != EnumFacing.DOWN)
            world.setBlockState(pos, world.getBlockState(pos).withProperty(PropertyRotation.FOUR_WAY, side))
        else
            world.setBlockState(pos, world.getBlockState(pos).withProperty(PropertyRotation.FOUR_WAY, WorldUtils.rotateRight(world.getBlockState(pos).getValue(PropertyRotation.FOUR_WAY))))
        true
    }

    override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        if(player.inventory.getCurrentItem == null || (player.inventory.getCurrentItem != null && player.inventory.getCurrentItem.getItem != ItemManager.wrench))
            new ContainerFlushableChest(player.inventory, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileFlushableChest])
        else null
    }

    @SideOnly(Side.CLIENT)
    override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        if(player.inventory.getCurrentItem == null || (player.inventory.getCurrentItem != null && player.inventory.getCurrentItem.getItem != ItemManager.wrench))
            new GuiFlushableChest(player, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileFlushableChest])
        else null
    }
}
