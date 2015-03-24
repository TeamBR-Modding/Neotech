package com.dyonovan.jatm.handlers;

import com.dyonovan.jatm.common.blocks.BlockBakeable;
import com.dyonovan.jatm.common.blocks.BlockCrafter;
import com.dyonovan.jatm.common.blocks.BlockMachine;
import com.dyonovan.jatm.common.blocks.cable.BlockBasicCable;
import com.dyonovan.jatm.common.blocks.ore.BlockOre;
import com.dyonovan.jatm.common.blocks.storage.BlockRFStorage;
import com.dyonovan.jatm.common.blocks.storage.BlockTank;
import com.dyonovan.jatm.common.tileentity.cable.TileBasicCable;
import com.dyonovan.jatm.common.tileentity.generator.TileFluidGenerator;
import com.dyonovan.jatm.common.tileentity.machine.TileElectricCrusher;
import com.dyonovan.jatm.common.tileentity.machine.TileElectricFurnace;
import com.dyonovan.jatm.common.tileentity.generator.TileFurnaceGenerator;
import com.dyonovan.jatm.common.tileentity.machine.TileElectricMiner;
import com.dyonovan.jatm.common.tileentity.machine.TileEntityCrafter;
import com.dyonovan.jatm.common.tileentity.storage.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class BlockHandler {

    public static Block furnaceGenerator, electricFurnace, electricFurnaceActive, electricCrusher, electricCrusherActive, fluidGenerator;
    public static Block ironTank, goldTank, diamondTank, oreCopper, oreTin;
    public static Block basicCable, basicStorage, advancedStorage, eliteStorage, electricMiner;
    public static Block crafter;

    public static List<BlockBakeable> blockRegistry;

    public static void preInit() {
        blockRegistry = new ArrayList<>();

        registerBlock(furnaceGenerator = new BlockMachine(false, "furnaceGenerator", TileFurnaceGenerator.class, GuiHandler.FURNACE_GENERATOR_GUI_ID),
                "furnaceGenerator", TileFurnaceGenerator.class);
        registerBlock(fluidGenerator = new BlockMachine(false, "fluidGenerator", TileFluidGenerator.class, GuiHandler.FLUID_GENERATOR_GUI_ID),
                "fluidGenerator", TileFluidGenerator.class);
        registerBlock(electricFurnace = new BlockMachine(false, "electricFurnace", TileElectricFurnace.class, GuiHandler.ELECTRIC_FURNACE_GUI_ID),
                "electricFurnace", TileElectricFurnace.class);
        registerBlock(electricFurnaceActive = new BlockMachine(true, "electricFurnaceActive", TileElectricFurnace.class, GuiHandler.ELECTRIC_FURNACE_GUI_ID),
                "electricFurnaceActive", TileElectricFurnace.class);
        registerBlock(electricCrusher = new BlockMachine(false, "electricCrusher", TileElectricCrusher.class, GuiHandler.ELECTRIC_CRUSHER_GUI_ID),
                "electricCrusher", TileElectricCrusher.class);
        registerBlock(electricCrusherActive = new BlockMachine(true, "electricCrusherActive", TileElectricCrusher.class, GuiHandler.ELECTRIC_CRUSHER_GUI_ID),
                "electricCrusherActive", TileElectricCrusher.class);
        registerBlock(electricMiner = new BlockMachine(false, "electricMiner", TileElectricMiner.class, GuiHandler.ELECTRIC_MINER_GUI_ID),
                "electricMiner", TileElectricCrusher.class);
        registerBlock(basicStorage = new BlockRFStorage("basicRFStorage", TileBasicRFStorage.class, GuiHandler.RF_STORAGE_GUI_ID, 1),
                "basicRFStorage", TileBasicRFStorage.class);
        registerBlock(advancedStorage = new BlockRFStorage("advancedRFStorage", TileAdvancedRFStorage.class, GuiHandler.RF_STORAGE_GUI_ID, 2),
                "advancedRFStorage", TileAdvancedRFStorage.class);
        registerBlock(eliteStorage = new BlockRFStorage("eliteRFStorage", TileEliteRFStorage.class, GuiHandler.RF_STORAGE_GUI_ID, 3),
                "eliteRFStorage", TileEliteRFStorage.class);
        registerBlock(basicCable = new BlockBasicCable(Material.cloth, "basicCable"),
                "basicCable", TileBasicCable.class);
        registerBlock(ironTank = new BlockTank("ironTank", 1),
                "ironTank", TileIronTank.class);
        registerBlock(goldTank = new BlockTank("goldTank", 2),
                "goldTank", TileGoldTank.class);
        registerBlock(diamondTank = new BlockTank("diamondTank", 3),
                "diamondTank", TileDiamondTank.class);
        registerBlock(crafter = new BlockCrafter(),
                "crafter", TileEntityCrafter.class);
        registerBlock(oreCopper = new BlockOre("oreCopper", 1),
                "oreCopper", null, "oreCopper");
        registerBlock(oreTin = new BlockOre("oreTin", 2),
                "oreTin", null, "oreTin");
    }

    public static void registerBlock(Block block, String name, Class<? extends TileEntity> tileEntity, String oreDict) {
        GameRegistry.registerBlock(block, name);
        if(tileEntity != null)
            GameRegistry.registerTileEntity(tileEntity, name);
        if(oreDict != null)
            OreDictionary.registerOre(oreDict, block);
        blockRegistry.add((BlockBakeable) block);
    }

    private static void registerBlock(Block block, String name, Class<? extends TileEntity> tileEntity) {
        registerBlock(block, name, tileEntity, null);
    }
}
