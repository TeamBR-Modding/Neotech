package com.dyonovan.jatm.events;

import com.dyonovan.jatm.handlers.BlockHandler;
import com.dyonovan.jatm.helpers.GuiHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.tools.Tool;

public class ToolTipEvent {
    @SubscribeEvent
    public void onToolTip(ItemTooltipEvent event) {
        if(event.itemStack != null) {
            if(event.itemStack.getItem() == Item.getItemFromBlock(BlockHandler.ironTank)) {
                if(event.itemStack.hasTagCompound()) {
                    NBTTagCompound liquidTag = event.itemStack.getTagCompound().getCompoundTag("Fluid");
                    if (liquidTag != null) {
                        FluidStack liquid = FluidStack.loadFluidStackFromNBT(liquidTag);
                        event.toolTip.add(GuiHelper.GuiColor.YELLOW + liquid.getLocalizedName());
                        event.toolTip.add(liquid.amount + "/" + FluidContainerRegistry.BUCKET_VOLUME * 8 + "mb");
                    }
                }
            }
            if(event.itemStack.getItem() == Item.getItemFromBlock(BlockHandler.goldTank)) {
                if(event.itemStack.hasTagCompound()) {
                    NBTTagCompound liquidTag = event.itemStack.getTagCompound().getCompoundTag("Fluid");
                    if (liquidTag != null) {
                        FluidStack liquid = FluidStack.loadFluidStackFromNBT(liquidTag);
                        event.toolTip.add(GuiHelper.GuiColor.YELLOW + liquid.getLocalizedName());
                        event.toolTip.add(liquid.amount + "/" + FluidContainerRegistry.BUCKET_VOLUME * 16 + "mb");
                    }
                }
            }
            if(event.itemStack.getItem() == Item.getItemFromBlock(BlockHandler.diamondTank)) {
                if(event.itemStack.hasTagCompound()) {
                    NBTTagCompound liquidTag = event.itemStack.getTagCompound().getCompoundTag("Fluid");
                    if (liquidTag != null) {
                        FluidStack liquid = FluidStack.loadFluidStackFromNBT(liquidTag);
                        event.toolTip.add(GuiHelper.GuiColor.YELLOW + liquid.getLocalizedName());
                        event.toolTip.add(liquid.amount + "/" + FluidContainerRegistry.BUCKET_VOLUME * 64 + "mb");
                    }
                }
            }
        }
    }
}
