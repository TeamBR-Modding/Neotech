package com.dyonovan.jatm.handlers;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CraftingHandler {

    public static void init() {
        //Coal Generator
        GameRegistry.addRecipe(new ItemStack(BlockHandler.coalGenerator), "ABA", "BCB", "ABA",
                'A', Items.iron_ingot, 'B', Items.redstone, 'C', Blocks.furnace);

        //Electric Furnace
        GameRegistry.addRecipe(new ItemStack(BlockHandler.electricFurnace), "ACA", "BDB", "ACA",
                'A', Items.iron_ingot, 'B', Items.redstone, 'C', Blocks.furnace, 'D', Items.glowstone_dust);

        //Electric Crusher
        GameRegistry.addRecipe(new ItemStack(BlockHandler.electricCrusher), "ABA", "BCB", "ABA",
                'A', Items.iron_ingot, 'B', Items.flint, 'C', Blocks.piston);
    }
}
