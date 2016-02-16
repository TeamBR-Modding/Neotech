package com.dyonovan.neotech.managers

import com.dyonovan.neotech.common.blocks.fluid.BlockFluidMetal
import com.dyonovan.neotech.common.fluids.FluidMetal
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids._
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.{SideOnly, Side}

/**
  * Created by Dyonovan on 2/9/2016.
  */
object FluidManager {

    var fluidCopper: Fluid = _
    var blockFluidCopper: BlockFluidClassic = _

    def preInit() = {
        fluidCopper = createFluidMetal("fluidCopper", "blocks/fluidCopper")
        blockFluidCopper = registerFluidBlock(fluidCopper, new BlockFluidMetal(fluidCopper))
    }

    @SideOnly(Side.CLIENT)
    def registerItemRendering() : Unit = {
        //Doesnt work for some reason. Oh well
        registerItem(Item.getItemFromBlock(blockFluidCopper), fluidCopper)
    }

    @SideOnly(Side.CLIENT)
    def registerItem(item: Item, fluid: Fluid): Unit = {
        Minecraft.getMinecraft.getRenderItem.getItemModelMesher.register(item, 0,
            new ModelResourceLocation(item.getUnlocalizedName.substring(5), fluid.getName))
    }

    def createFluidMetal(name: String, texture: String): Fluid = {
        val still = new ResourceLocation("neotech", texture + "_still")
        val flowing = new ResourceLocation("neotech", texture + "_flow")

        val fluid = new FluidMetal(name, still, flowing)
        FluidRegistry.registerFluid(fluid)
        FluidRegistry.addBucketForFluid(fluid)
        fluid
    }

    def registerFluidBlock(fluid: Fluid, block: BlockFluidClassic): BlockFluidClassic = {
        fluid.setBlock(block)
        val fluidName = block.getFluid.getUnlocalizedName()
        block.setUnlocalizedName(fluidName)
        GameRegistry.registerBlock(block, fluidName)
        block
    }
}
