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

        //Basic Cable
        GameRegistry.addRecipe(new ItemStack(BlockHandler.basicCable, 4), "AAA", "ABA", "AAA",
                'A', Blocks.carpet, 'B', Blocks.redstone_block);

        //Fluid Generator
        GameRegistry.addRecipe(new ItemStack(BlockHandler.fluidGenerator), "ABA", "DCD", "ABA",
                'A', Items.iron_ingot, 'B', Items.bucket, 'C', Blocks.furnace, 'D', Items.redstone);

        //Basic RF Storage
        GameRegistry.addRecipe(new ItemStack(BlockHandler.basicStorage), "ABA", "DCD", "ABA",
                'A', Items.iron_ingot, 'B', Blocks.iron_bars, 'C', Blocks.redstone_block, 'D', BlockHandler.basicCable);

        //Tanks
        GameRegistry.addRecipe(new ItemStack(BlockHandler.ironTank), "BAB", "B B", "BAB",
                'A', Blocks.heavy_weighted_pressure_plate, 'B', Blocks.glass);
        GameRegistry.addRecipe(new ItemStack(BlockHandler.goldTank), "BAB", "BCB", "BAB",
                'A', Blocks.light_weighted_pressure_plate, 'B', Blocks.glass, 'C', BlockHandler.ironTank);
        GameRegistry.addRecipe(new ItemStack(BlockHandler.diamondTank), "BAB", "BCB", "BAB",
                'A', Items.diamond, 'B', Blocks.glass, 'C', BlockHandler.goldTank);

        //Crafter
        GameRegistry.addShapelessRecipe(new ItemStack(BlockHandler.crafter),
                new ItemStack(Blocks.crafting_table, 1), new ItemStack(Blocks.crafting_table, 1));

        //Vanilla Furnace Recipes
        GameRegistry.addSmelting(ItemHandler.dustIron, new ItemStack(Items.iron_ingot, 1), 0.5F);
        GameRegistry.addSmelting(ItemHandler.dustGold, new ItemStack(Items.gold_ingot, 1), 0.7F);
        GameRegistry.addSmelting(BlockHandler.oreCopper, new ItemStack(ItemHandler.ingotCopper, 1), 0.3F);
        GameRegistry.addSmelting(BlockHandler.oreTin, new ItemStack(ItemHandler.ingotTin, 1), 0.5F);

        //OreProcessing Recipes
        OreProcessingRegistry.addOreProcessingRecipe(Blocks.cobblestone, Blocks.sand);
        OreProcessingRegistry.addOreProcessingRecipe(Blocks.gravel, Items.flint);
    }
}
