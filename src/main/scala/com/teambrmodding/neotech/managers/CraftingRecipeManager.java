package com.teambrmodding.neotech.managers;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
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
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.electricFurnace),
                "ACA",
                "BDB",
                "ACA", 'A', "ingotCopper", 'B', Items.REDSTONE, 'C', Blocks.FURNACE, 'D', Blocks.REDSTONE_BLOCK));

        //Electric Crusher
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.electricCrusher),
                "ABA",
                "DCD",
                "ABA", 'A', "ingotTin", 'B', Items.FLINT, 'C', Blocks.PISTON, 'D', Items.REDSTONE));

        //Electric Solidifier
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.electricSolidifier),
                "SRS",
                "BTB",
                "SRS", 'T', BlockManager.basicTank, 'S', "ingotLead", 'B', Items.SNOWBALL, 'R', Items.REDSTONE));

        //Electric Crucible
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.electricCrucible),
                "SRS",
                "BCB",
                "SRS", 'C', Items.CAULDRON, 'S', "ingotCopper", 'B', Items.BUCKET, 'R', Items.REDSTONE));

        //Electric Alloyer
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.electricAlloyer),
                "BLB",
                "TRT",
                "BLB", 'B', "ingotBronze", 'L', "ingotSilver", 'T', BlockManager.basicTank, 'R', Blocks.REDSTONE_BLOCK));

        //Electric Centrifuge
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.electricCentrifuge),
                "SRS",
                "TIT",
                "SRS", 'S', "ingotSteel", 'R', Blocks.REDSTONE_BLOCK, 'T', BlockManager.basicTank, 'I', Blocks.IRON_BLOCK));

        //Furnace Generator
        GameRegistry.addRecipe(new ItemStack(BlockManager.furnaceGenerator),
                "ABA",
                "CDC",
                "ABA", 'A', Items.IRON_INGOT, 'B', Items.REDSTONE, 'C', Blocks.FURNACE, 'D', Blocks.CHEST);

        //Fluid Generator
        GameRegistry.addRecipe(new ItemStack(BlockManager.fluidGenerator),
                "ABA",
                "CDC",
                "ABA", 'A', Items.GOLD_INGOT, 'B', Items.GLOWSTONE_DUST, 'C', BlockManager.furnaceGenerator, 'D', BlockManager.basicTank);

        //Solar Panels
        GameRegistry.addRecipe(new ItemStack(BlockManager.solarPanelT1),
                "CCC",
                "RRR",
                "ABA", 'A', Items.IRON_INGOT, 'B', Blocks.REDSTONE_BLOCK, 'C', Blocks.GLASS, 'R', Items.REDSTONE);

        GameRegistry.addRecipe(new ItemStack(BlockManager.solarPanelT2),
                "CCC",
                "BSB",
                "gsg", 'C', Blocks.GLASS, 'B', Items.BLAZE_POWDER, 'S', BlockManager.solarPanelT1, 's',Blocks.REDSTONE_BLOCK, 'g', Items.GOLD_INGOT);
        GameRegistry.addRecipe(new ItemStack(BlockManager.solarPanelT3),
                "CCC",
                "PSP",
                "DED", 'D', Items.DIAMOND, 'P', Items.ENDER_PEARL, 'C', Blocks.GLASS, 'S', BlockManager.solarPanelT2, 'E', Blocks.REDSTONE_BLOCK);

        //Electric Logger
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.treeFarm),
                "ACA",
                "BDB",
                "ACA", 'A', "ingotBronze", 'B', Items.IRON_AXE, 'C', Items.SHEARS, 'D', Blocks.REDSTONE_BLOCK));

        //RF Storage
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.basicRFStorage)),
                "ALA",
                "DCD",
                "ALA", 'A', "ingotIron", 'L', "ingotLead", 'C', Blocks.REDSTONE_BLOCK, 'D', new ItemStack(Blocks.REDSTONE_BLOCK, 1, OreDictionary.WILDCARD_VALUE)));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.advancedRFStorage)),
                "ALA",
                "DCD",
                "ALA", 'A', "ingotGold", 'L', "ingotLead", 'C', BlockManager.basicRFStorage, 'D', new ItemStack(Blocks.REDSTONE_BLOCK, 1, OreDictionary.WILDCARD_VALUE)));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.eliteRFStorage)),
                "ALA",
                "DCD",
                "ALA", 'A', "ingotSteel", 'L', "ingotLead", 'C', BlockManager.advancedRFStorage, 'D', new ItemStack(Blocks.REDSTONE_BLOCK, 1, OreDictionary.WILDCARD_VALUE)));

        //Tanks
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.basicTank),
                "ABA",
                "BCB",
                "ABA", 'A', Items.IRON_INGOT, 'B', "blockGlass", 'C', Items.BUCKET));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.advancedTank),
                "ABA",
                "BCB",
                "ABA", 'A', Items.GOLD_INGOT, 'B', "blockGlass", 'C', BlockManager.basicTank));
        GameRegistry.addRecipe(new ShapedOreRecipe(Item.getItemFromBlock(BlockManager.eliteTank),
                "ABA",
                "BCB",
                "ABA", 'A', Items.DIAMOND, 'B', "blockGlass", 'C', BlockManager.advancedTank));
        GameRegistry.addRecipe(new ShapedOreRecipe(Item.getItemFromBlock(BlockManager.voidTank),
                "ABA",
                "BCB",
                "ABA", 'A', Blocks.OBSIDIAN, 'B', "blockGlass", 'C', Items.ENDER_PEARL));

        //Wrench
        GameRegistry.addRecipe(new ItemStack(ItemManager.wrench),
                " I ",
                " II",
                "I  ", 'I', Items.IRON_INGOT);

        //Add in Iron nugget - ingot
        MetalManager.Metal metal = MetalManager.getMetal("iron");
        GameRegistry.addShapelessRecipe(new ItemStack(metal.getNugget(), 9), Items.IRON_INGOT);
        GameRegistry.addRecipe(new ItemStack(Items.IRON_INGOT, 1),
                "III",
                "III",
                "III", 'I', metal.getNugget());

        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(MetalManager.getMetal("bronze").getDust(), 4),
                "dustCopper", "dustCopper", "dustCopper", "dustTin"));

        //Smelting Recipes
        GameRegistry.addSmelting(MetalManager.getMetal("gold").getDust(), new ItemStack(Items.GOLD_INGOT), 2.0F);
        GameRegistry.addSmelting(MetalManager.getMetal("iron").getDust(), new ItemStack(Items.IRON_INGOT), 1.0F);

        // Upgrades
        GameRegistry.addRecipe(new ItemStack(ItemManager.processorSingleCore),
                "RSR",
                "RIR",
                "RSR", 'R', Items.REDSTONE, 'S', Items.STRING, 'I', Items.IRON_INGOT);
        GameRegistry.addRecipe(new ItemStack(ItemManager.processorDualCore),
                "RGR",
                "PgP",
                "RGR", 'R', Items.REDSTONE, 'G', Items.GLOWSTONE_DUST, 'P', ItemManager.processorSingleCore, 'g', Items.GOLD_INGOT);
        GameRegistry.addRecipe(new ItemStack(ItemManager.processorQuadCore),
                "RGR",
                "PgP",
                "RGR", 'R', Items.REDSTONE, 'G', Items.GOLD_INGOT, 'P', ItemManager.processorDualCore, 'g', new ItemStack(Items.DYE, 1, 11));
        GameRegistry.addRecipe(new ItemStack(ItemManager.processorOctCore),
                "RGR",
                "PgP",
                "RGR", 'R', Items.REDSTONE, 'G', Items.DIAMOND, 'P', ItemManager.processorQuadCore, 'g', Items.DIAMOND);

        GameRegistry.addRecipe(new ItemStack(ItemManager.memoryDDR1),
                "SSS",
                "RRR",
                "   ", 'S', Blocks.STONE_SLAB, 'R', Items.IRON_INGOT);
        GameRegistry.addRecipe(new ItemStack(ItemManager.memoryDDR2),
                "SSS",
                "RRR",
                "M M", 'S', Blocks.STONE_SLAB, 'R', Items.REDSTONE, 'M', ItemManager.memoryDDR1);
        GameRegistry.addRecipe(new ItemStack(ItemManager.memoryDDR3),
                "SSS",
                "RRR",
                "M M", 'S', Blocks.STONE_SLAB, 'R', Items.GOLD_INGOT, 'M', ItemManager.memoryDDR2);
        GameRegistry.addRecipe(new ItemStack(ItemManager.memoryDDR4),
                "SSS",
                "RRR",
                "M M", 'S', Blocks.STONE_SLAB, 'R', Items.DIAMOND, 'M', ItemManager.memoryDDR3);

        //TODO: HDD

        GameRegistry.addRecipe(new ItemStack(ItemManager.psu250W),
                "RRR",
                "R R",
                "RRR", 'R', Blocks.REDSTONE_BLOCK);
        GameRegistry.addRecipe(new ItemStack(ItemManager.psu500W),
                "RRR",
                "RTR",
                "RRR", 'R', Blocks.REDSTONE_BLOCK, 'T', ItemManager.psu250W);
        GameRegistry.addRecipe(new ItemStack(ItemManager.psu750W),
                "RRR",
                "RTR",
                "RRR", 'R', Blocks.REDSTONE_BLOCK, 'T', ItemManager.psu500W);
        GameRegistry.addRecipe(new ItemStack(ItemManager.psu960W),
                "RRR",
                "RTR",
                "RRR", 'R', Blocks.REDSTONE_BLOCK, 'T', ItemManager.psu750W);

        if(Loader.isModLoaded("IC2"))
            GameRegistry.addRecipe(new ItemStack(ItemManager.transformer),
                    "R  ",
                    "RSR",
                    "R  ", 'R', Blocks.REDSTONE_BLOCK, 'S', Items.STRING);

        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.expansion), Items.PAPER, ItemManager.processorSingleCore);
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.redstoneControl), Items.REDSTONE, ItemManager.processorSingleCore);
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.networkCard), Items.ENDER_PEARL, ItemManager.processorSingleCore);
    }
}
