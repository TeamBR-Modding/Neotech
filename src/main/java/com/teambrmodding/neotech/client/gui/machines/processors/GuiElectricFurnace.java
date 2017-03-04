package com.teambrmodding.neotech.client.gui.machines.processors;

import com.teambr.bookshelf.client.gui.component.display.GuiComponentColoredZone;
import com.teambr.bookshelf.client.gui.component.display.GuiComponentTextureAnimated;
import com.teambr.bookshelf.util.EnergyUtils;
import com.teambrmodding.neotech.client.gui.machines.GuiAbstractMachine;
import com.teambrmodding.neotech.collections.EnumInputOutputMode;
import com.teambrmodding.neotech.common.container.machines.processors.ContainerElectricFurnace;
import com.teambrmodding.neotech.common.tiles.AbstractMachine;
import com.teambrmodding.neotech.common.tiles.MachineProcessor;
import com.teambrmodding.neotech.lib.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/17/2017
 */
public class GuiElectricFurnace extends GuiAbstractMachine<ContainerElectricFurnace> {

    public GuiElectricFurnace(EntityPlayer player, AbstractMachine furnace) {
        super(new ContainerElectricFurnace(player.inventory, furnace), 175, 165, "neotech.furnace.title",
                new ResourceLocation(Reference.MOD_ID, "textures/gui/electricfurnace.png"), furnace, player);
    }

    /**
     * This will be called after the GUI has been initialized and should be where you add all components.
     */
    @Override
    protected void addComponents() {
        if(machine != null) {
            // Progress Arrow
            components.add(new GuiComponentTextureAnimated(this, 79, 34, 176, 80,
                    24, 17, GuiComponentTextureAnimated.ANIMATION_DIRECTION.RIGHT) {
                @Override
                protected int getCurrentProgress(int scale) {
                    return ((MachineProcessor)machine).getCookProgressScaled(24);
                }
            });

            // Power Bar
            components.add(new GuiComponentTextureAnimated(this, 16, 12, 176, 97,
                    16, 62, GuiComponentTextureAnimated.ANIMATION_DIRECTION.UP) {
                @Override
                protected int getCurrentProgress(int scale) {
                    return machine.getEnergyStored() * scale / machine.getMaxEnergyStored();
                }

                /**
                 * Used to determine if a dynamic tooltip is needed at runtime
                 *
                 * @param mouseX Mouse X Pos
                 * @param mouseY Mouse Y Pos
                 * @return A list of string to display
                 */
                @Nullable
                @Override
                public List<String> getDynamicToolTip(int mouseX, int mouseY) {
                    List<String> toolTip = new ArrayList<>();
                    EnergyUtils.addToolTipInfo(machine.getCapability(CapabilityEnergy.ENERGY, null),
                            toolTip, machine.energyStorage.getMaxInsert(), machine.energyStorage.getMaxExtract());
                    return toolTip;
                }
            });

            components.add(new GuiComponentColoredZone(this, 54, 33, 20, 20, new Color(0, 0, 0, 0)){
                /**
                 * Override this to change the color
                 *
                 * @return The color, by default the passed color
                 */
                @Override
                protected Color getDynamicColor() {
                    Color color = new Color(0, 0, 0, 0);

                    // Checking if input is enabled
                    for(EnumFacing dir : EnumFacing.values()) {
                        if(machine.getModeForSide(dir) == EnumInputOutputMode.ALL_MODES) {
                            color = EnumInputOutputMode.ALL_MODES.getHighlightColor();
                            break;
                        }
                        else if(machine.getModeForSide(dir) == EnumInputOutputMode.INPUT_ALL)
                            color = EnumInputOutputMode.INPUT_ALL.getHighlightColor();
                    }

                    // Color was assigned
                    if(color.getAlpha() != 0)
                        color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 80);
                    return color;
                }
            });

            components.add(new GuiComponentColoredZone(this, 110, 29, 28, 28, new Color(0, 0, 0, 0)){
                /**
                 * Override this to change the color
                 *
                 * @return The color, by default the passed color
                 */
                @Override
                protected Color getDynamicColor() {
                    Color color = new Color(0, 0, 0, 0);

                    // Checking if input is enabled
                    for(EnumFacing dir : EnumFacing.values()) {
                        if(machine.getModeForSide(dir) == EnumInputOutputMode.ALL_MODES) {
                            color = EnumInputOutputMode.ALL_MODES.getHighlightColor();
                            break;
                        }
                        else if(machine.getModeForSide(dir) == EnumInputOutputMode.OUTPUT_ALL)
                            color = EnumInputOutputMode.OUTPUT_ALL.getHighlightColor();
                    }

                    // Color was assigned
                    if(color.getAlpha() != 0)
                        color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 80);
                    return color;
                }
            });
        }
    }
}
