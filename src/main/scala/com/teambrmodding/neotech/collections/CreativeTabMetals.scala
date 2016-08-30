package com.teambrmodding.neotech.collections

import com.teambrmodding.neotech.managers.MetalManager
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.{Item, ItemStack}
import net.minecraftforge.common.ForgeModContainer
import net.minecraftforge.fluids.{FluidRegistry, FluidStack, UniversalBucket}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/17/2016
  */
class CreativeTabMetals extends CreativeTabs("tabNeoTechMetals") {

    override def getTabIconItem: Item = {
        ForgeModContainer.getInstance().universalBucket
    }

    override def getIconItemStack : ItemStack = {
        val universalBucket = ForgeModContainer.getInstance().universalBucket
        val stack = new ItemStack(universalBucket)
        if (FluidRegistry.isFluidRegistered("copper"))
            universalBucket.asInstanceOf[UniversalBucket].fill(stack, new FluidStack(FluidRegistry.getFluid("copper"), 1000), true)
        stack
    }

    @SideOnly(Side.CLIENT)
    override def displayAllRelevantItems(list : java.util.List[ItemStack]) : Unit = {
        super.displayAllRelevantItems(list)

        val universalBucket = ForgeModContainer.getInstance().universalBucket
        if(universalBucket != null) {
            val iterator = MetalManager.metalRegistry.keySet().iterator()
            while(iterator.hasNext) {
                val metal = MetalManager.metalRegistry.get(iterator.next())
                if(metal.fluid.isDefined) {
                    val stack = new ItemStack(universalBucket)
                    universalBucket.asInstanceOf[UniversalBucket].fill(stack, new FluidStack(metal.fluid.get, 1000), true)
                    list.add(stack)
                }
            }
        }
    }
}
