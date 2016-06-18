package com.dyonovan.neotech.common.blocks.misc

import com.dyonovan.neotech.client.gui.misc.GuiMobStand
import com.dyonovan.neotech.common.blocks.BaseBlock
import com.dyonovan.neotech.common.container.misc.ContainerMobStand
import com.dyonovan.neotech.common.tiles.misc.TileMobStand
import com.teambr.bookshelf.common.blocks.traits.DropsItems
import com.teambr.bookshelf.common.tiles.traits.OpensGui
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.math.BlockPos
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
  * @since 2/13/2016
  */
class BlockMobStand extends BaseBlock(Material.IRON, "mobStand", classOf[TileMobStand]) with OpensGui with DropsItems {

    override def getRenderType(state : IBlockState) : EnumBlockRenderType = EnumBlockRenderType.MODEL

    override def isOpaqueCube(state : IBlockState) : Boolean = false

    @SideOnly(Side.CLIENT)
    override def isTranslucent(state : IBlockState) : Boolean = true

    override def isFullCube(state : IBlockState) = false


    override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        new ContainerMobStand(player.inventory, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileMobStand])
    }

    override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        new GuiMobStand(player.inventory, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileMobStand])
    }
}
