package com.dyonovan.neotech.api.igw

import com.dyonovan.neotech.managers.BlockManager
import igwmod.gui.GuiWiki
import igwmod.gui.tabs.BaseWikiTab
import net.minecraft.client.resources.I18n
import net.minecraft.item.ItemStack

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 1/23/2016
  */
class NeotechTab extends BaseWikiTab {

    pageEntries.add("intro")

    override def getPageLocation(pageEntry: String): String = "neotech:tab/" + pageEntry

    override def getPageName(pageEntry: String): String = I18n.format("neotech.wiki.entry." + pageEntry)

    override def getName: String = "itemGroup.tabNeoTech"

    override def renderTabIcon(gui: GuiWiki): ItemStack = new ItemStack(BlockManager.eliteRFStorage)
}
