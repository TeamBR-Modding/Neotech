package com.dyonovan.neotech.managers;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

        //Furnace Generator
        GameRegistry.addRecipe(new ItemStack(BlockManager.furnaceGenerator()),
                "ABA",
                "CDC",
                "ABA", 'A', Items.iron_ingot, 'B', Items.redstone, 'C', Blocks.furnace, 'D', Blocks.chest);

        //Fluid Generator
        GameRegistry.addRecipe(new ItemStack(BlockManager.fluidGenerator()),
                "ABA",
                "CDC",
                "ABA", 'A', Items.gold_ingot, 'B', Items.glowstone_dust, 'C', BlockManager.furnaceGenerator(), 'D', BlockManager.ironTank());

        //Solar Panels
        GameRegistry.addRecipe(new ItemStack(BlockManager.solarPanelT1()),
                "   ",
                "CCC",
                "ABA", 'A', Items.redstone, 'B', BlockManager.basicRFStorage(), 'C', Blocks.glass);
        GameRegistry.addRecipe(new ItemStack(BlockManager.solarPanelT2()),
                "PPP",
                "PSP",
                "PPP", 'P', BlockManager.solarPanelT1(), 'S', BlockManager.advancedRFStorage());
        GameRegistry.addRecipe(new ItemStack(BlockManager.solarPanelT3()),
                "PPP",
                "PSP",
                "PPP", 'P', BlockManager.solarPanelT2(), 'S', BlockManager.eliteRFStorage());

        //Thermal Binder
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.thermalBinder()),
                "ACA",
                "BDB",
                "ACA", 'A', "ingotGold", 'B', Items.slime_ball, 'C', Blocks.furnace, 'D', Blocks.redstone_block));

        //Electric Logger
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.treeFarm()),
                "ACA",
                "BDB",
                "ACA", 'A', "ingotBronze", 'B', Items.iron_axe, 'C', Items.shears, 'D', Blocks.redstone_block));

        //RF Storage
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.basicRFStorage())),
                "ABA",
                "DCD",
                "ABA", 'A', Items.iron_ingot, 'B', Blocks.iron_bars, 'C', Blocks.redstone_block, 'D', "ingotTin"));
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
        GameRegistry.addRecipe(new ShapedOreRecipe(Item.getItemFromBlock(BlockManager.voidTank()),
                "ABA",
                "BCB",
                "ABA", 'A', Blocks.obsidian, 'B', "blockGlass", 'C', Items.ender_pearl));

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
                .redstone));
        GameRegistry.addShapelessRecipe(new ItemStack(BlockManager.pipeAdvancedSpeedStructure()),
                BlockManager.pipeBasicSpeedStructure(), BlockManager.pipeBasicSpeedStructure(), Blocks.glowstone);
        GameRegistry.addShapelessRecipe(new ItemStack(BlockManager.pipeEliteSpeedStructure()),
                BlockManager.pipeAdvancedSpeedStructure(), BlockManager.pipeAdvancedSpeedStructure(), Items.diamond);

        //Power
        GameRegistry.addShapelessRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.pipeEnergyInterface())),
                Blocks.redstone_block, BlockManager.pipeBasicStructure());

        //Item
        GameRegistry.addShapelessRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.pipeItemInterface())),
                Blocks.chest, BlockManager.pipeBasicStructure());


        //Liquid
        GameRegistry.addShapelessRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.pipeFluidInterface())),
                Items.bucket, BlockManager.pipeBasicStructure());

        //Crafter
        GameRegistry.addShapelessRecipe(new ItemStack(BlockManager.blockCrafter()),
                new ItemStack(Blocks.crafting_table), new ItemStack(Blocks.chest), new ItemStack(Blocks.crafting_table));


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

        //Magnet
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemManager.electroMagnet()),
                "I I",
                "L L",
                " L ", 'I', "ingotIron", 'L', "ingotLead"));

        //Dim Storage
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.dimStorage(), 1),
                "WWW",
                "CLC",
                "WWW", 'C', Blocks.chest, 'W', "plankWood", 'L', "logWood"));

        //Spawmer Mover Thinggy
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemManager.spawnerMover(),1),
                "  E",
                " S ",
                "S  ", 'E', Items.emerald, 'S', "stickWood"));

        //Pump
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.pump(),1),
                "BTB",
                "PRP",
                "BTB", 'B', "ingotBronze", 'R', Items.redstone, 'P', BlockManager.pipeFluidInterface(), 'T', BlockManager.ironTank()));

        //Mob Gun
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemManager.mobGun(), 1),
                "   ",
                "III",
                "  B", 'I', "ingotSteel", 'B', "rfBattery"));

        //Mob Net
        GameRegistry.addRecipe(new ItemStack(ItemManager.mobNet(), 1),
                "S S",
                " E ",
                "S S", 'E', Items.ender_pearl, 'S', Items.string);

        //Mob Stand
        GameRegistry.addRecipe(new ItemStack(BlockManager.mobStand(), 4),
                "QQQ",
                "Q Q",
                "Q Q", 'Q', Items.quartz);

        //Add in Iron nugget - ingot
        MetalManager.Metal metal = MetalManager.getMetal("iron").get();
        GameRegistry.addShapelessRecipe(new ItemStack(metal.nugget().get(), 9), Items.iron_ingot);
        GameRegistry.addRecipe(new ItemStack(Items.iron_ingot, 1),
                "III",
                "III",
                "III", 'I', metal.nugget().get());

        //RF Batteries
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemManager.basicRFBattery()),
                "S S",
                "ILI",
                "ILI", 'S', "ingotSilver", 'L', "ingotLead", 'I', "ingotIron"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemManager.advancedRFBattery()),
                "S S",
                "GBG",
                "GLG", 'S', "ingotSilver", 'L', "ingotLead", 'G', Items.gold_ingot, 'B', new ItemStack(ItemManager.basicRFBattery(), 1, OreDictionary.WILDCARD_VALUE)));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemManager.eliteRFBattery()),
                "S S",
                "DBD",
                "DLD", 'S', "ingotSilver", 'L', "ingotLead", 'D', Items.diamond, 'B', new ItemStack(ItemManager.advancedRFBattery(), 1, OreDictionary.WILDCARD_VALUE)));

        //Electric Solidifier
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.electricSolidifier()),
                "SRS",
                "BTB",
                "SRS", 'T', BlockManager.ironTank(), 'S', "ingotSteel", 'B', Items.snowball, 'R', Items.redstone));

        //Electric Crucible
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.electricCrucible()),
                "SRS",
                "BCB",
                "SRS", 'C', Blocks.cauldron, 'S', "ingotSteel", 'B', Items.bucket, 'R', Items.redstone));

        //Smelting Recipes
        GameRegistry.addSmelting(MetalManager.getMetal("gold").get().dust().get(), new ItemStack(Items.gold_ingot), 2.0F);
        GameRegistry.addSmelting(MetalManager.getMetal("iron").get().dust().get(), new ItemStack(Items.iron_ingot), 1.0F);
        //Temp Recipe TODO REMOVE
        GameRegistry.addSmelting(Items.iron_ingot, new ItemStack(MetalManager.getMetal("steel").get().ingot().get()), 0.0F);
    }
}
