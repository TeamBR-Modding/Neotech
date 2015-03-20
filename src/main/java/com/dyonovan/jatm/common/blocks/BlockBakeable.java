package com.dyonovan.jatm.common.blocks;

import com.dyonovan.jatm.lib.Constants;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class BlockBakeable extends BlockContainer {
    private String name;
    protected Class<? extends TileEntity> tileClass;

    protected BlockBakeable(Material materialIn, String name, Class<? extends TileEntity> tileClass) {
        super(materialIn);

        this.name = name;
        this.tileClass = tileClass;
    }

    public String getName() {
        return name;
    }

    public ResourceLocation getFrontIcon() {
        return new ResourceLocation(Constants.MODID, "name" + "_front");
    }


    @Override
    public int getRenderType() {
        return 3;
    }

    public TileEntity createNewTileEntity(World worldIn, int meta) {
        try {
            return tileClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
