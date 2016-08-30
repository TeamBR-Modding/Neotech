package com.teambrmodding.neotech.common.blocks

import com.teambrmodding.neotech.NeoTech
import com.teambrmodding.neotech.lib.Reference
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.{BlockPos, AxisAlignedBB}
import net.minecraft.world.{IBlockAccess, World}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since August 11, 2015
  */
class BaseBlock(material: Material, name: String, tileEntity: Class[_ <: TileEntity]) extends BlockContainer(material) {
    var BB = new AxisAlignedBB(0F, 0F, 0F, 1F, 1F, 1F)

    //Construction
    setUnlocalizedName(Reference.MOD_ID + ":" + name)
    setCreativeTab(getCreativeTab)
    setHardness(getHardness)

    /**
      * Used to change the hardness of a block, but will default to 2.0F if not overwritten
      *
      * @return The hardness value, default 2.0F
      */
    def getHardness: Float = 2.0F

    /**
      * Used to tell if this should be in a creative tab, and if so which one
      *
      * @return Null if none, defaults to the main NeoTech Tab
      */
    def getCreativeTab: CreativeTabs = NeoTech.tabNeoTech

    override def createNewTileEntity(world: World, meta: Int): TileEntity = {
        if (tileEntity != null) tileEntity.newInstance() else null
    }

    def setBlockBounds(x1 : Float, y1 : Float, z1 : Float, x2 : Float, y2 : Float, z2 : Float): AxisAlignedBB = {
        BB = new AxisAlignedBB(x1, y1, z1, x2, y2, z2)
        BB
    }

    override def getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB = {
        BB
    }
}
