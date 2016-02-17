package com.dyonovan.neotech.common.metals.blocks

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.lib.Reference
import net.minecraft.block.Block
import net.minecraft.block.material.Material

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 13, 2015
 */
class BlockMetalOre(name: String, color : Int, miningLevel: Int) extends Block(Material.rock) {

    setUnlocalizedName(Reference.MOD_ID + ":" + name)
    setCreativeTab(NeoTech.tabNeoTech)
    setHardness(3.0F)
    setHarvestLevel("pickaxe", miningLevel)

    def getName: String = name

    override def getBlockColor : Int = color
}
