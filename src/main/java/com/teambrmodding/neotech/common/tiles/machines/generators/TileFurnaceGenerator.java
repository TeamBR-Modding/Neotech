package com.teambrmodding.neotech.common.tiles.machines.generators;

import com.teambr.bookshelf.client.gui.GuiColor;
import com.teambr.bookshelf.client.gui.GuiTextFormat;
import com.teambr.bookshelf.util.ClientUtils;
import com.teambr.bookshelf.util.InventoryUtils;
import com.teambrmodding.neotech.client.gui.machines.generators.GuiFurnaceGenerator;
import com.teambrmodding.neotech.collections.EnumInputOutputMode;
import com.teambrmodding.neotech.common.container.machines.generators.ContainerFurnaceGenerator;
import com.teambrmodding.neotech.common.tiles.MachineGenerator;
import com.teambrmodding.neotech.common.tiles.traits.IUpgradeItem;
import com.teambrmodding.neotech.managers.FluidManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

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
public class TileFurnaceGenerator extends MachineGenerator {
    // Class variables
    public static final int BASE_ENERGY_TICK = 100;
    public static final int INPUT_SLOT       = 0;

    public static final int TANK             = 0;


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
        validModes.add(EnumInputOutputMode.INPUT_PRIMARY);
        validModes.add(EnumInputOutputMode.INPUT_SECONDARY);
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
     * This method handles how much energy to produce per tick
     *
     * @return How much energy to produce per tick
     */
    @Override
    public int getEnergyProduced() {
        int oxygenModifier = tanks[TANK].getFluid() != null && tanks[TANK].getFluid().getFluid().getName().equalsIgnoreCase(FluidManager.oxygen.getName()) ?
                5 : 1;
        return (BASE_ENERGY_TICK * getModifierForCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.MEMORY) +
                ((getModifierForCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.CPU) - 1) * 12)) * oxygenModifier;
    }

    /**
     * Called to tick generation. This is where you add power to the generator
     */
    @Override
    public void generate() {
        energyStorage.receivePower(getEnergyProduced(), true);
        if(tanks[TANK].getFluid() != null)
            tanks[TANK].drain(getModifierForCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.MEMORY), true);
    }

    /**
     * Called per tick to manage burn time. You can do nothing here if there is nothing to generate. You should decrease burn time here
     * You should be handling checks if burnTime is 0 in this method, otherwise the tile won't know what to do
     *
     * @return True if able to continue generating
     */
    @Override
    public boolean manageBurnTime() {
        if(energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored() && burnTime <= 1) {
            if(getStackInSlot(INPUT_SLOT) != null) {
                burnTime = TileEntityFurnace.getItemBurnTime(getStackInSlot(INPUT_SLOT));

                if(burnTime > 0) {
                    if(getStackInSlot(INPUT_SLOT).getItem().getContainerItem(getStackInSlot(INPUT_SLOT)) == null)
                        getStackInSlot(INPUT_SLOT).stackSize -= 1;
                    else
                        setStackInSlot(INPUT_SLOT, getStackInSlot(INPUT_SLOT).getItem().getContainerItem(getStackInSlot(INPUT_SLOT)));

                    if(getStackInSlot(INPUT_SLOT).stackSize <= 0)
                        setStackInSlot(INPUT_SLOT, null);

                    currentObjectBurnTime = burnTime;
                    return true;
                }
            }
        }

        burnTime -= getModifierForCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.MEMORY);
        return burnTime > 0;
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
            if (canInputFromSide(dir, true)) {
                if (worldObj.getTileEntity(pos.offset(dir)) != null &&
                        worldObj.getTileEntity(pos.offset(dir)).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite())) {
                    IFluidHandler otherTank = worldObj.getTileEntity(pos.offset(dir)).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite());
                    FluidStack drained = FluidUtil.tryFluidTransfer(this, otherTank,
                            tanks[TANK].getCapacity() - tanks[TANK].getFluidAmount(), false);
                    if (drained != null) {
                        FluidUtil.tryFluidTransfer(this, otherTank,
                                tanks[TANK].getCapacity() - tanks[TANK].getFluidAmount(), false);
                        return;
                    }
                } else if(worldObj.getTileEntity(pos.offset(dir)) != null &&
                        worldObj.getTileEntity(pos.offset(dir)).hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite())) {
                    IItemHandler otherInv = worldObj.getTileEntity(pos.offset(dir)).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite());
                    if(InventoryUtils.moveItemInto(otherInv, -1, this, INPUT_SLOT, 64,
                            dir, true, true, false))
                        return;
                }
            }
        }
    }

    /**
     * This will try to take things from our inventory and try to place them in others
     */
    @Override
    public void tryOutput() {
        // No Op
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
                canInputFromSide(dir, true) ?
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
                canInputFromSide(dir, false) ?
                super.getFluidHandlerCapability(dir) : null;
    }

    /*******************************************************************************************************************
     * Inventory Methods                                                                                               *
     *******************************************************************************************************************/

    /**
     * Used to get what slots are allowed to be input
     *
     * @param mode
     * @return The slots to input from
     */
    @Override
    public int[] getInputSlots(EnumInputOutputMode mode) {
        return new int[] { INPUT_SLOT };
    }

    /**
     * Used to get what slots are allowed to be output
     *
     * @param mode
     * @return The slots to output from
     */
    @Override
    public int[] getOutputSlots(EnumInputOutputMode mode) {
        return new int[] { INPUT_SLOT };
    }

    /**
     * Can insert the item into the inventory
     *
     * @param slot        The slot
     * @param itemStackIn The stack to insert
     * @param dir         The dir
     * @return True if can insert
     */
    @Override
    public boolean canInsertItem(int slot, ItemStack itemStackIn, EnumFacing dir) {
        return !isDisabled(dir) && isItemValidForSlot(slot, itemStackIn);
    }

    /**
     * Can this extract the item
     *
     * @param slot  The slot
     * @param stack The stack
     * @param dir   The dir
     * @return True if can extract
     */
    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing dir) {
        return !isDisabled(dir);
    }

    /**
     * Used to define if an item is valid for a slot
     *
     * @param index The slot id
     * @param stack The stack to check
     * @return True if you can put this there
     */
    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return TileEntityFurnace.getItemBurnTime(stack) > 0 && FluidUtil.getFluidHandler(stack) == null;
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
        tanks[0] = new FluidTank(bucketsToMB(10));
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
        return fluid.getName().equalsIgnoreCase(FluidManager.oxygen.getName()) && super.canFill(fluid);
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
        return new ContainerFurnaceGenerator(player.inventory, this);
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
        return new GuiFurnaceGenerator(player, this);
    }

    /**
     * Used to get the description to display on the tab
     *
     * @return The long string with the description
     */
    @Override
    public String getDescription() {
        return         "" +
                GuiColor.GREEN + GuiTextFormat.BOLD + GuiTextFormat.UNDERLINE + ClientUtils.translate("neotech.text.stats") + ":\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.generating") + ":\n" +
                GuiColor.WHITE + "  " + getEnergyProduced() + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.operations") + ":\n" +
                GuiColor.WHITE + "  " + getModifierForCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.MEMORY) + "\n\n" +
                GuiColor.WHITE + ClientUtils.translate("neotech.furnaceGenerator.desc") + "\n\n" +
                GuiColor.GREEN + GuiTextFormat.BOLD + GuiTextFormat.UNDERLINE + ClientUtils.translate("neotech.text.upgrade") + ":\n" + GuiTextFormat.RESET +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.processors") + ":\n" +
                GuiColor.WHITE + ClientUtils.translate("neotech.furnaceGenerator.processorUpgrade.desc") + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.hardDrives") + ":\n" +
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
        return (energyStorage.getEnergyStored() * 16) / energyStorage.getMaxEnergyStored();
    }

    /**
     * Used to get what particles to spawn. This will be called when the tile is active
     */
    @Override
    public void spawnActiveParticles(double x, double y, double z) {
        worldObj.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0, 0, 0);
        worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0, 0, 0);
    }
}
