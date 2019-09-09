package com.teambrmodding.neotech.common.tileentity;

import com.teambr.nucleus.common.tiles.InventorySided;
import com.teambr.nucleus.common.tiles.Syncable;
import com.teambrmodding.neotech.machine.MachineOS;
import com.teambrmodding.neotech.machine.program.AbstractProgram;
import com.teambrmodding.neotech.machine.program.FurnaceProgram;
import com.teambrmodding.neotech.managers.TileEntityManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.Arrays;

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
public class MachineTile extends Syncable implements IItemHandlerModifiable {

    // NBT references, to prevent typo issues
    private static final String PROGRAM_NAME_NBT = "program_name";
    private static final String PROGRAM_NBT      = "program";

    // The instance of this OS
    private MachineOS operatingSystem;

    /**
     * Creates the machine tile
     */
    public MachineTile() {
        super(TileEntityManager.machine);
        operatingSystem = new MachineOS(this);
        operatingSystem.installProgram(new FurnaceProgram());
    }

    /*******************************************************************************************************************
     * TileEntity                                                                                                      *
     *******************************************************************************************************************/

    /**
     * Called only on the server side tick. Override for server side operations
     */
    @Override
    protected void onServerTick() {
        operatingSystem.onCpuCycle();
    }

    /**
     * Read the data from the compound
     * @param compound Saved data
     */
    @SuppressWarnings("ConstantConditions")
    @Override
    public void read(@Nonnull CompoundNBT compound) {
        super.read(compound);

        // Find the list of programs that are installed
        ListNBT programsList = compound.getList(PROGRAM_NBT, 9);

        // Cycle programs
        if(!programsList.isEmpty()) {
            for(INBT nbt : programsList) {
                // Usable data for program
                if(nbt instanceof CompoundNBT) {
                    CompoundNBT programNBT = (CompoundNBT) nbt;
                    if(MachineOS.PROGRAM_REGISTRY.containsKey(programNBT.getString(PROGRAM_NAME_NBT))) {
                        // Is already installed? Called when already made but needs to sync
                        if(operatingSystem.isProgramInstalled(programNBT.getString(PROGRAM_NAME_NBT)))
                            operatingSystem.getInstalledProgram(programNBT.getString(PROGRAM_NAME_NBT)).read(programNBT);
                        else
                            // Try to create a new program and load the data from this tag, we are a new machine loading
                            try {
                                operatingSystem
                                        .installProgram(MachineOS.PROGRAM_REGISTRY.get(programNBT.getString(PROGRAM_NAME_NBT))
                                                .newInstance().createProgramFromData(programNBT));
                            } catch (InstantiationException | IllegalAccessException e) {
                                e.printStackTrace(); // Unable to create
                            }
                    }
                }
            }
        }
    }

