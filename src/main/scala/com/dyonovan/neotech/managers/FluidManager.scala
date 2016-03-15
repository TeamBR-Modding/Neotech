package com.dyonovan.neotech.managers

import com.dyonovan.neotech.common.fluids.{FluidBlockGas, FluidGas}
import com.dyonovan.neotech.lib.Reference
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids._
import net.minecraftforge.fml.common.registry.GameRegistry

/**
  * Created by Dyonovan on 2/9/2016.
  */
object FluidManager {
    def registerModels() : Unit = {}

    var hydrogen : FluidGas = null
    var oxygen : FluidGas = null

    var blockHydrogen : FluidBlockGas = null
    var blockOxygen : FluidBlockGas = null

    def preInit() = {
        hydrogen = createFluidGas(0xFF0044BB, "hydrogen")
        blockHydrogen = registerFluidBlock(hydrogen, new FluidBlockGas(hydrogen))

        oxygen = createFluidGas(0xFFDDDDDD, "oxygen")
        blockOxygen = registerFluidBlock(oxygen, new FluidBlockGas(oxygen))
    }

    def createFluidGas(color : Int, name: String): FluidGas = {
        val still = new ResourceLocation("neotech", "blocks/metal_still")
        val flowing = new ResourceLocation("neotech", "blocks/metal_flow")

        val fluid = new FluidGas(color, name, still, flowing)
        FluidRegistry.registerFluid(fluid)
        if (!FluidRegistry.getBucketFluids.contains(fluid))
            FluidRegistry.addBucketForFluid(fluid)
        fluid
    }

    def registerFluidBlock(fluid: Fluid, block: FluidBlockGas): FluidBlockGas = {
        if (fluid != null) {
            fluid.setBlock(block)
            GameRegistry.registerBlock(block, Reference.MOD_ID + "." + fluid.getName)
        }
        block
    }
}
