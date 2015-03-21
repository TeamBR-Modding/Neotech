package com.dyonovan.jatm.handlers;

import com.dyonovan.jatm.common.blocks.BlockBakeable;
import com.dyonovan.jatm.common.blocks.BlockMachine;
import com.dyonovan.jatm.common.blocks.BlockBasicCable;
import com.dyonovan.jatm.common.blocks.storage.BlockRFStorage;
import com.dyonovan.jatm.common.blocks.BlockTank;
import com.dyonovan.jatm.common.tileentity.cable.TileBasicCable;
import com.dyonovan.jatm.common.tileentity.generator.TileFluidGenerator;
import com.dyonovan.jatm.common.tileentity.machine.TileElectricCrusher;
import com.dyonovan.jatm.common.tileentity.machine.TileElectricFurnace;
import com.dyonovan.jatm.common.tileentity.generator.TileFurnaceGenerator;
import com.dyonovan.jatm.common.tileentity.notmachines.TileRFStorage;
import com.dyonovan.jatm.common.tileentity.notmachines.TileTank;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;

public class BlockHandler {

    public static Block furnaceGenerator, electricFurnace, electricCrusher, fluidGenerator;
    public static Block basicTank;
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
        registerBlock(basicCable = new BlockBasicCable(Material.cloth, "basicCable"),
                "basicCable", TileBasicCable.class);
        registerBlock(basicTank = new BlockTank("basicTank", TileTank.class),
                "basicTank", TileTank.class);
        registerBlock(basicStorage = new BlockRFStorage("basicRFStorage", TileRFStorage.class, GuiHandler.RF_STORAGE_GUI_ID),
                "basicRFStorage", TileRFStorage.class);
    }

    public static void registerBlock(Block block, String name, Class<? extends TileEntity> tileEntity) {
        GameRegistry.registerBlock(block, name);
        GameRegistry.registerTileEntity(tileEntity, name);
        blockRegistry.add((BlockBakeable)block);
    }
}
