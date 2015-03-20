package com.dyonovan.jatm.common.blocks;

import com.dyonovan.jatm.lib.Constants;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
        return new ResourceLocation(Constants.MODID, "blocks/" + name + "_front");
    }

    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getSide() {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(Constants.MODID + ":block/" + "machine_side");
    }

    public ModelResourceLocation getNormal() {
        return new ModelResourceLocation(Constants.MODID + ":" + name, "normal");
    }

    public ModelResourceLocation getInventory() {
        return new ModelResourceLocation(name, "inventory");
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
