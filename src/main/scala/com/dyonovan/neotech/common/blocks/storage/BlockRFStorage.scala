package com.dyonovan.neotech.common.blocks.storage

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.client.gui.storage.GuiRFStorage
import com.dyonovan.neotech.common.tiles.storage.TileRFStorage
import com.dyonovan.neotech.lib.Reference
import com.teambr.bookshelf.common.blocks.traits.KeepInventory
import com.teambr.bookshelf.common.container.ContainerGeneric
import com.teambr.bookshelf.common.tiles.traits.OpensGui
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockPos
import net.minecraft.world.World

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
class BlockRFStorage(name: String, tier: Int) extends BlockContainer(Material.iron)
    with KeepInventory with OpensGui {

    setUnlocalizedName(Reference.MOD_ID + ":" + name)
    setCreativeTab(NeoTech.tabNeoTech)
    setHardness(2.0F)

    override def createNewTileEntity(world: World, meta: Int): TileEntity = {
        new TileRFStorage(tier)
    }

    override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        new ContainerGeneric
    }

    override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        var title = ""
        tier match {
            case 1 => title = "inventory.rfstoragebasic.title"
            case 2 => title = "inventory.rfstorageadvanced.title"
            case 3 => title = "inventory.rfstorageelite.title"
            case 4 => title = "inventory.rfstoragecreative.title"
            case _ =>
        }
        new GuiRFStorage(player, world.getTileEntity(new BlockPos(x, y, z))
                .asInstanceOf[TileRFStorage], title)
    }

    override def getRenderType: Int = 3

    def getName: String = name
    def getTier: Int = tier
}
