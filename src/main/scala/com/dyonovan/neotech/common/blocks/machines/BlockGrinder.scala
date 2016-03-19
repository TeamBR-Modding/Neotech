package com.dyonovan.neotech.common.blocks.machines

import com.dyonovan.neotech.client.gui.machines.GuiGrinder
import com.dyonovan.neotech.common.blocks.BaseBlock
import com.dyonovan.neotech.common.container.machines.ContainerGrinder
import com.dyonovan.neotech.common.tiles.machines.TileGrinder
import com.dyonovan.neotech.managers.BlockManager
import com.teambr.bookshelf.client.gui.GuiColor
import com.teambr.bookshelf.common.blocks.traits.DropsItems
import com.teambr.bookshelf.common.tiles.traits.OpensGui
import com.teambr.bookshelf.traits.HasToolTip
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.lwjgl.input.Keyboard

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/11/2016
  */
class BlockGrinder extends BaseBlock(Material.rock, "grinder", classOf[TileGrinder]) with OpensGui with DropsItems with HasToolTip {
    override def getRenderType(state : IBlockState) : EnumBlockRenderType = EnumBlockRenderType.MODEL

    override def onLanded(world : World, entity : Entity) : Unit = {
        super.onLanded(world, entity)
        if(entity.isInstanceOf[EntityPlayer] && entity.fallDistance > 0.0 &&
                !world.isAirBlock(new BlockPos(entity.posX, entity.posY, entity.posZ))) {
            world.getBlockState(new BlockPos(entity.posX, entity.posY, entity.posZ)).getBlock match {
                case Blocks.wooden_pressure_plate =>
                    world.getTileEntity(new BlockPos(entity.posX, entity.posY - 1, entity.posZ)).asInstanceOf[TileGrinder].activateGrinder(entity.fallDistance.toInt, 1.00)
                case Blocks.stone_pressure_plate =>
                    world.getTileEntity(new BlockPos(entity.posX, entity.posY - 1, entity.posZ)).asInstanceOf[TileGrinder].activateGrinder(entity.fallDistance.toInt, 1.25)
                case BlockManager.playerPlate =>
                    world.getTileEntity(new BlockPos(entity.posX, entity.posY - 1, entity.posZ)).asInstanceOf[TileGrinder].activateGrinder(entity.fallDistance.toInt, 1.50)
                case Blocks.heavy_weighted_pressure_plate =>
                    world.getTileEntity(new BlockPos(entity.posX, entity.posY - 1, entity.posZ)).asInstanceOf[TileGrinder].activateGrinder(entity.fallDistance.toInt, 1.75)
                case Blocks.light_weighted_pressure_plate =>
                    world.getTileEntity(new BlockPos(entity.posX, entity.posY - 1, entity.posZ)).asInstanceOf[TileGrinder].activateGrinder(entity.fallDistance.toInt, 2.00)
                case _ =>
            }
        }
    }

    override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        world.getTileEntity(new BlockPos(x, y, z)) match {
            case tile : TileGrinder => new ContainerGrinder(player.inventory, tile)
            case _ => null
        }
    }

    override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        world.getTileEntity(new BlockPos(x, y, z)) match {
            case tile : TileGrinder => new GuiGrinder(player, tile)
            case _ => null
        }
    }

    override def getToolTip() : List[String] = {
        if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
            List[String](GuiColor.ORANGE + "Press <SHIFT> for more info")
         else
            List[String]("Place any vanilla pressure plate on top", "Jump on the plate to grind ores", "Better plates work faster")

    }
}
