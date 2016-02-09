package com.dyonovan.neotech.common.blocks.machines

import com.dyonovan.neotech.client.gui.machines.generators.{GuiFluidGenerator, GuiFurnaceGenerator}
import com.dyonovan.neotech.client.gui.machines.processors.{GuiElectricCrusher, GuiElectricFurnace, GuiThermalBinder}
import com.dyonovan.neotech.common.blocks.BaseBlock
import com.dyonovan.neotech.common.blocks.traits.Upgradeable
import com.dyonovan.neotech.common.container.machines.generators.{ContainerFluidGenerator, ContainerFurnaceGenerator}
import com.dyonovan.neotech.common.container.machines.processors.{ContainerElectricCrusher, ContainerElectricFurnace, ContainerThermalBinder}
import com.dyonovan.neotech.common.tiles.AbstractMachine
import com.dyonovan.neotech.common.tiles.machines.generators.{TileFluidGenerator, TileFurnaceGenerator}
import com.dyonovan.neotech.common.tiles.machines.processors.{TileElectricCrusher, TileElectricFurnace, TileThermalBinder}
import com.dyonovan.neotech.managers.{ItemManager, BlockManager}
import com.teambr.bookshelf.common.blocks.properties.PropertyRotation
import com.teambr.bookshelf.common.tiles.traits.{Inventory, OpensGui}
import com.teambr.bookshelf.util.WorldUtils
import net.minecraft.block.BlockPistonBase
import net.minecraft.block.material.Material
import net.minecraft.block.properties.{IProperty, PropertyBool}
import net.minecraft.block.state.{BlockState, IBlockState}
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{EnumWorldBlockLayer, MathHelper, BlockPos, EnumFacing}
import net.minecraft.world.{IBlockAccess, World, WorldServer}
import net.minecraftforge.common.property.{ExtendedBlockState, IUnlistedProperty}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import scala.collection.mutable.ArrayBuffer
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
class BlockMachine(name: String, tileEntity: Class[_ <: TileEntity], activeState : Boolean = true, fourWayRotation : Boolean = true, sixWayRotation : Boolean = false)
        extends BaseBlock(Material.iron, name, tileEntity) with OpensGui {

    @SideOnly(Side.CLIENT)
    override def randomDisplayTick(world: World, pos: BlockPos, state: IBlockState, rand: java.util.Random): Unit = {
        if (activeState && getActualState(state, world, pos).getValue(this.PROPERTY_ACTIVE).asInstanceOf[Boolean]) {
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
        if(fourWayRotation) {
            val tag = new NBTTagCompound
            world.getTileEntity(pos).writeToNBT(tag)
            if (side != EnumFacing.UP && side != EnumFacing.DOWN)
                world.setBlockState(pos, world.getBlockState(pos).withProperty(PropertyRotation.FOUR_WAY, side))
            else
                world.setBlockState(pos, world.getBlockState(pos).withProperty(PropertyRotation.FOUR_WAY, WorldUtils.rotateRight(world.getBlockState(pos).getValue(PropertyRotation.FOUR_WAY))))
            if (tag != null) {
                world.getTileEntity(pos).readFromNBT(tag)
            }
            return true
        }
        false
    }

    override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        world.getTileEntity(new BlockPos(x, y, z)) match {
            case abstractMachine : AbstractMachine if player.getHeldItem != null && player.getHeldItem.getItem != ItemManager.wrench =>
                abstractMachine.getServerGuiElement(ID, player, world, x, y, z)
            case _ => null
        }
    }

    @SideOnly(Side.CLIENT)
    override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        world.getTileEntity(new BlockPos(x, y, z)) match {
            case abstractMachine : AbstractMachine if player.getHeldItem != null && player.getHeldItem.getItem != ItemManager.wrench =>
                abstractMachine.getClientGuiElement(ID, player, world, x, y, z)
            case _ => null
        }
    }

    lazy val PROPERTY_ACTIVE = PropertyBool.create("isactive")

    /**
      * Called when the block is placed, we check which way the player is facing and put our value as the opposite of that
      */
    override def onBlockPlaced(world : World, blockPos : BlockPos, facing : EnumFacing, hitX : Float, hitY : Float, hitZ : Float, meta : Int, placer : EntityLivingBase) : IBlockState = {
        if (fourWayRotation) {
            val playerFacingDirection = if (placer == null) 0 else MathHelper.floor_double((placer.rotationYaw / 90.0F) + 0.5D) & 3
            val enumFacing = EnumFacing.getHorizontal(playerFacingDirection).getOpposite
            this.getDefaultState.withProperty(PropertyRotation.FOUR_WAY, enumFacing).withProperty(PROPERTY_ACTIVE, false.asInstanceOf[java.lang.Boolean])
        } else if(sixWayRotation)
            this.getDefaultState.withProperty(PropertyRotation.SIX_WAY, BlockPistonBase.getFacingFromEntity(world, blockPos, placer))
        else
            getDefaultState
    }

    /**
      * Used to say what our block state is
      */
    override def createBlockState() : BlockState = {
        val properties = new ArrayBuffer[IProperty[_]]()
        if(activeState) properties += PROPERTY_ACTIVE
        if(fourWayRotation) properties += PropertyRotation.FOUR_WAY
        if(sixWayRotation) properties += PropertyRotation.SIX_WAY
        val unlisted = new Array[IUnlistedProperty[_]](0)
        new ExtendedBlockState(this, properties.toArray, unlisted)
    }

    /**
      * Used to tell the actual state in world
      *
      * @param state The state that is currently in
      * @param worldIn The world
      * @param pos The position
      * @return The new state, with relevant info added
      */
    override def getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos) : IBlockState = {
        worldIn.getTileEntity(pos) match {
            case tile : AbstractMachine =>
                var newState = state
                if(activeState)
                    newState = newState.withProperty(PROPERTY_ACTIVE, tile.isActive.asInstanceOf[java.lang.Boolean])
                newState
            case _ => state
        }
    }

    /**
      * Used to convert the meta to state
      *
      * @param meta The meta
      * @return
      */
    override def getStateFromMeta(meta : Int) : IBlockState = {
        var facing = EnumFacing.getFront(meta)
        if(fourWayRotation) {
            if(facing.getAxis == EnumFacing.Axis.Y)
                facing = EnumFacing.NORTH
            getDefaultState.withProperty(PropertyRotation.FOUR_WAY, facing)
        }
        else if(sixWayRotation)
            getDefaultState.withProperty(PropertyRotation.FOUR_WAY, facing)
        else
            getDefaultState
    }

    /**
      * Called to convert state from meta
      *
      * @param state The state
      * @return
      */
    override def getMetaFromState(state : IBlockState) = {
        if(fourWayRotation)
            state.getValue(PropertyRotation.FOUR_WAY).ordinal()
        else if(sixWayRotation)
            state.getValue(PropertyRotation.SIX_WAY).ordinal()
        else
            0
    }

    override def getRenderType : Int = 3

    override def isOpaqueCube : Boolean = false

    @SideOnly(Side.CLIENT)
    override def isTranslucent : Boolean = true

    @SideOnly(Side.CLIENT)
    override def getBlockLayer : EnumWorldBlockLayer = EnumWorldBlockLayer.CUTOUT

    /***
      * Overwritten as we want to also drop the mother board
      */
    override def breakBlock(world: World, pos: BlockPos, state : IBlockState): Unit = {
        world match {
            case _: WorldServer => //We are on a server
                world.getTileEntity(pos) match {
                    case tile: Inventory => //This is an inventory
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
