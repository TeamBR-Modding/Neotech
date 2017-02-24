package com.teambrmodding.neotech.common.tiles.machines.processors;

import com.teambr.bookshelf.client.gui.GuiColor;
import com.teambr.bookshelf.client.gui.GuiTextFormat;
import com.teambr.bookshelf.util.ClientUtils;
import com.teambrmodding.neotech.client.gui.machines.processors.GuiCentrifuge;
import com.teambrmodding.neotech.collections.EnumInputOutputMode;
import com.teambrmodding.neotech.common.container.machines.processors.ContainerCentrifuge;
import com.teambrmodding.neotech.common.tiles.MachineProcessor;
import com.teambrmodding.neotech.common.tiles.traits.IUpgradeItem;
import com.teambrmodding.neotech.managers.MetalManager;
import com.teambrmodding.neotech.managers.RecipeManager;
import com.teambrmodding.neotech.registries.recipes.AbstractRecipe;
import com.teambrmodding.neotech.registries.CentrifugeRecipeHandler;
import com.teambrmodding.neotech.registries.recipes.CentrifugeRecipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
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
public class TileCentrifuge extends MachineProcessor<FluidStack, Pair<FluidStack, FluidStack>> {
    // Class Variables
    public static final int BASE_ENERGY_TICK = 300;

    public static final int INPUT_TANK    = 0;
    public static final int OUTPUT_TANK_1 = 1;
    public static final int OUTPUT_TANK_2 = 2;

    /**
     * The initial size of the inventory
     */
    @Override
    public int getInitialSize() {
        return 0;
    }

