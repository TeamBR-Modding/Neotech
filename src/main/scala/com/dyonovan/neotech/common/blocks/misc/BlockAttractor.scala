package com.dyonovan.neotech.common.blocks.misc

import com.dyonovan.neotech.common.blocks.BaseBlock
import com.dyonovan.neotech.common.tiles.misc.TileAttractor
import com.teambr.bookshelf.common.tiles.traits.OpensGui
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
  * Created by Dyonovan on 2/14/2016.
  */
class BlockAttractor extends BaseBlock(Material.portal, "blockAttractor", classOf[TileAttractor]) with OpensGui {

    override def getRenderType: Int = 3

    override def isOpaqueCube : Boolean = false

    @SideOnly(Side.CLIENT)
    override def isTranslucent : Boolean = true

    override def isFullCube = false

    override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        null
    }

    override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        null
    }
}
