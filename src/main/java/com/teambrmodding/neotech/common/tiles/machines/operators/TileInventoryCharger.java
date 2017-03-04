package com.teambrmodding.neotech.common.tiles.machines.operators;

import com.teambr.bookshelf.common.blocks.BlockSixWayRotation;
import com.teambr.bookshelf.common.tiles.EnergyHandler;
import com.teambr.bookshelf.common.tiles.UpdatingTile;
import com.teambr.bookshelf.util.EnergyUtils;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 3/3/2017
 */
public class TileInventoryCharger extends EnergyHandler {

    /*******************************************************************************************************************
     * Tile Methods                                                                                                    *
     *******************************************************************************************************************/

    @Override
    protected void onServerTick() {
        super.onServerTick();

        // Must not be powered and have energy
        if(!(world.isBlockIndirectlyGettingPowered(pos) > 0 || world.isBlockPowered(pos)) &&
                energyStorage.getEnergyStored() > 0) {
            // Grab the facing direction
            EnumFacing facing = world.getBlockState(getPos()).getValue(BlockSixWayRotation.SIX_WAY);

            // Make sure what we are looking at has items
            if(world.getTileEntity(getPos().offset(facing)) != null &&
                    world.getTileEntity(getPos().offset(facing))
                            .hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite())) {
                // Grab other inventory
                IItemHandler otherInventory = world.getTileEntity(pos.offset(facing))
                        .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());

                // Cycle the inventory
                for(int slot = 0; slot < otherInventory.getSlots(); slot++) {
                    // Can it handle energy
                    if(otherInventory.getStackInSlot(slot).hasCapability(CapabilityEnergy.ENERGY, null)) {
                        IEnergyStorage energyItem = otherInventory.getStackInSlot(slot)
                                .getCapability(CapabilityEnergy.ENERGY, null);
                        EnergyUtils.transferPower(this, energyItem, 1000, false);
                    }
                }
            }
        }
    }

    /*******************************************************************************************************************
     * EnergyHandler                                                                                                   *
     *******************************************************************************************************************/

    /**
     * Used to define the default size of this energy bank
     *
     * @return The default size of the energy bank
     */
    @Override
    protected int getDefaultEnergyStorageSize() {
        return 32000;
    }

    /**
     * Is this tile an energy provider
     *
     * @return True to allow energy out
     */
    @Override
    protected boolean isProvider() {
        return true;
    }

    /**
     * Is this tile an energy reciever
     *
     * @return True to accept energy
     */
    @Override
    protected boolean isReceiver() {
        return true;
    }
}
