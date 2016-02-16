package com.dyonovan.neotech.common.blocks.fluid

import com.dyonovan.neotech.NeoTech
import net.minecraft.block.material.Material
import net.minecraftforge.fluids.{Fluid, BlockFluidClassic}

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
class BlockFluidMetal(fluidMetal: Fluid) extends BlockFluidClassic(fluidMetal, Material.lava) {
    setCreativeTab(NeoTech.tabNeoTech)
    setUnlocalizedName(fluidMetal.getName)
}
