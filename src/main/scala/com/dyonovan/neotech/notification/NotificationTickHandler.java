package com.dyonovan.neotech.notification;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * This file was created for NeoTech
 * <p/>
 * NeoTech is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis pauljoda
 * @since August 17, 2015
 */
public class NotificationTickHandler {
    public static GuiNotification guiNotification;

    @SubscribeEvent
    public void renderTick(TickEvent.RenderTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.theWorld != null) {
            if(guiNotification == null)
                guiNotification = new GuiNotification(mc);
            guiNotification.update();
        }
    }
}
