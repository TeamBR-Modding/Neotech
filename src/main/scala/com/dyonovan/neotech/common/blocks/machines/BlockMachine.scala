package com.dyonovan.neotech.common.blocks.machines

import com.dyonovan.neotech.client.gui.machines._
import com.dyonovan.neotech.common.blocks.BaseBlock
import com.dyonovan.neotech.common.blocks.traits.{CoreStates, Upgradeable}
import com.dyonovan.neotech.common.container.machines._
import com.dyonovan.neotech.common.tiles.AbstractMachine
import com.dyonovan.neotech.common.tiles.machines._
import com.dyonovan.neotech.managers.{BlockManager, ItemManager}
import com.teambr.bookshelf.common.blocks.properties.PropertyRotation
import com.teambr.bookshelf.common.container.ContainerGeneric
import com.teambr.bookshelf.common.tiles.traits.OpensGui
import com.teambr.bookshelf.util.WorldUtils
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraft.world.{World, WorldServer}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import scala.util.Random

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
with OpensGui with CoreStates {

    @SideOnly(Side.CLIENT)
    override def randomDisplayTick(world: World, pos: BlockPos, state: IBlockState, rand: java.util.Random): Unit = {
        if (getActualState(state, world, pos).getValue(this.PROPERTY_ACTIVE).asInstanceOf[Boolean]) {
            val enumFacing:EnumFacing = state.getValue(PropertyRotation.FOUR_WAY)
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

    override def rotateBlock(world : World, pos : BlockPos, side : EnumFacing) : Boolean = {
        val tag = new NBTTagCompound
        world.getTileEntity(pos).writeToNBT(tag)
        if(side != EnumFacing.UP && side != EnumFacing.DOWN)
            world.setBlockState(pos, world.getBlockState(pos).withProperty(PropertyRotation.FOUR_WAY, side))
        else
            world.setBlockState(pos, world.getBlockState(pos).withProperty(PropertyRotation.FOUR_WAY, WorldUtils.rotateRight(world.getBlockState(pos).getValue(PropertyRotation.FOUR_WAY))))
        if(tag != null) {
            world.getTileEntity(pos).readFromNBT(tag)
        }
        true
    }

    override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        if(player.isSneaking && player.inventory.getCurrentItem != null && player.inventory.getCurrentItem.getItem == ItemManager.wrench) {
            new ContainerMachineUpgrade(player.inventory, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[Upgradeable])
        }
        else if(player.inventory.getCurrentItem == null || (player.inventory.getCurrentItem != null && player.inventory.getCurrentItem.getItem != ItemManager.wrench)) {
            world.getBlockState(new BlockPos(x, y, z)).getBlock match {
                case block: BlockManager.electricFurnace.type =>
                    new ContainerElectricFurnace(player.inventory, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileElectricFurnace])
                case block: BlockManager.electricCrusher.type =>
                    new ContainerElectricCrusher(player.inventory, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileElectricCrusher])
                case block: BlockManager.furnaceGenerator.type =>
                    new ContainerFurnaceGenerator(player.inventory, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileFurnaceGenerator])
                case block: BlockManager.fluidGenerator.type => new ContainerGeneric
                case block: BlockManager.thermalBinder.type =>
                    new ContainerThermalBinder(player.inventory, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileThermalBinder])
                case _ => null
            }
        }
        else
            null
    }

    @SideOnly(Side.CLIENT)
    override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        if(player.isSneaking && player.inventory.getCurrentItem != null && player.inventory.getCurrentItem.getItem == ItemManager.wrench) {
            new GuiMachineUpgrade(player, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[Upgradeable])
        }
        else if(player.inventory.getCurrentItem == null || (player.inventory.getCurrentItem != null && player.inventory.getCurrentItem.getItem != ItemManager.wrench)) {
            world.getBlockState(new BlockPos(x, y, z)).getBlock match {
                case block: BlockManager.electricFurnace.type =>
                    new GuiElectricFurnace(player, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileElectricFurnace])
                case block: BlockManager.electricCrusher.type =>
                    new GuiElectricCrusher(player, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileElectricCrusher])
                case block: BlockManager.furnaceGenerator.type =>
                    new GuiFurnaceGenerator(player, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileFurnaceGenerator])
                case block: BlockManager.fluidGenerator.type =>
                    new GuiFluidGenerator(player, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileFluidGenerator])
                case block: BlockManager.thermalBinder.type =>
                    new GuiThermalBinder(player, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileThermalBinder])
                case _ => null
            }
        }
        else if(Minecraft.getMinecraft.currentScreen != null) //Sometimes called twice, no idea why so work around
            new GuiMachineUpgrade(player, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[Upgradeable])
        else
            null
    }

    /***
      * Overwritten as we want to also drop the mother board
      */
    override def breakBlock(world: World, pos: BlockPos, state : IBlockState): Unit = {
        world match {
            case _: WorldServer => //We are on a server
                world.getTileEntity(pos) match {
                    case tile: IInventory => //This is an inventory
                        val random = new Random
                        val items = new java.util.ArrayList[ItemStack]()

                        //Add all from inventory
                        for (i <- 0 until tile.getSizeInventory) {
                            if(tile.getStackInSlot(i) != null)
                                items.add(tile.getStackInSlot(i))
                        }

                        tile match {
                            case upgradeable: Upgradeable =>
                                if (upgradeable.upgradeInventory.getStackInSlot(0) != null)
                                    items.add(upgradeable.upgradeInventory.getStackInSlot(0))
                            case _ =>
                        }

                        for (i <- 0 until items.size()) {
                            val stack = items.get(i)

                            if(stack != null && stack.stackSize > 0) {
                                val rx = random.nextFloat * 0.8F + 0.1F
                                val ry = random.nextFloat * 0.8F + 0.1F
                                val rz = random.nextFloat * 0.8F + 0.1F

                                val itemEntity = new EntityItem(world,
                                    pos.getX + rx, pos.getY + ry, pos.getZ + rz,
                                    new ItemStack(stack.getItem, stack.stackSize, stack.getItemDamage))

                                if(stack.hasTagCompound)
                                    itemEntity.getEntityItem.setTagCompound(stack.getTagCompound)

                                val factor = 0.05F

                                itemEntity.motionX = random.nextGaussian * factor
                                itemEntity.motionY = random.nextGaussian * factor + 0.2F
                                itemEntity.motionZ = random.nextGaussian * factor
                                world.spawnEntityInWorld(itemEntity)

                                stack.stackSize = 0
                            }
                        }
                    case _ => //Not an inventory
                }
            case _ => //Not on the server
        }
        super.breakBlock(world, pos, state)
    }
}
