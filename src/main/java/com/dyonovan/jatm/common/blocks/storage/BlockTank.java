package com.dyonovan.jatm.common.blocks.storage;

import com.dyonovan.jatm.JATM;
import com.dyonovan.jatm.collections.DummyState;
import com.dyonovan.jatm.common.blocks.BlockBakeable;
import com.dyonovan.jatm.common.tileentity.storage.TileDiamondTank;
import com.dyonovan.jatm.common.tileentity.storage.TileGoldTank;
import com.dyonovan.jatm.common.tileentity.storage.TileIronTank;
import com.dyonovan.jatm.helpers.GuiHelper;
import com.dyonovan.jatm.lib.Constants;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockTank extends BlockBakeable {
    protected int buckets;
    public BlockTank(String name, int bucketCount) {
        super(Material.glass, name, null);
        setCreativeTab(JATM.tabJATM);
        setUnlocalizedName(Constants.MODID + ":" + name);
        setHardness(3.0F);
        buckets = bucketCount;
    }

    public TileEntity createNewTileEntity(World worldIn, int meta) {
        switch (buckets) {
            case 3 :
                return new TileDiamondTank();
            case 2 :
                return new TileGoldTank();
            case 1 :
            default:
            return new TileIronTank();
        }
    }
    @Override
    public int getLightValue (IBlockAccess world, BlockPos pos)
    {
        TileEntity tank = world.getTileEntity(pos);
        if (tank != null && tank instanceof TileIronTank)
            return ((TileIronTank) tank).getBrightness();
        return 0;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        super.onBlockActivated(world, pos, state, player, side, hitX, hitY, hitZ);
        TileIronTank tank = (TileIronTank)world.getTileEntity(pos);

        ItemStack heldItem = player.getHeldItem();
        if(heldItem != null) {
            FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(heldItem);
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
        world.markBlockForUpdate(pos);
        if(world.isRemote)
            player.addChatComponentMessage(new ChatComponentText(tank.tank.getFluid() == null ? "Empty" : GuiHelper.GuiColor.YELLOW + tank.tank.getFluid().getLocalizedName() + GuiHelper.GuiColor.WHITE + " : " + tank.tank.getFluidAmount() + "/" + tank.tank.getCapacity()));
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
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        ItemStack stack = new ItemStack(this, 1);
        TileIronTank logic = (TileIronTank) worldIn.getTileEntity(pos);
        FluidStack liquid = logic.tank.getFluid();
        if (liquid != null)
        {
            NBTTagCompound tag = new NBTTagCompound();
            NBTTagCompound liquidTag = new NBTTagCompound();
            liquid.writeToNBT(liquidTag);
            tag.setTag("Fluid", liquidTag);
            stack.setTagCompound(tag);
        }
        dropTankBlock(worldIn, pos, stack);

        worldIn.setBlockToAir(pos);
        worldIn.markBlockForUpdate(pos);
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune){
        return null;
    }

    protected void dropTankBlock (World world, BlockPos pos, ItemStack stack)
    {
        if (!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops"))
        {
            float f = 0.7F;
            double d0 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            double d1 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            double d2 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            EntityItem entityitem = new EntityItem(world, (double) pos.getX() + d0, (double) pos.getY() + d1, (double) pos.getZ() + d2, stack);
            entityitem.setPickupDelay(10);
            world.spawnEntityInWorld(entityitem);
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        if (stack.hasTagCompound())
        {
            NBTTagCompound liquidTag = stack.getTagCompound().getCompoundTag("Fluid");
            if (liquidTag != null)
            {
                FluidStack liquid = FluidStack.loadFluidStackFromNBT(liquidTag);
                TileIronTank logic = (TileIronTank) worldIn.getTileEntity(pos);
                logic.tank.setFluid(liquid);
            }
        }
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return new DummyState(world, pos, this);
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
        return EnumWorldBlockLayer.CUTOUT;
    }

    @Override
    public boolean canRenderInLayer(EnumWorldBlockLayer layer)
    {
        return layer == EnumWorldBlockLayer.CUTOUT || layer == EnumWorldBlockLayer.TRANSLUCENT;
    }

}