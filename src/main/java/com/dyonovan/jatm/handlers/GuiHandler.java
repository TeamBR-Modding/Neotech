package com.dyonovan.jatm.handlers;

import com.dyonovan.jatm.client.gui.generators.GuiGenerator;
import com.dyonovan.jatm.common.container.generators.ContainerGenerator;
import com.dyonovan.jatm.common.tileentity.TileGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

    public static final int GENERATOR_GUI_ID = 0;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch(ID) {
            case GENERATOR_GUI_ID:
                return new ContainerGenerator(player.inventory, (TileGenerator) world.getTileEntity(new BlockPos(x, y, z)));
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch(ID) {
            case GENERATOR_GUI_ID:
                return new GuiGenerator(player.inventory, (TileGenerator) world.getTileEntity(new BlockPos(x, y, z)));
        }
        return null;
    }
}
