package com.teambrmodding.neotech.client

import com.teambrmodding.neotech.client.gui.GuiToggleMenu
import com.teambrmodding.neotech.utils.ClientUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.client.registry.ClientRegistry
import org.lwjgl.input.Keyboard

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis "pauljoda"
  * @since 3/4/2016
  */
object KeybindHandler {
    lazy val radialMenu =
        new KeyBinding(ClientUtils.translate("neotech.text.radialMenuKey"),
            Keyboard.KEY_G,
            ClientUtils.translate("neotech.text.modName"))

    def registerBindings() : Unit = {
        ClientRegistry.registerKeyBinding(radialMenu)
    }

    def keyPressed(binding : KeyBinding) : Unit = {
        val radialMenuKey = radialMenu.getKeyCode
        binding.getKeyCode match {
            case `radialMenuKey`
                if ClientTickHandler.getUpgrades.nonEmpty =>
                Minecraft.getMinecraft.displayGuiScreen(new GuiToggleMenu())
            case _ =>
        }
    }
}
