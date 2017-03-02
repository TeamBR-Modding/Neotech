package com.teambrmodding.neotech.common.blocks.machines;

import com.teambr.bookshelf.Bookshelf;
import com.teambr.bookshelf.common.IOpensGui;
import com.teambr.bookshelf.common.blocks.IToolable;
import com.teambr.bookshelf.util.PlayerUtils;
import com.teambr.bookshelf.util.WorldUtils;
import com.teambrmodding.neotech.common.blocks.BaseBlock;
import com.teambrmodding.neotech.common.tiles.AbstractMachine;
import com.teambrmodding.neotech.managers.ItemManager;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/11/2017
 */
@SuppressWarnings("deprecation")
public class BlockMachine extends BaseBlock implements IOpensGui, IToolable {
    // Instance of the property for rotation
    public static PropertyDirection FOUR_WAY =
            PropertyDirection.create("facing", Arrays.asList(EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST));
    public static PropertyBool PROPERTY_ACTIVE = PropertyBool.create("isactive");


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
        int playerFacingDirection = placer == null ? 0 : MathHelper.floor_float((placer.rotationYaw / 90.F) + 0.5F) & 3;
        EnumFacing facing = EnumFacing.getHorizontal(playerFacingDirection).getOpposite();
        worldIn.setBlockState(pos, getDefaultState().withProperty(FOUR_WAY, facing));
        WorldUtils.writeStackNBTToBlock(worldIn, pos, stack);
    }

    /**
     * Called when the block is clicked on
     * @return True to prevent future logic
     */
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
                                    EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side,
                                    float hitX, float hitY, float hitZ) {
        // Make sure our machine is reachable
        if(worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof AbstractMachine) {
            AbstractMachine machine = (AbstractMachine) worldIn.getTileEntity(pos);

            // First interact with fluid handlers
            if(machine.isFluidHandler()) {
                IFluidHandler fluidHandler = machine.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                if(FluidUtil.interactWithFluidHandler(heldItem, fluidHandler, playerIn)) {
                    return true;
                }
            }

            // Open a GUI
            if(!playerIn.isSneaking()) {
                playerIn.openGui(Bookshelf.INSTANCE, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
                return true;
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
            if(worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof AbstractMachine) {
                AbstractMachine machine = (AbstractMachine) worldIn.getTileEntity(pos);

                // Drop Upgrade Inventory
                List<ItemStack> upgradeItems = new ArrayList<>();
                for(ItemStack stack : machine.upgradeInventory.inventoryContents) {
                    if(stack != null)
                        upgradeItems.add(stack);
                }
                WorldUtils.dropStacks(worldIn, upgradeItems, pos);

                // Drop the inventory
                WorldUtils.dropStacksInInventory(machine, worldIn, pos);
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    /**
     * Used to define if we have a comparator output
     * @param state The state
     * @return If we have an output
     */
    @SuppressWarnings("deprecation")
    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    /**
     * Get the comparator output value
     * @param blockState The block state
     * @param worldIn The world
     * @param pos The pos
     * @return Redstone strength output
     */
    @SuppressWarnings("deprecation")
    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        if(worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof AbstractMachine) {
            AbstractMachine machine = (AbstractMachine) worldIn.getTileEntity(pos);
            return machine.getRedstoneOutput();
        }
        return super.getComparatorInputOverride(blockState, worldIn, pos);
    }

    /**
     * The following enable transparent textures to be rendered on top of the model
     * Is listed deprecated in favor of logic in block state, but our state calls this*
     */
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean isTranslucent(IBlockState state) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    /**
     * Called to display particles
     * @param stateIn The state
     * @param worldIn The world
     * @param pos The pos
     * @param rand An instance or Random
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if(getActualState(stateIn, worldIn, pos).getValue(PROPERTY_ACTIVE)) {
            EnumFacing facing = stateIn.getValue(FOUR_WAY);
            double d0 = pos.getX() + 0.5;
            double d1 = pos.getY() + rand.nextDouble() * 6.0 / 16.0;
            double d2 = pos.getZ() + 0.5;
            double d3 = 0.52;
            double d4 = rand.nextDouble() * 0.6 - 0.3;
            if(worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof AbstractMachine) {
                AbstractMachine machine = (AbstractMachine) worldIn.getTileEntity(pos);
                switch (facing) {
                    case WEST:
                        machine.spawnActiveParticles(d0 - d3, d1, d2 + d4);
                        break;
                    case EAST:  machine.spawnActiveParticles(d0 + d3, d1, d2 + d4);
                        break;
                    case NORTH: machine.spawnActiveParticles(d0 + d4, d1, d2 - d3);
                        break;
                    case SOUTH: machine.spawnActiveParticles(d0 + d4, d1, d2 + d3);
                        break;
                    default :
                }
            }
        }
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
     * Convert the given metadata into a BlockState for this Block
     *
     * @param meta
     */
    @Override
    public IBlockState getStateFromMeta(int meta) {
        if(meta == EnumFacing.DOWN.getIndex() || meta == EnumFacing.UP.getIndex())
            return getDefaultState();
        return getDefaultState().withProperty(FOUR_WAY, EnumFacing.getFront(meta));
    }

    /**
     * Creates the block state with our properties
     * @return The block state
     */
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FOUR_WAY, PROPERTY_ACTIVE);
    }

    /**
     * We want to get the actual state, passes info to the model not present in meta
     *
     * Is listed deprecated in favor of logic in block state, but our state calls this
     *
     * @param state The incoming state
     * @param worldIn The world
     * @param pos The position
     * @return A state that represents the actual state, not just what was stored
     */
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        if(worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof AbstractMachine) {
            AbstractMachine machine = (AbstractMachine) worldIn.getTileEntity(pos);
            return state.withProperty(PROPERTY_ACTIVE, machine.isActive());
        }
        return state;
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
            if(!PlayerUtils.isPlayerHoldingEither(player, ItemManager.wrench))
                return abstractMachine.getServerGuiElement(id, player, world, x, y, z);
        }
        return null;
    }

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
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if(world.getTileEntity(new BlockPos(x, y, z)) instanceof AbstractMachine) {
            AbstractMachine abstractMachine = (AbstractMachine) world.getTileEntity(new BlockPos(x, y, z));
            if(!PlayerUtils.isPlayerHoldingEither(player, ItemManager.wrench))
                return abstractMachine.getClientGuiElement(id, player, world, x, y, z);
        }
        return null;
    }

    /*******************************************************************************************************************
     * IToolable                                                                                                       *
     *******************************************************************************************************************/

    /**
     * Called to get what stack should be dropped on wrench
     * @param world The world
     * @param pos The position of the block
     * @return The stack to drop in the world
     */
    @Override
    public ItemStack getStackDroppedByWrench(World world, BlockPos pos) {
        return new ItemStack(this);
    }
}
