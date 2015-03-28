package com.dyonovan.neotech.common.blocks.pipe.item;

import com.dyonovan.neotech.common.blocks.pipe.BlockPipe;
import com.dyonovan.neotech.common.pipe.item.PipeBasicItem;
import com.dyonovan.neotech.lib.Constants;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.vecmath.Vector3f;
import java.util.List;

public class BlockPipeBasicItem extends BlockPipe {
    public BlockPipeBasicItem(Material materialIn, String name, Class<? extends TileEntity> tileClass) {
        super(materialIn, name, tileClass);
        setBlockBounds(0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);
        setHardness(1.0F);
    }

    public void drawExtras(List<BakedQuad> list, FaceBakery faceBakery, IBlockAccess world, BlockPos pos) {
        if(world != null) {
            PipeBasicItem pipe = (PipeBasicItem) world.getTileEntity(pos);

            int j = 0;

            if (pipe.isExtracting(EnumFacing.DOWN)) {
                this.drawExtractPoint(ModelRotation.X270_Y0, faceBakery, list);
                j++;
            }

            if (pipe.isExtracting(EnumFacing.UP)) {
                this.drawExtractPoint(ModelRotation.X90_Y0, faceBakery, list);
                j++;
            }

            if (pipe.isExtracting(EnumFacing.NORTH)) {
                this.drawExtractPoint(ModelRotation.X180_Y0, faceBakery, list);
                j++;
            }

            if (pipe.isExtracting(EnumFacing.SOUTH)) {
                this.drawExtractPoint(ModelRotation.X0_Y0, faceBakery, list);
                j++;
            }

            if (pipe.isExtracting(EnumFacing.WEST)) {
                this.drawExtractPoint(ModelRotation.X0_Y90, faceBakery, list);
                j++;
            }

            if (pipe.isExtracting(EnumFacing.EAST)) {
                this.drawExtractPoint(ModelRotation.X0_Y270, faceBakery, list);
                j++;
            }

            for (EnumFacing face : EnumFacing.values()) {
                if (isCableConnected(world, pos.offset(face), face))
                    j++;
            }

            if (j > 2) {
                float min = (8 - getWidth());
                float max = (8 + getWidth());
                ModelRotation modelRot = ModelRotation.X0_Y0;
                BlockPartFace face = new BlockPartFace(null, 0, "", new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0));
                boolean scale = true;
                TextureAtlasSprite foreGround = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(getJunction());
                list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, max), face, foreGround, EnumFacing.DOWN, modelRot, null, scale, true));
                list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, max), face, foreGround, EnumFacing.UP, modelRot, null, scale, true));
                list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, max), face, foreGround, EnumFacing.NORTH, modelRot, null, scale, true));
                list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, max), face, foreGround, EnumFacing.SOUTH, modelRot, null, scale, true));
                list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, max), face, foreGround, EnumFacing.EAST, modelRot, null, scale, true));
                list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, max), face, foreGround, EnumFacing.WEST, modelRot, null, scale, true));
            }
        } else {
            float min = (8 - getWidth());
            float max = (8 + getWidth());
            ModelRotation modelRot = ModelRotation.X0_Y0;
            BlockPartFace face = new BlockPartFace(null, 0, "", new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0));
            boolean scale = true;
            TextureAtlasSprite foreGround = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(getJunction());
            list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, max), face, foreGround, EnumFacing.DOWN, modelRot, null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, max), face, foreGround, EnumFacing.UP, modelRot, null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, max), face, foreGround, EnumFacing.NORTH, modelRot, null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, max), face, foreGround, EnumFacing.SOUTH, modelRot, null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, max), face, foreGround, EnumFacing.EAST, modelRot, null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, max), face, foreGround, EnumFacing.WEST, modelRot, null, scale, true));

        }
    }

    private void drawExtractPoint(ModelRotation modelRot, FaceBakery faceBakery, List<BakedQuad> list) {
        BlockPartFace face = new BlockPartFace(null, 0, "", new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0));
        boolean scale = true;
        float min = (8 - getWidth()) - 0.1F;
        float max = (8 + getWidth()) + 0.1F;
        TextureAtlasSprite backGround = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(Constants.MODID + ":blocks/pipeExtract");
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, max, max), new Vector3f(max, max, 16.0F), face, backGround, EnumFacing.UP, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, max), new Vector3f(max, min, 16.0F), face, backGround, EnumFacing.DOWN, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, max), new Vector3f(min, max, 16.0F), face, backGround, EnumFacing.WEST, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(max, min, max), new Vector3f(max, max, 16.0F), face, backGround, EnumFacing.EAST, modelRot, null, scale, true));
    }

    public String getJunction() {
        return Constants.MODID + ":blocks/" + name + "Junction";
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        super.onBlockActivated(world, pos, state, player, side, hitX, hitY, hitZ);

        PipeBasicItem pipe = (PipeBasicItem) world.getTileEntity(pos);
        pipe.toggleExtractMode();
        world.markBlockForUpdate(pos);

        return true;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
        float x1 = 0.15F;
        float x2 = 1.0F - x1;
        float y1 = 0.15F;
        float y2 = 1.0F - y1;
        float z1 = 0.15F;
        float z2 = 1.0F - z1;
        if(isCableConnected(worldIn, pos.west(), EnumFacing.WEST)) {
            x1 = 0.0F;
        }

        if(isCableConnected(worldIn, pos.east(), EnumFacing.EAST)) {
            x2 = 1.0F;
        }

        if(isCableConnected(worldIn, pos.north(), EnumFacing.NORTH)) {
            z1 = 0.0F;
        }

        if(isCableConnected(worldIn, pos.south(), EnumFacing.SOUTH)) {
            z2 = 1.0F;
        }

        if(isCableConnected(worldIn, pos.down(), EnumFacing.DOWN)) {
            y1 = 0.0F;
        }

        if(isCableConnected(worldIn, pos.up(), EnumFacing.UP)) {
            y2 = 1.0F;
        }

        this.setBlockBounds(x1, y1, z1, x2, y2, z2);
    }

    @Override
    public boolean isCableConnected(IBlockAccess blockaccess, BlockPos pos, EnumFacing face) {
        return blockaccess.getTileEntity(pos) instanceof IInventory || blockaccess.getTileEntity(pos) instanceof PipeBasicItem;
    }

    @Override
    public float getWidth() {
        return 5.0F;
    }

    @Override
    public String getBackgroundTexture() {
        return "minecraft:blocks/wool_colored_gray";
    }
}
