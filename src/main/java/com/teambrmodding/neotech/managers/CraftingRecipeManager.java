package com.teambrmodding.neotech.managers;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
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
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.electricFurnace),
                "CRC",
                "FBF",
                "CRC", 'C', "ingotCopper", 'R', "dustRedstone", 'F', Blocks.FURNACE, 'B', "blockCopper"));

        //Electric Crusher
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.electricCrusher),
                "TRT",
                "PBP",
                "TRT", 'T', "ingotTin", 'R', "dustRedstone", 'P', Blocks.PISTON, 'B', "blockTin"));

        //Electric Solidifier
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.electricSolidifier),
                "SRS",
                "BTB",
                "SRS", 'T', BlockManager.basicTank, 'S', "ingotLead", 'B', Blocks.PACKED_ICE, 'R', Items.REDSTONE));

        //Electric Crucible
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.electricCrucible),
                "SRS",
                "BCB",
                "SRS", 'C', Items.CAULDRON, 'S', "ingotCopper", 'B', Blocks.field_189877_df, 'R', "dustRedstone"));

        //Electric Alloyer
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.electricAlloyer),
                "BLB",
                "TRT",
                "BLB", 'B', "ingotBronze", 'L', "dustRedstone", 'T', BlockManager.basicTank, 'R', "blockSilver"));

        //Electric Centrifuge
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.electricCentrifuge),
                "SRS",
                "TIT",
                "SRS", 'S', "ingotSteel", 'R', "dustRedstone", 'T', BlockManager.basicTank, 'I', Blocks.HOPPER));

        //Furnace Generator
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.furnaceGenerator),
                "ABA",
                "CDC",
                "ABA", 'A', Items.IRON_INGOT, 'B', Items.REDSTONE, 'C', Blocks.FURNACE, 'D', BlockManager.electricFurnace));

        //Fluid Generator
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.fluidGenerator),
                "ABA",
                "CDC",
                "ABA", 'A', "ingotGold", 'B', "dustRedstone", 'C', BlockManager.furnaceGenerator, 'D', BlockManager.basicTank));

        //Solar Panels
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.solarPanelT1),
                "CCC",
                "RRR",
                "ABA", 'A', "ingotTormented", 'B', "blockRedstone", 'C', "blockGlass", 'R', "dustRedstone"));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.solarPanelT2),
                "CCC",
                "BSB",
                "gsg", 'C', "blockGlass", 'B', Items.BLAZE_POWDER, 'S', BlockManager.solarPanelT1, 's', "blockRedstone", 'g', "ingotOutlandish"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.solarPanelT3),
                "CCC",
                "PSP",
                "DED", 'D', "ingotNeodymium", 'P', "enderpearl", 'C', "blockGlass", 'S', BlockManager.solarPanelT2, 'E', "blockRedstone"));

        //Electric Logger
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.treeFarm),
                "ACA",
                "BDB",
                "ACA", 'A', "ingotBronze", 'B', Items.IRON_AXE, 'C', Items.SHEARS, 'D', Blocks.REDSTONE_BLOCK));

        //RF Storage
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.basicRFStorage)),
                "ALA",
                "DCD",
                "ALA", 'A', "ingotIron", 'L', "ingotLead", 'C', "blockRedstone", 'D', "blockTin"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.advancedRFStorage)),
                "ALA",
                "DCD",
                "ALA", 'A', "ingotGold", 'L', "ingotLead", 'C', BlockManager.basicRFStorage, 'D', "blockTormented"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.eliteRFStorage)),
                "ALA",
                "DCD",
                "ALA", 'A', "ingotSteel", 'L', "ingotLead", 'C', BlockManager.advancedRFStorage, 'D', "blockOutlandish"));

        //Tanks
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.basicTank),
                "ABA",
                "BCB",
                "ABA", 'A', "ingotIron", 'B', "blockGlass", 'C', Items.BUCKET));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.advancedTank),
                "ABA",
                "BCB",
                "ABA", 'A', "ingotTormented", 'B', "blockGlass", 'C', BlockManager.basicTank));
        GameRegistry.addRecipe(new ShapedOreRecipe(Item.getItemFromBlock(BlockManager.eliteTank),
                "ABA",
                "BCB",
                "ABA", 'A', "ingotOutlandish", 'B', "blockGlass", 'C', BlockManager.advancedTank));
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
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemManager.processorSingleCore),
                "RSR",
                "RIR",
                "RSR", 'R', "ingotTin", 'S', "dustRedstone", 'I', "blockIron"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemManager.processorDualCore),
                "RGR",
                "PgP",
                "RGR", 'R', "blockTormented", 'G', "dustTormented", 'P', ItemManager.processorSingleCore, 'g', "ingotTormented"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemManager.processorQuadCore),
                "RGR",
                "PgP",
                "RGR", 'R', "blockOutlandish", 'G', "dustOutlandish", 'P', ItemManager.processorDualCore, 'g', "ingotOutlandish"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemManager.processorOctCore),
                "RGR",
                "PgP",
                "RGR", 'R', "blockNeodymium", 'G', "dustNeodymium", 'P', ItemManager.processorQuadCore, 'g', "ingotNeodymium"));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemManager.memoryDDR1),
                "SSS",
                "RRR",
                "   ", 'S', "ingotIron", 'R', "dustRedstone"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemManager.memoryDDR2),
                "SSS",
                "RRR",
                "M M", 'S', "blockTormented", 'R', "dustTormented", 'M', ItemManager.memoryDDR1));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemManager.memoryDDR3),
                "SSS",
                "RRR",
                "M M", 'S', "blockOutlandish", 'R', "dustOutlandish", 'M', ItemManager.memoryDDR2));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemManager.memoryDDR4),
                "SSS",
                "RRR",
                "M M", 'S', "blockNeodymium", 'R', "dustNeodymium", 'M', ItemManager.memoryDDR3));

        //TODO: HDD

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemManager.psu250W),
                "RRR",
                "RSR",
                "RRR", 'R', "blockRedstone", 'S', BlockManager.basicRFStorage));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemManager.psu500W),
                "RRR",
                "RTR",
                "RRR", 'R', "ingotTormented", 'T', ItemManager.psu250W));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemManager.psu750W),
                "RRR",
                "RTR",
                "RRR", 'R', "ingotOutlandish", 'T', ItemManager.psu500W));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemManager.psu960W),
                "RRR",
                "RTR",
                "RRR", 'R', "ingotNeodymium", 'T', ItemManager.psu750W));

        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ItemManager.expansion), "ingotTormented", ItemManager.processorSingleCore));
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.redstoneControl), Items.REDSTONE, ItemManager.processorSingleCore);
        GameRegistry.addShapelessRecipe(new ItemStack(ItemManager.networkCard), Items.ENDER_PEARL, ItemManager.processorSingleCore);
    }
}
