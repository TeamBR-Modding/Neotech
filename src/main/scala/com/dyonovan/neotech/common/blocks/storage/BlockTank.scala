package com.dyonovan.neotech.common.blocks.storage

import com.dyonovan.neotech.common.blocks.BaseBlock
import com.dyonovan.neotech.common.items.ItemWrench
import com.dyonovan.neotech.common.tiles.storage.TileTank
import com.teambr.bookshelf.loadables.ILoadActionProvider
import com.teambr.bookshelf.notification.{Notification, NotificationHelper}
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{BlockRenderLayer, EnumBlockRenderType, EnumFacing, EnumHand}
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.fluids.{FluidContainerRegistry, FluidUtil, IFluidContainerItem, IFluidHandler}
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
class BlockTank(name: String, tier: Int) extends BaseBlock(Material.GLASS, name, classOf[TileTank]) with ILoadActionProvider {

    setHardness(2.0F)
    setBlockBounds(1F / 16F, 0F, 1F / 16F, 15F / 16F, 1F,  15F/ 16F)

    override def onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer,
                                  hand: EnumHand, heldItem: ItemStack, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) : Boolean = {

        val heldItem = player.getHeldItemMainhand
        val tank = world.getTileEntity(pos).asInstanceOf[TileTank]

        if(heldItem != null && tank.isInstanceOf[IFluidHandler]) {
            if(FluidUtil.interactWithTank(heldItem, player, tank, side.getOpposite))
                return true
        }

        //Wrench
        if (player.getHeldItemMainhand != null && player.getHeldItemMainhand.getItem.isInstanceOf[ItemWrench] && player.isSneaking) {
            if (breakTank(world, pos, state)) {
                world.setBlockToAir(pos)
                world.removeTileEntity(pos)
                world.setBlockState(pos, state, 3)
            }
        } else if (world.isRemote && tank != null) {
            var fluidName: String = ""
            var fluidAmount: String = ""
            if (tank.getCurrentFluid != null) {
                fluidName = tank.tank.getFluid.getLocalizedName
                fluidAmount = tank.tank.getFluid.amount.toString + " / " + tank.tank.getCapacity + " mb"
            } else {
                fluidName = "Empty"
                fluidAmount = "0 / " + tank.tank.getCapacity + " mb"
            }

            val item = new ItemStack(Item.getItemFromBlock(state.getBlock), 1)
            val tag = new NBTTagCompound
            tank.writeToNBT(tag)
            item.setTagCompound(tag)
            val notify = new Notification(item, fluidName, fluidAmount)
            NotificationHelper.addNotification(notify)
        }
        if(heldItem == null)
            return false
        FluidContainerRegistry.isFilledContainer(heldItem) || heldItem.getItem.isInstanceOf[IFluidContainerItem]
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

    override def canHarvestBlock(world: IBlockAccess, pos: BlockPos, player: EntityPlayer): Boolean = {
        if (!player.capabilities.isCreativeMode) {
            breakTank(player.getEntityWorld, pos, world.getBlockState(pos))
        } else {
            super.breakBlock(player.getEntityWorld, pos, world.getBlockState(pos))
        }
        false
    }

    private def breakTank(world: World, pos: BlockPos, state: IBlockState): Boolean = {
        if (world.isRemote) return false
        world.getTileEntity(pos) match {
            case tile: TileTank =>
                val item = new ItemStack(Item.getItemFromBlock(state.getBlock), 1)
                val tag = new NBTTagCompound
                val tileTag = new NBTTagCompound
                tile.writeToNBT(tileTag)
                tag.setTag("Fluid", tileTag)
                item.setTagCompound(tag)
                if (tile.tank.getFluid != null) {
                    val r = tile.tank.getFluid.amount.toFloat / tile.tank.getCapacity
                    val res = 16 - (r * 16).toInt
                    item.setItemDamage(res)
                } else
                    item.setItemDamage(16)
                dropItem(world, item, pos) //Drop it
                return true
            case _ =>
        }
        false
    }

    override def onBlockPlacedBy(world: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack:
    ItemStack): Unit = {
        if(stack.hasTagCompound && !world.isRemote) { //If there is a tag and is on the server
            world.getTileEntity(pos).readFromNBT(stack.getTagCompound.getCompoundTag("Fluid")) //Set the tag
            world.getTileEntity(pos).setPos(pos) //Set the saved tag to here
            world.setBlockState(pos, state, 3) //Mark for update to client
        }
    }

    override def getItemDropped(state: IBlockState, rand: java.util.Random, fortune: Int): Item = {
        null
    }

    override def createNewTileEntity(world: World, meta: Int): TileEntity = {
        new TileTank(tier)
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

    override def getRenderType(state : IBlockState) : EnumBlockRenderType = EnumBlockRenderType.MODEL

    override def isOpaqueCube(state : IBlockState) : Boolean = false

    @SideOnly(Side.CLIENT)
    override def isTranslucent(state : IBlockState) : Boolean = true

    override def isFullCube(state : IBlockState) = false

    @SideOnly(Side.CLIENT)
    override def getBlockLayer : BlockRenderLayer = BlockRenderLayer.CUTOUT

    override def canRenderInLayer(layer: BlockRenderLayer): Boolean = {
        layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.TRANSLUCENT
    }

    override def performLoadAction(event: AnyRef, isClient: Boolean): Unit = {
        event match {
            case modelBake : ModelBakeEvent =>
             case _ =>
        }
    }
}
