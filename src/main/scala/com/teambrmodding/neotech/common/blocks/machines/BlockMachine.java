package com.teambrmodding.neotech.common.blocks.machines;

import com.teambr.bookshelf.common.IOpensGui;
import com.teambr.bookshelf.common.blocks.IToolable;
import com.teambrmodding.neotech.common.blocks.BaseBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Arrays;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/11/2017
 */
public class BlockMachine extends BaseBlock implements IOpensGui, IToolable {
    // Instance of the property for rotation
    public static PropertyDirection FOUR_WAY =
            PropertyDirection.create("facing", Arrays.asList(EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST));


    /**
     * Main constructor for the block
     */
    public BlockMachine(String name, Class<? extends TileEntity> tileEntity) {
        super(Material.IRON, name, tileEntity);
    }

    /*******************************************************************************************************************
     * Block Methods                                                                                                   *
     *******************************************************************************************************************/

    /**
     * Called when the block is placed
     * @param worldIn The world
     * @param pos The block position
     * @param state The block state
     * @param placer Who placed the block
     * @param stack The stack that was the block
     */
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        int playerFacingDirection = placer == null ? 0 : MathHelper.floor((placer.rotationYaw / 90.F) + 0.5F) & 3;
        EnumFacing facing = EnumFacing.getHorizontal(playerFacingDirection).getOpposite();
        worldIn.setBlockState(pos, getDefaultState().withProperty(FOUR_WAY, facing));
    }

    /*******************************************************************************************************************
     * BlockState Methods                                                                                              *
     *******************************************************************************************************************/

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FOUR_WAY).getIndex();
    }

    /**
     * Creates the block state with our properties
     * @return The block state
     */
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FOUR_WAY);
    }

    /*******************************************************************************************************************
     * IOpensGui                                                                                                       *
     *******************************************************************************************************************/

    /**
     * Return the container for this tile
     *
     * @param id Id, probably not needed but could be used for multiple guis
     * @param player The player that is opening the gui
     * @param world The world
     * @param x X Pos
     * @param y Y Pos
     * @param z Z Pos
     * @return The container to open
     */
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if(world.getTileEntity(new BlockPos(x, y, z)) instanceof AbstractMachine) {
            AbstractMachine abstractMachine = (AbstractMachine) world.getTileEntity(new BlockPos(x, y, z));
            if(!PlayerUtils.isPlayerHoldingeither(player, ItemManager.wrench()))
        }
    }

}
