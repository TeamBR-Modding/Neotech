package com.dyonovan.neotech.managers;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * This file was created for NeoTech
 * <p/>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 14, 2015
 */
public class CraftingRecipeManager {

    public static void preInit() {
        //Electric Furnace
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.electricFurnace()), "ACA", "BDB", "ACA",
                'A', "ingotCopper", 'B', Items.redstone, 'C', Blocks.furnace, 'D', Blocks.redstone_block));

        //Electric Crusher
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.electricCrusher()), "ABA", "DCD", "ABA",
                'A', "ingotTin", 'B', Items.flint, 'C', Blocks.piston, 'D', Items.redstone));

        //Coal Generator
        GameRegistry.addRecipe(new ItemStack(BlockManager.furnaceGenerator()), "ABA", "CDC", "ABA",
                'A', Items.iron_ingot, 'B', Items.redstone, 'C', Blocks.furnace, 'D', Blocks.chest);

        //Ore Blocks
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.blockCopper()), "AAA", "AAA", "AAA",
                'A', "ingotCopper"));
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.ingotCopper(), 9), new ItemStack(BlockManager.blockCopper()));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.blockTin()), "AAA", "AAA", "AAA",
                'A', "ingotTin"));
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.ingotTin(), 9), new ItemStack(BlockManager
                .blockTin()));

        //Basic RF Storage
        GameRegistry.addRecipe(new ItemStack(BlockManager.basicRFStorage()), "ABA", "DCD", "ABA",
                'A', Items.iron_ingot, 'B', Blocks.iron_bars, 'C', Blocks.redstone_block, 'D', Items.comparator);

        //Advanced RF Storage
        GameRegistry.addRecipe(new ItemStack(BlockManager.advancedRFStorage()), "ABA", "DCD", "ABA",
                'A', Items.gold_ingot, 'B', Blocks.iron_bars, 'C', BlockManager.basicRFStorage(), 'D', Items.comparator);

        //Elite RF Storage
        GameRegistry.addRecipe(new ItemStack(BlockManager.eliteRFStorage()), "ABA", "DCD", "ABA",
                'A', Items.diamond, 'B', Blocks.iron_bars, 'C', BlockManager.advancedRFStorage(), 'D', Items.comparator);

    }
}
