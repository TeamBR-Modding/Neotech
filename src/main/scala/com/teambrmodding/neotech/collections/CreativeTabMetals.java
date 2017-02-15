package com.teambrmodding.neotech.collections;

import com.teambrmodding.neotech.managers.MetalManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Iterator;
import java.util.List;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/12/2017
 */
public class CreativeTabMetals extends CreativeTabs {

    public CreativeTabMetals() {
        super("tabNeoTechMetals");
    }

    /*******************************************************************************************************************
     * CreativeTabs                                                                                                    *
     *******************************************************************************************************************/

    /**
     * Get the display icon for the tab
     * @return We are returning the forge bucket
     */
    @Override
    public Item getTabIconItem() {
        return ForgeModContainer.getInstance().universalBucket;
    }

    /**
     * Here we are filling it with copper to show on icon
     */
    @Override
    public ItemStack getIconItemStack() {
        ItemStack stack = new ItemStack(ForgeModContainer.getInstance().universalBucket);
        if(FluidRegistry.isFluidRegistered("copper"))
            ForgeModContainer.getInstance().universalBucket
                    .fill(stack, new FluidStack(FluidRegistry.getFluid("copper"), 1000), true);
        return stack;
    }

    /**
     * Add in the buckets we add
     * @param list
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void displayAllRelevantItems(List<ItemStack> list) {
        super.displayAllRelevantItems(list);

        UniversalBucket universalBucket = ForgeModContainer.getInstance().universalBucket;
        if(universalBucket != null) {
            for (String metalString : MetalManager.metalRegistry().keySet()) {
                MetalManager.Metal metal = MetalManager.metalRegistry().get(metalString);
                if(metal.fluid().isDefined()) {
                    ItemStack stack = new ItemStack(universalBucket);
                    universalBucket.fill(stack, new FluidStack(metal.fluid().get(), 1000), true);
                    list.add(stack);
                }
            }
        }
    }
}
