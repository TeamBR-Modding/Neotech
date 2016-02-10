package com.dyonovan.neotech.managers

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.event.FMLInterModComms

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 2/9/2016
  */
object TinkersIntegration {

    def postInit(): Unit = {

        //Copper Ore
        val tag = new NBTTagCompound
        val item = new NBTTagCompound
        val stack = new ItemStack(BlockManager.oreCopper, 1)
        stack.writeToNBT(item)
        tag.setTag("Item", item)
        tag.setTag("Block", item)
        val fluid = new FluidStack(FluidManager.fluidCopper, 288)
        fluid.writeToNBT(tag)
        tag.setInteger("Temperature", 600)
        FMLInterModComms.sendMessage("tconstruct", "addSmelteryMelting", tag)
    }

}
