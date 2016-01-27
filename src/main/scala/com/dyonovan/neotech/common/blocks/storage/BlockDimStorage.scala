package com.dyonovan.neotech.common.blocks.storage

import java.util

import com.dyonovan.neotech.common.blocks.BaseBlock
import com.dyonovan.neotech.common.items.ItemWrench
import com.dyonovan.neotech.common.tiles.storage.TileDimStorage
import com.dyonovan.neotech.managers.BlockManager
import com.teambr.bookshelf.common.blocks.properties.PropertyRotation
import com.teambr.bookshelf.util.WorldUtils
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.{BlockState, IBlockState}
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.{BlockPos, EnumFacing, MathHelper}
import net.minecraft.world.{World, WorldServer}
import net.minecraftforge.common.property.{ExtendedBlockState, IUnlistedProperty}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import scala.util.Random
import scala.util.control.Breaks._

/**
  * Created by Dyonovan on 1/23/2016.
  */
class BlockDimStorage extends BaseBlock(Material.iron, "dimStorage", classOf[TileDimStorage]) {

    override def onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, facing: EnumFacing, f1: Float, f2: Float, f3: Float): Boolean = {
        val tile = world.getTileEntity(pos).asInstanceOf[TileDimStorage]
        if (!world.isRemote) {
            if (player.getHeldItem.getItem.isInstanceOf[ItemWrench] && player.isSneaking) {
                val item = new ItemStack(Item.getItemFromBlock(state.getBlock), 1)
                val tag = new NBTTagCompound
                tile.writeToNBT(tag)
                if (tile.getQty > 0)
                    item.setTagCompound(tag)
                dropItem(world, item, pos)
                world.setBlockToAir(pos)
            } else if (player.getHeldItem != null) {
                if (tile.getStackInSlot(0) == null) {
                    tile.setInventorySlotContents(0, player.getHeldItem)
                    player.getHeldItem.stackSize = 0
                    world.markBlockForUpdate(pos)
                    world.playSoundEffect(pos.getX + 0.5, pos.getY + 0.5D, pos.getZ + 0.5, "random.pop", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F)
                    return true
                }

                var actual = 0
                val amount = player.getHeldItem.stackSize
                if (tile.getQty + amount <= tile.maxStacks * tile.getStackInSlot(0).getMaxStackSize) {
                    tile.addQty(amount)
                    actual = amount
                } else {
                    val leftover = (tile.getQty + amount) - (tile.maxStacks * tile.getStackInSlot(0).getMaxStackSize)
                    tile.addQty(amount - leftover)
                    actual = amount - leftover
                }
                if (actual > 0) {
                    player.getHeldItem.stackSize -= actual
                    world.playSoundEffect(pos.getX + 0.5, pos.getY + 0.5D, pos.getZ + 0.5, "random.pop", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F)
                }
            }
            world.markBlockForUpdate(pos)
        }
        true
    }

    override def onBlockPlacedBy(world: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack:
    ItemStack): Unit = {
        if(stack.hasTagCompound && !world.isRemote) { //If there is a tag and is on the server
            world.getTileEntity(pos).readFromNBT(stack.getTagCompound) //Set the tag
            world.getTileEntity(pos).setPos(pos) //Set the saved tag to here
            world.markBlockForUpdate(pos) //Mark for update to client
        }
    }

    override def onBlockClicked(world: World, pos: BlockPos, player: EntityPlayer): Unit = {
        val tile = world.getTileEntity(pos).asInstanceOf[TileDimStorage]

        if (tile.getStackInSlot(0) != null) {
            var actual = 0
            val outStack = tile.getStackInSlot(0).copy
            if (!player.isSneaking) {
                tile.addQty(-1)
                actual = 1
            } else {
                actual = Math.min(tile.getQty, tile.getStackInSlot(0).getMaxStackSize)
                tile.addQty(-actual)
            }

            if (actual > 0) {
                outStack.stackSize = actual
                player.inventory.addItemStackToInventory(outStack)
                world.playSoundEffect(pos.getX + 0.5, pos.getY + 0.5D, pos.getZ + 0.5, "random.pop", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F)
            }

        }
    }

    override def onBlockHarvested(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer): Unit = {
        world match {
            case _: WorldServer =>
                val tile = world.getTileEntity(pos).asInstanceOf[TileDimStorage]
                if (tile.getQty > 0) {
                    val stacks = new util.ArrayList[ItemStack]()
                    breakable {
                        while (true) {
                            if (tile.getQty == 0) break
                            else if (tile.getQty > tile.getStackInSlot(0).getMaxStackSize) {
                                tile.addQty(tile.getStackInSlot(0).getMaxStackSize)
                                stacks.add(new ItemStack(tile.getStackInSlot(0).getItem, tile.getStackInSlot(0).getMaxStackSize, tile.getStackInSlot(0).getItemDamage))
                            } else {
                                stacks.add(new ItemStack(tile.getStackInSlot(0).getItem, tile.getQty, tile.getStackInSlot(0).getItemDamage))
                                tile.addQty(-tile.getQty)
                            }
                        }
                    }
                    for (stack <- stacks.toArray()) {
                        val itemStack = stack.asInstanceOf[ItemStack]
                        dropItem(world, itemStack, pos)
                    }
                }
            case _ =>
        }
        //super.breakBlock(world, pos, state)
        dropItem(world, new ItemStack(BlockManager.dimStorage), pos)
        world.removeTileEntity(pos)
        world.markBlockForUpdate(pos)
    }

    override def getItemDropped(state: IBlockState, rand: java.util.Random, fortune: Int): Item = {
        null
    }

    override def getRenderType : Int = 3

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

    override def onBlockPlaced(world : World, blockPos : BlockPos, facing : EnumFacing, hitX : Float, hitY : Float, hitZ : Float, meta : Int, placer : EntityLivingBase) : IBlockState = {
        val playerFacingDirection = if (placer == null) 0 else MathHelper.floor_double((placer.rotationYaw / 90.0F) + 0.5D) & 3
        val enumFacing = EnumFacing.getHorizontal(playerFacingDirection).getOpposite
        this.getDefaultState.withProperty(PropertyRotation.FOUR_WAY, enumFacing)
    }

    /**
      * Used to say what our block state is
      */
    override def createBlockState() : BlockState = {
        val listed = new Array[IProperty[_]](1)
        listed(0) = PropertyRotation.FOUR_WAY
        val unlisted = new Array[IUnlistedProperty[_]](0)
        new ExtendedBlockState(this, listed, unlisted)
    }

    /**
      * Used to convert the meta to state
      *
      * @param meta The meta
      * @return
      */
    override def getStateFromMeta(meta : Int) : IBlockState = getDefaultState.withProperty(PropertyRotation.FOUR_WAY, EnumFacing.getFront(meta))

    /**
      * Called to convert state from meta
      *
      * @param state The state
      * @return
      */
    override def getMetaFromState(state : IBlockState) = state.getValue(PropertyRotation.FOUR_WAY).getIndex

    override def isOpaqueCube : Boolean = false

    @SideOnly(Side.CLIENT)
    override def isTranslucent : Boolean = true

    override def isFullCube = false

    private def dropItem(world: World, stack: ItemStack, pos: BlockPos): Unit = {
        val random = new Random
        if (stack != null && stack.stackSize > 0) {
            val rx = random.nextFloat * 0.8F + 0.1F
            val ry = random.nextFloat * 0.8F + 0.1F
            val rz = random.nextFloat * 0.8F + 0.1F

            val itemEntity = new EntityItem(world,
                pos.getX + rx, pos.getY + ry, pos.getZ + rz,
                new ItemStack(stack.getItem, stack.stackSize, stack.getItemDamage))

            if (stack.hasTagCompound)
                itemEntity.getEntityItem.setTagCompound(stack.getTagCompound)

            val factor = 0.05F

            itemEntity.motionX = random.nextGaussian * factor
            itemEntity.motionY = random.nextGaussian * factor + 0.2F
            itemEntity.motionZ = random.nextGaussian * factor
            world.spawnEntityInWorld(itemEntity)

            stack.stackSize = 0
        }
    }
}
