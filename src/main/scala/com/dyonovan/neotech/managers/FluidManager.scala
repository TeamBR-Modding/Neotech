package com.dyonovan.neotech.managers

import net.minecraft.block.material.{MapColor, MaterialLiquid}
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.{FluidRegistry, BlockFluidClassic, Fluid}
import net.minecraftforge.fml.common.registry.GameRegistry

/**
  * Created by Dyonovan on 2/9/2016.
  */
object FluidManager {

    var fluidCopper: Fluid = _
    var blockFluidCopper: BlockFluidClassic = _

    def preInit() = {
        fluidCopper = createFluid("fluidCopper", "neotech:blocks/fluid_copper")
        blockFluidCopper = registerFluidBlock(new BlockFluidClassic(fluidCopper, new MaterialLiquid(MapColor.brownColor)))

    }

    def createFluid(name: String, texture: String): Fluid = {
        val still = new ResourceLocation(texture + "_still")
        val flowing = new ResourceLocation(texture + "_flowing")

        val fluid = new Fluid(name, still, flowing)
        FluidRegistry.registerFluid(fluid)
        fluid
    }

    def registerFluidBlock(block: BlockFluidClassic): BlockFluidClassic = {
        val fluidName = block.getFluid.getUnlocalizedName()
        block.setUnlocalizedName(fluidName)
        GameRegistry.registerBlock(block, fluidName)
        block
    }

}
