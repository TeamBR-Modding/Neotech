package com.teambrmodding.neotech.common.tiles.machines.processors;

import com.teambr.bookshelf.client.gui.GuiColor;
import com.teambr.bookshelf.client.gui.GuiTextFormat;
import com.teambr.bookshelf.util.ClientUtils;
import com.teambr.bookshelf.util.InventoryUtils;
import com.teambrmodding.neotech.client.gui.machines.processors.GuiElectricCrusher;
import com.teambrmodding.neotech.collections.EnumInputOutputMode;
import com.teambrmodding.neotech.common.container.machines.processors.ContainerElectricCrusher;
import com.teambrmodding.neotech.common.tiles.MachineProcessor;
import com.teambrmodding.neotech.common.tiles.traits.IUpgradeItem;
import com.teambrmodding.neotech.managers.RecipeManager;
import com.teambrmodding.neotech.registries.recipes.AbstractRecipe;
import com.teambrmodding.neotech.registries.CrusherRecipeHandler;
import com.teambrmodding.neotech.registries.recipes.CrusherRecipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
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
public class TileElectricCrusher extends MachineProcessor<ItemStack, ItemStack> {
    // Class Variables
    public static final int INPUT_SLOT    = 0;
    public static final int OUTPUT_SLOT_1 = 1;
    public static final int OUTPUT_SLOT_2 = 2;

    public static final int BASE_ENERGY_TICK = 100;

    /**
     * The initial size of the inventory
     */
    @Override
    public int getInitialSize() {
        return 3;
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
        list.add(IUpgradeItem.EXPANSION_CARD);
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
                ((getModifierForCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.CPU) - 1) * 12);
    }

    /**
     * Used to get how long it takes to cook things, you should check for upgrades at this point
     *
     * @return The time it takes in ticks to cook the current item
     */
    @Override
    public int getCookTime() {
        return 200 - (12 * (getModifierForCategory(IUpgradeItem.ENUM_UPGRADE_CATEGORY.CPU) - 1));
    }

    /**
     * Used to tell if this tile is able to process
     *
     * @return True if you are able to process
     */
    @Override
    public boolean canProcess() {
        if(energyStorage.getEnergyStored() >= getEnergyCostPerTick()) {
            if(getStackInSlot(INPUT_SLOT) == null || getOutput(getStackInSlot(INPUT_SLOT)) == null)
                return false;
            else if(getStackInSlot(OUTPUT_SLOT_1) == null)
                return true;
            else if(!getStackInSlot(OUTPUT_SLOT_1).isItemEqual(getOutput(getStackInSlot(INPUT_SLOT))))
                return false;
            else {
                int minStackSize = getStackInSlot(OUTPUT_SLOT_1).stackSize + getOutput(getStackInSlot(INPUT_SLOT)).stackSize;
                return minStackSize <= 64 && minStackSize <= getOutput(getStackInSlot(INPUT_SLOT)).getMaxStackSize();
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
                ItemStack input = getStackInSlot(INPUT_SLOT).copy();
                ItemStack recipeResult = getOutput(input);

                getStackInSlot(INPUT_SLOT).stackSize -= 1;
                if(getStackInSlot(INPUT_SLOT).stackSize <= 0)
                    setStackInSlot(INPUT_SLOT, null);

                if(getStackInSlot(OUTPUT_SLOT_1) == null)
                    setStackInSlot(OUTPUT_SLOT_1, recipeResult.copy());
                else
                    getStackInSlot(OUTPUT_SLOT_1).stackSize += recipeResult.stackSize;

                if(hasUpgradeByID(IUpgradeItem.EXPANSION_CARD)) {
                    CrusherRecipe recipe =
                            ((CrusherRecipeHandler) RecipeManager.getHandler(RecipeManager.RecipeType.CRUSHER)).getRecipe(input);
                    if(recipe != null && recipe.outputSecondary != null && recipe.secondaryOutputPercentChance > 0) {
                        if(recipe.secondaryOutputPercentChance > worldObj.rand.nextInt(100)) {
                            ItemStack extraStack = AbstractRecipe.getItemStackFromString(recipe.outputSecondary);
                            if(extraStack != null) {
                                if(getStackInSlot(OUTPUT_SLOT_2) == null)
                                    setStackInSlot(OUTPUT_SLOT_2, extraStack);
                                else
                                    InventoryUtils.tryMergeStacks(extraStack, getStackInSlot(OUTPUT_SLOT_2));
                            }
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
    public ItemStack getOutput(ItemStack input) {
        return RecipeManager.getHandler(RecipeManager.RecipeType.CRUSHER).isValidInput(input) ?
                ((CrusherRecipeHandler)RecipeManager.getHandler(RecipeManager.RecipeType.CRUSHER)).getOutput(input).getLeft().getLeft() : null;
    }

    /**
     * Get the output of the recipe (used in insert options)
     *
     * @param input The input
     * @return The output
     */
    @Override
    public ItemStack getOutputForStack(ItemStack input) {
        return getOutput(input);
    }

    /*******************************************************************************************************************
     * Tile Methods                                                                                                    *
     *******************************************************************************************************************/

    /**
     * This will try to take things from other inventories and put it into ours
     */
    @Override
    public void tryInput() {
        for(EnumFacing dir : EnumFacing.values())
            if(canInputFromSide(dir, true))
                InventoryUtils.moveItemInto(worldObj.getTileEntity(pos.offset(dir)), -1, this, INPUT_SLOT,
                        64, dir.getOpposite(), true, false, true);
    }

    /**
     * This will try to take things from our inventory and try to place them in others
     */
    @Override
    public void tryOutput() {
        for(EnumFacing dir : EnumFacing.values()) {
            if(canOutputFromSide(dir, true))
                InventoryUtils.moveItemInto(this, OUTPUT_SLOT_1, worldObj.getTileEntity(pos.offset(dir)), -1,
                        64, dir.getOpposite(), true, false, true);
            else if(canOutputFromSide(dir, false))
                InventoryUtils.moveItemInto(this, OUTPUT_SLOT_2, worldObj.getTileEntity(pos.offset(dir)), -1,
                        64, dir.getOpposite(), true, false, true);
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
        return getModeForSide(dir) == EnumInputOutputMode.DEFAULT ||
                canInputFromSide(dir, true) || canOutputFromSide(dir, true) | canOutputFromSide(dir, false) ?
                super.getItemHandlerCapability(dir) : null;
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
        return new int[] { OUTPUT_SLOT_1, OUTPUT_SLOT_2 };
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
        return slot == OUTPUT_SLOT_1 || slot == OUTPUT_SLOT_2;
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
        return !isDisabled(dir) && slot == 0 &&
                RecipeManager.getHandler(RecipeManager.RecipeType.CRUSHER).isValidInput(itemStackIn);
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
        return new ContainerElectricCrusher(player.inventory, this);
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
        return new GuiElectricCrusher(player, this);
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
                GuiColor.WHITE + ClientUtils.translate("neotech.electricCrusher.desc") + "\n\n" +
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
                GuiColor.WHITE +  ClientUtils.translate("neotech.electricFurnace.networkUpgrade.desc")  + "\n\n" +
                GuiColor.YELLOW + GuiTextFormat.BOLD + ClientUtils.translate("neotech.text.expansion") + ":\n" +
                GuiColor.WHITE +  ClientUtils.translate("neotech.electricCrusher.expansionUpgrade.desc");
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
        return InventoryUtils.calcRedstoneFromInventory(this);
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
        worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, xPos, yPos + 0.4, zPos, 0, 0, 0);
        worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, xPos, yPos + 0.4, zPos, 0, 0, 0);
    }
}
