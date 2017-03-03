package com.teambrmodding.neotech.common.tiles.machines.processors;

import com.teambr.bookshelf.client.gui.GuiColor;
import com.teambr.bookshelf.client.gui.GuiTextFormat;
import com.teambr.bookshelf.util.ClientUtils;
import com.teambrmodding.neotech.client.gui.machines.processors.GuiAlloyer;
import com.teambrmodding.neotech.collections.EnumInputOutputMode;
import com.teambrmodding.neotech.common.container.machines.processors.ContainerAlloyer;
import com.teambrmodding.neotech.common.tiles.MachineProcessor;
import com.teambrmodding.neotech.common.tiles.traits.IUpgradeItem;
import com.teambrmodding.neotech.managers.MetalManager;
import com.teambrmodding.neotech.managers.RecipeManager;
import com.teambrmodding.neotech.registries.AlloyerRecipeHandler;
import com.teambrmodding.neotech.registries.recipes.AbstractRecipe;
import com.teambrmodding.neotech.registries.recipes.AlloyerRecipe;
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
public class TileAlloyer extends MachineProcessor<Pair<FluidStack, FluidStack>, FluidStack> {
    // Class Variables
    public static final int BASE_ENERGY_TICK = 300;
    public static final int INPUT_TANK_1 = 0;
    public static final int INPUT_TANK_2 = 1;
    public static final int OUTPUT_TANK  = 2;

    /**
     * The initial size of the inventory
     */
    @Override
    public int getInventorySize() {
        return 0;
    }

