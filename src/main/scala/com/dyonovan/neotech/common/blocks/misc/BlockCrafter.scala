package com.dyonovan.neotech.common.blocks.misc

import com.dyonovan.neotech.client.gui.misc.GuiCrafter
import com.dyonovan.neotech.common.blocks.BaseBlock
import com.dyonovan.neotech.common.container.misc.ContainerCrafter
import com.dyonovan.neotech.common.tiles.misc.TileCrafter
import com.teambr.bookshelf.common.blocks.traits.DropsItems
import com.teambr.bookshelf.common.tiles.traits.OpensGui
import com.teambr.bookshelf.loadables.CreatesTextures
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import scala.collection.mutable.ArrayBuffer

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 18, 2015
 */
class BlockCrafter(name: String, tileEntity: Class[_ <: TileEntity]) extends BaseBlock(Material.wood, name, tileEntity)
        with OpensGui with DropsItems with CreatesTextures {

    setHardness(1.5F)

    override def getRenderType: Int = 3

    override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        new ContainerCrafter(player.inventory, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileCrafter])
    }

    @SideOnly(Side.CLIENT)
    override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        new GuiCrafter(player.inventory, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileCrafter])
    }

    override def getTexturesToStitch: ArrayBuffer[String] = ArrayBuffer("neotech:items/axe_ghost", "neotech:items/shears_ghost")
}
