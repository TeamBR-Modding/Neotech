package com.dyonovan.neotech.common.blocks.storage

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.client.gui.storage.GuiRFStorage
import com.dyonovan.neotech.common.tiles.storage.TileRFStorage
import com.dyonovan.neotech.lib.Reference
import com.teambr.bookshelf.common.container.ContainerGeneric
import com.teambr.bookshelf.common.tiles.traits.OpensGui
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockPos
import net.minecraft.world.World
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
 * @since August 15, 2015
 */
class BlockRFStorage(name: String, tier: Int) extends BlockContainer(Material.iron) with OpensGui {

    setUnlocalizedName(Reference.MOD_ID + ":" + name)
    setCreativeTab(NeoTech.tabNeoTech)
    setHardness(2.0F)

    override def onBlockHarvested(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer): Unit = {
        if (!player.capabilities.isCreativeMode) {
            world.getTileEntity(pos) match {
                case tile: TileRFStorage =>
                    val item = new ItemStack(Item.getItemFromBlock(state.getBlock), 1)
                    val tag = new NBTTagCompound
                    tile.writeToNBT(tag)
                    item.setTagCompound(tag)
                    val r = tile.getEnergyStored(null).toFloat / tile.getMaxEnergyStored(null)
                    val res = 16 - (r * 16).toInt
                    item.setItemDamage(res)
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
            world.getTileEntity(pos).asInstanceOf[TileRFStorage].energy.setEnergyStored(stack.getTagCompound
                    .getInteger("Energy"))
            world.markBlockForUpdate(pos)
        }
    }

    override def getItemDropped(state: IBlockState, rand: java.util.Random, fortune: Int): Item = {
        null
    }

    override def createNewTileEntity(world: World, meta: Int): TileEntity = {
        new TileRFStorage(tier)
    }

    override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        new ContainerGeneric
    }

    @SideOnly(Side.CLIENT)
    override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        var title = ""
        tier match {
            case 1 => title = "neotech.rfstoragebasic.title"
            case 2 => title = "neotech.rfstorageadvanced.title"
            case 3 => title = "neotech.rfstorageelite.title"
            case 4 => title = "neotech.rfstoragecreative.title"
            case _ =>
        }
        new GuiRFStorage(player, world.getTileEntity(new BlockPos(x, y, z))
                .asInstanceOf[TileRFStorage], title)
    }

    override def getRenderType: Int = 3

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
}
