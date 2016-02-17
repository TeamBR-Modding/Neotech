package com.dyonovan.neotech.common.fluids

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.Fluid

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
    this.setLuminosity(10)
    this.setDensity(3000)
    this.setViscosity(6000)
    this.setTemperature(600)

    override def getColor : Int = color
}
