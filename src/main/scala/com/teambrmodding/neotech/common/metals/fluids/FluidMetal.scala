package com.teambrmodding.neotech.common.metals.fluids

import com.teambrmodding.neotech.lib.Reference
import com.teambrmodding.neotech.utils.ClientUtils
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.{Fluid, FluidStack}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/16/2016
  */
class FluidMetal(color : Int, name : String, still : ResourceLocation, flow : ResourceLocation) extends Fluid(name, still, flow) {
    setUnlocalizedName(Reference.MOD_ID + "." + name)
    this.setLuminosity(10)
    this.setDensity(3000)
    this.setViscosity(6000)
    this.setTemperature(600)

    override def getColor : Int = color

    override def getLocalizedName(stack : FluidStack) : String = {
        ClientUtils.translate("fluid." + getName + ".name")
    }
}
