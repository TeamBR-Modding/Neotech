package com.dyonovan.jatm.handlers;

import com.dyonovan.jatm.client.gui.storage.GuiRFStorage;
import com.dyonovan.jatm.common.container.generators.ContainerFluidGenerator;
import com.dyonovan.jatm.client.gui.generators.GuiFluidGenerator;
import com.dyonovan.jatm.client.gui.machine.GuiElectricCrusher;
import com.dyonovan.jatm.client.gui.machine.GuiElectricFurnace;
import com.dyonovan.jatm.client.gui.generators.GuiFurnaceGenerator;
import com.dyonovan.jatm.common.container.machine.ContainerElectricCrusher;
import com.dyonovan.jatm.common.container.machine.ContainerElectricFurnace;
import com.dyonovan.jatm.common.container.generators.ContainerFurnaceGenerator;
import com.dyonovan.jatm.common.container.storage.ContainerRFStorage;
import com.dyonovan.jatm.common.tileentity.generator.TileFluidGenerator;
import com.dyonovan.jatm.common.tileentity.machine.TileElectricCrusher;
import com.dyonovan.jatm.common.tileentity.machine.TileElectricFurnace;
import com.dyonovan.jatm.common.tileentity.generator.TileFurnaceGenerator;
import com.dyonovan.jatm.common.tileentity.storage.TileRFStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

    public static final int FURNACE_GENERATOR_GUI_ID = 0;
    public static final int ELECTRIC_FURNACE_GUI_ID = 1;
    public static final int ELECTRIC_CRUSHER_GUI_ID = 2;
    public static final int FLUID_GENERATOR_GUI_ID = 3;
    public static final int RF_STORAGE_GUI_ID = 4;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch(ID) {
            case FURNACE_GENERATOR_GUI_ID:
                return new ContainerFurnaceGenerator(player.inventory, (TileFurnaceGenerator) world.getTileEntity(new BlockPos(x, y, z)));
            case ELECTRIC_FURNACE_GUI_ID:
                return new ContainerElectricFurnace(player.inventory, (TileElectricFurnace) world.getTileEntity(new BlockPos(x, y, z)));
            case ELECTRIC_CRUSHER_GUI_ID:
                return new ContainerElectricCrusher(player.inventory, (TileElectricCrusher) world.getTileEntity(new BlockPos(x, y, z)));
            case FLUID_GENERATOR_GUI_ID:
                return new ContainerFluidGenerator(player.inventory, (TileFluidGenerator) world.getTileEntity(new BlockPos(x, y, z)));
            case RF_STORAGE_GUI_ID:
                return new ContainerRFStorage(player.inventory, (TileRFStorage) world.getTileEntity(new BlockPos(x, y, z)));
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch(ID) {
            case FURNACE_GENERATOR_GUI_ID:
                return new GuiFurnaceGenerator(player.inventory, (TileFurnaceGenerator) world.getTileEntity(new BlockPos(x, y, z)));
            case ELECTRIC_FURNACE_GUI_ID:
                return new GuiElectricFurnace(player.inventory, (TileElectricFurnace) world.getTileEntity(new BlockPos(x, y, z)));
            case ELECTRIC_CRUSHER_GUI_ID:
                return new GuiElectricCrusher(player.inventory, (TileElectricCrusher) world.getTileEntity(new BlockPos(x, y, z)));
            case FLUID_GENERATOR_GUI_ID:
                return new GuiFluidGenerator(player.inventory, (TileFluidGenerator) world.getTileEntity(new BlockPos(x, y, z)));
            case RF_STORAGE_GUI_ID:
                return new GuiRFStorage(player.inventory, (TileRFStorage) world.getTileEntity(new BlockPos(x, y, z)));
        }
        return null;
    }
}
