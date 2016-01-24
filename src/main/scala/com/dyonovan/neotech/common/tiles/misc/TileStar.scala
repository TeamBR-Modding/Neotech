package com.dyonovan.neotech.common.tiles.misc

import com.teambr.bookshelf.common.tiles.traits.UpdatingTile
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
    var side = 6
    override def writeToNBT(tag : NBTTagCompound) = {
        super.writeToNBT(tag)
        tag.setInteger("side", side)
    }
    override def readFromNBT(tag : NBTTagCompound) = {
        super.readFromNBT(tag)
        side = tag.getInteger("side")
        if(worldObj != null)
            worldObj.markBlockRangeForRenderUpdate(pos, pos)
    }
}
