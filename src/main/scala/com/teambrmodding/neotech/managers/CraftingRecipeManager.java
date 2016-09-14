package com.teambrmodding.neotech.managers;

import com.teambrmodding.neotech.tools.UpgradeItemManager;
import com.teambrmodding.neotech.tools.modifier.ModifierAOE;
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
                "IHI",
                "SSS", 'S', Blocks.STONE_SLAB, 'H', Blocks.HOPPER, 'I', Items.IRON_INGOT);

        //Electric Furnace
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.electricFurnace()),
                "ACA",
                "BDB",
                "ACA", 'A', "ingotCopper", 'B', Items.REDSTONE, 'C', Blocks.FURNACE, 'D', Blocks.REDSTONE_BLOCK));

        //Electric Crusher
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.electricCrusher()),
                "ABA",
                "DCD",
                "ABA", 'A', "ingotTin", 'B', Items.FLINT, 'C', Blocks.PISTON, 'D', Items.REDSTONE));

        //Electric Solidifier
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.electricSolidifier()),
                "SRS",
                "BTB",
                "SRS", 'T', BlockManager.ironTank(), 'S', "ingotLead", 'B', Items.SNOWBALL, 'R', Items.REDSTONE));

        //Electric Crucible
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.electricCrucible()),
                "SRS",
                "BCB",
                "SRS", 'C', Items.CAULDRON, 'S', "ingotCopper", 'B', Items.BUCKET, 'R', Items.REDSTONE));

        //Electric Alloyer
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.electricAlloyer()),
                "BLB",
                "TRT",
                "BLB", 'B', "ingotBronze", 'L', "ingotSilver", 'T', BlockManager.ironTank(), 'R', Blocks.REDSTONE_BLOCK));

        //Electric Centrifuge
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.electricCentrifuge()),
                "SRS",
                "TIT",
                "SRS", 'S', "ingotSteel", 'R', Blocks.REDSTONE_BLOCK, 'T', BlockManager.ironTank(), 'I', Blocks.IRON_BLOCK));

        //Furnace Generator
        GameRegistry.addRecipe(new ItemStack(BlockManager.furnaceGenerator()),
                "ABA",
                "CDC",
                "ABA", 'A', Items.IRON_INGOT, 'B', Items.REDSTONE, 'C', Blocks.FURNACE, 'D', Blocks.CHEST);

        //Fluid Generator
        GameRegistry.addRecipe(new ItemStack(BlockManager.fluidGenerator()),
                "ABA",
                "CDC",
                "ABA", 'A', Items.GOLD_INGOT, 'B', Items.GLOWSTONE_DUST, 'C', BlockManager.furnaceGenerator(), 'D', BlockManager.ironTank());

        //Solar Panels
        GameRegistry.addRecipe(new ItemStack(BlockManager.solarPanelT1()),
                "CCC",
                "RRR",
                "ABA", 'A', Items.IRON_INGOT, 'B', ItemManager.basicRFBattery(), 'C', Blocks.GLASS, 'R', Items.REDSTONE);
        GameRegistry.addRecipe(new ItemStack(BlockManager.solarPanelT2()),
                "CCC",
                "BSB",
                "gsg", 'C', Blocks.GLASS, 'B', Items.BLAZE_POWDER, 'S', BlockManager.solarPanelT1(), 's', ItemManager.advancedRFBattery(), 'g', Items.GOLD_INGOT);
        GameRegistry.addRecipe(new ItemStack(BlockManager.solarPanelT3()),
                "CCC",
                "PSP",
                "DED", 'D', Items.DIAMOND, 'P', Items.ENDER_PEARL, 'C', Blocks.GLASS, 'S', BlockManager.solarPanelT2(), 'E', ItemManager.eliteRFBattery());

        //Thermal Binder
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.thermalBinder()),
                "ACA",
                "BDB",
                "ACA", 'A', "ingotGold", 'B', Items.SLIME_BALL, 'C', Blocks.FURNACE, 'D', Blocks.REDSTONE_BLOCK));

        //Electric Logger
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.treeFarm()),
                "ACA",
                "BDB",
                "ACA", 'A', "ingotBronze", 'B', Items.IRON_AXE, 'C', Items.SHEARS, 'D', Blocks.REDSTONE_BLOCK));

        //RF Storage
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.basicRFStorage())),
                "ALA",
                "DCD",
                "ALA", 'A', "ingotIron", 'L', "ingotLead", 'C', Blocks.REDSTONE_BLOCK, 'D', new ItemStack(ItemManager.basicRFBattery(), 1, OreDictionary.WILDCARD_VALUE)));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.advancedRFStorage())),
                "ALA",
                "DCD",
                "ALA", 'A', "ingotGold", 'L', "ingotLead", 'C', BlockManager.basicRFStorage(), 'D', new ItemStack(ItemManager.advancedRFBattery(), 1, OreDictionary.WILDCARD_VALUE)));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.eliteRFStorage())),
                "ALA",
                "DCD",
                "ALA", 'A', "ingotSteel", 'L', "ingotLead", 'C', BlockManager.advancedRFStorage(), 'D', new ItemStack(ItemManager.eliteRFBattery(), 1, OreDictionary.WILDCARD_VALUE)));

        //Tanks
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.ironTank()),
                "ABA",
                "BCB",
                "ABA", 'A', Items.IRON_INGOT, 'B', "blockGlass", 'C', Items.BUCKET));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.goldTank()),
                "ABA",
                "BCB",
                "ABA", 'A', Items.GOLD_INGOT, 'B', "blockGlass", 'C', BlockManager.ironTank()));
        GameRegistry.addRecipe(new ShapedOreRecipe(Item.getItemFromBlock(BlockManager.diamondTank()),
                "ABA",
                "BCB",
                "ABA", 'A', Items.DIAMOND, 'B', "blockGlass", 'C', BlockManager.goldTank()));
        GameRegistry.addRecipe(new ShapedOreRecipe(Item.getItemFromBlock(BlockManager.voidTank()),
                "ABA",
                "BCB",
                "ABA", 'A', Blocks.OBSIDIAN, 'B', "blockGlass", 'C', Items.ENDER_PEARL));

        //Pipes
        //Basic
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.pipeBasicStructure()), 8),
                "AAA",
                "BBB",
                "AAA", 'A', Items.IRON_INGOT, 'B', "blockGlass"));

        //Colors!!
        for(EnumDyeColor color : EnumDyeColor.values()) {
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.pipeBasicStructure()), 1, color.ordinal()),
                    "pipeStructure", new ItemStack(Items.DYE, 1, color.getDyeDamage())));
        }

        //Power
        GameRegistry.addShapelessRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.pipeEnergyInterface())),
                Blocks.REDSTONE_BLOCK, BlockManager.pipeBasicStructure());

        //Item
        GameRegistry.addShapelessRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.pipeItemInterface())),
                Blocks.CHEST, BlockManager.pipeBasicStructure());


        //Liquid
        GameRegistry.addShapelessRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.pipeFluidInterface())),
                Items.BUCKET, BlockManager.pipeBasicStructure());


        //Crafter
        GameRegistry.addShapelessRecipe(new ItemStack(BlockManager.blockCrafter()),
                new ItemStack(Blocks.CRAFTING_TABLE), new ItemStack(Blocks.CHEST), new ItemStack(Blocks.CRAFTING_TABLE));


        //Miniature Sun
        GameRegistry.addRecipe(new ItemStack(BlockManager.blockMiniatureSun()),
                "ABA",
                "BCB",
                "ABA", 'A', Items.GOLD_INGOT, 'B', Items.GLOWSTONE_DUST, 'C', Items.NETHER_STAR);

        GameRegistry.addShapelessRecipe(new ItemStack(BlockManager.blockMiniatureStar()),
                Items.GLOWSTONE_DUST, Item.getItemFromBlock(Blocks.TORCH));

        for(EnumDyeColor color : EnumDyeColor.values()) {
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Item.getItemFromBlock(BlockManager.blockMiniatureStar()), 1, color.ordinal()),
                    "blockMiniatureStar", new ItemStack(Items.DYE, 1, color.getDyeDamage())));
        }

        //Player Plate
        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(BlockManager.playerPlate()),
                "ingotCopper", "ingotCopper"));

        //Chunk Loader
        GameRegistry.addRecipe(new ItemStack(BlockManager.chunkLoader()),
                "GIG",
                "IRI",
                "GIG", 'G', Blocks.GOLD_BLOCK, 'I', Blocks.IRON_BLOCK, 'R', Blocks.REDSTONE_BLOCK);

        //Flushable Chest
        GameRegistry.addShapelessRecipe(new ItemStack(BlockManager.flushableChest()),
                Blocks.CHEST, Items.FLINT_AND_STEEL);

        GameRegistry.addRecipe(new ItemStack(BlockManager.redstoneClock()),
                "SRS",
                "SBS",
                "SRS", 'S', Blocks.STONE, 'R', Items.REDSTONE, 'B', Blocks.REDSTONE_BLOCK);

        //Wrench
        GameRegistry.addRecipe(new ItemStack(ItemManager.wrench()),
                " I ",
                " II",
                "I  ", 'I', Items.IRON_INGOT);

        //Trash Bag
        GameRegistry.addRecipe(new ItemStack(ItemManager.trashBag(), 1),
                "S S",
                "L L",
                "LLL", 'L', Items.LEATHER, 'S', Items.STRING);

        //Magnet
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemManager.electroMagnet()),
                "I I",
                "L L",
                " L ", 'I', "ingotIron", 'L', "ingotLead"));

        //Spawmer Mover Thinggy
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemManager.spawnerMover(),1),
                "  E",
                " S ",
                "S  ", 'E', Items.EMERALD, 'S', "stickWood"));

        //Mob Gun
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemManager.mobGun(), 1),
                "   ",
                "III",
                "  B", 'I', "ingotSteel", 'B', "rfBattery"));

        //Mob Net
        GameRegistry.addRecipe(new ItemStack(ItemManager.mobNet(), 1),
                "S S",
                " E ",
                "S S", 'E', Items.ENDER_PEARL, 'S', Items.STRING);

        //Mob Stand
        GameRegistry.addRecipe(new ItemStack(BlockManager.mobStand(), 4),
                "QQQ",
                "Q Q",
                "Q Q", 'Q', Items.QUARTZ);

        //Add in Iron nugget - ingot
        MetalManager.Metal metal = MetalManager.getMetal("iron").get();
        GameRegistry.addShapelessRecipe(new ItemStack(metal.nugget().get(), 9), Items.IRON_INGOT);
        GameRegistry.addRecipe(new ItemStack(Items.IRON_INGOT, 1),
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
                "GLG", 'S', "ingotSilver", 'L', "ingotLead", 'G', Items.GOLD_INGOT, 'B', new ItemStack(ItemManager.basicRFBattery(), 1, OreDictionary.WILDCARD_VALUE)));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemManager.eliteRFBattery()),
                "S S",
                "DBD",
                "DLD", 'S', "ingotSilver", 'L', "ingotLead", 'D', Items.DIAMOND, 'B', new ItemStack(ItemManager.advancedRFBattery(), 1, OreDictionary.WILDCARD_VALUE)));

        GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(MetalManager.getMetal("bronze").get().dust().get(), 4),
                "dustCopper", "dustCopper", "dustCopper", "dustTin"));

        //RF Tools
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemManager.electricPickaxe()),
                "CCC",
                " S ",
                " B ", 'C', "ingotSteel", 'S', "stickWood", 'B', "rfBattery"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemManager.electricSword()),
                " B ",
                " B ",
                " b ", 'B', "ingotSteel", 'b', "rfBattery"));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemManager.electricArmorHelmet()),
                "SSS",
                "S S",
                " B ", 'S', "ingotSteel", 'B', "rfBattery"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemManager.electricArmorChestplate()),
                "S S",
                "SSS",
                "SBS", 'S', "ingotSteel", 'B', "rfBattery"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemManager.electricArmorLeggings()),
                "SSS",
                "S S",
                "SBS", 'S', "ingotSteel", 'B', "rfBattery"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemManager.electricArmorBoots()),
                "   ",
                "S S",
                "SBS", 'S', "ingotSteel", 'B', "rfBattery"));

        // Phantom Glass
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.phantomGlass(), 8),
                "GGG",
                "GDG",
                "GGG", 'G', "blockGlass", 'D', Items.OAK_DOOR));

        // Void Glass
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.voidGlass(), 8),
                "GDG",
                "GGG",
                "GGG", 'G', "blockGlass", 'D', "dyeBlack"));

        // Rock Climbing Wall
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockManager.rockWall(), 8),
                "GGG",
                "TGT",
                "GGG", 'G', Blocks.COBBLESTONE, 'T', "ingotTin"));

        //Smelting Recipes
        GameRegistry.addSmelting(MetalManager.getMetal("gold").get().dust().get(), new ItemStack(Items.GOLD_INGOT), 2.0F);
        GameRegistry.addSmelting(MetalManager.getMetal("iron").get().dust().get(), new ItemStack(Items.IRON_INGOT), 1.0F);

        UpgradeItemManager.registerRecipes();
    }
}
