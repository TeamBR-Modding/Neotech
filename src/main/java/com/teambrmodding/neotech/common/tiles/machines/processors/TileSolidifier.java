package com.teambrmodding.neotech.common.tiles.machines.processors;

import com.teambr.bookshelf.client.gui.GuiColor;
import com.teambr.bookshelf.client.gui.GuiTextFormat;
import com.teambr.bookshelf.util.ClientUtils;
import com.teambr.bookshelf.util.InventoryUtils;
import com.teambrmodding.neotech.client.gui.machines.processors.GuiSolidifier;
import com.teambrmodding.neotech.collections.EnumInputOutputMode;
import com.teambrmodding.neotech.common.container.machines.processors.ContainerSolidifier;
import com.teambrmodding.neotech.common.tiles.MachineProcessor;
import com.teambrmodding.neotech.common.tiles.traits.IUpgradeItem;
import com.teambrmodding.neotech.managers.MetalManager;
import com.teambrmodding.neotech.managers.RecipeManager;
import com.teambrmodding.neotech.registries.AbstractRecipe;
import com.teambrmodding.neotech.registries.SolidifierRecipeHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/16/2017
 */
public class TileSolidifier extends MachineProcessor<FluidStack, ItemStack> {
    // Class Variables
    public static final int OUTPUT_SLOT = 0;
    public static final int TANK        = 0;

    public static final int BASE_ENERGY_TICK  = 300;

    public static final int UPDATE_MODE_NBT = 4;

    // The current mode for the machine
    public SolidifierRecipeHandler.SolidifierMode currentMode = SolidifierRecipeHandler.SolidifierMode.BLOCK_MODE;

    /**
     * The initial size of the inventory
     */
    @Override
    public int getInitialSize() {
        return 1;
    }

    /**
     * Add all modes you want, in order, here
     */
    @Override
    public void addValidModes() {
        validModes.add(EnumInputOutputMode.INPUT_ALL);
        validModes.add(EnumInputOutputMode.OUTPUT_ALL);
        validModes.add(EnumInputOutputMode.ALL_MODES);
    }

    /**
     * Return the list of upgrades by their id that are allowed in this machine
     *
     * @return A list of valid upgrades
     */
    @Override
    public List<String> getAcceptableUpgrades() {
        List<String> list = new ArrayList<>();
        list.add(IUpgradeItem.CPU_SINGLE_CORE);
        list.add(IUpgradeItem.CPU_DUAL_CORE);
        list.add(IUpgradeItem.CPU_QUAD_CORE);
        list.add(IUpgradeItem.CPU_OCT_CORE);
        list.add(IUpgradeItem.MEMORY_DDR1);
        list.add(IUpgradeItem.MEMORY_DDR2);
        list.add(IUpgradeItem.MEMORY_DDR3);
        list.add(IUpgradeItem.MEMORY_DDR4);
        list.add(IUpgradeItem.PSU_250W);
        list.add(IUpgradeItem.PSU_500W);
        list.add(IUpgradeItem.PSU_750W);
        list.add(IUpgradeItem.PSU_960W);
        list.add(IUpgradeItem.TRANSFORMER);
        list.add(IUpgradeItem.REDSTONE_CIRCUIT);
        list.add(IUpgradeItem.NETWORK_CARD);
        return list;
    }

