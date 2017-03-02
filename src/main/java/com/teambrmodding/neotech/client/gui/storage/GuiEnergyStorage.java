package com.teambrmodding.neotech.client.gui.storage;

import com.teambr.bookshelf.client.gui.GuiBase;
import com.teambr.bookshelf.client.gui.component.display.GuiComponentTextureAnimated;
import com.teambr.bookshelf.util.EnergyUtils;
import com.teambrmodding.neotech.common.container.storage.ContainerEnergyStorage;
import com.teambrmodding.neotech.common.tiles.storage.TileEnergyStorage;
import com.teambrmodding.neotech.lib.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nullable;
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
public class GuiEnergyStorage extends GuiBase<ContainerEnergyStorage> {
    protected TileEnergyStorage energyStorage;

    /**
     * Main constructor for Guis
     */
    public GuiEnergyStorage(EntityPlayer player, TileEnergyStorage energyStorage) {
        super(new ContainerEnergyStorage(player.inventory, energyStorage), 175, 165,
                energyStorage.getBlockType().getLocalizedName(), new ResourceLocation(Reference.MOD_ID, "textures/gui/energyStorage.png"));
        this.energyStorage = energyStorage;

        addComponents();
    }

    /**
     * This will be called after the GUI has been initialized and should be where you add all components.
     */
    @Override
    protected void addComponents() {
        if(energyStorage != null) {
            // Power Bar
            components.add(new GuiComponentTextureAnimated(this, 16, 12, 176, 97,
                    16, 62, GuiComponentTextureAnimated.ANIMATION_DIRECTION.UP) {
                @Override
                protected int getCurrentProgress(int scale) {
                    return energyStorage.getEnergyStored() * scale / energyStorage.getMaxEnergyStored();
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
                    EnergyUtils.addToolTipInfo(energyStorage.getCapability(CapabilityEnergy.ENERGY, null),
                            toolTip, energyStorage.energyStorage.getMaxInsert(), energyStorage.energyStorage.getMaxExtract());
                    return toolTip;
                }
            });
        }
    }
}
