package com.dyonovan.neotech.common.blocks.storage;

import com.dyonovan.neotech.NeoTech;
import com.dyonovan.neotech.collections.CubeTextures;
import com.dyonovan.neotech.common.blocks.BlockBakeable;
import com.dyonovan.neotech.common.tileentity.storage.TileAdvancedRFStorage;
import com.dyonovan.neotech.common.tileentity.storage.TileBasicRFStorage;
import com.dyonovan.neotech.common.tileentity.storage.TileCreativeRFStorage;
import com.dyonovan.neotech.common.tileentity.storage.TileEliteRFStorage;
import com.dyonovan.neotech.lib.Constants;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.Random;

public class BlockRFStorage extends BlockBakeable {

    private int guiID;
    public int tier;

    public BlockRFStorage(String name, Class<? extends TileEntity> tileClass, int guiID, int tier) {
        super(Material.iron, name, tileClass);
        this.setUnlocalizedName(Constants.MODID + ":" + name);
        this.setCreativeTab(NeoTech.tabNeoTech);
        this.setHardness(1.5F);
        this.guiID = guiID;
        this.tier = tier;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        switch (tier) {
            case 1:
                return new TileBasicRFStorage();
            case 2:
                return new TileAdvancedRFStorage();
            case 3:
                return new TileEliteRFStorage();
            case 4:
                return new TileCreativeRFStorage();
            default:
            return new TileBasicRFStorage();
        }
    }

    @Override
    public CubeTextures getDefaultTextures() {
        TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
        return new CubeTextures(
                map.getAtlasSprite(Constants.MODID + ":blocks/" + name + "_front"),
                map.getAtlasSprite(Constants.MODID + ":blocks/" + "machine_side"),
                map.getAtlasSprite(Constants.MODID + ":blocks/" + "machine_side"),
                map.getAtlasSprite(Constants.MODID + ":blocks/" + "machine_side"),
                map.getAtlasSprite(Constants.MODID + ":blocks/" + "machine_side"),
                map.getAtlasSprite(Constants.MODID + ":blocks/" + "machine_side")
        );
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        super.onBlockActivated(world, pos, state, player, side, hitX, hitY, hitZ);

        if (world.isRemote) return true;
        else {
            TileEntity tile =  world.getTileEntity(pos);
            if (tile != null) {
                player.openGui(NeoTech.instance, guiID, world, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }

    @Override
    protected BlockState createBlockState()
    {
        return new BlockState(this, PROPERTY_FACING);
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(PROPERTY_FACING, BlockPistonBase.getFacingFromEntity(worldIn, pos, placer));
    }

    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }
        return this.getDefaultState().withProperty(PROPERTY_FACING, enumfacing);
    }

    public int getMetaFromState(IBlockState state)
    {
        return ((EnumFacing)state.getValue(PROPERTY_FACING)).getIndex();
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        ItemStack stack = new ItemStack(this, 1);
        TileBasicRFStorage tile = (TileBasicRFStorage) worldIn.getTileEntity(pos);
        if (tile == null) return;

        tile.expelItems();

        if (tile.getEnergyStored(null) > 0)
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("Power", tile.getEnergyStored(null));
            stack.setTagCompound(tag);
        }
        dropStorageBlock(worldIn, pos, stack);

        worldIn.destroyBlock(pos, false);
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune){
        return null;
    }

    protected void dropStorageBlock (World world, BlockPos pos, ItemStack stack)
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
            NBTTagCompound tag = stack.getTagCompound();
            if (tag != null)
            {
                TileBasicRFStorage tile = (TileBasicRFStorage) worldIn.getTileEntity(pos);
                tile.setRFStorage(tag.getInteger("Power"));
            }
        }
    }
}
