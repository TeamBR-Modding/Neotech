package com.dyonovan.neotech.client.ingameconfig

import java.util

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.client.IModGuiFactory
import net.minecraftforge.fml.client.IModGuiFactory.{RuntimeOptionCategoryElement, RuntimeOptionGuiHandler}

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
class GuiFactoryNeoTech extends IModGuiFactory {
    override def runtimeGuiCategories(): util.Set[RuntimeOptionCategoryElement] = null

    override def initialize(minecraftInstance: Minecraft): Unit = { }

    override def getHandlerFor(element: RuntimeOptionCategoryElement): RuntimeOptionGuiHandler = null

    override def mainConfigGuiClass(): Class[_ <: GuiScreen] = classOf[GuiConfigNeoTech]
}
