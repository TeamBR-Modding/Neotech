package com.dyonovan.neotech.common.blocks.storage

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.collections.DummyState
import com.dyonovan.neotech.common.tiles.storage.TileTank
import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.managers.BlockManager
import com.teambr.bookshelf.notification.{Notification, NotificationHelper}
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{StatCollector, BlockPos, EnumFacing, EnumWorldBlockLayer}
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.fluids.FluidContainerRegistry
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
 * @since August 16, 2015
 */
class BlockTank(name: String, tier: Int) extends BlockContainer(Material.glass) {

    setUnlocalizedName(Reference.MOD_ID + ":" + name)
    setCreativeTab(NeoTech.tabNeoTech)
    setHardness(3.0F)
    setBlockBounds(1F / 16F, 0F, 1F / 16F, 15F / 16F, 1F,  15F/ 16F)

    override def onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, side: EnumFacing,
                                  hitX: Float, hitY: Float, hitZ: Float): Boolean = {
        val heldItem = player.getHeldItem
        val tank = world.getTileEntity(pos).asInstanceOf[TileTank]
        if (heldItem != null && FluidContainerRegistry.isContainer(heldItem)) {
            val fluid = FluidContainerRegistry.getFluidForFilledItem(heldItem)
            if (fluid != null) {
                val amount = tank.fill(EnumFacing.UP, fluid, doFill = false)
                if (amount == fluid.amount) {
                    if (getTier == 4)
                        fluid.amount = tank.getTierInfo(getTier)._2
                    tank.fill(EnumFacing.UP, fluid, doFill = true)
                    if (!player.capabilities.isCreativeMode)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, consumeItem(heldItem))
                    world.markBlockForUpdate(pos)
                    return true
                }
            } else if (FluidContainerRegistry.isEmptyContainer(heldItem)) {
                val fillStack = FluidContainerRegistry.fillFluidContainer(tank.tank.getFluid, heldItem)
                if (fillStack != null) {
                    val actual = tank.drain(EnumFacing.DOWN, FluidContainerRegistry.getFluidForFilledItem(fillStack).amount, doDrain = false)
                    if (actual.amount == FluidContainerRegistry.getFluidForFilledItem(fillStack).amount) {
                        if (tank.getTier != 4)
                            tank.drain(EnumFacing.DOWN, FluidContainerRegistry.getFluidForFilledItem(fillStack).amount, doDrain = true)
                        if (!player.capabilities.isCreativeMode) {
                            if (heldItem.stackSize == 1) {
                                player.inventory.setInventorySlotContents(player.inventory.currentItem, fillStack)
                            } else {
                                player.inventory.setInventorySlotContents(player.inventory.currentItem, consumeItem(heldItem))
                                if (!player.inventory.addItemStackToInventory(fillStack)) {
                                    player.dropPlayerItemWithRandomChoice(fillStack, false)
                                }
                            }
                        }
                        return true
                    }
                }
            }
        } else if (world.isRemote && tank != null) {
            var fluidName: String = ""
            var fluidAmount: String = ""
            if (tank.getCurrentFluid != null) {
                fluidName = StatCollector.translateToLocal(tank.getCurrentFluid.getUnlocalizedName)
                fluidAmount = tank.tank.getFluid.amount.toString + " / " + tank.tank.getCapacity + " mb"
            } else {
                fluidName = "Empty"
                fluidAmount = "0 / " + tank.tank.getCapacity + " mb"
            }

            val notify = new Notification(new ItemStack(Item.getItemFromBlock(BlockManager.ironTank)), fluidName, fluidAmount)
            NotificationHelper.addNotification(notify)
        }
        super.onBlockActivated(world, pos, state, player, side, hitX, hitY, hitZ)
        true
    }

    def consumeItem(stack: ItemStack): ItemStack = {
        if (stack.stackSize == 1) {
            if (FluidContainerRegistry.isContainer(stack))
                stack.getItem.getContainerItem(stack)
            else null
        } else {
            stack.splitStack(1)
            stack
        }
    }

    override def onBlockHarvested(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer): Unit = {
        if (!player.capabilities.isCreativeMode) {
            world.getTileEntity(pos) match {
                case tile: TileTank =>
                    val item = new ItemStack(Item.getItemFromBlock(state.getBlock), 1)
                    val tag = new NBTTagCompound
                    tile.writeToNBT(tag)
                    item.setTagCompound(tag)
                    if (tile.tank.getFluid != null) {
                        val r = tile.tank.getFluid.amount.toFloat / tile.tank.getCapacity
                        val res = 16 - (r * 16).toInt
                        item.setItemDamage(res)
                    } else
                        item.setItemDamage(16)
                    dropItem(world, item, pos) //Drop it
                case _ =>
            }
        } else {
            super.breakBlock(world, pos, state)
        }
    }

    override def onBlockPlacedBy(world: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack:
    ItemStack): Unit = {
        if(stack.hasTagCompound && !world.isRemote) { //If there is a tag and is on the server
            world.getTileEntity(pos).readFromNBT(stack.getTagCompound) //Set the tag
            world.getTileEntity(pos).setPos(pos) //Set the saved tag to here
            world.markBlockForUpdate(pos) //Mark for update to client
        }
    }

    override def getItemDropped(state: IBlockState, rand: java.util.Random, fortune: Int): Item = {
        null
    }

    override def createNewTileEntity(world: World, meta: Int): TileEntity = {
        new TileTank(tier)
    }

    override def getLightValue(world: IBlockAccess, pos: BlockPos): Int = {
        world.getTileEntity(pos) match {
            case tank: TileTank => tank.getBrightness
            case _ => 0
        }
    }

    def getName: String = name

    def getTier: Int = tier

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

    override def getExtendedState(state: IBlockState,  world: IBlockAccess, pos: BlockPos ): IBlockState = {
        new DummyState(world, pos, this)
    }

    override def getRenderType: Int = 3

    override def isOpaqueCube : Boolean = false

    @SideOnly(Side.CLIENT)
    override def isTranslucent : Boolean = true

    override def isFullCube = false

    @SideOnly(Side.CLIENT)
    override def getBlockLayer : EnumWorldBlockLayer = EnumWorldBlockLayer.CUTOUT

    override def canRenderInLayer(layer: EnumWorldBlockLayer): Boolean = {
        layer == EnumWorldBlockLayer.CUTOUT || layer == EnumWorldBlockLayer.TRANSLUCENT
    }
}
