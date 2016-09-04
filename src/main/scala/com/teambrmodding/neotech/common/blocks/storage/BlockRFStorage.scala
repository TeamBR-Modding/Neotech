package com.teambrmodding.neotech.common.blocks.storage

import com.teambr.bookshelf.common.blocks.traits.{DropsItems, IToolable}
import com.teambr.bookshelf.common.tiles.traits.OpensGui
import com.teambrmodding.neotech.NeoTech
import com.teambrmodding.neotech.client.gui.storage.GuiRFStorage
import com.teambrmodding.neotech.common.container.storage.ContainerRFStorage
import com.teambrmodding.neotech.common.tiles.storage.TileRFStorage
import com.teambrmodding.neotech.lib.Reference
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
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
class BlockRFStorage(name: String, tier: Int) extends BlockContainer(Material.IRON) with OpensGui with DropsItems with IToolable {

    setUnlocalizedName(Reference.MOD_ID + ":" + name)
    setCreativeTab(NeoTech.tabNeoTech)
    setHardness(2.0F)

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
