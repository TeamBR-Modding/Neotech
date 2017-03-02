package com.teambrmodding.neotech.common.blocks.storage;

import com.teambr.bookshelf.common.ICraftingListener;
import com.teambr.bookshelf.common.items.EnergyContainingItem;
import com.teambr.bookshelf.util.EnergyUtils;
import com.teambrmodding.neotech.managers.BlockManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;

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
public class ItemBlockEnergyStorage extends ItemBlock implements ICraftingListener {
    private BlockEnergyStorage energyStorage;

    /**
     * Main Constructor
     */
    public ItemBlockEnergyStorage(BlockEnergyStorage block) {
        super(block);
        energyStorage = block;
        setRegistryName(block.getRegistryName());
    }

    /*******************************************************************************************************************
     * Item                                                                                                            *
     *******************************************************************************************************************/

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
        int capacity = 256000;
        if(energyStorage == BlockManager.advancedRFStorage)
            capacity *= 8;
        else if(energyStorage == BlockManager.eliteRFStorage)
            capacity *= 16;
        return new EnergyContainingItem(stack, capacity);
    }

    /**
     * Allows items to add custom lines of information to the mouseover description
     */
    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        if(energyStorage.getTier() != 4) {
            EnergyUtils.addToolTipInfo(stack, tooltip);
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
        if(craftingList[4] != null) {
            EnergyUtils.transferPower(craftingList[4].getCapability(CapabilityEnergy.ENERGY, null),
                    craftingStack.getCapability(CapabilityEnergy.ENERGY, null), Integer.MAX_VALUE, false);
        }
        return craftingStack;
    }
}
