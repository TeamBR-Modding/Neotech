package com.dyonovan.neotech.common.tiles.machines.generators

import java.text.NumberFormat
import java.util.Locale

import com.dyonovan.neotech.common.tiles.MachineGenerator
import com.teambr.bookshelf.api.waila.Waila
import net.minecraft.client.Minecraft
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
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
class TileSolarPanel extends MachineGenerator with Waila {

    var tier = 0

    def this(t: Int) {
        this()
        tier = t
        initEnergy(t)
    }

    def initEnergy(t: Int): Unit = {
        t match {
            case 1 =>
                setMaxEnergyStored(amountEnergy(t))
                setMaxExtract(2000)
                setMaxReceive(2000)
            case 2 =>
                setMaxEnergyStored(amountEnergy(t))
                setMaxExtract(10000)
                setMaxReceive(10000)
            case 3 =>
                setMaxEnergyStored(amountEnergy(t))
                setMaxExtract(100000)
                setMaxReceive(100000)
            case _ =>
        }
        if (worldObj != null)
            worldObj.markBlockForUpdate(pos)
    }

    def amountEnergy(t: Int): Int = {
        t match {
            case 1 => 25000
            case 2 => 1000000
            case 3 => 10000000
            case _ => 0
        }
    }

    override def getSupposedEnergy : Int = { amountEnergy(tier) }

    /**
      * Called to tick generation. This is where you add power to the generator
      */
    override def generate(): Unit = energyStorage.receiveEnergy(generating(), false)

    private def generating(): Int = {
        if (worldObj.canSeeSky(pos) && worldObj.getSunBrightnessFactor(1.0F) > 0.7F) {
            val light = worldObj.getSunBrightnessFactor(1.0F)
            (light * getEnergyProduced).toInt
        } else 0
    }

    /**
      * Called per tick to manage burn time. You can do nothing here if there is nothing to generate. You should decrease burn time here
      * You should be handling checks if burnTime is 0 in this method, otherwise the tile won't know what to do
      *
      * @return True if able to continue generating
      */
    override def manageBurnTime(): Boolean = {
        if (worldObj.canSeeSky(pos) && worldObj.getSunBrightnessFactor(1.0F) > 0.7F) return true

        false
    }

    /**
      * This method handles how much energy to produce per tick
      *
      * @return How much energy to produce per tick
      */
    override def getEnergyProduced: Int = {
        tier match {
            case 1 => 8
            case 2 => 32
            case 3 => 128
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

    override def shouldHandleIO = false

    override def canConnectEnergy(from: EnumFacing): Boolean = from == EnumFacing.DOWN

    /**
      * Write the tag
      */
    override def writeToNBT(tag: NBTTagCompound): Unit = {
        super.writeToNBT(tag)
        tag.setInteger("Tier", tier)
    }

    /**
      * Read the tag
      */
    override def readFromNBT(tag: NBTTagCompound): Unit = {
        super.readFromNBT(tag)
        if (tag.hasKey("Tier")) {
            if (tier == 0) initEnergy(tag.getInteger("Tier"))
            tier = tag.getInteger("Tier")
        }
        tier = tag.getInteger("Tier")
    }

    /**
      * Waila
      */
    override def returnWailaBody(tipList: java.util.List[String]) : java.util.List[String] = {
        tipList.add("Generating: " + generating() + " (" + (if (generating() == 0) 0 else (worldObj.getSunBrightnessFactor(1.0F) * 100).toInt) + "%)")
        tipList.add("Max: " + getEnergyProduced)
        val stored = NumberFormat.getNumberInstance(Locale.forLanguageTag(Minecraft.getMinecraft.gameSettings.language))
          .format(energyStorage.getEnergyStored)
        val max = NumberFormat.getNumberInstance(Locale.forLanguageTag(Minecraft.getMinecraft.gameSettings.language))
          .format(energyStorage.getMaxEnergyStored)
        tipList.add(stored + "/" + max)
        tipList
    }
}
