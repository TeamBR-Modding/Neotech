package com.dyonovan.jatm.common.blocks;

import com.dyonovan.jatm.JATM;
import com.dyonovan.jatm.handlers.GuiHandler;
import com.dyonovan.jatm.lib.Constants;
import com.dyonovan.jatm.common.tileentity.generator.TileGenerator;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockGenerator extends BlockContainer {

    private final String name = "blockGenerator";

    public BlockGenerator() {
        super(Material.iron);
        GameRegistry.registerBlock(this, name);

        this.setUnlocalizedName(Constants.MODID + "_" + name);
        this.setCreativeTab(JATM.tabJATM);
        this.setHardness(1.5F);
    }

    public String getName() {
        return name;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileGenerator();
    }

    @Override
    public int getRenderType() {
        return 3;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        super.onBlockActivated(world, pos, state, player, side, hitX, hitY, hitZ);

        if (world.isRemote) return true;
        else {
            TileGenerator tile = (TileGenerator)world.getTileEntity(pos);
            if (tile != null) {
                player.openGui(JATM.instance, GuiHandler.GENERATOR_GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
                            }
        }
        return true;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState blockState) {
        super.onBlockAdded(world, pos, blockState);
        world.markBlockForUpdate(pos);
    }

}
