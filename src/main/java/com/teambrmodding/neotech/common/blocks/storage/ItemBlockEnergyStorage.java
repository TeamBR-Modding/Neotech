package com.teambrmodding.neotech.common.blocks.storage;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.teambr.bookshelf.common.IAdvancedToolTipProvider;
import com.teambr.bookshelf.common.ICraftingListener;
import com.teambr.bookshelf.common.items.EnergyContainingItem;
import com.teambr.bookshelf.util.ClientUtils;
import com.teambr.bookshelf.util.EnergyUtils;
import com.teambrmodding.neotech.managers.BlockManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;

import javax.annotation.Nonnull;
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
 * @since 2/15/2017
 */
public class ItemBlockEnergyStorage extends ItemBlock implements ICraftingListener, IAdvancedToolTipProvider {
    // NBT
    public static final String ACTIVE = "ActiveCharging";

    // Variables
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
        if(energyStorage == BlockManager.advancedEnergyStorage)
            capacity *= 8;
        else if(energyStorage == BlockManager.eliteEnergyStorage)
            capacity *= 16;
        return new EnergyContainingItem(stack, capacity);
    }

    /**
     * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
     * update it's contents.
     *
     * @param stack
     * @param worldIn
     * @param entityIn
     * @param itemSlot
     * @param isSelected
     */
    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        // Make sure we have a tag
        if(!stack.hasTagCompound()) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setBoolean(ACTIVE, false);
            stack.setTagCompound(compound);
        }

        // Make sure we are a player and should even charge
        if (stack.getTagCompound().getBoolean(ACTIVE) && entityIn instanceof EntityPlayer) {
            // Cast
            EntityPlayer player = (EntityPlayer) entityIn;
            // Wrap all inventory including armor and offhand
            PlayerInvWrapper playerInventory = new PlayerInvWrapper(player.inventory);
            for (int slot = 0; slot < playerInventory.getSlots(); slot++) {
                if (!(playerInventory.getStackInSlot(slot).getItem() instanceof ItemBlockEnergyStorage) && // Don't charge each other
                        playerInventory.getStackInSlot(slot).hasCapability(CapabilityEnergy.ENERGY, null)) {
                    // Get our energy
                    IEnergyStorage source = stack.getCapability(CapabilityEnergy.ENERGY, null);
                    // Get their energy
                    IEnergyStorage energyItem = playerInventory.getStackInSlot(slot).getCapability(CapabilityEnergy.ENERGY, null);
                    // Transfer
                    EnergyUtils.transferPower(source, energyItem, source.getMaxEnergyStored() / 200, false);
                }
            }
        }
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand handIn) {
        if (playerIn.isSneaking() && playerIn.getHeldItem(handIn).getItem() instanceof ItemBlockEnergyStorage) {
            ItemStack heldStack = playerIn.getHeldItem(handIn);
            if(heldStack.getTagCompound() != null) {
                heldStack.getTagCompound().setBoolean(ACTIVE, !heldStack.getTagCompound().getBoolean(ACTIVE));
                return new ActionResult<>(EnumActionResult.SUCCESS, heldStack);
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(ACTIVE) && stack.getTagCompound().getBoolean(ACTIVE);
    }

    /**
     * Allows items to add custom lines of information to the mouseover description
     */
    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nonnull EntityPlayer playerIn, @Nonnull List<String> tooltip, boolean advanced) {
        if(energyStorage.getTier() != 4) {
            EnergyUtils.addToolTipInfo(stack, tooltip);
        }
    }

    /*******************************************************************************************************************
     * IAdvancedToolTipProvider                                                                                        *
     *******************************************************************************************************************/

    /**
     * Defines if the tooltip will add the press shift for more info text
     * <p>
     * Override this to false if you just want it to show up on shift, useful if press shift for info may already be
     * present
     *
     * @param stack
     * @return True to display
     */
    @Override
    public boolean displayShiftForInfo(ItemStack stack) {
        return false;
    }

    /**
     * Get the tool tip to present when shift is pressed
     *
     * @param stack The itemstack
     * @return The list to display
     */
    @Nullable
    @Override
    public List<String> getAdvancedToolTip(@Nonnull ItemStack stack) {
        List<String> toolTip = new ArrayList<>();

        // Active Status
        toolTip.add("");
        toolTip.add(ChatFormatting.GOLD + ClientUtils.translate("neotech.text.chargingStatus"));
        boolean chargingStatus = stack.hasTagCompound() && stack.getTagCompound().getBoolean(ACTIVE);
        toolTip.add("  " + (chargingStatus ?
                ChatFormatting.GREEN + ClientUtils.translate("neotech.text.enabled") :
                ChatFormatting.RED   + ClientUtils.translate("neotech.text.disabled")));

        // Desc
        toolTip.add("");
        toolTip.add(ChatFormatting.GRAY + ClientUtils.translate("neotech.energyStorage.desc"));

        return toolTip;
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
