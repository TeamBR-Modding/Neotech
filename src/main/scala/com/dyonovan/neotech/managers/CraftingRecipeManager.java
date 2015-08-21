package com.dyonovan.neotech.managers;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * This file was created for NeoTech
 *
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
        GameRegistry.addRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.basicRFStorage()), 1, 16), "ABA",
                "DCD", "ABA", 'A', Items.iron_ingot, 'B', Blocks.iron_bars, 'C', Blocks.redstone_block, 'D', Items.comparator);

        //Advanced RF Storage
        GameRegistry.addRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.advancedRFStorage()), 1, 16), "ABA",
                "DCD", "ABA", 'A', Items.gold_ingot, 'B', Blocks.iron_bars, 'C',
                BlockManager.basicRFStorage(), 'D', Items.comparator);

        //Elite RF Storage
        GameRegistry.addRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.eliteRFStorage()), 1, 16), "ABA",
                "DCD", "ABA", 'A', Items.diamond, 'B', Blocks.iron_bars, 'C',
                BlockManager.advancedRFStorage(), 'D', Items.comparator);

        //Tanks
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.ironTank()), 1, 16),
                "ABA", "BCB", "ABA", 'A', Items.iron_ingot, 'B', "blockGlass", 'C', Items.bucket));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.goldTank()), 1, 16),
                "ABA", "BCB", "ABA", 'A', Items.gold_ingot, 'B', "blockGlass", 'C', BlockManager.ironTank()));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.diamondTank()), 1, 16),
                "ABA", "BCB", "ABA", 'A', Items.diamond, 'B', "blockGlass", 'C', BlockManager.goldTank()));

        //Pipes
        //Basic
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.pipeBasicStructure()), 4),
                "AAA", "BBB", "AAA", 'A', Items.iron_ingot, 'B', "blockGlass"));

        //Colors!!
        for(EnumDyeColor color : EnumDyeColor.values()) {
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.pipeBasicStructure()), 1, color.ordinal()),
                    "pipeStructure", new ItemStack(Items.dye, 1, color.getDyeDamage())));
        }

        //Acceleration
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.pipeBasicSpeedStructure())),
                "CAC", "ABA", "CAC", 'A', "ingotBronze", 'B', BlockManager.pipeBasicStructure(), 'C', Items
                .glowstone_dust));
        //Power
        GameRegistry.addRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.pipeEnergySource())),
                " C ", "BAB", "   ", 'A', BlockManager.basicRFStorage(), 'B', Blocks.sticky_piston, 'C',
                BlockManager.pipeBasicStructure());
        GameRegistry.addRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.pipeEnergySink())),
                "   ", "BAB", " C ", 'A', BlockManager.basicRFStorage(), 'B', Blocks.piston, 'C',
                BlockManager.pipeBasicStructure());
        //Item
        GameRegistry.addRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.pipeItemSource())),
                " C ", "BAB", "   ", 'A', Blocks.chest, 'B', Blocks.sticky_piston, 'C',
                BlockManager.pipeBasicStructure());
        GameRegistry.addRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.pipeItemSink())),
                "   ", "BAB", " C ", 'A', Blocks.chest, 'B', Blocks.piston, 'C',
                BlockManager.pipeBasicStructure());
        //Liquid
        GameRegistry.addRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.pipeFluidSource())),
                " C ", "BAB", 'A', BlockManager.ironTank(), 'B', Blocks.sticky_piston, 'C',
                BlockManager.pipeBasicStructure());
        GameRegistry.addRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.pipeFluidSink())),
                "BAB", " C ", 'A', BlockManager.ironTank(), 'B', Blocks.piston, 'C',
                BlockManager.pipeBasicStructure());
        //Crafter
        GameRegistry.addRecipe(new ItemStack(BlockManager.blockCrafter()), "ABA", 'A', Blocks.crafting_table,
                'B', Blocks.chest);

        //Bronze Dust
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemManager.dustBronze()), "AA", "AB",
                'A', "dustCopper", 'B', "dustTin"));

        //Smelting Recipes
        GameRegistry.addSmelting(ItemManager.dustGold(), new ItemStack(Items.gold_ingot), 2.0F);
        GameRegistry.addSmelting(ItemManager.dustIron(), new ItemStack(Items.iron_ingot), 1.0F);
        GameRegistry.addSmelting(ItemManager.dustCopper(), new ItemStack(ItemManager.ingotCopper()), 1.0F);
        GameRegistry.addSmelting(ItemManager.dustTin(), new ItemStack(ItemManager.ingotTin()), 2.0F);
        GameRegistry.addSmelting(BlockManager.oreCopper(), new ItemStack(ItemManager.ingotCopper()), 1.0F);
        GameRegistry.addSmelting(BlockManager.oreTin(), new ItemStack(ItemManager.ingotTin()), 1.0F);
        GameRegistry.addSmelting(ItemManager.dustBronze(), new ItemStack(ItemManager.ingotBronze()), 2.0F);
    }
}
