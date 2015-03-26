package com.dyonovan.neotech.common.blocks.fluid;

import com.dyonovan.neotech.handlers.BlockHandler;
import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class BlockMoltenTin extends BlockFluidClassic {

    public BlockMoltenTin() {
        super(BlockHandler.moltenTin, Material.water);

    }
}
