package com.teambrmodding.neotech.common.tiles.machines.generators;

import com.teambr.bookshelf.util.EnergyUtils;
import com.teambrmodding.neotech.collections.EnumInputOutputMode;
import com.teambrmodding.neotech.common.tiles.MachineGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;

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
public class TileSolarPanel extends MachineGenerator {
    public static int BASE_MAX_STORED      = 32000;
    public static int BASE_ENERGY_CHANGE   = 2000;
    public static int BASE_ENERGY_PRODUCED = 16;
    protected int tier;

    public TileSolarPanel() {

    }

    public TileSolarPanel(int tier) {
        this.tier = tier;
        initEnergy();
    }

    /**
     * Sets the default energy settings based on tier
     */
    protected void initEnergy() {
        switch (tier) {
            case 3:
                energyStorage.setMaxExtract(BASE_ENERGY_CHANGE * 8);
                energyStorage.setMaxInsert(BASE_ENERGY_CHANGE * 8);
            case 2:
                energyStorage.setMaxExtract(BASE_ENERGY_CHANGE * 4);
                energyStorage.setMaxInsert(BASE_ENERGY_CHANGE * 4);
                break;
            case 1:
            default :
                energyStorage.setMaxExtract(BASE_ENERGY_CHANGE);
                energyStorage.setMaxInsert(BASE_ENERGY_CHANGE);
        }
        energyStorage.setMaxStored(maxStored());
    }

    /**
     * Gets what the max storage for this tier should be
     * @return
     */
    protected int maxStored() {
        switch (tier) {
            case 3 :
                return BASE_MAX_STORED * 8;
            case 2 :
                return BASE_MAX_STORED * 4;
            case 1:
            default:
                return BASE_MAX_STORED;
        }
    }

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

    }

    /**
     * Used to specify if this tile should handle IO, and render in GUI
     *
     * @return False to prevent
     */
    @Override
    public boolean shouldHandleIO() {
        return false;
    }

    /**
     * This method handles how much energy to produce per tick
     *
     * @return How much energy to produce per tick
     */
    @Override
    public int getEnergyProduced() {
        float lightModifier = 0;
        if(worldObj.canSeeSky(pos) && worldObj.getSunBrightnessFactor(1.0F) > 0.7F)
            lightModifier = worldObj.getSunBrightnessFactor(1.0F);
        switch (tier) {
            case 3:
                return (int) ((BASE_ENERGY_PRODUCED * 8) * lightModifier);
            case 2:
                return (int) ((BASE_ENERGY_PRODUCED * 4) * lightModifier);
            case 1:
            default:
                return (int) (BASE_ENERGY_PRODUCED * lightModifier);
        }
    }

    /**
     * Called to tick generation. This is where you add power to the generator
     */
    @Override
    public void generate() {
        energyStorage.receivePower(getEnergyProduced(), true);
    }

    /**
     * Called per tick to manage burn time. You can do nothing here if there is nothing to generate. You should decrease burn time here
     * You should be handling checks if burnTime is 0 in this method, otherwise the tile won't know what to do
     *
     * @return True if able to continue generating
     */
    @Override
    public boolean manageBurnTime() {
        return worldObj.canSeeSky(pos) && worldObj.getSunBrightnessFactor(1.0F) > 0.7F;
    }

    /*******************************************************************************************************************
     * Tile Methods                                                                                                    *
     *******************************************************************************************************************/

    /**
     * Used to determine how much energy should be in this tile
     *
     * @return How much energy should be available
     */
    @Override
    public int getSupposedEnergy() {
        return maxStored();
    }


    /**
     * This will try to take things from other inventories and put it into ours
     */
    @Override
    public void tryInput() {
        // No Op
    }

    /**
     * This will try to take things from our inventory and try to place them in others
     */
    @Override
    public void tryOutput() {
        if(energyStorage.getEnergyStored() > 0) {
            EnergyUtils.distributePowerToFaces(this.getCapability(CapabilityEnergy.ENERGY, null),
                    worldObj, pos, energyStorage.getMaxExtract(), false);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("Tier", tier);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if(compound.hasKey("Tier")) {
            if(tier == 0) {
                tier = compound.getInteger("Tier");
                initEnergy();
            }
        }
        tier = compound.getInteger("Tier");
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
        return new int[0];
    }

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
     * Used to define if an item is valid for a slot
     *
     * @param index The slot id
     * @param stack The stack to check
     * @return True if you can put this there
     */
    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return false;
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
        return false;
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
     * @param worldObj The worldObj
     * @param x        X Pos
     * @param y        Y Pos
     * @param z        Z Pos
     * @return The container to open
     */
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World worldObj, int x, int y, int z) {
        return null;
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
        return null;
    }

    /**
     * Used to get the description to display on the tab
     *
     * @return The long string with the description
     */
    @Override
    public String getDescription() {
        return null;
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
     *
     * @param xPos
     * @param yPos
     * @param zPos
     */
    @Override
    public void spawnActiveParticles(double xPos, double yPos, double zPos) {

    }
}
