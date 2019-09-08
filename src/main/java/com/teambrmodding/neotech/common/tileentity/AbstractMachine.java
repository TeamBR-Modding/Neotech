package com.teambrmodding.neotech.common.tileentity;

import com.teambr.nucleus.client.gui.misc.SidePicker;
import com.teambr.nucleus.common.tiles.EnergyHandler;
import com.teambrmodding.neotech.collections.EnumInputOutputMode;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This file was created for AssistedProgression
 * <p>
 * AssistedProgression is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author James Rogers - Dyonovan
 * @since 9/4/2019
 */
public class AbstractMachine extends EnergyHandler implements IItemHandlerModifiable, IFluidHandler {

    // Current Redstone Mode
    public int redstone    = 0;
    // Working variable
    public boolean working = false;

    // NBT Tags
    public static final String REDSTONE_NBT      = "RedstoneMode";
    public static final String UPDATE_ENERGY_NBT = "UpdateEnergy";
    public static final String SIDE_MODE_NBT     = "Side: ";

    // Syncing Variables
    public static final int REDSTONE_FIELD_ID = 0;
    public static final int IO_FIELD_ID       = 1;
    public static final int UPDATE_CLIENT_ID  = 2;

    // Holds valid modes for this instance
    public List<EnumInputOutputMode> validModes = new ArrayList<>();
    // Holds the current mode for each side
    public HashMap<E, EnumInputOutputMode> sideModes = new HashMap<>();

    /**
     * Main Constructor
     *
     * @param type
     */
    public AbstractMachine(TileEntityType<?> type) {
        super(type);
    }

    @Override
    protected int getDefaultEnergyStorageSize() {
        return 0;
    }

    @Override
    protected boolean isProvider() {
        return false;
    }

    @Override
    protected boolean isReceiver() {
        return false;
    }

    @Override
    public int getTanks() {
        return 0;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return null;
    }

    @Override
    public int getTankCapacity(int tank) {
        return 0;
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return false;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return 0;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return null;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return null;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {

    }

    @Override
    public int getSlots() {
        return 0;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return null;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return null;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return null;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 0;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return false;
    }
}
