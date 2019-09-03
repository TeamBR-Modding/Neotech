package com.teambrmodding.neotech.client.screen;

import com.teambr.nucleus.client.gui.GuiBase;
import com.teambrmodding.neotech.common.container.GeneratorContainer;
import com.teambrmodding.neotech.common.tileentity.GeneratorTile;
import com.teambrmodding.neotech.lib.Reference;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

/**
 * This file was created for AssistedProgression
 * <p>
 * AssistedProgression is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author James Rogers - Dyonovan
 * @since 9/3/2019
 */
public class GeneratorScreen extends GuiBase<GeneratorContainer> {

    protected GeneratorTile generator;

    public GeneratorScreen(GeneratorContainer inventory, PlayerInventory playerInventory, ITextComponent title) {
        super(inventory, playerInventory, title, 176, 166,
                new ResourceLocation(Reference.MOD_NAME, "textures/gui/generator.png"));
        addComponents();
    }

    @Override
    protected void addComponents() {

    }
}
