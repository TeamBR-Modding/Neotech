package com.teambrmodding.neotech.common.blocks.storage;

import com.teambr.bookshelf.client.gui.GuiColor;
import com.teambr.bookshelf.common.tiles.FluidHandler;
import com.teambr.bookshelf.util.ClientUtils;
import com.teambrmodding.neotech.common.tiles.storage.tanks.TileBasicTank;
import com.teambrmodding.neotech.managers.BlockManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

import java.util.List;

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/15/2017
 */
public class ItemBlockFluidStorage extends ItemBlock {
    private BlockFluidStorage fluidStorage;

    /**
     * Creates the ItemBlock
     */
    public ItemBlockFluidStorage(BlockFluidStorage block) {
        super(block);
        this.fluidStorage = block;
        setRegistryName(block.getRegistryName());
    }

    /**
     * Called from ItemStack.setItem, will hold extra data for the life of this ItemStack.
     * Can be retrieved from stack.getCapabilities()
     * The NBT can be null if this is not called from readNBT or if the item the stack is
     * changing FROM is different then this item, or the previous item had no capabilities.
     *
     * This is called BEFORE the stacks item is set so you can use stack.getItem() to see the OLD item.
     * Remember that getItem CAN return null.
     *
     * @param stack The ItemStack
     * @param nbt NBT of this item serialized, or null.
     * @return A holder instance associated with this ItemStack where you can hold capabilities for the life of this item.
     */
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
        int capacity = 8000;
        if(fluidStorage == BlockManager.advancedTank)
            capacity *= 4;
        else if(fluidStorage == BlockManager.eliteTank)
            capacity *= 8;

        return new FluidHandlerItemStack(stack, capacity);
    }

    /*******************************************************************************************************************
     * Item                                                                                                            *
     *******************************************************************************************************************/

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        if(stack.hasTagCompound()) {
            FluidStack currentStored = FluidUtil.getFluidContained(stack);
            if(currentStored == null)
                return;

            tooltip.add(GuiColor.ORANGE + ClientUtils.translate("neotech.text.fluidStored"));
            tooltip.add("  " + currentStored.getLocalizedName() + ": " + ClientUtils.formatNumber(currentStored.amount) + " mb");
        }
    }
}
