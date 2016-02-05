package com.dyonovan.neotech.common.tiles.machines.operators

import com.teambr.bookshelf.common.tiles.traits.{FluidHandler, UpdatingTile}
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fluids.FluidTank

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/4/2016
  */
class TilePump extends UpdatingTile with FluidHandler {

    val RANGE = 50

    override def setupTanks(): Unit = {
        tanks += new FluidTank(bucketsToMB(10))
    }

    override def onTankChanged(tank: FluidTank): Unit = worldObj.markBlockForUpdate(pos)

    override def writeToNBT(tag : NBTTagCompound) : Unit = {
        super[TileEntity].writeToNBT(tag)
        super[FluidHandler].writeToNBT(tag)
    }

    override def readFromNBT(tag : NBTTagCompound) : Unit = {
        super[TileEntity].readFromNBT(tag)
        super[FluidHandler].readFromNBT(tag)
    }
}
