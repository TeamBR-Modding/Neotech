package com.teambrmodding.neotech.managers;

import com.teambrmodding.neotech.common.blocks.machines.BlockMachine;
import com.teambrmodding.neotech.common.blocks.machines.BlockSolarPanel;
import com.teambrmodding.neotech.common.blocks.storage.BlockRFStorage;
import com.teambrmodding.neotech.common.blocks.storage.BlockTank;
import com.teambrmodding.neotech.common.tiles.machines.generators.TileFluidGenerator;
import com.teambrmodding.neotech.common.tiles.machines.generators.TileFurnaceGenerator;
import com.teambrmodding.neotech.common.tiles.machines.generators.TileSolarPanel;
import com.teambrmodding.neotech.common.tiles.machines.operators.TileTreeFarm;
import com.teambrmodding.neotech.common.tiles.machines.processors.*;
import com.teambrmodding.neotech.common.tiles.storage.TileRFStorage;
import com.teambrmodding.neotech.common.tiles.storage.tanks.*;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/11/2017
 */
public class BlockManager {
    // Machines
    public static BlockMachine electricFurnace    = new BlockMachine("electricFurnace", TileElectricFurnace.class);
    public static BlockMachine electricCrusher    = new BlockMachine("electricCrusher", TileElectricCrusher.class);
    public static BlockMachine furnaceGenerator   = new BlockMachine("furnaceGenerator", TileFurnaceGenerator.class);
    public static BlockMachine fluidGenerator     = new BlockMachine("fluidGenerator", TileFluidGenerator.class);
    public static BlockMachine electricCrucible   = new BlockMachine("electricCrucible", TileCrucible.class);
    public static BlockMachine electricSolidifier = new BlockMachine("electricSolidifier", TileSolidifier.class);
    public static BlockMachine electricAlloyer    = new BlockMachine("alloyer", TileAlloyer.class);
    public static BlockMachine electricCentrifuge = new BlockMachine("centrifuge", TileCentrifuge.class);

    // Operators
    public static BlockMachine treeFarm = new BlockMachine("treeFarm", TileTreeFarm.class);

    // RF Storage
    public static BlockRFStorage basicRFStorage = new BlockRFStorage("basicRFStorage", 1);
    public static BlockRFStorage advancedRFStorage = new BlockRFStorage("advancedRFStorage", 2);
    public static BlockRFStorage eliteRFStorage = new BlockRFStorage("eliteRFStorage", 3);
    public static BlockRFStorage creativeRFStorage = new BlockRFStorage("creativeRFStorage", 4);

    // Tanks
    public static BlockTank ironTank = new BlockTank("ironTank", 1);
    public static BlockTank goldTank = new BlockTank("goldTank", 2);
    public static BlockTank diamondTank = new BlockTank("diamondTank", 3);
    public static BlockTank creativeTank = new BlockTank("creativeTank", 4);
    public static BlockTank voidTank = new BlockTank("voidTank", 5);

    // Solar Panels
    public static BlockSolarPanel solarPanelT1 = new BlockSolarPanel("solarPanelT1", 1);
    public static BlockSolarPanel solarPanelT2 = new BlockSolarPanel("solarPanelT2", 2);
    public static BlockSolarPanel solarPanelT3 = new BlockSolarPanel("solarPanelT3", 3);

    public static void preInit() {
        // Machines
        registerBlock(electricFurnace, TileElectricFurnace.class);
        registerBlock(electricCrusher, TileElectricCrusher.class);
        registerBlock(furnaceGenerator, TileFurnaceGenerator.class);
        registerBlock(fluidGenerator, TileFluidGenerator.class);
        registerBlock(electricCrucible, TileCrucible.class);
        registerBlock(electricSolidifier, TileSolidifier.class);
        registerBlock(electricAlloyer, TileAlloyer.class);
        registerBlock(electricCentrifuge, TileCentrifuge.class);

        // Operators
        registerBlock(treeFarm, TileTreeFarm.class);

        // RF Storage
        registerBlock(basicRFStorage, TileRFStorage.class);
        registerBlock(advancedRFStorage, TileRFStorage.class);
        registerBlock(eliteRFStorage, TileRFStorage.class);
        registerBlock(creativeRFStorage, TileRFStorage.class);

        // Tanks
        registerBlock(ironTank, TileBasicTank.class);
        registerBlock(goldTank, TileAdvancedTank.class);
        registerBlock(diamondTank, TileEliteTank.class);
        registerBlock(creativeTank, TileCreativeTank.class);
        registerBlock(voidTank, TileVoidTank.class);

        // Solar Panels
        registerBlock(solarPanelT1, TileSolarPanel.class);
        registerBlock(solarPanelT2, TileSolarPanel.class);
        registerBlock(solarPanelT3, TileSolarPanel.class);
    }

    /**
     * Helper Method to register a block
     * @param block The block to register
     * @param itemBlock The item block for this block
     * @param tileEntity The tile class
     * @param oreDict The ore dict tag
     * @return The block registered
     */
    public static <T extends Block> T registerBlock(T block, ItemBlock itemBlock,
                                                    @Nullable Class<? extends TileEntity> tileEntity,
                                                    @Nullable String oreDict) {
        GameRegistry.register(block);
        GameRegistry.register(itemBlock);

        if(tileEntity != null)
            GameRegistry.registerTileEntity(tileEntity, block.getRegistryName().toString());
        if(oreDict != null)
            OreDictionary.registerOre(oreDict, block);

        return block;
    }

    /**
     * Short hand to register a block
     */
    public static <T extends Block> T registerBlock(T block, @Nullable Class<? extends TileEntity> tileEntity) {
        ItemBlock itemBlock = new ItemBlock(block);
        itemBlock.setRegistryName(block.getRegistryName());
        return registerBlock(block, itemBlock, tileEntity, null);
    }

    /**
     * Short hand to register a block
     */
    public static <T extends Block> T registerBlock(T block, String oreDict) {
        ItemBlock itemBlock = new ItemBlock(block);
        itemBlock.setRegistryName(block.getRegistryName());
        return registerBlock(block, itemBlock, null, oreDict);    }

    /**
     * Short hand to register a block
     */
    public static <T extends Block> T registerBlock(T block) {
        ItemBlock itemBlock = new ItemBlock(block);
        itemBlock.setRegistryName(block.getRegistryName());
        return registerBlock(block, itemBlock, null, null);    }
}