    /**
     * Write the current state to the compound
     * @param compound NBT
     * @return The finished data
     */
    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT compound) {
        super.write(compound);

        // Write the list
        ListNBT programList = new ListNBT();
        int listId = 0;

        // Write programs to compound
        for(AbstractProgram program : operatingSystem.getInstalledPrograms()) {
            CompoundNBT programCompound = new CompoundNBT();
            programCompound.putString(PROGRAM_NAME_NBT, program.getIdentifier());
            program.write(programCompound);
            programList.add(listId, programCompound);
            listId += 1;
        }

        compound.put(PROGRAM_NBT, programList);
        return compound;
    }

    // Wrapper for capabilities
    private LazyOptional<?> lazyHandler = LazyOptional.of(() -> this);

    /**
     * Used to access the capability
     *
     * @param capability The capability
     * @param facing     Which face
     * @param <T>        The object to case
     * @return Us as INSTANCE of T
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (LazyOptional<T>) lazyHandler;
        else
            return super.getCapability(capability, facing);
    }

    /*******************************************************************************************************************
     * Syncable                                                                                                        *
     *******************************************************************************************************************/

    /**
     * Used to set the value of a field
     *
     * @param id    The field id
     * @param value The value of the field
     */
    @Override
    public void setVariable(int id, double value) {
        operatingSystem.setVariable(id, value);
    }

    /**
     * Used to get the field on the server, this will fetch the server value and overwrite the current
     *
     * @param id The field id
     * @return The value on the server, now set to ourselves
     */
    @Override
    public Double getVariable(int id) {
        return operatingSystem.getVariable(id);
    }

    /*******************************************************************************************************************
     * IItemHandlerModifiable                                                                                          *
     *******************************************************************************************************************/

    /**
     * Overrides the stack in the given slot. This method is used by the
     * standard Forge helper methods and classes. It is not intended for
     * general use by other mods, and the handler may throw an error if it
     * is called unexpectedly.
     *
     * @param slot  Slot to modify
     * @param stack ItemStack to set slot to (may be empty).
     * @throws RuntimeException if the handler is called in a way that the handler
     *                          was not expecting.
     **/
    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {

    }

    /*******************************************************************************************************************
     * IItemHandler                                                                                                    *
     *******************************************************************************************************************/

    /**
     * Returns the number of slots available
     *
     * @return The number of slots available
     **/
    @Override
    public int getSlots() {
        return 0;
    }

    /**
     * Returns the ItemStack in a given slot.
     * <p>
     * The result's stack size may be greater than the itemstack's max size.
     * <p>
     * If the result is empty, then the slot is empty.
     *
     * <p>
     * <strong>IMPORTANT:</strong> This ItemStack <em>MUST NOT</em> be modified. This method is not for
     * altering an inventory's contents. Any implementers who are able to detect
     * modification through this method should throw an exception.
     * </p>
     * <p>
     * <strong><em>SERIOUSLY: DO NOT MODIFY THE RETURNED ITEMSTACK</em></strong>
     * </p>
     *
     * @param slot Slot to query
     * @return ItemStack in given slot. Empty Itemstack if the slot is empty.
     **/
    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return null;
    }

    /**
     * <p>
     * Inserts an ItemStack into the given slot and return the remainder.
     * The ItemStack <em>should not</em> be modified in this function!
     * </p>
     * Note: This behaviour is subtly different from {IFluidHandler#fill(FluidStack, boolean)}
     *
     * @param slot     Slot to insert into.
     * @param stack    ItemStack to insert. This must not be modified by the item handler.
     * @param simulate If true, the insertion is only simulated
     * @return The remaining ItemStack that was not inserted (if the entire stack is accepted, then return an empty ItemStack).
     * May be the same as the input ItemStack if unchanged, otherwise a new ItemStack.
     * The returned ItemStack can be safely modified after.
     **/
    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return null;
    }

    /**
     * Extracts an ItemStack from the given slot.
     * <p>
     * The returned value must be empty if nothing is extracted,
     * otherwise its stack size must be less than or equal to {@code amount} and {@link ItemStack#getMaxStackSize()}.
     * </p>
     *
     * @param slot     Slot to extract from.
     * @param amount   Amount to extract (may be greater than the current stack's max limit)
     * @param simulate If true, the extraction is only simulated
     * @return ItemStack extracted from the slot, must be empty if nothing can be extracted.
     * The returned ItemStack can be safely modified after, so item handlers should return a new or copied stack.
     **/
    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return null;
    }

    /**
     * Retrieves the maximum stack size allowed to exist in the given slot.
     *
     * @param slot Slot to query.
     * @return The maximum stack size allowed in the slot.
     */
    @Override
    public int getSlotLimit(int slot) {
        return 0;
    }

    /**
     * <p>
     * This function re-implements the vanilla function {isItemValidForSlot(int, ItemStack)}.
     * It should be used instead of simulated insertions in cases where the contents and state of the inventory are
     * irrelevant, mainly for the purpose of automation and logic (for instance, testing if a minecart can wait
     * to deposit its items into a full inventory, or if the items in the minecart can never be placed into the
     * inventory and should move on).
     * </p>
     * <ul>
     * <li>isItemValid is false when insertion of the item is never valid.</li>
     * <li>When isItemValid is true, no assumptions can be made and insertion must be simulated case-by-case.</li>
     * <li>The actual items in the inventory, its fullness, or any other state are <strong>not</strong> considered by isItemValid.</li>
     * </ul>
     *
     * @param slot  Slot to query for validity
     * @param stack Stack to test with for validity
     * @return true if the slot can insert the ItemStack, not considering the current state of the inventory.
     * false if the slot can never insert the ItemStack in any situation.
     */
    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return false;
    }
}
