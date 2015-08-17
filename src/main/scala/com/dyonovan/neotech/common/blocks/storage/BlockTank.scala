package com.dyonovan.neotech.common.blocks.storage

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.collections.DummyState
import com.dyonovan.neotech.common.tiles.storage.TileTank
import com.dyonovan.neotech.lib.Reference
import com.teambr.bookshelf.common.blocks.traits.BlockBakeable
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
import net.minecraft.util.{EnumWorldBlockLayer, BlockPos}
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.fml.relauncher.{SideOnly, Side}

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
class BlockTank(name: String, tier: Int) extends BlockContainer(Material.glass) with OpensGui with BlockBakeable {

    setUnlocalizedName(Reference.MOD_ID + ":" + name)
    setCreativeTab(NeoTech.tabNeoTech)
    setHardness(3.0F)

    override def onBlockHarvested(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer): Unit = {
        if (!player.capabilities.isCreativeMode) {
            world.getTileEntity(pos) match {
                case tile: TileTank =>
                    val item = new ItemStack(Item.getItemFromBlock(state.getBlock), 1)
                    val tag = new NBTTagCompound
                    tile.writeToNBT(tag)
                    item.setTagCompound(tag)
                    item.asInstanceOf[ItemBlockTank].updateDamage(item)
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
            case tank: TileTank => 0//tank.getBrightness
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

    override def isBlockNormalCube: Boolean = {
        false
    }

    override def getRenderType(): Int = 3

    override def isOpaqueCube : Boolean = false

    @SideOnly(Side.CLIENT)
    override def isTranslucent : Boolean = true

    @SideOnly(Side.CLIENT)
    override def getBlockLayer : EnumWorldBlockLayer = EnumWorldBlockLayer.CUTOUT

    override def canRenderInLayer(layer: EnumWorldBlockLayer): Boolean = {
        layer == EnumWorldBlockLayer.CUTOUT || layer == EnumWorldBlockLayer.TRANSLUCENT
    }

    override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = ???

    override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = ???

    override def MODID: String = Reference.MOD_ID

    override def blockName: String = name
}
