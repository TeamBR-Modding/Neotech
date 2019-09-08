package com.teambrmodding.neotech.common.block;

import com.teambrmodding.neotech.common.tileentity.MachineTile;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 9/7/2019
 */
public class MachineBlock extends BaseBlock {

    /**
     * Creates the machine block
     */
    public MachineBlock() {
        super(Properties.create(Material.ROCK)
                .hardnessAndResistance(2.0F),
                "machine",
                MachineTile.class);
    }
}
