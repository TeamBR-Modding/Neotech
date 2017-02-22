package com.teambrmodding.neotech.common.blocks.storage;

import com.teambr.bookshelf.common.blocks.IToolable;
import com.teambr.bookshelf.util.ClientUtils;
import com.teambr.bookshelf.util.WorldUtils;
import com.teambrmodding.neotech.common.blocks.BaseBlock;
import com.teambrmodding.neotech.common.tiles.storage.tanks.TileBasicTank;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/15/2017
 */
@SuppressWarnings("deprecation")
public class BlockFluidStorage extends BaseBlock implements IToolable {

    /**
     * Creates the storage block
     */
    public BlockFluidStorage(String name, Class<? extends TileEntity> tileEntityClass) {
        super(Material.IRON, name, tileEntityClass);
    }

    /*******************************************************************************************************************
     * Block Methods                                                                                                   *
     *******************************************************************************************************************/

    /**
     * Get the bounding box of this block
     * @param state  The block state
     * @param source The world
     * @param pos    The block location
     * @return The bounding box
     */
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0.0625, 0.0, 0.0625, 0.9375, 1.0, 0.9375);
    }

    /**
     * Called when the block is clicked on
     * @return True to prevent future logic
     */
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
                                    EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side,
                                    float hitX, float hitY, float hitZ) {
        // Make sure our storage is reachable
        if(worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof TileBasicTank) {
            TileBasicTank fluidStorage = (TileBasicTank) worldIn.getTileEntity(pos);

            // First interact with fluid handlers
            IFluidHandler fluidHandler = fluidStorage.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            if(FluidUtil.interactWithFluidHandler(heldItem, fluidHandler, playerIn)) {
                return true;
            }

            // We want to display in chat the current levels
            if(worldIn.isRemote && fluidStorage.tanks[TileBasicTank.TANK].getFluid() != null) {
                FluidStack fluidStack = fluidStorage.tanks[TileBasicTank.TANK].getFluid();
                if(fluidStack.getFluid() != null) {
                    String display = fluidStack.getLocalizedName() + ": " +
                            ClientUtils.formatNumber(fluidStorage.tanks[TileBasicTank.TANK].getFluidAmount()) + " / " +
                            ClientUtils.formatNumber(fluidStorage.tanks[TileBasicTank.TANK].getCapacity());
                    playerIn.addChatMessage(new TextComponentString(display));
                }
            }
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ);
    }

    /**
     * Called when the block is broken, allows us to drop items from inventory
     * @param worldIn The world
     * @param pos The pos
     * @param state The state
     */
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if(!worldIn.isRemote) {
            WorldUtils.breakBlockSavingNBT(worldIn, pos, this);
        }
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if(stack.hasTagCompound() && !worldIn.isRemote) {
            worldIn.getTileEntity(pos).readFromNBT(stack.getTagCompound());
            worldIn.getTileEntity(pos).setPos(pos);
            worldIn.setBlockState(pos, state, 3);
        }
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    @Nullable
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

    /**
     * Queries if this block should render in a given layer.
     */
    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.TRANSLUCENT;
    }

    /***
     * Used to tell if this is a full block
     *
     * Is listed deprecated in favor of logic in block state, but our state calls this
     *
     * @param state The block state
     * @return Is this cube full
     */
    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    /*******************************************************************************************************************
     * IToolable                                                                                                       *
     *******************************************************************************************************************/

    /**
     * Called to get what stack should be dropped on wrench
     *
     * @param world The world
     * @param pos   The position of the block
     * @return The stack to drop in the world
     */
    @Override
    public ItemStack getStackDroppedByWrench(World world, BlockPos pos) {
        return new ItemStack(this);
    }
}
