package com.dyonovan.neotech.handlers;

import com.dyonovan.neotech.crafting.OreProcessingRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class CraftingHandler {

    public static void preInit() {
        //Coal Generator
        GameRegistry.addRecipe(new ItemStack(BlockHandler.furnaceGenerator), "ABA", "BCB", "ABA",
                'A', Items.iron_ingot, 'B', Items.redstone, 'C', Blocks.furnace);

        //Electric Furnace
        GameRegistry.addRecipe(new ItemStack(BlockHandler.electricFurnace), "ACA", "BBB", "ACA",
                'A', Items.iron_ingot, 'B', Items.redstone, 'C', Blocks.furnace);

        //Electric Crusher
        GameRegistry.addRecipe(new ItemStack(BlockHandler.electricCrusher), "ABA", "BCB", "ABA",
                'A', Items.iron_ingot, 'B', Items.flint, 'C', Blocks.piston);

        //Basic Cable
        GameRegistry.addRecipe(new ItemStack(BlockHandler.basicCable, 4), "AAA", "ABA", "AAA",
                'A', Blocks.carpet, 'B', Blocks.redstone_block);
        //Advanced Cable
        GameRegistry.addRecipe(new ItemStack(BlockHandler.advancedCable, 4), "ACA", "CBC", "ACA",
                'A', Items.iron_ingot, 'B', Blocks.redstone_block, 'C', Items.glowstone_dust);
        GameRegistry.addShapelessRecipe(new ItemStack(BlockHandler.advancedCable),
                new ItemStack(BlockHandler.basicCable), new ItemStack(Items.iron_ingot), new ItemStack(Items.glowstone_dust));

        //Elite Cable
        GameRegistry.addRecipe(new ItemStack(BlockHandler.eliteCable, 4), "ACA", "CBC", "ACA",
                'A', Items.gold_ingot, 'B', Blocks.redstone_block, 'C', Items.diamond);
        GameRegistry.addShapelessRecipe(new ItemStack(BlockHandler.eliteCable),
                new ItemStack(BlockHandler.advancedCable), new ItemStack(Items.gold_ingot), new ItemStack(Items.diamond));

        //Fluid Generator
        GameRegistry.addRecipe(new ItemStack(BlockHandler.fluidGenerator), "ABA", "DCD", "ABA",
                'A', Items.iron_ingot, 'B', Items.bucket, 'C', Blocks.furnace, 'D', Items.redstone);

        //Thermal Binder
        GameRegistry.addRecipe(new ItemStack(BlockHandler.thermalBinder), "ABA", "CDC", "ABA",
                'A', Items.gold_ingot, 'B', ItemHandler.upgradeMB, 'C', Items.glowstone_dust, 'D', Items.magma_cream);

        //Basic RF Storage
        GameRegistry.addRecipe(new ItemStack(BlockHandler.basicStorage), "ABA", "DCD", "ABA",
                'A', Items.iron_ingot, 'B', Blocks.iron_bars, 'C', Blocks.redstone_block, 'D', BlockHandler.basicCable);

        //Advanced RF Storage
        GameRegistry.addRecipe(new ItemStack(BlockHandler.advancedStorage), "ABA", "DCD", "ABA",
                'A', Items.gold_ingot, 'B', Blocks.iron_bars, 'C', BlockHandler.basicStorage, 'D', BlockHandler.advancedCable);

        //Elite RF Storage
        GameRegistry.addRecipe(new ItemStack(BlockHandler.eliteStorage), "ABA", "DCD", "ABA",
                'A', Items.diamond, 'B', Blocks.iron_bars, 'C', BlockHandler.advancedStorage, 'D', BlockHandler.eliteCable);

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

        //Item Pipes
        //Basic
        GameRegistry.addRecipe(new ItemStack(BlockHandler.basicItemPipe, 4), "AAA", "ABA", "ACA",
                'A', Blocks.carpet, 'B', Blocks.chest, 'C', Blocks.piston);
        //Advanced
        GameRegistry.addRecipe(new ItemStack(BlockHandler.advancedItemPipe, 4), "ACA", "CBC", "ADA",
                'A', Items.iron_ingot, 'B', Blocks.chest, 'C', Items.glowstone_dust, 'D', Blocks.piston);
        GameRegistry.addShapelessRecipe(new ItemStack(BlockHandler.advancedItemPipe),
                new ItemStack(BlockHandler.basicItemPipe),
                new ItemStack(Items.iron_ingot), new ItemStack(Items.glowstone_dust), new ItemStack(Blocks.piston));

        //Elite
        GameRegistry.addRecipe(new ItemStack(BlockHandler.eliteItemPipe, 4), "ACA", "CBC", "ADA",
                'A', Items.gold_ingot, 'B', Blocks.chest, 'C', Items.diamond, 'D', Blocks.piston);
        GameRegistry.addShapelessRecipe(new ItemStack(BlockHandler.eliteItemPipe),
                new ItemStack(BlockHandler.advancedItemPipe),
                new ItemStack(Items.gold_ingot), new ItemStack(Items.diamond), new ItemStack(Blocks.piston));

        //Smooth Glass
        GameRegistry.addRecipe(new ItemStack(BlockHandler.smoothGlass, 8), "AAA", "A A", "AAA",
                'A', Blocks.glass);

        //Upgrades
        //Speed
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemHandler.speedProcessor), "ABA", "CDC", "ABA",
                'A', "ingotTin", 'B', "dyeBlue", 'C', Items.redstone, 'D', Blocks.quartz_block));
        //Capacity
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemHandler.capRam), "ABA", "CDC", "ABA",
                'A', "ingotCopper", 'B', Items.quartz, 'C', Items.redstone, 'D', Blocks.gold_block));
        //Efficency
        GameRegistry.addRecipe(new ItemStack(ItemHandler.effFan), "A A", " B ", "A A",
                'A', Items.iron_ingot, 'B', Blocks.iron_block);





        //Vanilla Furnace Recipes
        GameRegistry.addSmelting(ItemHandler.dustIron, new ItemStack(Items.iron_ingot, 1), 0.5F);
        GameRegistry.addSmelting(ItemHandler.dustGold, new ItemStack(Items.gold_ingot, 1), 0.7F);
        GameRegistry.addSmelting(ItemHandler.dustCopper, new ItemStack(ItemHandler.ingotCopper, 1), 0.3F);
        GameRegistry.addSmelting(ItemHandler.dustTin, new ItemStack(ItemHandler.ingotTin, 1), 0.5F);
        GameRegistry.addSmelting(BlockHandler.oreCopper, new ItemStack(ItemHandler.ingotCopper, 1), 0.3F);
        GameRegistry.addSmelting(BlockHandler.oreTin, new ItemStack(ItemHandler.ingotTin, 1), 0.5F);

        //OreProcessing Recipes
        OreProcessingRegistry.addOreProcessingRecipe(Blocks.cobblestone, Blocks.sand);
        OreProcessingRegistry.addOreProcessingRecipe(Blocks.gravel, Items.flint);
    }
}
