package com.teambrmodding.neotech.common.blocks.storage

import javax.annotation.Nullable

import com.teambr.bookshelf.common.blocks.traits.IToolable
import com.teambr.bookshelf.common.tiles.traits.Inventory
import com.teambrmodding.neotech.common.blocks.BaseBlock
import com.teambrmodding.neotech.common.items.ItemWrench
import com.teambrmodding.neotech.common.tiles.storage.tanks._
import com.teambr.bookshelf.loadables.ILoadActionProvider
import com.teambr.bookshelf.notification.{Notification, NotificationHelper}
import com.teambr.bookshelf.util.WorldUtils
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util._
import net.minecraft.world.{IBlockAccess, World, WorldServer}
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
class BlockTank(name: String, tier: Int) extends BaseBlock(Material.GLASS, name, classOf[TileIronTank]) with ILoadActionProvider with IToolable {

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

        if (hand == EnumHand.MAIN_HAND && world.isRemote && tank != null) {
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


    /**
      * Called when a wrench clicks on this block
      * @param stack The stack clicking on this block, an instance of IToolWrench
      * @param playerIn The player clicking
      * @param worldIn The world
      * @param pos The block pos (us)
      * @param hand The player's hand
      * @param facing Which face was clicked
      * @param hitX X hit
      * @param hitY Y hit
      * @param hitZ Z hit
      * @return The result, pass to send to next, success to end
      */
    override def onWrench(stack: ItemStack, playerIn: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand,
                 facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) : EnumActionResult = {
        worldIn.getTileEntity(pos).asInstanceOf[TileIronTank].dropItem = false
        if(playerIn.isSneaking && breakSavingNBT(worldIn, pos))
            EnumActionResult.SUCCESS
        else
            EnumActionResult.PASS
    }

    /**
      * Breaks the block and saves the NBT to the tag, calls getStackDropped to drop (just item)
      * @param world The world
      * @param pos The block pos
      * @return True if successful
      */
    override def breakSavingNBT(world : World, pos : BlockPos): Boolean = {
        if(world.isRemote) return false
        world.getTileEntity(pos) match {
            case savable =>
                val tag = savable.writeToNBT(new NBTTagCompound)
                val stack = getStackDroppedByWrench(world, pos)
                stack.setTagCompound(tag)
                WorldUtils.dropStack(world, stack, pos)
                //world.removeTileEntity(pos) // Remove to prevent drop code from triggering
                world.setBlockToAir(pos)
                return true
            case _ =>
        }
        false
    }

    override def breakBlock(world: World, pos: BlockPos, state : IBlockState): Unit = {
        world match {
            case _: WorldServer => //We are on a server
                val tile = world.getTileEntity(pos)
                if(world.getTileEntity(pos) != null && !world.getTileEntity(pos).isInvalid && world.getTileEntity(pos).asInstanceOf[TileIronTank].dropItem)
                    WorldUtils.dropStack(world, new ItemStack(state.getBlock), pos)
            case _ => //Not on the server
        }
        super.breakBlock(world, pos, state)
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
