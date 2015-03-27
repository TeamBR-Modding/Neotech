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
        PipeBasicItem pipe = (PipeBasicItem) world.getTileEntity(pos);
        if(pipe.isExtracting(EnumFacing.DOWN)) {
            this.drawExtract(ModelRotation.X270_Y0, faceBakery, list);
        }

        if(pipe.isExtracting(EnumFacing.UP)) {
            this.drawExtract(ModelRotation.X90_Y0, faceBakery, list);
        }

        if(pipe.isExtracting(EnumFacing.NORTH)) {
            this.drawExtract(ModelRotation.X180_Y0, faceBakery, list);
        }

        if(pipe.isExtracting(EnumFacing.SOUTH)) {
            this.drawExtract(ModelRotation.X0_Y0, faceBakery, list);
        }

        if(pipe.isExtracting(EnumFacing.WEST)) {
            this.drawExtract(ModelRotation.X0_Y90, faceBakery, list);
        }

        if(pipe.isExtracting(EnumFacing.EAST)) {
            this.drawExtract(ModelRotation.X0_Y270, faceBakery, list);
        }
    }

    private void drawExtract(ModelRotation modelRot, FaceBakery faceBakery, List<BakedQuad> list) {
        BlockPartFace face = new BlockPartFace(null, 0, "", new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0));
        boolean scale = true;
        float min = 8 - getWidth();
        float max = 8 + getWidth();
        TextureAtlasSprite backGround = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(Constants.MODID + ":blocks/pipeExtract");
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, max, max), new Vector3f(max, max, 16.0F), face, backGround, EnumFacing.UP, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, max), new Vector3f(max, min, 16.0F), face, backGround, EnumFacing.DOWN, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, max), new Vector3f(min, max, 16.0F), face, backGround, EnumFacing.WEST, modelRot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(max, min, max), new Vector3f(max, max, 16.0F), face, backGround, EnumFacing.EAST, modelRot, null, scale, true));
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
    public boolean isCableConnected(IBlockAccess blockaccess, BlockPos pos, EnumFacing face) {
        return blockaccess.getTileEntity(pos) instanceof IInventory || blockaccess.getTileEntity(pos) instanceof PipeBasicItem;
    }

    @Override
    public float getWidth() {
        return 3.0F;
    }

    @Override
    public String getBackgroundTexture() {
        return "minecraft:blocks/iron_block";
    }
}
