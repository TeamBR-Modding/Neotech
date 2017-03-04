package com.teambrmodding.neotech.client.gui.machines.processors;

import com.teambr.bookshelf.client.gui.GuiColor;
import com.teambr.bookshelf.client.gui.GuiTextFormat;
import com.teambr.bookshelf.client.gui.component.display.GuiComponentColoredZone;
import com.teambr.bookshelf.client.gui.component.display.GuiComponentFluidTank;
import com.teambr.bookshelf.client.gui.component.display.GuiComponentTextureAnimated;
import com.teambr.bookshelf.network.PacketManager;
import com.teambr.bookshelf.util.ClientUtils;
import com.teambr.bookshelf.util.EnergyUtils;
import com.teambrmodding.neotech.client.gui.machines.GuiAbstractMachine;
import com.teambrmodding.neotech.collections.EnumInputOutputMode;
import com.teambrmodding.neotech.common.container.machines.processors.ContainerCentrifuge;
import com.teambrmodding.neotech.common.tiles.machines.processors.TileCentrifuge;
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
public class GuiCentrifuge extends GuiAbstractMachine<ContainerCentrifuge> {
    protected TileCentrifuge centrifuge;

    public GuiCentrifuge(EntityPlayer player, TileCentrifuge centrifuge) {
        super(new ContainerCentrifuge(player.inventory, centrifuge), 175, 165, "neotech.centrifuge.title",
                new ResourceLocation(Reference.MOD_ID, "textures/gui/electriccentrifuge.png"), centrifuge, player);
        this.centrifuge = centrifuge;

        addComponents();
    }

