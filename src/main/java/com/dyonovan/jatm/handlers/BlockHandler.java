package com.dyonovan.jatm.handlers;

import com.dyonovan.jatm.common.blocks.BlockMachine;
import com.dyonovan.jatm.common.tileentity.machine.TileElectricCrusher;
import com.dyonovan.jatm.common.tileentity.machine.TileElectricFurnace;
import com.dyonovan.jatm.common.tileentity.generator.TileCoalGenerator;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockHandler {

    public static Block coalGenerator, electricFurnace, electricCrusher;

    public static void init() {

        registerBlock(coalGenerator = new BlockMachine("coalGenerator", TileCoalGenerator.class, GuiHandler.COAL_GENERATOR_GUI_ID),
                "coalGenerator", TileCoalGenerator.class);
        registerBlock(electricFurnace = new BlockMachine("electricFurnace", TileElectricFurnace.class, GuiHandler.ELECTRIC_FURNACE_GUI_ID),
                "electricFurnace", TileElectricFurnace.class);
        registerBlock(electricCrusher = new BlockMachine("electricCrusher", TileElectricCrusher.class, GuiHandler.ELECTRIC_CRUSHER_GUI_ID),
                "electricCrusher", TileElectricCrusher.class);


    }

    public static void registerBlock(Block block, String name, Class<? extends TileEntity> tileEntity) {
        GameRegistry.registerBlock(block, name);
        GameRegistry.registerTileEntity(tileEntity, name);
    }
}
