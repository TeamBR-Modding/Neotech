package com.teambrmodding.neotech.common.blocks.storage;

import com.teambr.bookshelf.client.gui.GuiColor;
import com.teambr.bookshelf.common.ICraftingListener;
import com.teambr.bookshelf.util.ClientUtils;
import com.teambrmodding.neotech.managers.BlockManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
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
public class ItemBlockFluidStorage extends ItemBlock implements ICraftingListener {
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
        int capacity = 16000;
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

    /*******************************************************************************************************************
     * ICraftingListener                                                                                               *
     *******************************************************************************************************************/

    /**
     * Called when this item is crafted, handle NBT moving or other stuff here
     *
     * @param craftingList  The list of items that were used, can have null at locations,
     *                      you should already know where important items are and read from this, be sure to check size
     *                      to find out if what kind of grid it is
     *                      <p>
     *                      Format Full Crafting:
     *                      0  1  2
     *                      3  4  5
     *                      6  7  8
     *                      <p>
     *                      Format Player Crafting:
     *                      0  1
     *                      2  3
     * @param craftingStack The output stack, modify this to modify what the player gets on crafting
     * @return The stack to give the player, should probably @param craftingStack but can be something different
     */
    @Override
    public ItemStack onCrafted(ItemStack[] craftingList, ItemStack craftingStack) {
        if(craftingList.length > 5 && craftingList[4].hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
            IFluidHandler oldTank = craftingList[4].getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            IFluidHandler newTank = craftingStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            FluidUtil.tryFluidTransfer(newTank, oldTank, Integer.MAX_VALUE, true);
        }
        return craftingStack;
    }
}
