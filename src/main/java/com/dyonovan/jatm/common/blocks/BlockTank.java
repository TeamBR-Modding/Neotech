package com.dyonovan.jatm.common.blocks;

import com.dyonovan.jatm.JATM;
import com.dyonovan.jatm.collections.DummyState;
import com.dyonovan.jatm.common.tileentity.notmachines.TileTank;
import com.dyonovan.jatm.lib.Constants;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTank extends BlockBakeable {
    public BlockTank(String name, Class<? extends TileEntity> tileClass) {
        super(Material.glass, name, tileClass);
        setCreativeTab(JATM.tabJATM);
        setUnlocalizedName(Constants.MODID + ":" + name);
        setHardness(3.0F);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        super.onBlockActivated(world, pos, state, player, side, hitX, hitY, hitZ);

        ItemStack heldItem = player.getHeldItem();
        if(heldItem != null) {
            FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(heldItem);
            TileTank tank = (TileTank)world.getTileEntity(pos);
            if(fluid != null) {
                int amount = tank.fill(EnumFacing.UP, fluid, false);
                if(amount == fluid.amount) {
                    tank.fill(EnumFacing.UP, fluid, true);
                    if (!player.capabilities.isCreativeMode)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, consumeItem(heldItem));
                    return true;
                }
                else
                    return false;
            } else if (FluidContainerRegistry.isBucket(heldItem)) {
                FluidTankInfo[] tanks = tank.getTankInfo(EnumFacing.UP);
                FluidStack fillFluid = tanks[0].fluid;// getFluid();
                ItemStack fillStack = FluidContainerRegistry.fillFluidContainer(fillFluid, heldItem);
                if (fillStack != null) {
                    tank.drain(EnumFacing.DOWN, FluidContainerRegistry.getFluidForFilledItem(fillStack).amount, true);
                    if (!player.capabilities.isCreativeMode) {
                        if (heldItem.stackSize == 1) {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, fillStack);
                        }
                        else {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, consumeItem(heldItem));
                            if (!player.inventory.addItemStackToInventory(fillStack)) {
                                player.dropPlayerItemWithRandomChoice(fillStack, false);
                            }
                        }
                    }
                    return true;
                }
            }
        }
        return super.onBlockActivated(world, pos, state, player, side, hitX, hitY, hitZ);
    }

    public static ItemStack consumeItem (ItemStack stack)
    {
        if (stack.stackSize == 1)
        {
            if (stack.getItem().hasContainerItem())
                return stack.getItem().getContainerItem(stack);
            else
                return null;
        }
        else
        {
            stack.splitStack(1);

            return stack;
        }
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return new DummyState(world, pos);
    }

    @Override
    public boolean isBlockNormalCube() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public boolean isTranslucent() {
        return true;
    }

    public boolean isFullCube()
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.TRANSLUCENT;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
        return true;
    }
}
