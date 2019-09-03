package com.teambrmodding.neotech.common.block;

import com.teambrmodding.neotech.common.tileentity.GeneratorTile;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;

/**
 * This file was created for AssistedProgression
 * <p>
 * AssistedProgression is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author James Rogers - Dyonovan
 * @since 9/3/2019
 */
public class GeneratorBlock extends BaseBlock {

    public GeneratorBlock() {
        super(Properties.create(
                new Material(MaterialColor.BLACK, false, true,
                        false, false,
                        false, false,
                        false, PushReaction.NORMAL))
                .harvestTool(ToolType.PICKAXE).hardnessAndResistance(2.0F),
                "generator", GeneratorTile.class);
    }

    /*
      Block
     */

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isRemote)
            return true;
        else {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof GeneratorTile) {
                GeneratorTile generatorTile = (GeneratorTile) tileEntity;
                NetworkHooks.openGui((ServerPlayerEntity) player, generatorTile, pos);
            }
            return true;
        }
    }

    @Override
    public boolean isSolid(BlockState state) {
        return true;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.SOLID;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
