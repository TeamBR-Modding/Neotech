package com.teambrmodding.neotech.common.container.machines.processors;

import com.teambrmodding.neotech.common.container.machines.ContainerAbstractMachine;
import com.teambrmodding.neotech.common.tiles.AbstractMachine;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.items.SlotItemHandler;

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
public class ContainerElectricFurnace extends ContainerAbstractMachine {
    /** The player that is using the GUI where this slot resides. */
    private final EntityPlayer thePlayer;

    /**
     * Creates the object
     *
     * @param playerInventory The players inventory
     * @param inventory       The tile/object inventory
     */
    public ContainerElectricFurnace(InventoryPlayer playerInventory, AbstractMachine inventory) {
        super(playerInventory, inventory);

        this.thePlayer = playerInventory.player;

        addSlotToContainer(new SlotItemHandler(inventory, 0, 56, 35));
        addSlotToContainer(new SlotItemHandler(inventory, 1, 116, 35) {
            /**
             * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
             */
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }

            /**
             * The itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
             */
            @Override
            protected void onCrafting(ItemStack stack) {
                stack.onCrafting(thePlayer.world, thePlayer, stack.getCount());

                if (!thePlayer.world.isRemote) {
                    int orbCount = stack.getCount();
                    float smeltRecipeExperience = FurnaceRecipes.instance().getSmeltingExperience(stack);

                    if (smeltRecipeExperience == 0.0F) {
                        orbCount = 0;
                    }
                    else if (smeltRecipeExperience < 1.0F) {
                        int experienceTotal = MathHelper.floor((float)orbCount * smeltRecipeExperience);

                        if (experienceTotal < MathHelper.ceil((float)orbCount * smeltRecipeExperience) &&
                                Math.random() < (double)((float)orbCount * smeltRecipeExperience - (float)experienceTotal)) {
                            ++experienceTotal;
                        }

                        orbCount = experienceTotal;
                    }

                    while (orbCount > 0) {
                        int experienceLeft = EntityXPOrb.getXPSplit(orbCount);
                        orbCount -= experienceLeft;
                        thePlayer.world.spawnEntity(
                                new EntityXPOrb(thePlayer.world, thePlayer.posX, thePlayer.posY + 0.5D, thePlayer.posZ + 0.5D, experienceLeft));
                    }
                }


                FMLCommonHandler.instance().firePlayerSmeltedEvent(thePlayer, stack);

                if (stack.getItem() == Items.IRON_INGOT) {
                    thePlayer.addStat(AchievementList.ACQUIRE_IRON);
                }

                if (stack.getItem() == Items.COOKED_FISH) {
                    thePlayer.addStat(AchievementList.COOK_FISH);
                }
            }
        });

        addPlayerInventorySlots(84);
    }
}
