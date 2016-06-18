package com.dyonovan.neotech.common.fluids

import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.utils.ClientUtils
import net.minecraft.block.material.Material
import net.minecraftforge.fluids.{BlockFluidClassic, Fluid}

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
class FluidBlockGas(fluidGas: Fluid) extends BlockFluidClassic(fluidGas, Material.LAVA) {
    setUnlocalizedName(Reference.MOD_ID + "." + fluidGas.getName)
    def getBlockColor : Int = fluidGas.getColor

    override def getLocalizedName : String = {
        ClientUtils.translate("fluid." + fluidGas.getName + ".name")
    }
}