package com.dyonovan.neotech.client

import com.dyonovan.neotech.client.gui.GuiToggleMenu
import com.dyonovan.neotech.tools.tools.BaseElectricTool
import com.dyonovan.neotech.utils.ClientUtils
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
                if Minecraft.getMinecraft.thePlayer.getHeldItem != null &&
                        Minecraft.getMinecraft.thePlayer.getHeldItem.getItem.isInstanceOf[BaseElectricTool] =>
                Minecraft.getMinecraft.displayGuiScreen(new GuiToggleMenu(Minecraft.getMinecraft.thePlayer.getHeldItem))
            case _ =>
        }
    }
}
