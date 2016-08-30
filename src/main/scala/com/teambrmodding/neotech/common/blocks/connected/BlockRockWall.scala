package com.teambrmodding.neotech.common.blocks.connected

import com.teambrmodding.neotech.NeoTech
import com.teambrmodding.neotech.common.tiles.misc.TileRockWall
import com.teambrmodding.neotech.lib.Reference
import com.teambrmodding.neotech.utils.ClientUtils
import com.teambr.bookshelf.client.gui.GuiTextFormat
import com.teambr.bookshelf.common.blocks.BlockConnectedTextures
import com.teambr.bookshelf.traits.HasToolTip
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{EnumFacing, EnumHand}
import net.minecraft.util.EnumFacing.Axis
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis "pauljoda"
  * @since 2/28/2016
  */
class BlockRockWall extends BlockContainer(Material.ROCK) with BlockConnectedTextures with HasToolTip {

    def rockWall = "rockWall"

    override def onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer,
                                  hand:EnumHand, heldItem: ItemStack, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) : Boolean = {

        if(side == EnumFacing.UP) {
            playerIn.setPosition(pos.getX + hitX, pos.getY + 1, pos.getZ + hitZ)
            return true
        } else if(side != EnumFacing.DOWN) {
            if(side.getAxis == Axis.X)
                playerIn.setPosition(playerIn.posX, pos.getY + hitY, pos.getZ + hitZ)
            else
                playerIn.setPosition(pos.getX + hitX, pos.getY + hitY, playerIn.posZ)
            return true
        }

        false
    }

    setCreativeTab(NeoTech.tabDecorations)
    setUnlocalizedName(Reference.MOD_ID + ":" + rockWall)
    setHardness(2.0F)

    override def isClear: Boolean = false
    // Methods to move textures to lower class, handle others here
    override def NoCornersTextureLocation: String = "neotech:blocks/connected/" + rockWall + "/" + rockWall
    override def CornersTextureLocation: String = "neotech:blocks/connected/" + rockWall + "/" + rockWall + "_corners"
    override def VerticalTextureLocation: String = "neotech:blocks/connected/" + rockWall + "/" + rockWall + "_vertical"
    override def AntiCornersTextureLocation: String = "neotech:blocks/connected/" + rockWall + "/" + rockWall + "_anti_corners"
    override def HorizontalTextureLocation: String = "neotech:blocks/connected/" + rockWall + "/" + rockWall + "_horizontal"

    override def createNewTileEntity(worldIn: World, meta: Int): TileEntity = new TileRockWall

    override def getToolTip(): List[String] = List(GuiTextFormat.ITALICS + ClientUtils.translate("neotech.rockWall.tip"))
}
