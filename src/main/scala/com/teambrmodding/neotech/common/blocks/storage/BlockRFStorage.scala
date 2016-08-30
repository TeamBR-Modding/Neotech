package com.teambrmodding.neotech.common.blocks.storage

import com.teambrmodding.neotech.NeoTech
import com.teambrmodding.neotech.client.gui.storage.GuiRFStorage
import com.teambrmodding.neotech.common.container.storage.ContainerRFStorage
import com.teambrmodding.neotech.common.tiles.AbstractMachine
import com.teambrmodding.neotech.common.tiles.storage.TileRFStorage
import com.teambrmodding.neotech.lib.Reference
import com.teambr.bookshelf.common.blocks.traits.DropsItems
import com.teambr.bookshelf.common.tiles.traits.OpensGui
import com.teambr.bookshelf.util.WorldUtils
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumBlockRenderType, EnumFacing}
import net.minecraft.world.{IBlockAccess, World, WorldServer}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

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
class BlockRFStorage(name: String, tier: Int) extends BlockContainer(Material.IRON) with OpensGui with DropsItems {

    setUnlocalizedName(Reference.MOD_ID + ":" + name)
    setCreativeTab(NeoTech.tabNeoTech)
    setHardness(2.0F)

    override def breakBlock(world: World, pos: BlockPos, state : IBlockState): Unit = {
        world match {
            case _: WorldServer => //We are on a server
                world.getTileEntity(pos) match {
                    case tile: TileRFStorage =>
                        val item = new ItemStack(Item.getItemFromBlock(state.getBlock), 1)
                        val tag = new NBTTagCompound
                        tile.writeToNBT(tag)
                        tag.setInteger("Energy", tile.getEnergyStored(EnumFacing.UP))
                        item.setTagCompound(tag)
                        val r = tile.getEnergyStored(null).toFloat / tile.getMaxEnergyStored(null)
                        val res = 16 - (r * 16).toInt
                        item.setItemDamage(res)
                        WorldUtils.dropStack(world, item, pos)
                    case _ =>
                }
            case _ =>
        }
        super.breakBlock(world, pos, state)
    }

    override def onBlockPlacedBy(world: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack:
    ItemStack): Unit = {
        if(stack.hasTagCompound && !world.isRemote) { //If there is a tag and is on the server
        val tile = world.getTileEntity(pos).asInstanceOf[TileRFStorage]
            tile.writeToNBT(stack.getTagCompound)
            if (stack.getTagCompound.hasKey("Energy"))
                tile.energyStorage.setEnergyStored(stack.getTagCompound.getInteger("Energy"))
            if (tile.tier == 4)
                tile.energyStorage.setEnergyStored(tile.amountEnergy(tile.tier))
            world.setBlockState(pos, state, 3)
        }
    }

    override def getItemDropped(state: IBlockState, rand: java.util.Random, fortune: Int): Item = {
        null
    }

    override def createNewTileEntity(world: World, meta: Int): TileEntity = {
        new TileRFStorage(tier)
    }

    override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        new ContainerRFStorage(player.inventory, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[TileRFStorage])
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

    override def getRenderType(state : IBlockState) : EnumBlockRenderType = EnumBlockRenderType.MODEL

    def getName: String = name

    def getTier: Int = tier

    override def hasComparatorInputOverride(state: IBlockState): Boolean =
        true

    override def getComparatorInputOverride(state: IBlockState, worldIn: World, pos: BlockPos): Int =
        worldIn.getTileEntity(pos).asInstanceOf[TileRFStorage].getRedstoneOutput
}
