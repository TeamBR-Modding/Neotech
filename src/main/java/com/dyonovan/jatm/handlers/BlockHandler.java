package com.dyonovan.jatm.handlers;

import com.dyonovan.jatm.common.blocks.BlockBakeable;
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
import com.dyonovan.jatm.common.tileentity.storage.TileBasicRFStorage;
import com.dyonovan.jatm.common.tileentity.storage.TileDiamondTank;
import com.dyonovan.jatm.common.tileentity.storage.TileGoldTank;
import com.dyonovan.jatm.common.tileentity.storage.TileIronTank;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class BlockHandler {

    public static Block furnaceGenerator, electricFurnace, electricCrusher, fluidGenerator;
    public static Block ironTank, goldTank, diamondTank, oreCopper;
    public static Block basicCable, basicStorage;

    public static List<BlockBakeable> blockRegistry;

    public static void preInit() {
        blockRegistry = new ArrayList<>();

        registerBlock(furnaceGenerator = new BlockMachine("furnaceGenerator", TileFurnaceGenerator.class, GuiHandler.FURNACE_GENERATOR_GUI_ID),
                "furnaceGenerator", TileFurnaceGenerator.class);
        registerBlock(fluidGenerator = new BlockMachine("fluidGenerator", TileFluidGenerator.class, GuiHandler.FLUID_GENERATOR_GUI_ID),
                "fluidGenerator", TileFluidGenerator.class);
        registerBlock(electricFurnace = new BlockMachine("electricFurnace", TileElectricFurnace.class, GuiHandler.ELECTRIC_FURNACE_GUI_ID),
                "electricFurnace", TileElectricFurnace.class);
        registerBlock(electricCrusher = new BlockMachine("electricCrusher", TileElectricCrusher.class, GuiHandler.ELECTRIC_CRUSHER_GUI_ID),
                "electricCrusher", TileElectricCrusher.class);
        registerBlock(basicStorage = new BlockRFStorage("basicRFStorage", TileBasicRFStorage.class, GuiHandler.RF_STORAGE_GUI_ID, 1),
                "basicRFStorage", TileBasicRFStorage.class);
        registerBlock(basicCable = new BlockBasicCable(Material.cloth, "basicCable"),
                "basicCable", TileBasicCable.class);
        registerBlock(ironTank = new BlockTank("ironTank", 1),
                "ironTank", TileIronTank.class);
        registerBlock(goldTank = new BlockTank("goldTank", 2),
                "goldTank", TileGoldTank.class);
        registerBlock(diamondTank = new BlockTank("diamondTank", 3),
                "diamondTank", TileDiamondTank.class);
        registerBlock(oreCopper = new BlockOre("oreCopper"),
                "oreCopper", null, "oreCopper");
    }

    public static void registerBlock(Block block, String name, Class<? extends TileEntity> tileEntity, String oreDict) {
        GameRegistry.registerBlock(block, name);
        if(tileEntity != null)
            GameRegistry.registerTileEntity(tileEntity, name);
        if(oreDict != null)
            OreDictionary.registerOre(oreDict, block);
        blockRegistry.add((BlockBakeable)block);
    }

    private static void registerBlock(Block block, String name, Class<? extends TileEntity> tileEntity) {
        registerBlock(block, name, tileEntity, null);
    }
}
