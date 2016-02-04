package com.dyonovan.neotech.common.tiles.machines.generators

import cofh.api.energy.EnergyStorage
import com.dyonovan.neotech.common.tiles.MachineGenerator
import com.teambr.bookshelf.helper.LogHelper
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

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
class TileSolarPanel extends MachineGenerator {

    var tier = 0

    def this(t: Int) {
        this()
        tier = t
    }

    energy = new EnergyStorage(BASE_ENERGY)

    /**
      * Called to tick generation. This is where you add power to the generator
      */
    override def generate(): Unit = {
        val light = worldObj.getSunBrightness(1.0F)
        energy.receiveEnergy((light * getEnergyProduced).toInt, false)
    }

    /**
      * Called per tick to manage burn time. You can do nothing here if there is nothing to generate. You should decrease burn time here
      * You should be handling checks if burnTime is 0 in this method, otherwise the tile won't know what to do
      *
      * @return True if able to continue generating
      */
    override def manageBurnTime(): Boolean = {
        if (worldObj.canSeeSky(pos) && worldObj.isDaytime) return true

        false
    }

    /**
      * This method handles how much energy to produce per tick
      *
      * @return How much energy to produce per tick
      */
    override def getEnergyProduced: Int = {
        tier match {
            case 1 => 10
            case 2 => 80
            case 3 => 640
            case _ => 0
        }
    }

    /**
      * The initial size of the inventory
      *
      * @return
      */
    override def initialSize: Int = 0

    /**
      * Used to get what slots are allowed to be output
      *
      * @return The slots to output from
      */
    override def getOutputSlots: Array[Int] = Array()

    /**
      * Used to output the redstone single from this structure
      *
      * Use a range from 0 - 16.
      *
      * 0 Usually means that there is nothing in the tile, so take that for lowest level. Like the generator has no energy while
      * 16 is usually the flip side of that. Output 16 when it is totally full and not less
      *
      * @return int range 0 - 16
      */
    override def getRedstoneOutput: Int = 0

    /**
      * Used to get what slots are allowed to be input
      *
      * @return The slots to input from
      */
    override def getInputSlots: Array[Int] = Array()

    /**
      * Used to get what particles to spawn. This will be called when the tile is active
      */
    override def spawnActiveParticles(xPos: Double, yPos: Double, zPos: Double): Unit = { }

    override def canExtractItem(index: Int, stack: ItemStack, direction: EnumFacing): Boolean = false

    override def canInsertItem(slot: Int, itemStackIn: ItemStack, direction: EnumFacing): Boolean = false
}
