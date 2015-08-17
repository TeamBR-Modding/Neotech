package com.dyonovan.neotech.collections

import java.util

import com.google.common.collect.ImmutableMap
import com.teambr.bookshelf.common.blocks.traits.BlockBakeable
import net.minecraft.block.Block
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.IBlockState
import net.minecraft.util.BlockPos
import net.minecraft.world.IBlockAccess

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 17, 2015
 */
class DummyState(w: IBlockAccess, p: BlockPos, blockBakeable: BlockBakeable) extends IBlockState {

    val blockAccess = w
    val pos = p
    val block = blockBakeable

    override def withProperty(property: IProperty, value: Comparable[_]): IBlockState = null

    override def getValue(property: IProperty): Comparable[_] = null

    override def cycleProperty(property: IProperty): IBlockState = null

    override def getPropertyNames: util.Collection[_] = null

    override def getBlock: Block = block

    override def getProperties: ImmutableMap[_, _] = null
}
