package com.dyonovan.neotech.common.metals.blocks

import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.utils.ClientUtils
import net.minecraft.block.material.Material
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraft.world.IBlockAccess
import net.minecraftforge.fluids.{BlockFluidClassic, Fluid}

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
    setUnlocalizedName(Reference.MOD_ID + "." + fluidMetal.getName)

    override def getBlockColor : Int = fluidMetal.getColor

    //For setting fire and not burning ourselves
    override def getFireSpreadSpeed(world : IBlockAccess, pos : BlockPos, facing : EnumFacing) : Int = 300
    override def getFlammability(world : IBlockAccess, pos: BlockPos, facing: EnumFacing) : Int = 0

    override def getLocalizedName : String = {
        ClientUtils.translate("fluid." + fluidMetal.getName + ".name")
    }
}
