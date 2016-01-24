package com.dyonovan.neotech.common.tiles.misc

import com.teambr.bookshelf.common.tiles.traits.UpdatingTile
import net.minecraft.item.EnumDyeColor
import net.minecraft.nbt.{NBTTagCompound, NBTTagByte}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/23/2016
  */
class TileStar extends UpdatingTile {
    var color = EnumDyeColor.ORANGE.getMetadata
    override def writeToNBT(tag : NBTTagCompound) = {
        super.writeToNBT(tag)
        tag.setInteger("color", color)
    }
    override def readFromNBT(tag : NBTTagCompound) = {
        super.readFromNBT(tag)
        color = tag.getInteger("color")
        if(worldObj != null)
            worldObj.markBlockRangeForRenderUpdate(pos, pos)
    }
}
