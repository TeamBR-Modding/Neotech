package com.dyonovan.neotech.events;

import com.dyonovan.neotech.handlers.BlockHandler;
import com.dyonovan.neotech.handlers.ItemHandler;
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
            if (event.itemStack.getItem() == ItemHandler.upgradeMBFull) {
                if (event.itemStack.hasTagCompound()) {
                    NBTTagCompound tag = event.itemStack.getTagCompound();
                    if (tag.hasKey("Speed"))
                        event.toolTip.add(GuiHelper.GuiColor.GREEN + "Speed: +" + tag.getInteger("Speed") * 10 + "%");
                    if (tag.hasKey("Efficiency") || tag.hasKey("Speed")) {
                        int eff = tag.hasKey("Efficiency") ? tag.getInteger("Efficiency") : 0;
                        int speed = tag.hasKey("Speed") ? tag.getInteger("Speed") : 0;
                        int actual = eff * 10 - speed * 10;
                        if (actual >= 0)
                            event.toolTip.add(GuiHelper.GuiColor.GREEN + "Efficiency: +" + actual + "%");
                        else
                            event.toolTip.add(GuiHelper.GuiColor.RED + "Efficiency: " + actual + "%");
                    }
                    if (tag.hasKey("Capacity"))
                        event.toolTip.add(GuiHelper.GuiColor.GREEN + "Capacity: +" + tag.getInteger("Capacity") * 1000 + GuiHelper.GuiColor.ORANGE + " RF");
                    if (tag.hasKey("AutoOutput")) {
                        if (tag.getBoolean("AutoOutput"))
                            event.toolTip.add(GuiHelper.GuiColor.GREEN + "Auto Output: " + "True");
                        else
                            event.toolTip.add(GuiHelper.GuiColor.RED + "Auto Output: " + "False");
                    }
                }
            }
            if (event.itemStack.getItem() == ItemHandler.ioPort) {
                event.toolTip.add(GuiHelper.GuiColor.GREEN + "Allows Machines to Auto Output");
            }
            if (event.itemStack.getItem() == ItemHandler.effFan) {
                event.toolTip.add(GuiHelper.GuiColor.GREEN + "Increases Machines Efficiency");
                event.toolTip.add(GuiHelper.GuiColor.GREEN + "-10% RF Use");
            }
            if (event.itemStack.getItem() == ItemHandler.capRam) {
                event.toolTip.add(GuiHelper.GuiColor.GREEN + "Increases the RF the machine can hold");
                event.toolTip.add(GuiHelper.GuiColor.GREEN + "+1000 RF");
            }
            if (event.itemStack.getItem() == ItemHandler.speedProcessor) {
                event.toolTip.add(GuiHelper.GuiColor.GREEN + "Increases Speed at the cost of Efficiency");
                event.toolTip.add(GuiHelper.GuiColor.GREEN + "+10% Speed " + GuiHelper.GuiColor.RED + "+10 RF Use");
            }
            if (event.itemStack.getItem() == Item.getItemFromBlock(BlockHandler.basicItemPipe)) {
                event.toolTip.add(GuiHelper.GuiColor.YELLOW + "Max Item Transfer");
                event.toolTip.add(String.valueOf("1 Item"));
            }
            if (event.itemStack.getItem() == Item.getItemFromBlock(BlockHandler.advancedItemPipe)) {
                event.toolTip.add(GuiHelper.GuiColor.YELLOW + "Max Item Transfer");
                event.toolTip.add(String.valueOf("32 Items"));
            }
            if (event.itemStack.getItem() == Item.getItemFromBlock(BlockHandler.eliteItemPipe)) {
                event.toolTip.add(GuiHelper.GuiColor.YELLOW + "Max Item Transfer");
                event.toolTip.add(String.valueOf("64 Items"));
            }
        }
    }
}
