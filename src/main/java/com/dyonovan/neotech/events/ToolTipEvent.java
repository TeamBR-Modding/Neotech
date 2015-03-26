package com.dyonovan.neotech.events;

import com.dyonovan.neotech.handlers.BlockHandler;
import com.dyonovan.neotech.helpers.GuiHelper;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
            if(event.itemStack.getItem() == Item.getItemFromBlock(BlockHandler.basicStorage) ||
                    event.itemStack.getItem() == Item.getItemFromBlock(BlockHandler.advancedStorage) ||
                    event.itemStack.getItem() == Item.getItemFromBlock(BlockHandler.eliteStorage)) {
                if(event.itemStack.hasTagCompound()) {
                    NBTTagCompound tag = event.itemStack.getTagCompound();
                    if (tag != null) {
                        event.toolTip.add(GuiHelper.GuiColor.YELLOW + "Stored Energy");
                        event.toolTip.add(String.valueOf(tag.getInteger("Power")) + GuiHelper.GuiColor.RED + " RF");
                    }
                }
            }
            if (event.itemStack.getItem() == Item.getItemFromBlock(BlockHandler.basicCable)) {
                event.toolTip.add(GuiHelper.GuiColor.YELLOW + "Max Energy Transfer");
                event.toolTip.add(String.valueOf("200" + GuiHelper.GuiColor.RED + " RF/t"));
            }
            if (event.itemStack.getItem() == Item.getItemFromBlock(BlockHandler.advancedCable)) {
                event.toolTip.add(GuiHelper.GuiColor.YELLOW + "Max Energy Transfer");
                event.toolTip.add(String.valueOf("1000" + GuiHelper.GuiColor.RED + " RF/t"));
            }
            if (event.itemStack.getItem() == Item.getItemFromBlock(BlockHandler.eliteCable)) {
                event.toolTip.add(GuiHelper.GuiColor.YELLOW + "Max Energy Transfer");
                event.toolTip.add(String.valueOf("5000" + GuiHelper.GuiColor.RED + " RF/t"));
            }
        }
    }
}
