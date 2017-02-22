package com.teambrmodding.neotech.managers;

import com.teambrmodding.neotech.common.blocks.machines.BlockMachine;
import com.teambrmodding.neotech.common.blocks.machines.BlockSolarPanel;
import com.teambrmodding.neotech.common.blocks.machines.BlockTreeFarm;
import com.teambrmodding.neotech.common.blocks.storage.BlockEnergyStorage;
import com.teambrmodding.neotech.common.blocks.storage.BlockFluidStorage;
import com.teambrmodding.neotech.common.blocks.storage.ItemBlockEnergyStorage;
import com.teambrmodding.neotech.common.blocks.storage.ItemBlockFluidStorage;
import com.teambrmodding.neotech.common.tiles.machines.generators.TileFluidGenerator;
import com.teambrmodding.neotech.common.tiles.machines.generators.TileFurnaceGenerator;
import com.teambrmodding.neotech.common.tiles.machines.generators.TileSolarPanel;
import com.teambrmodding.neotech.common.tiles.machines.operators.TileTreeFarm;
import com.teambrmodding.neotech.common.tiles.machines.processors.*;
import com.teambrmodding.neotech.common.tiles.storage.TileEnergyStorage;
import com.teambrmodding.neotech.common.tiles.storage.tanks.*;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/11/2017
 */
public class BlockManager {
    // Machines
    public static BlockMachine electricFurnace;
    public static BlockMachine electricCrusher;
    public static BlockMachine furnaceGenerator;
    public static BlockMachine fluidGenerator;
    public static BlockMachine electricCrucible;
    public static BlockMachine electricSolidifier;
    public static BlockMachine electricAlloyer;
    public static BlockMachine electricCentrifuge;

    // Operators
    public static BlockMachine treeFarm;

    // RF Storage
    public static BlockEnergyStorage basicRFStorage;
    public static BlockEnergyStorage advancedRFStorage;
    public static BlockEnergyStorage eliteRFStorage;
    public static BlockEnergyStorage creativeRFStorage;

    // Tanks
    public static BlockFluidStorage basicTank;
    public static BlockFluidStorage advancedTank;
    public static BlockFluidStorage eliteTank;
    public static BlockFluidStorage creativeTank;
    public static BlockFluidStorage voidTank;

    // Solar Panels
    public static BlockSolarPanel solarPanelT1;
    public static BlockSolarPanel solarPanelT2;
    public static BlockSolarPanel solarPanelT3;

    public static void preInit() {
        // Machines
        electricFurnace = registerBlock(new BlockMachine("electricFurnace", TileElectricFurnace.class), TileElectricFurnace.class);
        electricCrusher = registerBlock(new BlockMachine("electricCrusher", TileElectricCrusher.class), TileElectricCrusher.class);
        furnaceGenerator = registerBlock(new BlockMachine("furnaceGenerator", TileFurnaceGenerator.class), TileFurnaceGenerator.class);
        fluidGenerator = registerBlock( new BlockMachine("fluidGenerator", TileFluidGenerator.class), TileFluidGenerator.class);
        electricCrucible = registerBlock(new BlockMachine("electricCrucible", TileCrucible.class), TileCrucible.class);
        electricSolidifier = registerBlock(new BlockMachine("electricSolidifier", TileSolidifier.class), TileSolidifier.class);
        electricAlloyer = registerBlock(new BlockMachine("alloyer", TileAlloyer.class), TileAlloyer.class);
        electricCentrifuge = registerBlock(new BlockMachine("centrifuge", TileCentrifuge.class), TileCentrifuge.class);

        // Operators
        treeFarm = registerBlock(new BlockTreeFarm("treeFarm"), TileTreeFarm.class);

        // RF Storage
        basicRFStorage = new BlockEnergyStorage("basicRFStorage", 1);
        registerBlock(basicRFStorage, new ItemBlockEnergyStorage(basicRFStorage), TileEnergyStorage.class, null);
        advancedRFStorage = new BlockEnergyStorage("advancedRFStorage", 2);
        registerBlock(advancedRFStorage, new ItemBlockEnergyStorage(advancedRFStorage), TileEnergyStorage.class, null);
        eliteRFStorage = new BlockEnergyStorage("eliteRFStorage", 3);
        registerBlock(eliteRFStorage, new ItemBlockEnergyStorage(eliteRFStorage), TileEnergyStorage.class, null);
        creativeRFStorage = new BlockEnergyStorage("creativeRFStorage", 4);
        registerBlock(creativeRFStorage, new ItemBlockEnergyStorage(creativeRFStorage), TileEnergyStorage.class, null);

        // Tanks
        basicTank = new BlockFluidStorage("basicTank", TileBasicTank.class);
        registerBlock(basicTank, new ItemBlockFluidStorage(basicTank), TileBasicTank.class, null);
        advancedTank = new BlockFluidStorage("advancedTank", TileAdvancedTank.class);
        registerBlock(advancedTank, new ItemBlockFluidStorage(advancedTank), TileAdvancedTank.class, null);
        eliteTank = new BlockFluidStorage("eliteTank", TileEliteTank.class);
        registerBlock(eliteTank, new ItemBlockFluidStorage(eliteTank), TileEliteTank.class, null);
        creativeTank = new BlockFluidStorage("creativeTank", TileCreativeTank.class);
        registerBlock(creativeTank, new ItemBlockFluidStorage(creativeTank), TileCreativeTank.class, null);
        voidTank = new BlockFluidStorage("voidTank", TileVoidTank.class);
        registerBlock(voidTank, new ItemBlockFluidStorage(voidTank), TileVoidTank.class, null);

        // Solar Panels
        solarPanelT1 = registerBlock(new BlockSolarPanel("solarPanelT1", 1), TileSolarPanel.class);
        solarPanelT2 = registerBlock(new BlockSolarPanel("solarPanelT2", 2), TileSolarPanel.class);
        solarPanelT3 = registerBlock(new BlockSolarPanel("solarPanelT3", 3), TileSolarPanel.class);
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
