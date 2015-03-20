package com.dyonovan.jatm.handlers;

import com.dyonovan.jatm.crafting.OreProcessingRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CraftingHandler {

    public static void preInit() {
        //Coal Generator
        GameRegistry.addRecipe(new ItemStack(BlockHandler.furnaceGenerator), "ABA", "BCB", "ABA",
                'A', Items.iron_ingot, 'B', Items.redstone, 'C', Blocks.furnace);

        //Electric Furnace
        GameRegistry.addRecipe(new ItemStack(BlockHandler.electricFurnace), "ACA", "BDB", "ACA",
                'A', Items.iron_ingot, 'B', Items.redstone, 'C', Blocks.furnace, 'D', Items.glowstone_dust);

        //Electric Crusher
        GameRegistry.addRecipe(new ItemStack(BlockHandler.electricCrusher), "ABA", "BCB", "ABA",
                'A', Items.iron_ingot, 'B', Items.flint, 'C', Blocks.piston);

        //Vanilla Furnace Recipes
        GameRegistry.addSmelting(ItemHandler.dustIron, new ItemStack(Items.iron_ingot, 1), 0.5F);
        GameRegistry.addSmelting(ItemHandler.dustGold, new ItemStack(Items.gold_ingot, 1), 0.7F);

        //OreProcessing Recipes
        OreProcessingRegistry.addOreProcessingRecipe(Blocks.cobblestone, Blocks.sand);
        OreProcessingRegistry.addOreProcessingRecipe(Blocks.gravel, Items.flint);

    }
}
