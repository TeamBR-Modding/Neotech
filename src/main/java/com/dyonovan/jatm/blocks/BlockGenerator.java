package com.dyonovan.jatm.blocks;

import com.dyonovan.jatm.JATM;
import com.dyonovan.jatm.lib.Constants;
import com.dyonovan.jatm.tileentity.TileGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockGenerator extends BlockContainer {

    private final String name = "blockGenerator";

    public BlockGenerator() {
        super(Material.iron);
        GameRegistry.registerBlock(this, name);

        this.setUnlocalizedName(Constants.MODID + ":" + name);
        this.setCreativeTab(JATM.tabJATM);
        this.setHardness(1.5F);
    }

    public String getName() {
        return name;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileGenerator();
    }
}