    /**
     * Used to get how much energy to drain per tick, you should check for upgrades at this point
     *
     * @return How much energy to drain per tick
     */
    @Override
    public int getEnergyCostPerTick() {
        return BASE_ENERGY_TICK * getModifierForCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.MEMORY) +
                ((getModifierForCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.CPU) - 1) * 62);
    }

    /**
     * Used to get how long it takes to cook things, you should check for upgrades at this point
     *
     * @return The time it takes in ticks to cook the current item
     */
    @Override
    public int getCookTime() {
        return 1000 - (62 * (getModifierForCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.CPU) - 1));
    }

    /**
     * Used to tell if this tile is able to process
     *
     * @return True if you are able to process
     */
    @Override
    public boolean canProcess() {
        if(energyStorage.getEnergyStored() > getEnergyCostPerTick() && tanks[TANK].getFluid() != null) {
            if(getStackInSlot(OUTPUT_SLOT) == null) {
                SolidifierRecipeHandler.SolidifierRecipe recipe =
                        ((SolidifierRecipeHandler)RecipeManager.getHandler(RecipeManager.RecipeType.SOLIDIFIER)).getRecipe(Pair.of(currentMode, tanks[TANK].getFluid()));
                if(recipe != null && tanks[TANK].getFluidAmount() >= AbstractRecipe.getFluidStackFromString(recipe.inputFluidStack).amount)
                    return true;
            } else {
                SolidifierRecipeHandler.SolidifierRecipe recipe =
                        ((SolidifierRecipeHandler)RecipeManager.getHandler(RecipeManager.RecipeType.SOLIDIFIER)).getRecipe(Pair.of(currentMode, tanks[TANK].getFluid()));
                if(recipe != null) {
                    ItemStack output = AbstractRecipe.getItemStackFromString(recipe.outputItemStack);
                    if (tanks[TANK].getFluidAmount() >= AbstractRecipe.getFluidStackFromString(recipe.inputFluidStack).amount) {
                        ItemStack ourStack = getStackInSlot(OUTPUT_SLOT);
                        if (output.getItem() == ourStack.getItem() && output.getItemDamage() == ourStack.getItemDamage() &&
                                ourStack.stackSize + output.stackSize <= ourStack.getMaxStackSize())
                            return true;
                    }
                }
                return false;
            }
        }
        failCoolDown = 40;
        return false;
    }

    /**
     * Used to actually cook the item
     */
    @Override
    protected void cook() {
        cookTime++;
    }

    /**
     * Called when the tile has completed the cook process
     */
    @Override
    protected void completeCook() {
        for(int x = 0; x < getModifierForCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.MEMORY); x++) {
            if(canProcess()) {
                if(tanks[TANK].getFluid() != null) {
                    SolidifierRecipeHandler.SolidifierRecipe recipe =
                            ((SolidifierRecipeHandler)RecipeManager.getHandler(RecipeManager.RecipeType.SOLIDIFIER)).getRecipe(Pair.of(currentMode, tanks[TANK].getFluid()));
                    ItemStack output = AbstractRecipe.getItemStackFromString(recipe.outputItemStack);
                    FluidStack drainedStack = drain(AbstractRecipe.getFluidStackFromString(recipe.inputFluidStack).amount, false);
                    if(drainedStack != null && drainedStack.amount == AbstractRecipe.getFluidStackFromString(recipe.inputFluidStack).amount &&
                            output != null && (getStackInSlot(OUTPUT_SLOT) == null ||
                            getStackInSlot(OUTPUT_SLOT).stackSize + output.stackSize
                                    <= getStackInSlot(OUTPUT_SLOT).getMaxStackSize())) {
                        // Drain
                        drain(AbstractRecipe.getFluidStackFromString(recipe.inputFluidStack).amount, true);
                        if(getStackInSlot(OUTPUT_SLOT) != null)
                            getStackInSlot(OUTPUT_SLOT).stackSize += output.stackSize;
                        else
                            setStackInSlot(OUTPUT_SLOT, output);
                    }
                }
            } else
                break;
        }
        markForUpdate(6);
    }

    /**
     * Get the output of the recipe
     *
     * @param input The input
     * @return The output
     */
    @Override
    public ItemStack getOutput(FluidStack input) {
        return ((SolidifierRecipeHandler)RecipeManager.getHandler(RecipeManager.RecipeType.SOLIDIFIER)).getOutput(Pair.of(currentMode, input));
    }

    /**
     * Get the output of the recipe (used in insert options)
     *
     * @param input The input
     * @return The output
     */
    @Override
    public ItemStack getOutputForStack(ItemStack input) {
        return null;
    }

    /*******************************************************************************************************************
     * Tile Methods                                                                                                    *
     *******************************************************************************************************************/

    /**
     * This will try to take things from other inventories and put it into ours
     */
    @Override
    public void tryInput() {
        for(EnumFacing dir : EnumFacing.values()) {
            if(canInputFromSide(dir, true)) {
                if(worldObj.getTileEntity(pos.offset(dir)) != null &&
                        worldObj.getTileEntity(pos.offset(dir)).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite())) {
                    IFluidHandler otherTank = worldObj.getTileEntity(pos.offset(dir)).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite());

                    // Try and match us
                    if(tanks[TANK].getFluid() != null && otherTank.drain(tanks[TANK].getFluid(), false) != null) {
                        int amount = fill(otherTank.drain(tanks[TANK].getFluid(), false), false);
                        if(amount > 0) {
                            fill(otherTank.drain(amount, true), true);
                            markForUpdate(6);
                        }
                    }

                    // Can we accept
                    else if(tanks[TANK].getFluid() == null && otherTank.drain(1000, false) != null) {
                        int amount = fill(otherTank.drain(1000, false), false);
                        if(amount > 0) {
                            fill(otherTank.drain(amount, true), true);
                            markForUpdate(6);
                        }
                    }
                }
            }
        }
    }

    /**
     * This will try to take things from our inventory and try to place them in others
     */
    @Override
    public void tryOutput() {
        for(EnumFacing dir : EnumFacing.values()) {
            if(canOutputFromSide(dir, true))
                InventoryUtils.moveItemInto(this, OUTPUT_SLOT, worldObj.getTileEntity(pos.offset(dir)), -1,
                        64, dir.getOpposite(), true, false, true);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("ProcessMode", currentMode.ordinal());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        currentMode = SolidifierRecipeHandler.SolidifierMode.values()[compound.getInteger("ProcessMode")];
    }

    /**
     * Used to expose the item handler capability, in child classes, manage input and output based on this
     *
     * @param dir The direction
     * @return The item handler
     */
    @Nullable
    @Override
    protected IItemHandler getItemHandlerCapability(EnumFacing dir) {
        return getModeForSide(dir) == EnumInputOutputMode.DEFAULT ||
                canOutputFromSide(dir, true) ?
                super.getItemHandlerCapability(dir) : null;
    }

    /**
     * Used to get the fluid handler for exposing
     *
     * @param dir The direction
     * @return The fluid handler
     */
    @Nullable
    @Override
    protected IFluidHandler getFluidHandlerCapability(EnumFacing dir) {
        return getModeForSide(dir) == EnumInputOutputMode.DEFAULT ||
                canInputFromSide(dir, true) ?
                super.getFluidHandlerCapability(dir) : null;
    }

    /**
     * Used to set the variable for this tile, the Syncable will use this when you send a value to the server
     *
     * @param id    The ID of the variable to send
     * @param value The new value to set to (you can use this however you want, eg using the ordinal of EnumFacing)
     */
    @Override
    public void setVariable(int id, double value) {
        if(id == UPDATE_MODE_NBT)
            toggleMode();
        super.setVariable(id, value);
    }

    /**
     * Toggles what mode we are in
     */
    public void toggleMode() {
        currentMode = currentMode.getNextMode();
    }

    /*******************************************************************************************************************
     * Inventory Methods                                                                                               *
     *******************************************************************************************************************/

    /**
     * Used to get what slots are allowed to be output
     *
     * @param mode
     * @return The slots to output from
     */
    @Override
    public int[] getOutputSlots(EnumInputOutputMode mode) {
        return new int[] { OUTPUT_SLOT };
    }

    /**
     * Used to get what slots are allowed to be input
     *
     * @param mode
     * @return The slots to input from
     */
    @Override
    public int[] getInputSlots(EnumInputOutputMode mode) {
        return new int[0];
    }

    /*******************************************************************************************************************
     * FluidHandler Methods                                                                                            *
     *******************************************************************************************************************/

    /**
     * Method to define if this tile is a fluid handler
     *
     * @return False by default, override in child classes to enable fluid handling
     */
    @Override
    public boolean isFluidHandler() {
        return true;
    }

    /**
     * Used to set up the tanks needed. You can insert any number of tanks
     *
     * MUST OVERRIDE IN CHILD CLASSES IF isFluidHandler RETURNS TRUE
     */
    @Override
    protected void setupTanks() {
        tanks = new FluidTank[1];
        tanks[0] = new FluidTank(10 * MetalManager.BLOCK_MB);
    }

    /**
     * Which tanks can input
     *
     * MUST OVERRIDE IN CHILD CLASSES IF isFluidHandler RETURNS TRUE
     *
     * @return An array with the indexes of the input tanks
     */
    @Override
    protected int[] getInputTanks() {
        return new int[] { TANK };
    }

    /**
     * Which tanks can output
     *
     * MUST OVERRIDE IN CHILD CLASSES IF isFluidHandler RETURNS TRUE
     *
     * @return An array with the indexes of the output tanks
     */
    @Override
    protected int[] getOutputTanks() {
        return new int[] { TANK };
    }

    /**
     * Returns true if the given fluid can be inserted
     *
     * More formally, this should return true if fluid is able to enter
     *
     * @param fluid
     */
    @Override
    protected boolean canFill(Fluid fluid) {
        return fluid != null &&
                ((tanks[TANK].getFluid() == null &&
                        RecipeManager.getHandler(RecipeManager.RecipeType.SOLIDIFIER)
                                .isValidInput(Pair.of(currentMode, new FluidStack(fluid, currentMode.getRequiredAmount())))) ||
                        (tanks[TANK].getFluid() != null && fluid == tanks[TANK].getFluid().getFluid()));
    }

    /*******************************************************************************************************************
     * Misc Methods                                                                                                    *
     *******************************************************************************************************************/

    /**
     * Return the container for this tile
     *
     * @param id       Id, probably not needed but could be used for multiple guis
     * @param player   The player that is opening the gui
     * @param worldObj The worldObj
     * @param x        X Pos
     * @param y        Y Pos
     * @param z        Z Pos
     * @return The container to open
     */
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World worldObj, int x, int y, int z) {
        return new ContainerSolidifier(player.inventory, this);
    }

    /**
     * Return the gui for this tile
     *
     * @param id       Id, probably not needed but could be used for multiple guis
     * @param player   The player that is opening the gui
     * @param worldObj The worldObj
     * @param x        X Pos
     * @param y        Y Pos
     * @param z        Z Pos
     * @return The gui to open
     */
    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World worldObj, int x, int y, int z) {
        return new GuiSolidifier(player, this);
    }

    /**
     * Used to get the description to display on the tab
     *
     * @return The long string with the description
     */
    @Override
    public String getDescription() {
        return "" +
                GuiColor.GREEN + GuiTextFormat.BOLD + GuiTextFormat.UNDERLINE + ClientUtils.translate("neotech.text.stats") + ":\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.energyUsage") + ":\n" +
                GuiColor.WHITE + "  " + ClientUtils.formatNumber(getEnergyCostPerTick()) + " RF/tick\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.processTime") + ":\n" +
                GuiColor.WHITE + "  " + getCookTime() + " ticks\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.operations") + ":\n" +
                GuiColor.WHITE + "  " + getModifierForCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.MEMORY) + "\n\n" +
                GuiColor.WHITE + ClientUtils.translate("neotech.electricSolidifier.desc") + "\n\n" +
                GuiColor.GREEN + GuiTextFormat.BOLD + GuiTextFormat.UNDERLINE + ClientUtils.translate("neotech.text.upgrade") + ":\n" + GuiTextFormat.RESET +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.processors") + ":\n" +
                GuiColor.WHITE + ClientUtils.translate("neotech.electricFurnace.processorUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.memory") + ":\n" +
                GuiColor.WHITE + ClientUtils.translate("neotech.electricFurnace.memoryUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.psu") + ":\n" +
                GuiColor.WHITE + ClientUtils.translate("neotech.electricFurnace.psuUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.control") + ":\n" +
                GuiColor.WHITE + ClientUtils.translate("neotech.electricFurnace.controlUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.network") + ":\n" +
                GuiColor.WHITE +  ClientUtils.translate("neotech.electricFurnace.networkUpgrade.desc");
    }

    /**
     * Used to output the redstone single from this structure
     *
     * Use a range from 0 - 16.
     *
     * 0 Usually means that there is nothing in the tile, so take that for lowest level. Like the generator has no energy while
     * 16 is usually the flip side of that. Output 16 when it is totally full and not less
     *
     * @return int range 0 - 16
     */
    @Override
    public int getRedstoneOutput() {
        return isActive() ? 16 : 0;
    }

    /**
     * Used to get what particles to spawn. This will be called when the tile is active
     *
     * @param xPos
     * @param yPos
     * @param zPos
     */
    @Override
    public void spawnActiveParticles(double xPos, double yPos, double zPos) {
        worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, xPos, yPos, zPos, 0, 0, 0);
        worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, xPos, yPos, zPos, 0, 0, 0);
        worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, xPos, yPos, zPos, 0, 0, 0);
    }
}