    /**
     * Add all modes you want, in order, here
     */
    @Override
    public void addValidModes() {
        validModes.add(EnumInputOutputMode.INPUT_ALL);
        validModes.add(EnumInputOutputMode.INPUT_PRIMARY);
        validModes.add(EnumInputOutputMode.INPUT_SECONDARY);
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
        return Math.max(500 - (((500 / 16) * getModifierForCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.CPU) - 1)), 5);
    }

    /**
     * Used to tell if this tile is able to process
     *
     * @return True if you are able to process
     */
    @Override
    public boolean canProcess() {
        if(energyStorage.getEnergyStored() >= getEnergyCostPerTick()) {
            return tanks[INPUT_TANK_1].getFluid() != null && tanks[INPUT_TANK_2].getFluid() != null && // Has inputs
                    RecipeManager.getHandler(RecipeManager.RecipeType.ALLOYER)
                            .isValidInput(Pair.of(tanks[INPUT_TANK_1].getFluid(), tanks[INPUT_TANK_2].getFluid())) && // Is Recipe
                    (tanks[OUTPUT_TANK].getFluid() == null || // Has nothing in output
                    ((AlloyerRecipeHandler) RecipeManager.getHandler(RecipeManager.RecipeType.ALLOYER))
                            .getOutput(Pair.of(tanks[INPUT_TANK_1].getFluid(), tanks[INPUT_TANK_2].getFluid())).amount +
                            tanks[OUTPUT_TANK].getFluidAmount() <= tanks[OUTPUT_TANK].getCapacity()); // Can fill output
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
                AlloyerRecipe recipe =
                        ((AlloyerRecipeHandler)RecipeManager.getHandler(RecipeManager.RecipeType.ALLOYER))
                                .getRecipe(Pair.of(tanks[INPUT_TANK_1].getFluid(), tanks[INPUT_TANK_2].getFluid()));
                if(recipe != null) {
                    // Drain the inputs
                    FluidStack drainInputOne;
                    FluidStack drainOutputTwo;

                    // Find Recipe Fluid Stack One
                    if(tanks[INPUT_TANK_1].getFluid().getFluid() == AbstractRecipe.getFluidStackFromString(recipe.fluidStackOne).getFluid())
                        drainInputOne = tanks[INPUT_TANK_1].drain(AbstractRecipe.getFluidStackFromString(recipe.fluidStackOne).amount, false);
                    else
                        drainInputOne = tanks[INPUT_TANK_1].drain(AbstractRecipe.getFluidStackFromString(recipe.fluidStackTwo).amount, false);

                    // Find Recipe Fluid Stack Two
                    if(tanks[INPUT_TANK_2].getFluid().getFluid() == AbstractRecipe.getFluidStackFromString(recipe.fluidStackTwo).getFluid())
                        drainOutputTwo = tanks[INPUT_TANK_2].drain(AbstractRecipe.getFluidStackFromString(recipe.fluidStackTwo).amount, false);
                    else
                        drainOutputTwo = tanks[INPUT_TANK_2].drain(AbstractRecipe.getFluidStackFromString(recipe.fluidStackOne).amount, false);

                    if(drainInputOne != null && drainOutputTwo != null && drainInputOne.amount > 0 && drainOutputTwo.amount > 0) {
                        tanks[INPUT_TANK_1].drain(drainInputOne, true);
                        tanks[INPUT_TANK_2].drain(drainOutputTwo, true);
                        tanks[OUTPUT_TANK].fill(AbstractRecipe.getFluidStackFromString(recipe.fluidStackOutput), true);
                    }
                }
            }
            else
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
    public FluidStack getOutput(Pair<FluidStack, FluidStack> input) {
        return ((AlloyerRecipeHandler)RecipeManager.getHandler(RecipeManager.RecipeType.ALLOYER)).getOutput(input);
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
            if(!isDisabled(dir)) {
                if(world.getTileEntity(pos.offset(dir)) != null &&
                        world.getTileEntity(pos.offset(dir)).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite())) {
                    IFluidHandler otherTank = world.getTileEntity(pos.offset(dir)).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite());

                    // Attempt fill left tank
                    if(canInputFromSide(dir, true)) {
                        // Try and match our fluid
                        if(tanks[INPUT_TANK_1].getFluid() != null &&
                                otherTank.drain(tanks[INPUT_TANK_1].getFluid(), false) != null) {
                            int amount = tanks[INPUT_TANK_1].fill(otherTank.drain(1000, false), false);
                            if(amount > 0) {
                                tanks[INPUT_TANK_1].fill(otherTank.drain(amount, true), true);
                                markForUpdate(6);
                            }
                        }

                        // Check if we can accept other fluid
                        else if(tanks[INPUT_TANK_1].getFluid() == null && otherTank.drain(1000, false) != null) {
                            FluidStack drainedFluid = otherTank.drain(1000, false);
                            boolean hasExistingOtherFluid = tanks[INPUT_TANK_2].getFluid() != null;
                            int amount = tanks[INPUT_TANK_1].fill(drainedFluid, false);
                            if(amount > 0 &&
                                    (!hasExistingOtherFluid || RecipeManager.getHandler(RecipeManager.RecipeType.ALLOYER)
                                            .isValidInput(Pair.of(drainedFluid, tanks[INPUT_TANK_2].getFluid())))) {
                                tanks[INPUT_TANK_1].fill(otherTank.drain(amount, true), true);
                                markForUpdate(6);
                            }
                        }
                    }

                    // Right Tank
                    if(canInputFromSide(dir, false)) {
                        // Try and match existing
                        if(tanks[INPUT_TANK_2].getFluid() != null && otherTank.drain(tanks[INPUT_TANK_2].getFluid(), false) != null) {
                            int amount = tanks[INPUT_TANK_2].fill(otherTank.drain(1000, false), false);
                            if(amount > 0) {
                                tanks[INPUT_TANK_2].fill(otherTank.drain(amount, true), true);
                                markForUpdate(6);
                            }
                        }

                        // Check if applicable
                        else if(tanks[INPUT_TANK_2].getFluid() == null && otherTank.drain(1000, false) != null) {
                            FluidStack drainedStack = otherTank.drain(1000, false);
                            boolean hasExistingInput = tanks[INPUT_TANK_1].getFluid() != null;
                            int amount = tanks[INPUT_TANK_2].fill(drainedStack, false);
                            if(amount > 0 &&
                                    (!hasExistingInput || RecipeManager.getHandler(RecipeManager.RecipeType.ALLOYER)
                                            .isValidInput(Pair.of(drainedStack, tanks[INPUT_TANK_1].getFluid())))) {
                                tanks[INPUT_TANK_2].fill(otherTank.drain(amount, true), true);
                                markForUpdate(6);
                            }
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
            if(canOutputFromSide(dir, true)) {
                if(world.getTileEntity(pos.offset(dir)) != null &&
                        world.getTileEntity(pos.offset(dir)).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite())) {
                    IFluidHandler otherTank =
                            world.getTileEntity(pos.offset(dir)).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite());

                    // If we have something, try move out
                    if(tanks[OUTPUT_TANK].getFluid() != null && otherTank.fill(tanks[OUTPUT_TANK].getFluid(), false) > 0) {
                        FluidStack amount = drain(otherTank.fill(tanks[OUTPUT_TANK].getFluid(), false), false);
                        if(amount != null) {
                            drain(otherTank.fill(amount, true), true);
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
                canInputFromSide(dir, true) || canInputFromSide(dir, false) || canOutputFromSide(dir, true) ?
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
        return new int[] { INPUT_TANK_1, INPUT_TANK_2 };
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
        return new int[] { OUTPUT_TANK };
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
        if(tanks[INPUT_TANK_1].getFluid() == null && tanks[INPUT_TANK_2].getFluid() == null)
            return ((AlloyerRecipeHandler)RecipeManager.getHandler(RecipeManager.RecipeType.ALLOYER)).isValidSingle(new FluidStack(fluid, 1000));
        else {
            if(tanks[INPUT_TANK_1].getFluid() != null && fluid == tanks[INPUT_TANK_1].getFluid().getFluid())
                return true;
            else if(tanks[INPUT_TANK_2].getFluid() != null && fluid == tanks[INPUT_TANK_2].getFluid().getFluid())
                return true;
            else if(tanks[INPUT_TANK_1].getFluid() != null &&
                    RecipeManager.getHandler(RecipeManager.RecipeType.ALLOYER).isValidInput(Pair.of(new FluidStack(fluid, 1000), tanks[INPUT_TANK_1].getFluid())))
                return true;
            else if(tanks[INPUT_TANK_2].getFluid() != null &&
                    RecipeManager.getHandler(RecipeManager.RecipeType.ALLOYER).isValidInput(Pair.of(new FluidStack(fluid, 1000), tanks[INPUT_TANK_2].getFluid())))
                return true;
        }
        return false;
    }

    /*******************************************************************************************************************
     * Misc Methods                                                                                                    *
     *******************************************************************************************************************/

    /**
     * Return the container for this tile
     *
     * @param id       Id, probably not needed but could be used for multiple guis
     * @param player   The player that is opening the gui
     * @param world The world
     * @param x        X Pos
     * @param y        Y Pos
     * @param z        Z Pos
     * @return The container to open
     */
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        return new ContainerAlloyer(player.inventory, this);
    }

    /**
     * Return the gui for this tile
     *
     * @param id       Id, probably not needed but could be used for multiple guis
     * @param player   The player that is opening the gui
     * @param world The world
     * @param x        X Pos
     * @param y        Y Pos
     * @param z        Z Pos
     * @return The gui to open
     */
    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        return new GuiAlloyer(player, this);
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
                GuiColor.WHITE + ClientUtils.translate("neotech.alloyer.desc") + "\n\n" +
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
        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0, 0, 0);
        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0, 0, 0);
        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0, 0, 0);
    }
}
