package com.dyonovan.neotech.common.blocks.misc

import com.dyonovan.neotech.client.gui.misc.GuiFertilizer
import com.dyonovan.neotech.common.blocks.BaseBlock
import com.dyonovan.neotech.common.container.misc.ContainerFertilizer
import com.dyonovan.neotech.common.tiles.misc.TileFertilizer
import com.teambr.bookshelf.common.tiles.traits.OpensGui
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockPos
import net.minecraft.world.World

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
class BlockFertilizer(name: String, tileEntity: Class[_ <: TileEntity]) extends
        BaseBlock(Material.iron, name, tileEntity) with OpensGui {

    setLightLevel(1.0F)


    override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        new ContainerFertilizer(player.inventory, world.getTileEntity(new BlockPos(x, y, x)).asInstanceOf[TileFertilizer])
    }

    override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        new GuiFertilizer(player, world.getTileEntity(new BlockPos(x, y, x)).asInstanceOf[TileFertilizer])
    }
}
