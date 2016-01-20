package com.dyonovan.neotech.common.blocks.misc

import com.dyonovan.neotech.client.gui.misc.GuiChunkLoader
import com.dyonovan.neotech.common.blocks.BaseBlock
import com.dyonovan.neotech.common.tiles.misc.TileChunkLoader
import com.teambr.bookshelf.common.container.ContainerGeneric
import com.teambr.bookshelf.common.tiles.traits.OpensGui
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.{EnumParticleTypes, BlockPos, EnumWorldBlockLayer}
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/19/2016
  */
class BlockChunkLoader extends BaseBlock(Material.rock, "chunkLoader", classOf[TileChunkLoader]) with OpensGui {

    @SideOnly(Side.CLIENT)
    override def randomDisplayTick(world: World, pos: BlockPos, state: IBlockState, rand: java.util.Random): Unit = {
        world.spawnParticle(EnumParticleTypes.PORTAL, pos.getX + 0.5, pos.getY + 1, pos.getZ + 0.5, Math.sin(rand.nextInt(5)), 0, Math.sin(rand.nextInt(5)))
        world.spawnParticle(EnumParticleTypes.PORTAL, pos.getX + 0.5, pos.getY + 1, pos.getZ + 0.5, Math.sin(rand.nextInt(5)), 0, Math.sin(rand.nextInt(5)))
        world.spawnParticle(EnumParticleTypes.PORTAL, pos.getX + 0.5, pos.getY + 1, pos.getZ + 0.5, Math.sin(rand.nextInt(5)), 0, Math.sin(rand.nextInt(5)))
        world.spawnParticle(EnumParticleTypes.PORTAL, pos.getX + 0.5, pos.getY + 1, pos.getZ + 0.5, Math.sin(rand.nextInt(5)), 0, Math.sin(rand.nextInt(5)))


        world.spawnParticle(EnumParticleTypes.REDSTONE, pos.getX + (3 / 16D), pos.getY + 1, pos.getZ + (3 / 16D), 255, 0, 0)
        world.spawnParticle(EnumParticleTypes.REDSTONE, pos.getX + (3 / 16D), pos.getY + 1, pos.getZ + (3 / 16D), 255, 0, 0)
        world.spawnParticle(EnumParticleTypes.REDSTONE, pos.getX + (3 / 16D), pos.getY + 1, pos.getZ + (13 / 16D), 255, 0, 0)
        world.spawnParticle(EnumParticleTypes.REDSTONE, pos.getX + (3 / 16D), pos.getY + 1, pos.getZ + (13 / 16D), 255, 0, 0)
        world.spawnParticle(EnumParticleTypes.REDSTONE, pos.getX + (13 / 16D), pos.getY + 1, pos.getZ + (3 / 16D), 255, 0, 0)
        world.spawnParticle(EnumParticleTypes.REDSTONE, pos.getX + (13 / 16D), pos.getY + 1, pos.getZ + (3 / 16D), 255, 0, 0)
        world.spawnParticle(EnumParticleTypes.REDSTONE, pos.getX + (13 / 16D), pos.getY + 1, pos.getZ + (13 / 16D), 255, 0, 0)
        world.spawnParticle(EnumParticleTypes.REDSTONE, pos.getX + (13 / 16D), pos.getY + 1, pos.getZ + (13 / 16D), 255, 0, 0)
    }

    setHardness(1.5F)
    override def getRenderType: Int = 3
    setLightOpacity(0)
    override def isOpaqueCube : Boolean = false

    @SideOnly(Side.CLIENT)
    override def isTranslucent : Boolean = true

    override def isFullCube : Boolean = false

    @SideOnly(Side.CLIENT)
    override def getBlockLayer : EnumWorldBlockLayer = EnumWorldBlockLayer.CUTOUT

    override def canRenderInLayer(layer : EnumWorldBlockLayer) : Boolean =
        layer == EnumWorldBlockLayer.TRANSLUCENT || layer == EnumWorldBlockLayer.CUTOUT

    override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        new ContainerGeneric
    }

    override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        world.getTileEntity(new BlockPos(x, y, z)) match {
            case tile : TileChunkLoader => new GuiChunkLoader(tile)
            case _ => null
        }
    }
}
