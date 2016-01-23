package com.dyonovan.neotech.pipes.collections

import com.dyonovan.neotech.pipes.entities.{ItemResourceEntity, FluidResourceEntity, EnergyResourceEntity, ResourceEntity}
import com.teambr.bookshelf.common.tiles.traits.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fluids.{FluidContainerRegistry, IFluidContainerItem}
import net.minecraftforge.oredict.OreDictionary

import scala.util.control.Breaks._
/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/22/2016
  */
trait Filter {
    val filterInventory = new Inventory {
        override def hasCustomName(): Boolean = false
        override def initialSize: Int = 9
        override var inventoryName: String = "Pipe Filter"
    }

    var matchTag = false
    var matchDamage = false
    var matchOreDict = false
    var blackList = false

    def readFromNBT(tag : NBTTagCompound) = {
        filterInventory.readFromNBT(tag, "filter")
        matchTag = tag.getBoolean("matchTag")
        matchDamage = tag.getBoolean("matchDamage")
        matchOreDict = tag.getBoolean("matchOreDict")
        blackList = tag.getBoolean("blackList")
    }

    def writeToNBT(tag : NBTTagCompound) = {
        filterInventory.writeToNBT(tag, "filter")
        tag.setBoolean("matchTag", matchTag)
        tag.setBoolean("matchDamage", matchDamage)
        tag.setBoolean("matchOreDict", matchOreDict)
        tag.setBoolean("blackList", blackList)
    }

    def isResourceValidForFilter(resource : ResourceEntity[_]) : Boolean = {
        resource match {
            case energy : EnergyResourceEntity => true
            case liquid : FluidResourceEntity =>
                if(liquid.resource.getFluid == null) //Just to be safe
                    return false

                var hasItems = false
                var matched = false
                for(x  <- 0 until filterInventory.inventoryContents.size()) {
                    val item = filterInventory.getStackInSlot(x)
                    if(item != null && item.getItem != null) {
                        hasItems = true
                        if(FluidContainerRegistry.isFilledContainer(item)) {
                            if (FluidContainerRegistry.getFluidForFilledItem(item) != null &&
                                    FluidContainerRegistry.getFluidForFilledItem(item).getFluid != null &&
                                    FluidContainerRegistry.getFluidForFilledItem(item).getFluid == liquid.resource.getFluid.getFluid) {
                                matched = true
                                if(!blackList)
                                    return true
                            }
                        }
                    }
                }
                !hasItems || (if(blackList) !matched else matched)
            case item : ItemResourceEntity =>
                if(item.resource.getItem == null)
                    return false

                var matched = false
                var hasItems = false
                breakable {
                    for(x  <- 0 until filterInventory.inventoryContents.size()) {
                        val itemStack = filterInventory.getStackInSlot(x)
                        if (itemStack != null && itemStack.getItem != null) {
                            hasItems = true
                            if (itemStack.getItem == item.resource.getItem) {
                                matched = true
                                if (matchDamage && itemStack.getItemDamage != item.resource.getItemDamage)
                                    matched = false
                                if (matchTag && !ItemStack.areItemStackTagsEqual(item.resource, itemStack))
                                    matched = false
                            }
                            if (matchOreDict && OreDictionary.getOreIDs(itemStack).nonEmpty) {
                                for (oreId: Int <- OreDictionary.getOreIDs(item.resource)) {
                                    for (oreIdUs: Int <- OreDictionary.getOreIDs(itemStack)) {
                                        if (oreId == oreIdUs)
                                            matched = true
                                    }
                                }
                            }
                        }
                        if(matched)
                            break
                    }
                }
                !hasItems || (if(blackList) !matched else matched)
            case _ => false
        }
    }

    def resetFilter(): Unit = {
        matchTag = false
        matchDamage = false
        matchOreDict = false
        blackList = false
        filterInventory.clear()
    }
}
