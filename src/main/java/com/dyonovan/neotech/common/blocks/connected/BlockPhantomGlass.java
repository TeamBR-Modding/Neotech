package com.dyonovan.neotech.common.blocks.connected;

import com.dyonovan.neotech.helpers.GuiHelper;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class BlockPhantomGlass extends BlockConnectedTextures {
    public BlockPhantomGlass(Material materialIn, String name, Class<? extends TileEntity> tileClass) {
        super(materialIn, name, tileClass);
        setHardness(1.5F);
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List list, Entity collidingEntity) {
        if (((collidingEntity instanceof EntityPlayer)) && (!collidingEntity.isSneaking())) {
            return;
        }
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
    }

    @SideOnly(Side.CLIENT)
    public boolean isTranslucent() {
        return true;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public void addToolTip(List<String> toolTip) {
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            toolTip.add(GuiHelper.GuiColor.YELLOW + "Is solid to all entities except players");
            toolTip.add("Sneaking removes this effect");
        } else {
            toolTip.add(GuiHelper.GuiColor.GRAY + "Shift for More Info");
        }
    }
}
