package com.teambrmodding.neotech.client.ingameconfig;

import com.teambrmodding.neotech.lib.Reference;
import com.teambrmodding.neotech.managers.ConfigManager;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since 3/17/2016
 */
public class GuiConfigNeotech extends GuiConfig {

    public GuiConfigNeotech(GuiScreen parent) {
        super(parent, getConfigElements(), Reference.MOD_ID, false, false, "NeoTech Config Options");
    }

    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> list = new ArrayList<>();

        list.add(categoryElement(Reference.CONFIG_CLIENT, "Client Side Config", "neotech.guiconfig.client"));
        list.add(categoryElement(Reference.CONFIG_WORLD, "World", "neotech.guiconfig.world"));

        return list;
    }

    private static IConfigElement categoryElement(String category, String name, String tooltip_key) {
        return new DummyConfigElement.DummyCategoryElement(name, tooltip_key,
                new ConfigElement(ConfigManager.config.getCategory(category.toLowerCase())).getChildElements());
    }
}
