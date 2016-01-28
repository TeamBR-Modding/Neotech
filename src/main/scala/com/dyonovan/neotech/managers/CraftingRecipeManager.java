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
        //Grinder
        GameRegistry.addRecipe(new ItemStack(BlockManager.grinder()),
                "SSS",
                "CsC",
                "SSS", 'S', Blocks.stone, 'C', Blocks.cobblestone, 's', Items.stick);

        //Electric Furnace
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.electricFurnace()),
                "ACA",
                "BDB",
                "ACA", 'A', "ingotCopper", 'B', Items.redstone, 'C', Blocks.furnace, 'D', Blocks.redstone_block));

        //Electric Crusher
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.electricCrusher()),
                "ABA",
                "DCD",
                "ABA", 'A', "ingotTin", 'B', Items.flint, 'C', Blocks.piston, 'D', Items.redstone));

        //Coal Generator
        GameRegistry.addRecipe(new ItemStack(BlockManager.furnaceGenerator()),
                "ABA",
                "CDC",
                "ABA", 'A', Items.iron_ingot, 'B', Items.redstone, 'C', Blocks.furnace, 'D', Blocks.chest);

        //Fluid Generator
        GameRegistry.addRecipe(new ItemStack(BlockManager.fluidGenerator()),
                "ABA",
                "CDC",
                "ABA", 'A', Items.gold_ingot, 'B', Items.glowstone_dust, 'C', BlockManager.furnaceGenerator(), 'D', BlockManager.ironTank());

        //Thermal Binder
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.thermalBinder()),
                "ACA",
                "BDB",
                "ACA", 'A', "ingotGold", 'B', Items.slime_ball, 'C', Blocks.furnace, 'D', Blocks.redstone_block));

        //Ore Blocks
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.blockCopper()),
                "AAA",
                "AAA",
                "AAA", 'A', "ingotCopper"));
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.ingotCopper(), 9), new ItemStack(BlockManager.blockCopper()));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.blockTin()),
                "AAA",
                "AAA",
                "AAA", 'A', "ingotTin"));
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.ingotTin(), 9), new ItemStack(BlockManager.blockTin()));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.blockBronze()),
                "AAA",
                "AAA",
                "AAA",
                'A', "ingotBronze"));
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.ingotBronze(), 9), new ItemStack(BlockManager.blockBronze()));

        //RF Storage
        GameRegistry.addRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.basicRFStorage())),
                "ABA",
                "DCD",
                "ABA", 'A', Items.iron_ingot, 'B', Blocks.iron_bars, 'C', Blocks.redstone_block, 'D', Items.comparator);
        GameRegistry.addRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.advancedRFStorage())),
                "ABA",
                "DCD",
                "ABA", 'A', Items.gold_ingot, 'B', Blocks.iron_bars, 'C', BlockManager.basicRFStorage(), 'D', Items.comparator);
        GameRegistry.addRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.eliteRFStorage())),
                "ABA",
                "DCD",
                "ABA", 'A', Items.diamond, 'B', Blocks.iron_bars, 'C', BlockManager.advancedRFStorage(), 'D', Items.comparator);

        //Tanks
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.ironTank()),
                "ABA",
                "BCB",
                "ABA", 'A', Items.iron_ingot, 'B', "blockGlass", 'C', Items.bucket));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.goldTank()),
                "ABA",
                "BCB",
                "ABA", 'A', Items.gold_ingot, 'B', "blockGlass", 'C', BlockManager.ironTank()));
        GameRegistry.addRecipe(new ShapedOreRecipe(Item.getItemFromBlock(BlockManager.diamondTank()),
                "ABA",
                "BCB",
                "ABA", 'A', Items.diamond, 'B', "blockGlass", 'C', BlockManager.goldTank()));

        //Pipes
        //Basic
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.pipeBasicStructure()), 4),
                "AAA",
                "BBB",
                "AAA", 'A', Items.iron_ingot, 'B', "blockGlass"));

        //Colors!!
        for(EnumDyeColor color : EnumDyeColor.values()) {
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.pipeBasicStructure()), 1, color.ordinal()),
                    "pipeStructure", new ItemStack(Items.dye, 1, color.getDyeDamage())));
        }

        //Acceleration
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.pipeBasicSpeedStructure())),
                "CAC",
                "ABA",
                "CAC", 'A', "ingotBronze", 'B', BlockManager.pipeBasicStructure(), 'C', Items
                .glowstone_dust));

        //Power
        GameRegistry.addRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.pipeEnergyInterface())),
                " C ",
                "BA ",
                "   ", 'A', Blocks.redstone_block, 'B', Blocks.sticky_piston, 'C', BlockManager.pipeBasicStructure());

        //Item
        GameRegistry.addRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.pipeItemInterface())),
                " C ",
                "BA ",
                "   ", 'A', Blocks.chest, 'B', Blocks.sticky_piston, 'C', BlockManager.pipeBasicStructure());


        //Liquid
        GameRegistry.addRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.pipeFluidInterface())),
                " C ",
                "BA ",
                "   ", 'A', Items.bucket, 'B', Blocks.sticky_piston, 'C', BlockManager.pipeBasicStructure());

        //Crafter
        GameRegistry.addShapelessRecipe(new ItemStack(BlockManager.blockCrafter()),
                new ItemStack(Blocks.crafting_table), new ItemStack(Blocks.chest), new ItemStack(Blocks.crafting_table));

        //Bronze Dust
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemManager.dustBronze(), 4),
                "AA",
                "AB", 'A', "dustCopper", 'B', "dustTin"));

        //Miniature Sun
        GameRegistry.addRecipe(new ItemStack(BlockManager.blockMiniatureSun()),
                "ABA",
                "BCB",
                "ABA", 'A', Items.gold_ingot, 'B', Items.glowstone_dust, 'C', Items.nether_star);

        GameRegistry.addShapelessRecipe(new ItemStack(BlockManager.blockMiniatureStar()),
                Items.glowstone_dust, Item.getItemFromBlock(Blocks.torch));

        for(EnumDyeColor color : EnumDyeColor.values()) {
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.blockMiniatureStar()), 1, color.ordinal()),
                    "blockMiniatureStar", new ItemStack(Items.dye, 1, color.getDyeDamage())));
        }

        //Player Plate
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(BlockManager.playerPlate()),
                "ingotCopper", "ingotCopper"));

       //Chunk Loader
        GameRegistry.addRecipe(new ItemStack(BlockManager.chunkLoader()),
                "GIG",
                "IRI",
                "GIG", 'G', Blocks.gold_block, 'I', Blocks.iron_block, 'R', Blocks.redstone_block);

        //Flushable Chest
        GameRegistry.addShapelessRecipe(new ItemStack(BlockManager.flushableChest()),
                Blocks.chest, Items.flint_and_steel);

        GameRegistry.addRecipe(new ItemStack(BlockManager.redstoneClock()),
                "SRS",
                "SBS",
                "SRS", 'S', Blocks.stone, 'R', Items.redstone, 'B', Blocks.redstone_block);

        //Empty Motherboard
        GameRegistry.addRecipe(new ItemStack(ItemManager.upgradeMBEmpty()),
                "RRR",
                "RIR",
                "RRR", 'R', Items.redstone, 'I', Items.iron_ingot);

        //Hard Drive
        GameRegistry.addRecipe(new ItemStack(ItemManager.upgradeHardDrive()),
                "II ",
                "II ",
                "RG ", 'I', Items.iron_ingot, 'R', Items.redstone, 'G', Items.gold_ingot);

        //Control Upgrade
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.upgradeControl()),
                Items.stick, Items.redstone);

        //Processor
        GameRegistry.addRecipe(new ItemStack(ItemManager.upgradeProcessor()),
                "RRR",
                "RDR",
                "RRR", 'R', Items.redstone, 'D', Items.diamond);

        //Expansion Card
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.upgradeExpansion()),
                Items.redstone, Items.iron_ingot, Items.paper);

        //Wrench
        GameRegistry.addRecipe(new ItemStack(ItemManager.wrench()),
                " I ",
                " II",
                "I  ", 'I', Items.iron_ingot);

        //Trash Bag
        GameRegistry.addRecipe(new ItemStack(ItemManager.trashBag(), 1),
                "S S",
                "L L",
                "LLL", 'L', Items.leather, 'S', Items.string);

        //Dim Storage
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.dimStorage(), 1),
                "WWW",
                "CLC",
                "WWW", 'C', Blocks.chest, 'W', "plankWood", 'L', "logWood"));

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
