package com.dyonovan.neotech.client.modelfactory.models;

import com.dyonovan.neotech.collections.ConnectedTextures;
import com.dyonovan.neotech.collections.DummyState;
import com.dyonovan.neotech.common.blocks.connected.BlockConnectedTextures;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;

import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings( "deprecation" )
public class ModelConnectedTextures implements ISmartBlockModel, ISmartItemModel {
    protected ConnectedTextures textures;
    protected IBlockAccess world;
    protected BlockPos pos;
    protected BlockConnectedTextures block;
    protected static FaceBakery faceBakery = new FaceBakery();

    public ModelConnectedTextures(DummyState state) {
        textures = ((BlockConnectedTextures)state.block).getConnectedTextures();
        block = (BlockConnectedTextures) state.block;
        world = state.blockAccess;
        pos = state.pos;
    }

    public ModelConnectedTextures(BlockConnectedTextures block) {
        textures = block.getConnectedTextures();
    }

    @Override
    public List getFaceQuads(EnumFacing face) {
        return new ArrayList<>();
    }

    public void drawFace(boolean[] connections, List<BakedQuad> list, ModelRotation rot) {
        BlockPartFace face = new BlockPartFace(null, 0, "", new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0));
        boolean scale = true;

        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.0F, 16.0F), new Vector3f(8.0F, 8.0F, 15.999F), face, textures.getTextureForCorner(2, connections), EnumFacing.SOUTH, rot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(8.0F, 0.0F, 16.0F), new Vector3f(16.0F, 8.0F, 15.999F), face, textures.getTextureForCorner(3, connections), EnumFacing.SOUTH, rot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 8.0F, 16.0F), new Vector3f(8.0F, 16.0F, 15.999F), face, textures.getTextureForCorner(0, connections), EnumFacing.SOUTH, rot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(8.0F, 8.0F, 16.0F), new Vector3f(16.0F, 16.0F, 15.999F), face, textures.getTextureForCorner(1, connections), EnumFacing.SOUTH, rot, null, scale, true));

        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.0F, 16.0F), new Vector3f(8.0F, 8.0F, 15.999F), face, textures.getTextureForCorner(6, connections), EnumFacing.SOUTH, rot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(8.0F, 0.0F, 16.0F), new Vector3f(16.0F, 8.0F, 15.999F), face, textures.getTextureForCorner(7, connections), EnumFacing.SOUTH, rot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 8.0F, 16.0F), new Vector3f(8.0F, 16.0F, 15.999F), face, textures.getTextureForCorner(4, connections), EnumFacing.SOUTH, rot, null, scale, true));
        list.add(faceBakery.makeBakedQuad(new Vector3f(8.0F, 8.0F, 16.0F), new Vector3f(16.0F, 16.0F, 15.999F), face, textures.getTextureForCorner(5, connections), EnumFacing.SOUTH, rot, null, scale, true));

        if (block != null && block.isTranslucent()) {
            list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.0F, 16.0F), new Vector3f(8.0F, 8.0F, 15.999F), face, textures.getTextureForCorner(2, connections), EnumFacing.NORTH, rot, null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(8.0F, 0.0F, 16.0F), new Vector3f(16.0F, 8.0F, 15.999F), face, textures.getTextureForCorner(3, connections), EnumFacing.NORTH, rot, null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 8.0F, 16.0F), new Vector3f(8.0F, 16.0F, 15.999F), face, textures.getTextureForCorner(0, connections), EnumFacing.NORTH, rot, null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(8.0F, 8.0F, 16.0F), new Vector3f(16.0F, 16.0F, 15.999F), face, textures.getTextureForCorner(1, connections), EnumFacing.NORTH, rot, null, scale, true));

            list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.0F, 16.0F), new Vector3f(8.0F, 8.0F, 15.999F), face, textures.getTextureForCorner(6, connections), EnumFacing.NORTH, rot, null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(8.0F, 0.0F, 16.0F), new Vector3f(16.0F, 8.0F, 15.999F), face, textures.getTextureForCorner(7, connections), EnumFacing.NORTH, rot, null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 8.0F, 16.0F), new Vector3f(8.0F, 16.0F, 15.999F), face, textures.getTextureForCorner(4, connections), EnumFacing.NORTH, rot, null, scale, true));
            list.add(faceBakery.makeBakedQuad(new Vector3f(8.0F, 8.0F, 16.0F), new Vector3f(16.0F, 16.0F, 15.999F), face, textures.getTextureForCorner(5, connections), EnumFacing.NORTH, rot, null, scale, true));

        }
    }

    @Override
    public List getGeneralQuads() {
        ArrayList<BakedQuad> list = new ArrayList<>();
        boolean[] connections = new boolean[16];

        //Item (DON'T LOOK AROUND!)
        if (world == null) {
            Arrays.fill(connections, false);
            for (EnumFacing facing : EnumFacing.values())
                drawFace(connections, list, lookUpRotationForFace(facing));
            return list;
        }
        for(EnumFacing dir : EnumFacing.values()) {
            if (world.isAirBlock(pos.offset(dir)) || (!world.getBlockState(pos.offset(dir)).getBlock().isOpaqueCube() &&
                    !block.canBlockConnect(world.getBlockState(pos.offset(dir)).getBlock()))) {
                switch (dir) {
                    case UP:
                        connections[0] = block.canBlockConnect(world.getBlockState(pos.add(-1, 0, -1)).getBlock());
                        connections[1] = block.canBlockConnect(world.getBlockState(pos.add(0, 0, -1)).getBlock());
                        connections[2] = block.canBlockConnect(world.getBlockState(pos.add(1, 0, -1)).getBlock());
                        connections[3] = block.canBlockConnect(world.getBlockState(pos.add(-1, 0, 0)).getBlock());
                        connections[4] = block.canBlockConnect(world.getBlockState(pos.add(1, 0, 0)).getBlock());
                        connections[5] = block.canBlockConnect(world.getBlockState(pos.add(-1, 0, 1)).getBlock());
                        connections[6] = block.canBlockConnect(world.getBlockState(pos.add(0, 0, 1)).getBlock());
                        connections[7] = block.canBlockConnect(world.getBlockState(pos.add(1, 0, 1)).getBlock());
                        connections[8] = block.canBlockConnect(world.getBlockState(pos.add(-1, 1, -1)).getBlock());
                        connections[9] = block.canBlockConnect(world.getBlockState(pos.add(0, 1, -1)).getBlock());
                        connections[10] = block.canBlockConnect(world.getBlockState(pos.add(1, 1, -1)).getBlock());
                        connections[11] = block.canBlockConnect(world.getBlockState(pos.add(-1, 1, 0)).getBlock());
                        connections[12] = block.canBlockConnect(world.getBlockState(pos.add(1, 1, 0)).getBlock());
                        connections[13] = block.canBlockConnect(world.getBlockState(pos.add(-1, 1, 1)).getBlock());
                        connections[14] = block.canBlockConnect(world.getBlockState(pos.add(0, 1, 1)).getBlock());
                        connections[15] = block.canBlockConnect(world.getBlockState(pos.add(1, 1, 1)).getBlock());
                        drawFace(connections, list, lookUpRotationForFace(dir));
                        break;
                    case DOWN:
                        connections[0] = block.canBlockConnect(world.getBlockState(pos.add(-1, 0, 1)).getBlock());
                        connections[1] = block.canBlockConnect(world.getBlockState(pos.add(0, 0, 1)).getBlock());
                        connections[2] = block.canBlockConnect(world.getBlockState(pos.add(1, 0, 1)).getBlock());
                        connections[3] = block.canBlockConnect(world.getBlockState(pos.add(-1, 0, 0)).getBlock());
                        connections[4] = block.canBlockConnect(world.getBlockState(pos.add(1, 0, 0)).getBlock());
                        connections[5] = block.canBlockConnect(world.getBlockState(pos.add(-1, 0, -1)).getBlock());
                        connections[6] = block.canBlockConnect(world.getBlockState(pos.add(0, 0, -1)).getBlock());
                        connections[7] = block.canBlockConnect(world.getBlockState(pos.add(1, 0, -1)).getBlock());
                        connections[8] = block.canBlockConnect(world.getBlockState(pos.add(-1, -1, 1)).getBlock());
                        connections[9] = block.canBlockConnect(world.getBlockState(pos.add(0, -1, 1)).getBlock());
                        connections[10] = block.canBlockConnect(world.getBlockState(pos.add(1, -1, 1)).getBlock());
                        connections[11] = block.canBlockConnect(world.getBlockState(pos.add(-1, -1, 0)).getBlock());
                        connections[12] = block.canBlockConnect(world.getBlockState(pos.add(1, -1, 0)).getBlock());
                        connections[13] = block.canBlockConnect(world.getBlockState(pos.add(-1, -1, -1)).getBlock());
                        connections[14] = block.canBlockConnect(world.getBlockState(pos.add(0, -1, -1)).getBlock());
                        connections[15] = block.canBlockConnect(world.getBlockState(pos.add(1, -1, -1)).getBlock());
                        drawFace(connections, list, lookUpRotationForFace(dir));
                        break;
                    case NORTH:
                        connections[0] = block.canBlockConnect(world.getBlockState(pos.add(1, 1, 0)).getBlock());
                        connections[1] = block.canBlockConnect(world.getBlockState(pos.add(0, 1, 0)).getBlock());
                        connections[2] = block.canBlockConnect(world.getBlockState(pos.add(-1, 1, 0)).getBlock());
                        connections[3] = block.canBlockConnect(world.getBlockState(pos.add(1, 0, 0)).getBlock());
                        connections[4] = block.canBlockConnect(world.getBlockState(pos.add(-1, 0, 0)).getBlock());
                        connections[5] = block.canBlockConnect(world.getBlockState(pos.add(1, -1, 0)).getBlock());
                        connections[6] = block.canBlockConnect(world.getBlockState(pos.add(0, -1, 0)).getBlock());
                        connections[7] = block.canBlockConnect(world.getBlockState(pos.add(-1, -1, 0)).getBlock());
                        connections[8] = block.canBlockConnect(world.getBlockState(pos.add(1, 1, -1)).getBlock());
                        connections[9] = block.canBlockConnect(world.getBlockState(pos.add(0, 1, -1)).getBlock());
                        connections[10] = block.canBlockConnect(world.getBlockState(pos.add(-1, 1, -1)).getBlock());
                        connections[11] = block.canBlockConnect(world.getBlockState(pos.add(1, 0, -1)).getBlock());
                        connections[12] = block.canBlockConnect(world.getBlockState(pos.add(-1, 0, -1)).getBlock());
                        connections[13] = block.canBlockConnect(world.getBlockState(pos.add(1, -1, -1)).getBlock());
                        connections[14] = block.canBlockConnect(world.getBlockState(pos.add(0, -1, -1)).getBlock());
                        connections[15] = block.canBlockConnect(world.getBlockState(pos.add(-1, -1, -1)).getBlock());
                        drawFace(connections, list, lookUpRotationForFace(dir));
                        break;
                    case SOUTH:
                        connections[0] = block.canBlockConnect(world.getBlockState(pos.add(-1, 1, 0)).getBlock());
                        connections[1] = block.canBlockConnect(world.getBlockState(pos.add(0, 1, 0)).getBlock());
                        connections[2] = block.canBlockConnect(world.getBlockState(pos.add(1, 1, 0)).getBlock());
                        connections[3] = block.canBlockConnect(world.getBlockState(pos.add(-1, 0, 0)).getBlock());
                        connections[4] = block.canBlockConnect(world.getBlockState(pos.add(1, 0, 0)).getBlock());
                        connections[5] = block.canBlockConnect(world.getBlockState(pos.add(-1, -1, 0)).getBlock());
                        connections[6] = block.canBlockConnect(world.getBlockState(pos.add(0, -1, 0)).getBlock());
                        connections[7] = block.canBlockConnect(world.getBlockState(pos.add(1, -1, 0)).getBlock());
                        connections[8] = block.canBlockConnect(world.getBlockState(pos.add(-1, 1, 1)).getBlock());
                        connections[9] = block.canBlockConnect(world.getBlockState(pos.add(0, 1, 1)).getBlock());
                        connections[10] = block.canBlockConnect(world.getBlockState(pos.add(1, 1, 1)).getBlock());
                        connections[11] = block.canBlockConnect(world.getBlockState(pos.add(-1, 0, 1)).getBlock());
                        connections[12] = block.canBlockConnect(world.getBlockState(pos.add(1, 0, 1)).getBlock());
                        connections[13] = block.canBlockConnect(world.getBlockState(pos.add(-1, -1, 1)).getBlock());
                        connections[14] = block.canBlockConnect(world.getBlockState(pos.add(0, -1, 1)).getBlock());
                        connections[15] = block.canBlockConnect(world.getBlockState(pos.add(1, -1, 1)).getBlock());
                        drawFace(connections, list, lookUpRotationForFace(dir));
                        break;
                    case WEST:
                        connections[0] = block.canBlockConnect(world.getBlockState(pos.add(0, 1, -1)).getBlock());
                        connections[1] = block.canBlockConnect(world.getBlockState(pos.add(0, 1, 0)).getBlock());
                        connections[2] = block.canBlockConnect(world.getBlockState(pos.add(0, 1, 1)).getBlock());
                        connections[3] = block.canBlockConnect(world.getBlockState(pos.add(0, 0, -1)).getBlock());
                        connections[4] = block.canBlockConnect(world.getBlockState(pos.add(0, 0, 1)).getBlock());
                        connections[5] = block.canBlockConnect(world.getBlockState(pos.add(0, -1, -1)).getBlock());
                        connections[6] = block.canBlockConnect(world.getBlockState(pos.add(0, -1, 0)).getBlock());
                        connections[7] = block.canBlockConnect(world.getBlockState(pos.add(0, -1, 1)).getBlock());
                        connections[8] = block.canBlockConnect(world.getBlockState(pos.add(-1, 1, -1)).getBlock());
                        connections[9] = block.canBlockConnect(world.getBlockState(pos.add(-1, 1, 0)).getBlock());
                        connections[10] = block.canBlockConnect(world.getBlockState(pos.add(-1, 1, 1)).getBlock());
                        connections[11] = block.canBlockConnect(world.getBlockState(pos.add(-1, 0, -1)).getBlock());
                        connections[12] = block.canBlockConnect(world.getBlockState(pos.add(-1, 0, 1)).getBlock());
                        connections[13] = block.canBlockConnect(world.getBlockState(pos.add(-1, -1, -1)).getBlock());
                        connections[14] = block.canBlockConnect(world.getBlockState(pos.add(-1, -1, 0)).getBlock());
                        connections[15] = block.canBlockConnect(world.getBlockState(pos.add(-1, -1, 1)).getBlock());
                        drawFace(connections, list, lookUpRotationForFace(dir));
                        break;
                    case EAST:
                        connections[0] = block.canBlockConnect(world.getBlockState(pos.add(0, 1, 1)).getBlock());
                        connections[1] = block.canBlockConnect(world.getBlockState(pos.add(0, 1, 0)).getBlock());
                        connections[2] = block.canBlockConnect(world.getBlockState(pos.add(0, 1, -1)).getBlock());
                        connections[3] = block.canBlockConnect(world.getBlockState(pos.add(0, 0, 1)).getBlock());
                        connections[4] = block.canBlockConnect(world.getBlockState(pos.add(0, 0, -1)).getBlock());
                        connections[5] = block.canBlockConnect(world.getBlockState(pos.add(0, -1, 1)).getBlock());
                        connections[6] = block.canBlockConnect(world.getBlockState(pos.add(0, -1, 0)).getBlock());
                        connections[7] = block.canBlockConnect(world.getBlockState(pos.add(0, -1, -1)).getBlock());
                        connections[8] = block.canBlockConnect(world.getBlockState(pos.add(1, 1, 1)).getBlock());
                        connections[9] = block.canBlockConnect(world.getBlockState(pos.add(1, 1, 0)).getBlock());
                        connections[10] = block.canBlockConnect(world.getBlockState(pos.add(1, 1, -1)).getBlock());
                        connections[11] = block.canBlockConnect(world.getBlockState(pos.add(1, 0, 1)).getBlock());
                        connections[12] = block.canBlockConnect(world.getBlockState(pos.add(1, 0, -1)).getBlock());
                        connections[13] = block.canBlockConnect(world.getBlockState(pos.add(1, -1, 1)).getBlock());
                        connections[14] = block.canBlockConnect(world.getBlockState(pos.add(1, -1, 0)).getBlock());
                        connections[15] = block.canBlockConnect(world.getBlockState(pos.add(1, -1, -1)).getBlock());
                        drawFace(connections, list, lookUpRotationForFace(dir));
                        break;
                    default:
                        break;
                }
            }
        }
        return list;
    }

    public ModelRotation lookUpRotationForFace(EnumFacing face) {
        switch (face) {
            case UP :
                return ModelRotation.X90_Y0;
            case DOWN:
                return ModelRotation.X270_Y0;
            case NORTH:
                return ModelRotation.X0_Y180;
            case EAST:
                return ModelRotation.X0_Y270;
            case SOUTH:
                return ModelRotation.X0_Y0;
            case WEST:
                return ModelRotation.X0_Y90;
            default:
                return ModelRotation.X0_Y0;
        }
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public IBakedModel handleBlockState(IBlockState state) {
        return state instanceof DummyState ? new ModelConnectedTextures((DummyState)state) : null;
    }

    @Override
    public IBakedModel handleItemState(ItemStack stack) {
        return Block.getBlockFromItem(stack.getItem()) instanceof BlockConnectedTextures ? new ModelConnectedTextures((BlockConnectedTextures) Block.getBlockFromItem(stack.getItem())) : null;
    }

    @Override
    public TextureAtlasSprite getTexture() {
        return textures.corners;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return new ItemCameraTransforms(ModelBlock.MovedUp, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT);
    }
}
