package com.dyonovan.neotech.managers

import com.dyonovan.neotech.common.blocks.fluid.BlockFluidMetal
import com.dyonovan.neotech.common.fluids.FluidMetal
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids._
import net.minecraftforge.fml.common.registry.GameRegistry

/**
  * Created by Dyonovan on 2/9/2016.
  */
object FluidManager {

    var fluidCopper: Fluid = _
    var blockFluidCopper: BlockFluidClassic = _

    var fluidTin : Fluid = _
    var blockFluidTin : BlockFluidClassic = _

    var fluidBronze : Fluid = _
    var blockFluidBronze : BlockFluidClassic = _

    def preInit() = {
        // Copper
        fluidCopper = createFluidMetal(0xFFc27646, "copper", "blocks/metal")
        blockFluidCopper = registerFluidBlock(fluidCopper, new BlockFluidMetal(fluidCopper))

        // Tin
        fluidTin = createFluidMetal(0xFFebeced, "tin", "blocks/metal")
        blockFluidTin = registerFluidBlock(fluidTin, new BlockFluidMetal(fluidTin))

        // Bronze
        fluidBronze = createFluidMetal(0xFFdfa62b, "bronze", "blocks/metal")
        blockFluidBronze = registerFluidBlock(fluidBronze, new BlockFluidMetal(fluidBronze))
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