    /**
     * This will be called after the GUI has been initialized and should be where you add all components.
     */
    @Override
    protected void addComponents() {
        if(centrifuge != null) {
            // Progress Arrow
            components.add(new GuiComponentTextureAnimated(this, 95, 35, 176, 80,
                    24, 17, GuiComponentTextureAnimated.ANIMATION_DIRECTION.RIGHT) {
                @Override
                protected int getCurrentProgress(int scale) {
                    return centrifuge.getCookProgressScaled(24);
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

            // Fluid Input
            components.add(new GuiComponentFluidTank(this, 40, 12, 49, 62, centrifuge.tanks[TileCentrifuge.INPUT_TANK]){
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
                    toolTip.add(centrifuge.tanks[TileCentrifuge.INPUT_TANK].getFluid() != null ?
                            GuiColor.ORANGE + centrifuge.tanks[TileCentrifuge.INPUT_TANK].getFluid().getLocalizedName() :
                            GuiColor.RED + ClientUtils.translate("neotech.text.empty"));
                    toolTip.add(ClientUtils.formatNumber(centrifuge.tanks[TileCentrifuge.INPUT_TANK].getFluidAmount()) + " / " +
                            ClientUtils.formatNumber(centrifuge.tanks[TileCentrifuge.INPUT_TANK].getCapacity()) + " mb");
                    toolTip.add("");
                    toolTip.add(GuiColor.GRAY + "" + GuiTextFormat.ITALICS + ClientUtils.translate("neotech.text.clearTank"));
                    return toolTip;
                }

                /**
                 * Called when the mouse is pressed
                 *
                 * @param x      Mouse X Position
                 * @param y      Mouse Y Position
                 * @param button Mouse Button
                 */
                @Override
                public void mouseDown(int x, int y, int button) {
                    if(ClientUtils.isCtrlPressed() && ClientUtils.isShiftPressed()) {
                        centrifuge.tanks[TileCentrifuge.INPUT_TANK].setFluid(null);
                        PacketManager.updateTileWithClientInfo(centrifuge);
                    }
                }
            });
            components.add(new GuiComponentColoredZone(this, 39, 11, 51, 64, new Color(0, 0, 0, 0)){
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

            // Fluid Output One
            components.add(new GuiComponentFluidTank(this, 124, 12, 16, 62, centrifuge.tanks[TileCentrifuge.OUTPUT_TANK_1]){
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
                    toolTip.add(centrifuge.tanks[TileCentrifuge.OUTPUT_TANK_1].getFluid() != null ?
                            GuiColor.ORANGE + centrifuge.tanks[TileCentrifuge.OUTPUT_TANK_1].getFluid().getLocalizedName() :
                            GuiColor.RED + ClientUtils.translate("neotech.text.empty"));
                    toolTip.add(ClientUtils.formatNumber(centrifuge.tanks[TileCentrifuge.OUTPUT_TANK_1].getFluidAmount()) + " / " +
                            ClientUtils.formatNumber(centrifuge.tanks[TileCentrifuge.OUTPUT_TANK_1].getCapacity()) + " mb");
                    toolTip.add("");
                    toolTip.add(GuiColor.GRAY + "" + GuiTextFormat.ITALICS + ClientUtils.translate("neotech.text.clearTank"));
                    return toolTip;
                }

                /**
                 * Called when the mouse is pressed
                 *
                 * @param x      Mouse X Position
                 * @param y      Mouse Y Position
                 * @param button Mouse Button
                 */
                @Override
                public void mouseDown(int x, int y, int button) {
                    if(ClientUtils.isCtrlPressed() && ClientUtils.isShiftPressed()) {
                        centrifuge.tanks[TileCentrifuge.OUTPUT_TANK_1].setFluid(null);
                        PacketManager.updateTileWithClientInfo(centrifuge);
                    }
                }
            });
            components.add(new GuiComponentColoredZone(this, 123, 11, 18, 64, new Color(0, 0, 0, 0)){
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
                        else if(machine.getModeForSide(dir) == EnumInputOutputMode.OUTPUT_ALL) {
                            color = EnumInputOutputMode.OUTPUT_ALL.getHighlightColor();
                            break;
                        } else if(machine.getModeForSide(dir) == EnumInputOutputMode.OUTPUT_PRIMARY)
                            color = EnumInputOutputMode.OUTPUT_PRIMARY.getHighlightColor();
                    }

                    // Color was assigned
                    if(color.getAlpha() != 0)
                        color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 80);
                    return color;
                }
            });

            // Fluid Output Two
            components.add(new GuiComponentFluidTank(this, 145, 12, 16, 62, centrifuge.tanks[TileCentrifuge.OUTPUT_TANK_2]){
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
                    toolTip.add(centrifuge.tanks[TileCentrifuge.OUTPUT_TANK_2].getFluid() != null ?
                            GuiColor.ORANGE + centrifuge.tanks[TileCentrifuge.OUTPUT_TANK_2].getFluid().getLocalizedName() :
                            GuiColor.RED + ClientUtils.translate("neotech.text.empty"));
                    toolTip.add(ClientUtils.formatNumber(centrifuge.tanks[TileCentrifuge.OUTPUT_TANK_2].getFluidAmount()) + " / " +
                            ClientUtils.formatNumber(centrifuge.tanks[TileCentrifuge.OUTPUT_TANK_2].getCapacity()) + " mb");
                    toolTip.add("");
                    toolTip.add(GuiColor.GRAY + "" + GuiTextFormat.ITALICS + ClientUtils.translate("neotech.text.clearTank"));
                    return toolTip;
                }

                /**
                 * Called when the mouse is pressed
                 *
                 * @param x      Mouse X Position
                 * @param y      Mouse Y Position
                 * @param button Mouse Button
                 */
                @Override
                public void mouseDown(int x, int y, int button) {
                    if(ClientUtils.isCtrlPressed() && ClientUtils.isShiftPressed()) {
                        centrifuge.tanks[TileCentrifuge.OUTPUT_TANK_2].setFluid(null);
                        PacketManager.updateTileWithClientInfo(centrifuge);
                    }
                }
            });
            components.add(new GuiComponentColoredZone(this, 144, 11, 18, 64, new Color(0, 0, 0, 0)){
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
                        else if(machine.getModeForSide(dir) == EnumInputOutputMode.OUTPUT_ALL) {
                            color = EnumInputOutputMode.OUTPUT_ALL.getHighlightColor();
                            break;
                        } else if(machine.getModeForSide(dir) == EnumInputOutputMode.OUTPUT_SECONDARY)
                            color = EnumInputOutputMode.OUTPUT_SECONDARY.getHighlightColor();
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
