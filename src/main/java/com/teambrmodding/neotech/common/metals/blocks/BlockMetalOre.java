package com.teambrmodding.neotech.common.metals.blocks;

import com.teambrmodding.neotech.Neotech;
import com.teambrmodding.neotech.lib.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;

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
public class BlockMetalOre extends Block {
    private String name;

    /**
     * Creates an ore block
     * @param name        The block name
     * @param miningLevel The mining level
     */
    public BlockMetalOre(String name, int miningLevel) {
        super(Material.ROCK);
        this.name = name;
        setRegistryName(new ResourceLocation(Reference.MOD_ID, name));
        setUnlocalizedName(getRegistryName().toString());
        setHardness(3.0F);
        setHarvestLevel("pickaxe", miningLevel);
        setCreativeTab(Neotech.tabMetals);
    }

    /**
     * The name of this block
     * @return The name
     */
    public String getName() {
        return name;
    }

}