    /**
     * Add all modes you want, in order, here
     */
    @Override
    public void addValidModes() {
        validModes.add(EnumInputOutputMode.INPUT_ALL);
        validModes.add(EnumInputOutputMode.OUTPUT_ALL);
        validModes.add(EnumInputOutputMode.OUTPUT_PRIMARY);
        validModes.add(EnumInputOutputMode.OUTPUT_SECONDARY);
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
        if(energyStorage.getEnergyStored() > getEnergyCostPerTick() && tanks[INPUT_TANK].getFluid() != null) {
            Pair<FluidStack, FluidStack> recipeOutput =
                    ((CentrifugeRecipeHandler) RecipeManager.getHandler(RecipeManager.RecipeType.CENTRIFUGE))
                            .getOutput(tanks[INPUT_TANK].getFluid());
            if(recipeOutput != null) {
                if(tanks[OUTPUT_TANK_1].getFluid() == null && tanks[OUTPUT_TANK_2].getFluid() == null)
                    return true;
                else if(tanks[OUTPUT_TANK_1].getFluid() != null && tanks[OUTPUT_TANK_2].getFluid() != null) {
                    FluidStack left  = recipeOutput.getLeft();
                    FluidStack right = recipeOutput.getRight();

                    boolean configurationOne =
                            left.getFluid().getName().equalsIgnoreCase(tanks[OUTPUT_TANK_1].getFluid().getFluid().getName()) &&
                                    right.getFluid().getName().equalsIgnoreCase(tanks[OUTPUT_TANK_2].getFluid().getFluid().getName()) &&
                                    left.amount + tanks[OUTPUT_TANK_1].getFluidAmount() <= tanks[OUTPUT_TANK_1].getCapacity() &&
                                    right.amount + tanks[OUTPUT_TANK_2].getFluidAmount() <= tanks[OUTPUT_TANK_2].getCapacity();
                    boolean configurationTwo =
                            left.getFluid().getName().equalsIgnoreCase(tanks[OUTPUT_TANK_2].getFluid().getFluid().getName()) &&
                                    right.getFluid().getName().equalsIgnoreCase(tanks[OUTPUT_TANK_1].getFluid().getFluid().getName()) &&
                                    left.amount + tanks[OUTPUT_TANK_2].getFluidAmount() <= tanks[OUTPUT_TANK_2].getCapacity() &&
                                    right.amount + tanks[OUTPUT_TANK_1].getFluidAmount() <= tanks[OUTPUT_TANK_1].getCapacity();

                    return configurationOne || configurationTwo;
                }
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
        for (int x = 0; x < getModifierForCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.MEMORY); x++) {
            if(canProcess()) {
                CentrifugeRecipe recipe =
                        ((CentrifugeRecipeHandler)RecipeManager.getHandler(RecipeManager.RecipeType.CENTRIFUGE)).getRecipe(tanks[INPUT_TANK].getFluid());
                if(recipe != null) {
                    FluidStack drainedStack = tanks[INPUT_TANK].drain(AbstractRecipe.getFluidStackFromString(recipe.fluidStackInput).amount, false);
                    if(drainedStack != null && drainedStack.amount > 0) {
                        tanks[INPUT_TANK].drain(drainedStack.amount, true);

                        FluidStack fluidOutputOne = AbstractRecipe.getFluidStackFromString(recipe.fluidStackOutputOne);
                        FluidStack fluidOutputTwo = AbstractRecipe.getFluidStackFromString(recipe.fluidStackOutputTwo);

                        if(tanks[OUTPUT_TANK_1].getFluid() == null ||
                                fluidOutputOne.getFluid().getName().equalsIgnoreCase(tanks[OUTPUT_TANK_1].getFluid().getFluid().getName())) {
                            tanks[OUTPUT_TANK_1].fill(fluidOutputOne, true);
                            tanks[OUTPUT_TANK_2].fill(fluidOutputTwo, true);
                        } else {
                            tanks[OUTPUT_TANK_1].fill(fluidOutputTwo, true);
                            tanks[OUTPUT_TANK_2].fill(fluidOutputOne, true);
                        }
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
    public Pair<FluidStack, FluidStack> getOutput(FluidStack input) {
        return ((CentrifugeRecipeHandler)RecipeManager.getHandler(RecipeManager.RecipeType.CENTRIFUGE)).getOutput(input);
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
                    IFluidHandler otherTank =
                            worldObj.getTileEntity(pos.offset(dir)).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite());

                    // Try match existing
                    if(tanks[INPUT_TANK].getFluid() != null && otherTank.drain(tanks[INPUT_TANK].getFluid(), false) != null) {
                        int amount = fill(otherTank.drain(1000, false), false);
                        if(amount > 0) {
                            fill(otherTank.drain(amount, true), true);
                            markForUpdate(6);
                        }
                    }

                    // Check if we can accept
                    else if(tanks[INPUT_TANK].getFluid() == null && otherTank.drain(1000, false) != null) {
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
            if(worldObj.getTileEntity(pos.offset(dir)) != null &&
                    worldObj.getTileEntity(pos.offset(dir)).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite())) {
                IFluidHandler otherTank =
                        worldObj.getTileEntity(pos.offset(dir)).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite());

                if(canOutputFromSide(dir, true)) {
                    if(tanks[OUTPUT_TANK_1].getFluid() != null && otherTank.fill(tanks[OUTPUT_TANK_1].getFluid(), false) > 0) {
                        FluidStack drainedStack = tanks[OUTPUT_TANK_1].drain(otherTank.fill(tanks[OUTPUT_TANK_1].getFluid(), false), false);
                        if(drainedStack != null && drainedStack.amount > 0) {
                            tanks[OUTPUT_TANK_1].drain(otherTank.fill(drainedStack, true), true);
                            markForUpdate(6);
                        }
                    }
                }

                if(canOutputFromSide(dir, false)) {
                    if(tanks[OUTPUT_TANK_2].getFluid() != null && otherTank.fill(tanks[OUTPUT_TANK_2].getFluid(), false) > 0) {
                        FluidStack drainedStack = tanks[OUTPUT_TANK_2].drain(otherTank.fill(tanks[OUTPUT_TANK_2].getFluid(), false), false);
                        if(drainedStack != null && drainedStack.amount > 0) {
                            tanks[OUTPUT_TANK_2].drain(otherTank.fill(drainedStack, true), true);
                            markForUpdate(6);
                        }
                    }
                }
            }
        }
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
        return null;
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
                canInputFromSide(dir, true) || canOutputFromSide(dir, true) || canOutputFromSide(dir, false) ?
                super.getFluidHandlerCapability(dir) : null;
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
        return new int[0];
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
        tanks = new FluidTank[3];
        tanks[0] = new FluidTank(10 * MetalManager.BLOCK_MB);
        tanks[1] = new FluidTank(10 * MetalManager.BLOCK_MB);
        tanks[2] = new FluidTank(10 * MetalManager.BLOCK_MB);
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
        return new int[] { INPUT_TANK };
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
        return new int[] { OUTPUT_TANK_1, OUTPUT_TANK_2 };
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
        if(fluid == null)
            return false;
        if(tanks[INPUT_TANK].getFluid() == null)
            return RecipeManager.getHandler(RecipeManager.RecipeType.CENTRIFUGE).isValidInput(new FluidStack(fluid, 1000));
        else {
            return fluid == tanks[INPUT_TANK].getFluid().getFluid();
        }
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
        return new ContainerCentrifuge(player.inventory, this);
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
        return new GuiCentrifuge(player, this);
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
                GuiColor.WHITE + ClientUtils.translate("neotech.centrifuge.desc") + "\n\n" +
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
     */
    @Override
    public void spawnActiveParticles(double x, double y, double z) {
        worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0, 0, 0);
        worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0, 0, 0);
        worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0, 0, 0);
    }
}
