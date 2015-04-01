package com.dyonovan.neotech.common.blocks.connected;

import com.dyonovan.neotech.helpers.GuiHelper;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import javax.swing.text.JTextComponent;
import java.util.List;

public class BlockClimbingRock extends BlockConnectedTextures {
    public BlockClimbingRock(Material materialIn, String name, Class<? extends TileEntity> tileClass) {
        super(materialIn, name, tileClass);
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isNormalCube() {
        return false;
    }

    @Override public boolean isLadder(IBlockAccess world, BlockPos pos, EntityLivingBase entity) { return true; }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        super.onBlockActivated(world, pos, state, player, side, hitX, hitY, hitZ);
        if(player.getCurrentEquippedItem() == null) {
            if(pos.distanceSq(player.posX, player.posY, player.posZ) < 9.0) {
                player.setPosition(pos.offset(side).getX() + 0.5, pos.offset(side).getY() + 0.5, pos.offset(side).getZ() + 0.5);
                return true;
            }
        }
        return false;
    }
        @Override
    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List list, Entity collidingEntity) {
        float border = 0.0625F;
        setBlockBounds(0.0F + border, 0.0F, 0.0F + border, 1.0F - border, 1.0F, 1.0F - border);
        if (((collidingEntity instanceof EntityPlayer))) {
            EntityPlayer player = (EntityPlayer)collidingEntity;
            player.fallDistance = 0.0F;
            if (player.motionY < -0.15D) {
                player.motionY = -0.15D;
            }
            if ((Keyboard.isKeyDown(Keyboard.KEY_W)) &&
                    (player.motionY < 0.2D)) {
                player.motionY = 0.2D;
            }
            if(player.isSneaking())
                player.motionY = 0.08;
        }
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
        return side == EnumFacing.UP || side == EnumFacing.DOWN;
    }

    @Override
    public void addToolTip(List<String> toolTip) {
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            toolTip.add(GuiHelper.GuiColor.YELLOW + "Jump onto this wall to start climbing");
            toolTip.add("Works like a ladder");
            toolTip.add("Right click with an empty hand to climb to that block");
        } else {
            toolTip.add(GuiHelper.GuiColor.GRAY + "Shift for More Info");
        }
    }
}
