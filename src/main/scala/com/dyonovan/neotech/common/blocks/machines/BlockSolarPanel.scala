package com.dyonovan.neotech.common.blocks.machines

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.common.blocks.traits.CoreStates
import com.dyonovan.neotech.common.tiles.machines.generators.TileSolarPanel
import com.dyonovan.neotech.lib.Reference
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 2/3/2016
  */
class BlockSolarPanel(name: String, tier: Int) extends BlockContainer(Material.iron) with CoreStates {

    setUnlocalizedName(Reference.MOD_ID + ":" + name)
    setCreativeTab(NeoTech.tabNeoTech)
    setHardness(2.0F)

    override def createNewTileEntity(worldIn: World, meta: Int): TileEntity = new TileSolarPanel(tier)

    override def getRenderType: Int = 3

    def getTier: Int = tier
}
