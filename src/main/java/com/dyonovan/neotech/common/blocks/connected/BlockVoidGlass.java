package com.dyonovan.neotech.common.blocks.connected;

import com.dyonovan.neotech.helpers.GuiHelper;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class BlockVoidGlass extends BlockConnectedTextures {
    public BlockVoidGlass(Material materialIn, String name, Class<? extends TileEntity> tileClass) {
        super(materialIn, name, tileClass);
        setHardness(2.0F);
        setLightOpacity(1000);
    }

    @SideOnly(Side.CLIENT)
    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.TRANSLUCENT;
    }

    @SideOnly(Side.CLIENT)
    public boolean isTranslucent() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public void addToolTip(List<String> toolTip) {
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            toolTip.add(GuiHelper.GuiColor.YELLOW + "Absorbs all light passing through it");
        } else {
            toolTip.add(GuiHelper.GuiColor.GRAY + "Shift for More Info");
        }
    }
}
