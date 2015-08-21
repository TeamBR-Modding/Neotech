package com.dyonovan.neotech.common.blocks.machines

import java.util.Random

import com.dyonovan.neotech.client.gui.machines.{GuiFluidGenerator, GuiElectricCrusher, GuiElectricFurnace, GuiFurnaceGenerator}
import com.dyonovan.neotech.common.blocks.BaseBlock
import com.dyonovan.neotech.common.blocks.traits.CoreStates
import com.dyonovan.neotech.common.container.machines.{ContainerElectricCrusher, ContainerElectricFurnace, ContainerFurnaceGenerator}
import com.dyonovan.neotech.common.tiles.AbstractMachine
import com.dyonovan.neotech.common.tiles.machines.{TileFluidGenerator, TileElectricCrusher, TileElectricFurnace, TileFurnaceGenerator}
import com.dyonovan.neotech.managers.BlockManager
import com.teambr.bookshelf.Bookshelf
import com.teambr.bookshelf.common.blocks.properties.PropertyRotation
import com.teambr.bookshelf.common.blocks.traits.DropsItems
import com.teambr.bookshelf.common.container.ContainerGeneric
import com.teambr.bookshelf.common.tiles.traits.OpensGui
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{BlockPos, EnumFacing}
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
 * @since August 11, 2015
 */
class BlockMachine(name: String, tileEntity: Class[_ <: TileEntity]) extends BaseBlock(Material.iron, name, tileEntity)
    with OpensGui with CoreStates with DropsItems {

    override def onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
        world.getTileEntity(pos) match {
            case tile: AbstractMachine =>
                player.openGui(Bookshelf, 0, world, pos.getX, pos.getY, pos.getZ)
            case _ =>
        }
        true
    }

    @SideOnly(Side.CLIENT)
    override def randomDisplayTick(world: World, pos: BlockPos, state: IBlockState, rand: Random): Unit = {
        if (getActualState(state, world, pos).getValue(this.PROPERTY_ACTIVE).asInstanceOf[Boolean]) {
            val enumFacing:EnumFacing = state.getValue(PropertyRotation.FOUR_WAY).asInstanceOf[EnumFacing]
            val d0: Double = pos.getX + 0.5
            val d1: Double = pos.getY + rand.nextDouble() * 6.0D / 16.0D
            val d2: Double = pos.getZ + 0.5D
            val d3: Double = 0.52D
            val d4: Double = rand.nextDouble() * 0.6D - 0.3D

            val machine = world.getTileEntity(pos)
            if (machine != null && machine.isInstanceOf[AbstractMachine]) {
                enumFacing match {
                    case EnumFacing.WEST => machine.asInstanceOf[AbstractMachine].spawnActiveParticles(d0 - d3, d1, d2 + d4)
                    case EnumFacing.EAST => machine.asInstanceOf[AbstractMachine].spawnActiveParticles(d0 + d3, d1, d2 + d4)
                    case EnumFacing.NORTH => machine.asInstanceOf[AbstractMachine].spawnActiveParticles(d0 + d4, d1, d2 - d3)
                    case EnumFacing.SOUTH => machine.asInstanceOf[AbstractMachine].spawnActiveParticles(d0 + d4, d1, d2 + d3)
                    case _ =>
                }
            }
        }
    }

    override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        world.getBlockState(new BlockPos(x, y, z)).getBlock match {
            case block: BlockManager.electricFurnace.type =>
                new ContainerElectricFurnace(player.inventory, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileElectricFurnace])
            case block: BlockManager.electricCrusher.type =>
                new ContainerElectricCrusher(player.inventory, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileElectricCrusher])
            case block: BlockManager.furnaceGenerator.type =>
                new ContainerFurnaceGenerator(player.inventory, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileFurnaceGenerator])
            case block: BlockManager.fluidGenerator.type => new ContainerGeneric
            case _ => null
        }
    }

    @SideOnly(Side.CLIENT)
    override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        world.getBlockState(new BlockPos(x, y, z)).getBlock match {
            case block: BlockManager.electricFurnace.type =>
                new GuiElectricFurnace(player, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileElectricFurnace])
            case block: BlockManager.electricCrusher.type =>
                new GuiElectricCrusher(player, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileElectricCrusher])
            case block: BlockManager.furnaceGenerator.type =>
                new GuiFurnaceGenerator(player, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileFurnaceGenerator])
            case block: BlockManager.fluidGenerator.type =>
                new GuiFluidGenerator(player, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileFluidGenerator])
            case _ => null
        }
    }
}
