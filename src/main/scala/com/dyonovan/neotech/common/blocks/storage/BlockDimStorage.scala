package com.dyonovan.neotech.common.blocks.storage

import com.dyonovan.neotech.common.blocks.BaseBlock
import com.dyonovan.neotech.common.items.ItemWrench
import com.dyonovan.neotech.common.tiles.storage.TileDimStorage
import com.dyonovan.neotech.managers.{BlockManager, ItemManager}
import com.teambr.bookshelf.common.blocks.properties.PropertyRotation
import com.teambr.bookshelf.util.WorldUtils
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.{BlockState, IBlockState}
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.{BlockPos, EnumFacing, MathHelper}
import net.minecraft.world.{WorldServer, IBlockAccess, World}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
  * Created by Dyonovan on 1/23/2016.
  */
class BlockDimStorage(name: String) extends BaseBlock(Material.iron, name, classOf[TileDimStorage]) {

    lazy val LOCKED = PropertyBool.create("locked")
    var time: Long = 0

    override def onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, facing: EnumFacing, f1: Float, f2: Float, f3: Float): Boolean = {
        val tile = world.getTileEntity(pos).asInstanceOf[TileDimStorage]
        if (!world.isRemote) {
            if (player.getHeldItem != null && player.getHeldItem.getItem.isInstanceOf[ItemWrench] && player.isSneaking) {
                val item = new ItemStack(Item.getItemFromBlock(state.getBlock), 1)
                val tag = new NBTTagCompound
                tile.writeToNBT(tag)
                if (tag.getInteger("Qty") > 0 || tile.getUpgradeBoard != null)
                    item.setTagCompound(tag)
                tile.dropItem(world, item, pos)
                world.setBlockToAir(pos)
                world.removeTileEntity(pos)
            } else if (player.getHeldItem != null && player.getHeldItem.isItemEqual(new ItemStack(ItemManager.upgradeMBFull))) {
                if (tile.upgradeInventory.getStackInSlot(0) == null) {
                    tile.upgradeInventory.setStackInSlot(0, player.getHeldItem.copy())
                    player.getHeldItem.stackSize = 0
                }
            } else if (player.getHeldItem != null) {
                var actual = tile.insertItem(0, player.getHeldItem, simulate = true)
                if (actual != null && actual.stackSize == player.getHeldItem.stackSize) return true
                actual = tile.insertItem(0, player.getHeldItem, simulate = false)
                if (actual == null) player.getHeldItem.stackSize = 0 else player.getHeldItem.stackSize = actual.stackSize
                world.playSoundEffect(pos.getX + 0.5, pos.getY + 0.5D, pos.getZ + 0.5, "random.pop", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F)
            } else if (player.getHeldItem == null && player.isSneaking)
                tile.setLock(!tile.isLocked)
            else if (world.getTotalWorldTime - time <= 10 && tile.getStackInSlot(0) != null) {
                val item = tile.getStackInSlot(0)
                val allowed = (tile.getStackInSlot(0).getMaxStackSize * tile.maxStacks) - tile.getQty

                if (allowed > 0) {
                    val actual = player.inventory.clearMatchingItems(item.getItem, item.getItemDamage, allowed, item.getTagCompound)
                    if (actual > 0) {
                        tile.addQty(actual)
                        world.updateEntity(player)
                    }
                }

            }
            world.markBlockForUpdate(pos)
            time = world.getTotalWorldTime
        }
        true
    }

    override def onBlockPlacedBy(world: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack:
    ItemStack): Unit = {
        if (stack.hasTagCompound && !world.isRemote) {
            //If there is a tag and is on the server
            val tile = world.getTileEntity(pos).asInstanceOf[TileDimStorage]
            tile.readFromNBT(stack.getTagCompound) //Set the tag
            tile.setPos(pos) //Set the saved tag to here
            world.markBlockForUpdate(pos) //Mark for update to client
            tile.upgradeInventoryChanged(0)
        }
    }

    override def onBlockClicked(world: World, pos: BlockPos, player: EntityPlayer): Unit = {
        if (!world.isRemote) {
            val tile = world.getTileEntity(pos).asInstanceOf[TileDimStorage]
            if (tile.getUpgradeBoard != null && player.getHeldItem != null && player.getHeldItem.getItem.isInstanceOf[ItemWrench]) {
                val status = player.inventory.addItemStackToInventory(tile.upgradeInventory.getStackInSlot(0).copy())
                if (!status) tile.dropItem(world, tile.upgradeInventory.getStackInSlot(0).copy(), pos)
                world.playSoundEffect(pos.getX + 0.5, pos.getY + 0.5D, pos.getZ + 0.5, "random.pop", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F)
                tile.upgradeInventory.setStackInSlot(0, null)
            } else if (tile.getStackInSlot(0) != null) {
                val amt = if (!player.isSneaking) 1 else tile.getStackInSlot(0).getMaxStackSize
                var actual = tile.extractItem(0, amt, simulate = true)
                if (actual != null) {
                    actual = tile.extractItem(0, amt, simulate = false)
                    val status = player.inventory.addItemStackToInventory(actual)
                    if (!status) tile.dropItem(world, actual, pos)
                    world.playSoundEffect(pos.getX + 0.5, pos.getY + 0.5D, pos.getZ + 0.5, "random.pop", 0.5F, world.rand.nextFloat() * 0.1F + 0.9F)
                }
            }
        }
    }

    override def onBlockHarvested(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer): Unit = {
        world match {
            case _: WorldServer =>
                val tile = world.getTileEntity(pos).asInstanceOf[TileDimStorage]
                if (tile.getQty > 0 && tile.getStackInSlot(0) != null)
                    tile.dropStacks(tile.getQty, tile.getStackInSlot(0))
                if (tile.getUpgradeBoard != null)
                    tile.dropItem(world, tile.upgradeInventory.getStackInSlot(0).copy(), pos)
                tile.dropItem(world, new ItemStack(BlockManager.dimStorage), pos)
                world.removeTileEntity(pos)
                world.markBlockForUpdate(pos)
        }
    }

    override def getItemDropped(state: IBlockState, rand: java.util.Random, fortune: Int): Item = {
        null
    }

    override def getRenderType: Int = 3

    override def rotateBlock(world: World, pos: BlockPos, side: EnumFacing): Boolean = {
        val tag = new NBTTagCompound
        world.getTileEntity(pos).writeToNBT(tag)
        if (side != EnumFacing.UP && side != EnumFacing.DOWN)
            world.setBlockState(pos, world.getBlockState(pos).withProperty(PropertyRotation.FOUR_WAY, side))
        else
            world.setBlockState(pos, world.getBlockState(pos).withProperty(PropertyRotation.FOUR_WAY, WorldUtils.rotateRight(world.getBlockState(pos).getValue(PropertyRotation.FOUR_WAY))))
        if (tag != null) {
            world.getTileEntity(pos).readFromNBT(tag)
        }
        true
    }

    override def onBlockPlaced(world: World, blockPos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase): IBlockState = {
        val playerFacingDirection = if (placer == null) 0 else MathHelper.floor_double((placer.rotationYaw / 90.0F) + 0.5D) & 3
        val enumFacing = EnumFacing.getHorizontal(playerFacingDirection).getOpposite
        this.getDefaultState.withProperty(PropertyRotation.FOUR_WAY, enumFacing)
    }

    /**
      * Used to say what our block state is
      */
    override def createBlockState(): BlockState = {
        new BlockState(this, PropertyRotation.FOUR_WAY, LOCKED)
    }

    override def getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState = {
        state.withProperty(LOCKED, (worldIn.getTileEntity(pos) == null || worldIn.getTileEntity(pos).asInstanceOf[TileDimStorage].isLocked).asInstanceOf[java.lang.Boolean])
    }

    /**
      * Used to convert the meta to state
      *
      * @param meta The meta
      * @return
      */
    override def getStateFromMeta(meta: Int): IBlockState = getDefaultState.withProperty(PropertyRotation.FOUR_WAY, EnumFacing.getFront(meta))

    /**
      * Called to convert state from meta
      *
      * @param state The state
      * @return
      */
    override def getMetaFromState(state: IBlockState) = state.getValue(PropertyRotation.FOUR_WAY).getIndex

    override def isOpaqueCube: Boolean = false

    @SideOnly(Side.CLIENT)
    override def isTranslucent: Boolean = true

    override def isFullCube = false


}
