package com.teambrmodding.neotech.client.gui.machines.operators;

import com.teambr.bookshelf.client.gui.GuiColor;
import com.teambr.bookshelf.client.gui.component.display.GuiComponentColoredZone;
import com.teambr.bookshelf.client.gui.component.display.GuiComponentTextureAnimated;
import com.teambr.bookshelf.util.ClientUtils;
import com.teambrmodding.neotech.client.gui.machines.GuiAbstractMachine;
import com.teambrmodding.neotech.collections.EnumInputOutputMode;
import com.teambrmodding.neotech.common.container.machines.operators.ContainerTreeFarm;
import com.teambrmodding.neotech.common.tiles.AbstractMachine;
import com.teambrmodding.neotech.common.tiles.machines.operators.TileTreeFarm;
import com.teambrmodding.neotech.lib.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/17/2017
 */
public class GuiTreeFarm extends GuiAbstractMachine<ContainerTreeFarm> {

    /**
     * Main Constructor
     */
    public GuiTreeFarm(EntityPlayer player, AbstractMachine machine) {
        super(new ContainerTreeFarm(player.inventory, (TileTreeFarm) machine), 175, 165, "neotech.treeFarm.title",
                new ResourceLocation(Reference.MOD_ID, "textures/gui/treeFarm.png"), machine, player);
    }

    /**
     * This will be called after the GUI has been initialized and should be where you add all components.
     */
    @Override
    protected void addComponents() {
        if(machine != null) {
            // Power Bar
            components.add(new GuiComponentTextureAnimated(this, 26, 12, 176, 97,
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
                    toolTip.add(GuiColor.ORANGE + ClientUtils.translate("neotech.text.redstoneFlux"));
                    toolTip.add(ClientUtils.formatNumber(machine.getEnergyStored()) + " / " +
                            ClientUtils.formatNumber(machine.getMaxEnergyStored()));
                    return toolTip;
                }
            });

            // Axe Slot
            components.add(new GuiComponentColoredZone(this, 60, 18, 20, 20, new Color(0, 0, 0, 0)){
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
                        else if(machine.getModeForSide(dir) == EnumInputOutputMode.INPUT_ALL) {
                            color = EnumInputOutputMode.INPUT_ALL.getHighlightColor();
                            break;
                        } else if(machine.getModeForSide(dir) == EnumInputOutputMode.INPUT_PRIMARY)
                            color = EnumInputOutputMode.INPUT_PRIMARY.getHighlightColor();
                    }

                    // Color was assigned
                    if(color.getAlpha() != 0)
                        color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 80);
                    return color;
                }
            });

            // Shears Slot
            components.add(new GuiComponentColoredZone(this, 87, 18, 20, 20, new Color(0, 0, 0, 0)){
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
                        else if(machine.getModeForSide(dir) == EnumInputOutputMode.INPUT_ALL) {
                            color = EnumInputOutputMode.INPUT_ALL.getHighlightColor();
                            break;
                        } else if(machine.getModeForSide(dir) == EnumInputOutputMode.INPUT_SECONDARY)
                            color = EnumInputOutputMode.INPUT_SECONDARY.getHighlightColor();
                    }

                    // Color was assigned
                    if(color.getAlpha() != 0)
                        color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 80);
                    return color;
                }
            });

            // Output Slots
            components.add(new GuiComponentColoredZone(this, 60, 38, 110, 38, new Color(0, 0, 0, 0)){
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
