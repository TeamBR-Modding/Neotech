package com.teambrmodding.neotech.common.metals.blocks

import com.teambrmodding.neotech.NeoTech
import com.teambrmodding.neotech.lib.Reference
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.util.BlockRenderLayer

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
class BlockMetalOre(name: String, color : Int, miningLevel: Int) extends Block(Material.ROCK) {

    setUnlocalizedName(Reference.MOD_ID + ":" + name)
    setCreativeTab(NeoTech.tabMetals)
    setHardness(3.0F)
    setHarvestLevel("pickaxe", miningLevel)

    def getName: String = name

    def getBlockColor : Int = color

    override def getBlockLayer : BlockRenderLayer = BlockRenderLayer.CUTOUT
}
