package com.dyonovan.neotech.handlers;

import com.dyonovan.neotech.common.blocks.BlockBakeable;
import com.dyonovan.neotech.common.blocks.BlockCrafter;
import com.dyonovan.neotech.common.blocks.BlockMachine;
import com.dyonovan.neotech.common.blocks.connected.BlockClimbingRock;
import com.dyonovan.neotech.common.blocks.connected.BlockConnectedTextures;
import com.dyonovan.neotech.common.blocks.connected.BlockPhantomGlass;
import com.dyonovan.neotech.common.blocks.connected.BlockVoidGlass;
import com.dyonovan.neotech.common.blocks.misc.BlockTrampoline;
import com.dyonovan.neotech.common.blocks.ore.BlockOre;
import com.dyonovan.neotech.common.blocks.pipe.energy.BlockAdvancedCable;
import com.dyonovan.neotech.common.blocks.pipe.energy.BlockBasicCable;
import com.dyonovan.neotech.common.blocks.pipe.energy.BlockEliteCable;
import com.dyonovan.neotech.common.blocks.pipe.item.BlockPipeAdvancedItem;
import com.dyonovan.neotech.common.blocks.pipe.item.BlockPipeBasicItem;
import com.dyonovan.neotech.common.blocks.pipe.item.BlockPipeEliteItem;
import com.dyonovan.neotech.common.blocks.storage.BlockRFStorage;
import com.dyonovan.neotech.common.blocks.storage.BlockTank;
import com.dyonovan.neotech.common.pipe.energy.PipeAdvancedEnergy;
import com.dyonovan.neotech.common.pipe.energy.PipeBasicEnergy;
import com.dyonovan.neotech.common.pipe.energy.PipeEliteEnergy;
import com.dyonovan.neotech.common.pipe.item.PipeAdvancedItem;
import com.dyonovan.neotech.common.pipe.item.PipeBasicItem;
import com.dyonovan.neotech.common.pipe.item.PipeEliteItem;
import com.dyonovan.neotech.common.tileentity.generator.TileFluidGenerator;
import com.dyonovan.neotech.common.tileentity.generator.TileFurnaceGenerator;
import com.dyonovan.neotech.common.tileentity.machine.*;
import com.dyonovan.neotech.common.tileentity.storage.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class BlockHandler {

    public static Block furnaceGenerator, furnaceGeneratorActive, electricFurnace, electricFurnaceActive, electricCrusher, electricCrusherActive, fluidGenerator, fluidGeneratorActive;
    public static Block ironTank, goldTank, diamondTank, oreCopper, oreTin;
    public static Block basicCable, advancedCable, eliteCable, basicStorage, advancedStorage, eliteStorage, electricMiner, electricMinerActive;
    public static Block crafter, thermalBinder, creativeStorage, blockCopper, blockTin;
    public static Block basicItemPipe, advancedItemPipe, eliteItemPipe;
    public static Block phantomGlass, voidGlass, borderedBrick, borderedStoneBrick, climbingRock;
    public static Block trampoline;

    public static List<BlockBakeable> blockRegistry;

    public static void preInit() {
        blockRegistry = new ArrayList<>();

        //Machines
        registerBlock(furnaceGenerator = new BlockMachine(false, "furnaceGenerator", TileFurnaceGenerator.class, GuiHandler.FURNACE_GENERATOR_GUI_ID),
                "furnaceGenerator", TileFurnaceGenerator.class);
        registerBlock(furnaceGeneratorActive = new BlockMachine(true, "furnaceGeneratorActive", TileFurnaceGenerator.class, GuiHandler.FURNACE_GENERATOR_GUI_ID),
                "furnaceGeneratorActive", TileFurnaceGenerator.class);
        registerBlock(fluidGenerator = new BlockMachine(false, "fluidGenerator", TileFluidGenerator.class, GuiHandler.FLUID_GENERATOR_GUI_ID),
                "fluidGenerator", TileFluidGenerator.class);
        registerBlock(fluidGeneratorActive = new BlockMachine(true, "fluidGeneratorActive", TileFluidGenerator.class, GuiHandler.FLUID_GENERATOR_GUI_ID),
                "fluidGeneratorActive", TileFluidGenerator.class);
        registerBlock(electricFurnace = new BlockMachine(false, "electricFurnace", TileElectricFurnace.class, GuiHandler.ELECTRIC_FURNACE_GUI_ID),
                "electricFurnace", TileElectricFurnace.class);
        registerBlock(electricFurnaceActive = new BlockMachine(true, "electricFurnaceActive", TileElectricFurnace.class, GuiHandler.ELECTRIC_FURNACE_GUI_ID),
                "electricFurnaceActive", TileElectricFurnace.class);
        registerBlock(electricCrusher = new BlockMachine(false, "electricCrusher", TileElectricCrusher.class, GuiHandler.ELECTRIC_CRUSHER_GUI_ID),
                "electricCrusher", TileElectricCrusher.class);
        registerBlock(electricCrusherActive = new BlockMachine(true, "electricCrusherActive", TileElectricCrusher.class, GuiHandler.ELECTRIC_CRUSHER_GUI_ID),
                "electricCrusherActive", TileElectricCrusher.class);
        registerBlock(electricMiner = new BlockMachine(false, "electricMiner", TileElectricMiner.class, GuiHandler.ELECTRIC_MINER_GUI_ID),
                "electricMiner", TileElectricMiner.class);
        registerBlock(electricMinerActive = new BlockMachine(true, "electricMinerActive", TileElectricMiner.class, GuiHandler.ELECTRIC_MINER_GUI_ID),
                "electricMinerActive", TileElectricMiner.class);
        registerBlock(thermalBinder = new BlockMachine(false, "thermalBinder", TileThermalBinder.class, GuiHandler.THERMAL_BINDER_GUI_ID),
                "thermalBinder", TileThermalBinder.class);


        //Storage Blocks
        registerBlock(basicStorage = new BlockRFStorage("basicRFStorage", TileBasicRFStorage.class, GuiHandler.RF_STORAGE_GUI_ID, 1),
                "basicRFStorage", TileBasicRFStorage.class);
        registerBlock(advancedStorage = new BlockRFStorage("advancedRFStorage", TileAdvancedRFStorage.class, GuiHandler.RF_STORAGE_GUI_ID, 2),
                "advancedRFStorage", TileAdvancedRFStorage.class);
        registerBlock(eliteStorage = new BlockRFStorage("eliteRFStorage", TileEliteRFStorage.class, GuiHandler.RF_STORAGE_GUI_ID, 3),
                "eliteRFStorage", TileEliteRFStorage.class);
        registerBlock(creativeStorage = new BlockRFStorage("creativeRFStorage", TileCreativeRFStorage.class, GuiHandler.RF_STORAGE_GUI_ID, 4),
                "creativeRFStorage", TileCreativeRFStorage.class);
        registerBlock(ironTank = new BlockTank("ironTank", 1),
                "ironTank", TileIronTank.class);
        registerBlock(goldTank = new BlockTank("goldTank", 2),
                "goldTank", TileGoldTank.class);
        registerBlock(diamondTank = new BlockTank("diamondTank", 3),
                "diamondTank", TileDiamondTank.class);

        //Crafter
        registerBlock(crafter = new BlockCrafter(),
                "crafter", TileEntityCrafter.class);

        //Ores
        registerBlock(oreCopper = new BlockOre("oreCopper", 1),
                "oreCopper", null, "oreCopper");
        registerBlock(oreTin = new BlockOre("oreTin", 2),
                "oreTin", null, "oreTin");
        registerBlock(blockCopper = new BlockOre("blockCopper", 1),
                "blockCopper", null, "blockCopper");
        registerBlock(blockTin = new BlockOre("blockTin", 2),
                "blockTin", null, "blockTin");

        //Cables & Pipes
        registerBlock(basicCable = new BlockBasicCable(Material.cloth, "basicCable", PipeBasicEnergy.class),
                "basicCable", PipeBasicEnergy.class);
        registerBlock(advancedCable = new BlockAdvancedCable(Material.iron, "advancedCable", PipeAdvancedEnergy.class),
                "advancedCable", PipeAdvancedEnergy.class);
        registerBlock(eliteCable = new BlockEliteCable(Material.iron, "eliteCable", PipeEliteEnergy.class),
                "eliteCable", PipeEliteEnergy.class);

        registerBlock(basicItemPipe = new BlockPipeBasicItem(Material.cloth, "basicItemPipe", PipeBasicItem.class),
                "basicItemPipe", PipeBasicItem.class);
        registerBlock(advancedItemPipe = new BlockPipeAdvancedItem(Material.iron, "advancedItemPipe", PipeAdvancedItem.class),
                "advancedItemPipe", PipeAdvancedItem.class);
        registerBlock(eliteItemPipe = new BlockPipeEliteItem(Material.iron, "eliteItemPipe", PipeEliteItem.class),
                "eliteItemPipe", PipeEliteItem.class);

        //Misc
        registerBlock(trampoline = new BlockTrampoline(Material.cloth, "trampoline", null),
                "trampoline", null);

        //Connected Textures
        registerBlock(phantomGlass = new BlockPhantomGlass(Material.glass, "phantomGlass", null),
                "phantomGlass", null);
        registerBlock(voidGlass = new BlockVoidGlass(Material.glass, "voidGlass", null),
                "voidGlass", null);
        registerBlock(borderedBrick = new BlockConnectedTextures(Material.rock, "borderedBrick", null),
                "borderedBrick", null);
        registerBlock(borderedStoneBrick = new BlockConnectedTextures(Material.rock, "borderedStoneBrick", null),
                "borderedStoneBrick", null);
        registerBlock(climbingRock = new BlockClimbingRock(Material.rock, "climbingRock", null),
                "climbingRock", null);
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
