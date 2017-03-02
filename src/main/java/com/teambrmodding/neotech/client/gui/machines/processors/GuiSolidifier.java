package com.teambrmodding.neotech.client.gui.machines.processors;

import com.teambr.bookshelf.client.gui.GuiColor;
import com.teambr.bookshelf.client.gui.GuiTextFormat;
import com.teambr.bookshelf.client.gui.component.control.GuiComponentItemStackButton;
import com.teambr.bookshelf.client.gui.component.display.GuiComponentColoredZone;
import com.teambr.bookshelf.client.gui.component.display.GuiComponentFluidTank;
import com.teambr.bookshelf.client.gui.component.display.GuiComponentTextureAnimated;
import com.teambr.bookshelf.network.PacketManager;
import com.teambr.bookshelf.util.ClientUtils;
import com.teambr.bookshelf.util.EnergyUtils;
import com.teambrmodding.neotech.client.gui.machines.GuiAbstractMachine;
import com.teambrmodding.neotech.collections.EnumInputOutputMode;
import com.teambrmodding.neotech.common.container.machines.processors.ContainerSolidifier;
import com.teambrmodding.neotech.common.tiles.MachineProcessor;
import com.teambrmodding.neotech.common.tiles.machines.processors.TileSolidifier;
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
public class GuiSolidifier extends GuiAbstractMachine<ContainerSolidifier> {
    protected TileSolidifier solidifier;

    public GuiSolidifier(EntityPlayer player, TileSolidifier solidifier) {
        super(new ContainerSolidifier(player.inventory, solidifier), 175, 165, "neotech.electricSolidifier.title",
                new ResourceLocation(Reference.MOD_ID, "textures/gui/electricSolidifier.png"), solidifier, player);
        this.solidifier = solidifier;

        addComponents();
    }


    /**
     * This will be called after the GUI has been initialized and should be where you add all components.
     */
    @Override
    protected void addComponents() {
        if(solidifier != null) {
            // Progress Arrow
            components.add(new GuiComponentTextureAnimated(this, 95, 35, 176, 80,
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

            // Input Tanks
            components.add(new GuiComponentFluidTank(this, 40, 12, 49, 62, solidifier.tanks[TileSolidifier.TANK]){
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
                    toolTip.add(solidifier.tanks[TileSolidifier.TANK].getFluid() != null ?
                            GuiColor.ORANGE + solidifier.tanks[TileSolidifier.TANK].getFluid().getLocalizedName() :
                            GuiColor.RED + ClientUtils.translate("neotech.text.empty"));
                    toolTip.add(ClientUtils.formatNumber(solidifier.tanks[TileSolidifier.TANK].getFluidAmount()) + " / " +
                            ClientUtils.formatNumber(solidifier.tanks[TileSolidifier.TANK].getCapacity()) + " mb");
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
                        solidifier.tanks[TileSolidifier.TANK].setFluid(null);
                        PacketManager.updateTileWithClientInfo(solidifier);
                    }
                }
            });
            components.add(new GuiComponentColoredZone(this, 39, 11, 51, 63, new Color(0, 0, 0, 0)){
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

            // Output Item
            components.add(new GuiComponentColoredZone(this, 127, 29, 28, 28, new Color(0, 0, 0, 0)){
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

            // Item Stack Button
            components.add(new GuiComponentItemStackButton(this, 96, 54, 224, 111, 22, 22,
                    solidifier.currentMode.getDisplayStack()) {
                @Override
                protected void doAction() {
                    solidifier.toggleMode();
                    solidifier.sendValueToServer(TileSolidifier.UPDATE_MODE_NBT, 0);
                    setDisplayStack(solidifier.currentMode.getDisplayStack());
                }
            });
        }
    }
}
