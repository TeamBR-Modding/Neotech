package com.teambrmodding.neotech.common.blocks.storage

import com.teambrmodding.neotech.common.blocks.BaseBlock
import com.teambrmodding.neotech.common.items.ItemWrench
import com.teambrmodding.neotech.common.tiles.storage.tanks._
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
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.{FluidContainerRegistry, FluidStack, FluidUtil, IFluidContainerItem}
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
class BlockTank(name: String, tier: Int) extends BaseBlock(Material.GLASS, name, classOf[TileIronTank]) with ILoadActionProvider {

    setHardness(2.0F)
    setBlockBounds(1F / 16F, 0F, 1F / 16F, 15F / 16F, 1F,  15F/ 16F)

    override def onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer,
                                  hand: EnumHand, heldItem: ItemStack, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) : Boolean = {

        val heldItem = player.getHeldItemMainhand
        val tank = world.getTileEntity(pos).asInstanceOf[TileIronTank]

        if(heldItem != null && tank.isInstanceOf[IFluidHandler] && !heldItem.getItem.isInstanceOf[ItemBlockTank]) {
            if(FluidUtil.interactWithFluidHandler(heldItem, tank, player))
                return true
        }

        //Wrench
        if (hand == EnumHand.MAIN_HAND && player.getHeldItemMainhand != null && player.getHeldItemMainhand.getItem.isInstanceOf[ItemWrench] && player.isSneaking) {
            if (breakTank(world, pos, state)) {
                world.setBlockToAir(pos)
                world.removeTileEntity(pos)
                world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 6)
                return true
            }
        } else if (hand == EnumHand.MAIN_HAND && world.isRemote && tank != null) {
            var fluidName: String = ""
            var fluidAmount: String = ""
            if (tank.tanks(tank.TANK).getFluid != null) {
                fluidName = tank.tanks(tank.TANK).getFluid.getLocalizedName
                fluidAmount = tank.tanks(tank.TANK).getFluid.amount.toString + " / " + tank.tanks(tank.TANK).getCapacity + " mb"
            } else {
                fluidName = "Empty"
                fluidAmount = "0 / " + tank.tanks(tank.TANK).getCapacity + " mb"
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
            case tile: TileIronTank =>
                val item = new ItemStack(Item.getItemFromBlock(state.getBlock), 1)
                val tag = new NBTTagCompound
                val tileTag = new NBTTagCompound
                tile.writeToNBT(tileTag)
                if (tile.tanks(tile.TANK).getFluid != null) {
                    val r = tile.tanks(tile.TANK).getFluid.amount.toFloat / tile.tanks(tile.TANK).getCapacity
                    val res = 16 - (r * 16).toInt
                    item.setItemDamage(res)
                    tileTag.setTag("Fluid", tile.tanks(tile.TANK).getFluid.writeToNBT(tag))
                } else
                    item.setItemDamage(16)
                item.setTagCompound(tileTag)
                dropItem(world, item, pos) //Drop it
                return true
            case _ =>
        }
        false
    }

    override def onBlockPlacedBy(world: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack:
    ItemStack): Unit = {
        if(stack.hasTagCompound && !world.isRemote) { //If there is a tag and is on the server
            world.getTileEntity(pos).readFromNBT(stack.getTagCompound) //Set the tag
            world.getTileEntity(pos).setPos(pos) //Set the saved tag to here
            world.setBlockState(pos, state, 3) //Mark for update to client
        }
    }

    override def getItemDropped(state: IBlockState, rand: java.util.Random, fortune: Int): Item = {
        null
    }

    override def createNewTileEntity(world: World, meta: Int): TileEntity = {
        tier match {
            case 1 => new TileIronTank
            case 2 => new TileGoldTank
            case 3 => new TileDiamondTank
            case 4 => new TileCreativeTank
            case 5 => new TileVoidTank
            case _ => new TileIronTank
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
