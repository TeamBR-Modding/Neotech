package com.dyonovan.neotech.events

import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.registries.ConfigRegistry
import com.teambr.bookshelf.client.gui.GuiColor
import com.teambr.bookshelf.helper.GuiHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.{IChatComponent, ChatComponentText}
import net.minecraftforge.common.ForgeVersion
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.fml.common.{FMLModContainer, Loader}
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 1/24/2016
  */
object OnPlayerLoginEvent {

    @SubscribeEvent
    def onPlayerLogin(event: EntityJoinWorldEvent): Unit = {
        if (event.entity.isInstanceOf[EntityPlayer] && event.world.isRemote && ConfigRegistry.versionCheck) {
            val mod = Loader.instance().getModList.toArray()
            var modContainer: FMLModContainer = null
            for (m <- mod) {
                m match {
                    case container: FMLModContainer =>
                        if (container.getModId == Reference.MOD_ID) {
                            modContainer = container
                        }
                    case _ =>
                }
            }
            val versionCheck = ForgeVersion.getResult(modContainer)
            if (versionCheck.status == ForgeVersion.Status.OUTDATED) {
                val msg = GuiColor.ORANGE + "NEOTECH" + GuiColor.WHITE + " is outdated. Newset version is " + GuiColor.GREEN +
                  versionCheck.target + GuiColor.WHITE + " Update at " + versionCheck.url
                event.entity.addChatMessage(new ChatComponentText(msg))
            }
        }
    }

}
