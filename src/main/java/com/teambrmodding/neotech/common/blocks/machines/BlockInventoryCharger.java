package com.teambrmodding.neotech.common.blocks.machines;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.teambr.bookshelf.common.IAdvancedToolTipProvider;
import com.teambr.bookshelf.common.blocks.IToolable;
import com.teambr.bookshelf.util.ClientUtils;
import com.teambrmodding.neotech.common.blocks.BaseBlock;
import com.teambrmodding.neotech.common.tiles.machines.operators.TileInventoryCharger;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.teambr.bookshelf.common.blocks.BlockSixWayRotation.SIX_WAY;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 3/3/2017
 */
@SuppressWarnings("ALL")
public class BlockInventoryCharger extends BaseBlock implements IToolable, IAdvancedToolTipProvider {

    /**
     * Main constructor for the block
     */
    public BlockInventoryCharger() {
        super(Material.IRON, "inventorycharger", TileInventoryCharger.class);
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
        worldIn.setBlockState(pos, getDefaultState().withProperty(SIX_WAY, EnumFacing.getDirectionFromEntityLiving(pos, placer)));
    }

    /*******************************************************************************************************************
     * BlockState Methods                                                                                              *
     *******************************************************************************************************************/

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(SIX_WAY).getIndex();
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     *
     * @param meta
     */
    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(SIX_WAY, EnumFacing.getFront(meta));
    }

    /**
     * Creates the block state with our properties
     * @return The block state
     */
    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, SIX_WAY);
    }

    /*******************************************************************************************************************
     * IToolsable Methods                                                                                              *
     *******************************************************************************************************************/

    /**
     * Called when a wrench clicks on this block
     *
     * @param stack  The stack clicking on this block, an INSTANCE of IToolWrench
     * @param player The player clicking
     * @param world  The world
     * @param pos    The block pos (us)
     * @param hand   The player's hand
     * @param facing Which face was clicked
     * @param hitX   X hit
     * @param hitY   Y hit
     * @param hitZ   Z hit
     * @return The result, pass to send to next, success to end
     */
    @Override
    public EnumActionResult onWrench(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
                                     EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(world.getBlockState(pos) != null) {
            IBlockState state = world.getBlockState(pos);
            EnumFacing machineFacing = state.getValue(SIX_WAY);
            if(machineFacing == facing) // If we are on the same face, invert
                world.setBlockState(pos, state.withProperty(SIX_WAY, machineFacing.getOpposite()));
            else // Rotate to clicked face
                world.setBlockState(pos, state.withProperty(SIX_WAY, facing));
            world.notifyBlockUpdate(pos, state, world.getBlockState(pos), 3);
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    /**
     * Called to get what stack should be dropped on wrench
     *
     * @param world The world
     * @param pos   The position of the block
     * @return The stack to drop in the world
     */
    @Nonnull
    @Override
    public ItemStack getStackDroppedByWrench(World world, BlockPos pos) {
        return new ItemStack(this);
    }

    /*******************************************************************************************************************
     * IAdvancedToolTipProvider                                                                                        *
     *******************************************************************************************************************/

    /**
     * Get the tool tip to present when shift is pressed
     *
     * @param stack The itemstack
     * @return The list to display
     */
    @Nullable
    @Override
    public List<String> getAdvancedToolTip(@Nonnull ItemStack stack) {
        List<String> toolTip = new ArrayList<>();

        toolTip.add("");
        toolTip.add(ChatFormatting.GRAY + ClientUtils.translate("neotech.inventoryCharger.desc"));

        return toolTip;
    }
}
