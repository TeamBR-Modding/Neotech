package com.teambrmodding.neotech.common.blocks;

import com.teambrmodding.neotech.Neotech;
import com.teambrmodding.neotech.lib.Reference;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.sql.Ref;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/15/2017
 */
public class BaseBlock extends BlockContainer {
    private Class<? extends TileEntity> tileClass;

    protected BaseBlock(Material materialIn, String name, Class<? extends TileEntity> tileEntityClass) {
        super(materialIn);
        this.tileClass = tileEntityClass;

        setRegistryName(new ResourceLocation(Reference.MOD_ID, name));
        setUnlocalizedName(Reference.MOD_ID + ":" + name);

        if(getCreativeTab() != null)
            setCreativeTab(getCreativeTab());
    }

    /*******************************************************************************************************************
     * BaseBlock Methods                                                                                               *
     *******************************************************************************************************************/

    /**
     * Used to tell if this should be in a creative tab, and if so which one
     *
     * @return Null if none, defaults to the main NeoTech Tab
     */
    protected CreativeTabs getCreativeTab() {
        return Neotech.tabNeotech;
    }

    /**
     * Used to change the hardness of a block, but will default to 2.0F if not overwritten
     *
     * @return The hardness value, default 2.0F
     */
    protected float getHardness() {
        return 2.0F;
    }

    /*******************************************************************************************************************
     * BlockContainer                                                                                                  *
     *******************************************************************************************************************/

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        if(tileClass != null)
            try {
                tileClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }

        return null;
    }

    /*******************************************************************************************************************
     * Block                                                                                                           *
     *******************************************************************************************************************/

    /**
     * Block container changes this, we still want a normal model
     * @return The model type
     */
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}
