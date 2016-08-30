package com.teambrmodding.neotech.common.fluids

import com.teambrmodding.neotech.lib.Reference
import com.teambrmodding.neotech.utils.ClientUtils
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.{FluidStack, Fluid}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis "pauljoda"
  * @since 3/14/2016
  */
class FluidGas(color : Int, name : String, still : ResourceLocation, flow : ResourceLocation) extends Fluid(name, still, flow) {
    setUnlocalizedName(Reference.MOD_ID + "." + name)
    isGaseous = true

    override def getColor : Int = color

    override def getLocalizedName(stack : FluidStack) : String = {
        ClientUtils.translate("fluid." + getName + ".name")
    }
}