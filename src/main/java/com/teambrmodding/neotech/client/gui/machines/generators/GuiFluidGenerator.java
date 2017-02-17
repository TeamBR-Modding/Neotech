package com.teambrmodding.neotech.client.gui.machines.generators;

import com.teambr.bookshelf.client.gui.GuiColor;
import com.teambr.bookshelf.client.gui.component.display.GuiComponentColoredZone;
import com.teambr.bookshelf.client.gui.component.display.GuiComponentFluidTank;
import com.teambr.bookshelf.client.gui.component.display.GuiComponentText;
import com.teambr.bookshelf.client.gui.component.display.GuiComponentTextureAnimated;
import com.teambr.bookshelf.util.ClientUtils;
import com.teambrmodding.neotech.client.gui.machines.GuiAbstractMachine;
import com.teambrmodding.neotech.collections.EnumInputOutputMode;
import com.teambrmodding.neotech.common.container.machines.generators.ContainerFluidGenerator;
import com.teambrmodding.neotech.common.tiles.machines.generators.TileFluidGenerator;
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
 * @since 2/16/2017
 */
public class GuiFluidGenerator extends GuiAbstractMachine<ContainerFluidGenerator> {
    protected TileFluidGenerator generator;

    /**
     * Main constructor for Guis
     */
    public GuiFluidGenerator(EntityPlayer player, TileFluidGenerator generator) {
        super(new ContainerFluidGenerator(player.inventory, generator), 175, 165, "neotech.fluidgenerator.title",
                new ResourceLocation(Reference.MOD_ID, "textures/gui/generatorFluid.png"), generator, player);
        this.generator = generator;
        addComponents();
    }

    /**
     * This will be called after the GUI has been initialized and should be where you add all components.
     */
    @Override
    protected void addComponents() {
        if(generator != null) {

            // Flame for Burn
            components.add(new GuiComponentTextureAnimated(this, 80, 41, 176, 83,
                    14, 14, GuiComponentTextureAnimated.ANIMATION_DIRECTION.UP) {
                @Override
                protected int getCurrentProgress(int scale) {
                    return generator.isActive() ? generator.getBurnProgressScaled(14) : 0;
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
                    toolTip.add(GuiColor.ORANGE + ClientUtils.translate("neotech.text.redstoneFlux"));
                    toolTip.add(ClientUtils.formatNumber(generator.getEnergyStored()) + " / " +
                            ClientUtils.formatNumber(generator.getMaxEnergyStored()));
                    return toolTip;
                }
            });

            // Currently Generating
            components.add(new GuiComponentText(this, 64, 18, GuiColor.RED + "rf/t = " + generator.getEnergyProduced(), Color.DARK_GRAY) {
                /**
                 * Called after base render, is already translated to guiLeft and guiTop, just move offset
                 *
                 * @param guiLeft
                 * @param guiTop
                 * @param mouseX
                 * @param mouseY
                 */
                @Override
                public void renderOverlay(int guiLeft, int guiTop, int mouseX, int mouseY) {
                    setLabel(GuiColor.RED + "rf/t = " +
                            ClientUtils.formatNumber(generator.getEnergyProduced()));
                    super.renderOverlay(guiLeft, guiTop, mouseX, mouseY);
                }
            });

            // Fluid Tank
            components.add(new GuiComponentFluidTank(this, 144, 12, 16, 62,
                    generator.tanks[TileFluidGenerator.TANK]) {
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
                    toolTip.add(generator.tanks[TileFluidGenerator.TANK].getFluid() != null ?
                            GuiColor.ORANGE + generator.tanks[TileFluidGenerator.TANK].getFluid().getLocalizedName() :
                            GuiColor.RED + ClientUtils.translate("neotech.text.empty"));
                    toolTip.add(ClientUtils.formatNumber(generator.tanks[TileFluidGenerator.TANK].getFluidAmount()) + " / " +
                            ClientUtils.formatNumber(generator.tanks[TileFluidGenerator.TANK].getCapacity()) + " mb");
                    return toolTip;
                }
            });

            // Fluid Color Indicator
            components.add(new GuiComponentColoredZone(this, 143, 11, 18, 64, new Color(0, 0, 0, 0)){
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
                        if(generator.getModeForSide(dir) == EnumInputOutputMode.INPUT_ALL)
                            color = EnumInputOutputMode.INPUT_ALL.getHighlightColor();
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
