package com.teambrmodding.neotech.common.blocks.storage;

import com.teambr.bookshelf.client.gui.GuiColor;
import com.teambr.bookshelf.common.items.ItemBlockBattery;
import com.teambr.bookshelf.util.ClientUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/15/2017
 */
public class ItemBlockEnergyStorage extends ItemBlockBattery {
    private BlockEnergyStorage energyStorage;

    /**
     * Main Constructor
     */
    public ItemBlockEnergyStorage(BlockEnergyStorage block) {
        super(block);
        energyStorage = block;
        setRegistryName(block.getRegistryName());
    }

    /**
     * Used to set the default tags
     * You must define:
     * EnergyCapacity
     * MaxRecieve
     * MaxExtract
     * Use the static strings for nbt tags
     *
     * @param stack The stack
     */
    @Override
    protected void setDefaultTags(ItemStack stack) {
        NBTTagCompound tag = new NBTTagCompound();
        int maxCapacity, maxExtract, maxRecieve;
        switch (energyStorage.getTier()) {
            case 4:
                maxExtract = maxRecieve = maxCapacity = Integer.MAX_VALUE;
                break;
            case 3:
                maxCapacity = 4096000;
                maxExtract = maxRecieve = 10000;
                break;
            case 2:
                maxCapacity = 512000;
                maxExtract = maxRecieve = 1000;
                break;
            case 1:
            default:
                maxCapacity = 32000;
                maxExtract = maxRecieve = 200;
        }
        tag.setInteger(ENERGY_NBT_TAG, 0);
        tag.setInteger(ENERGY_CAPACITY_NBT_TAG, maxCapacity);
        tag.setInteger(ENERGY_MAX_EXTRACT_NBT_TAG, maxExtract);
        tag.setInteger(ENERGY_MAX_RECIEVE_NBT_TAG, maxRecieve);
        stack.setTagCompound(tag);
    }

    /*******************************************************************************************************************
     * Item                                                                                                            *
     *******************************************************************************************************************/

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        if(energyStorage.getTier() != 4) {
            if(stack.hasTagCompound() && stack.getTagCompound().hasKey(ENERGY_NBT_TAG)) {
                int currentStored = stack.getTagCompound().getInteger(ENERGY_NBT_TAG);
                int maxStored     = stack.getTagCompound().getInteger(ENERGY_CAPACITY_NBT_TAG);
                tooltip.add(GuiColor.ORANGE + ClientUtils.translate("neotech.text.energyStored"));
                tooltip.add("  " + ClientUtils.formatNumber(currentStored) + " / " + ClientUtils.formatNumber(maxStored));
            }
        }
    }
}
