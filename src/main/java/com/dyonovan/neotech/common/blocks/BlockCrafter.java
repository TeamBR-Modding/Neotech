package com.dyonovan.neotech.common.blocks;

import com.dyonovan.neotech.NeoTech;
import com.dyonovan.neotech.collections.CubeTextures;
import com.dyonovan.neotech.collections.DummyState;
import com.dyonovan.neotech.common.tileentity.machine.TileEntityCrafter;
import com.dyonovan.neotech.handlers.GuiHandler;
import com.dyonovan.neotech.lib.Constants;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCrafter extends BlockBakeable {

    public BlockCrafter() {
        super(Material.wood, "crafter", TileEntityCrafter.class);
        this.setUnlocalizedName(Constants.MODID + ":" + name);
        this.setCreativeTab(NeoTech.tabNeoTech);
        this.setHardness(1.5F);
    }

    public CubeTextures getDefaultTextures() {
        TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
        return new CubeTextures(
                map.getAtlasSprite("minecraft:blocks/" + "crafting_table_front"),
                map.getAtlasSprite("minecraft:blocks/" + "crafting_table_side"),
                map.getAtlasSprite("minecraft:blocks/" + "bookshelf"),
                map.getAtlasSprite("minecraft:blocks/" + "bookshelf"),
                map.getAtlasSprite("minecraft:blocks/" + "crafting_table_top"),
                map.getAtlasSprite("minecraft:blocks/" + "log_oak_top")
        );
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        super.onBlockActivated(world, pos, state, player, side, hitX, hitY, hitZ);

        if (world.isRemote) return true;
        else {
            TileEntity tile =  world.getTileEntity(pos);
            if (tile != null) {
                player.openGui(NeoTech.instance, GuiHandler.CRAFTER_GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof IExpellable) {
                ((IExpellable) tile).expelItems();
            }
        super.breakBlock(worldIn, pos, state);
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
    protected BlockState createBlockState() {
        return new BlockState(this, PROPERTY_FACING);
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return new DummyState(world, pos, this);
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        int playerFacingDirection = (placer == null) ? 0 : MathHelper.floor_double((placer.rotationYaw / 90.0F) + 0.5D) & 3;
        EnumFacing enumfacing = EnumFacing.getHorizontal(playerFacingDirection).getOpposite();
        return this.getDefaultState().withProperty(PROPERTY_FACING, enumfacing);
    }
}
