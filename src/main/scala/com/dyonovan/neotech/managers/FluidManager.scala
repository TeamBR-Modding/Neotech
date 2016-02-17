package com.dyonovan.neotech.managers

import com.dyonovan.neotech.common.metals.fluids.FluidMetal
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids._
import net.minecraftforge.fml.common.registry.GameRegistry

/**
  * Created by Dyonovan on 2/9/2016.
  */
object FluidManager {

    def preInit() = {}

    def registerModels() : Unit = {

    }

    def createFluidMetal(color : Int, name: String, texture: String): Fluid = {
        val still = new ResourceLocation("neotech", texture + "_still")
        val flowing = new ResourceLocation("neotech", texture + "_flow")

        val fluid = new FluidMetal(color, name, still, flowing)
        if(FluidRegistry.getFluid(fluid.getName) == null) {
            FluidRegistry.registerFluid(fluid)
            FluidRegistry.addBucketForFluid(fluid)
            fluid
        } else null
    }

    def registerFluidBlock(fluid: Fluid, block: BlockFluidClassic): BlockFluidClassic = {
        if(fluid != null) {
            fluid.setBlock(block)
            val fluidName = block.getFluid.getUnlocalizedName()
            block.setUnlocalizedName(fluidName)
            GameRegistry.registerBlock(block, fluidName)
        }
        block
    }
}
