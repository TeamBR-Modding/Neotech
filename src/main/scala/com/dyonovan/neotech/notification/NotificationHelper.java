package com.dyonovan.neotech.notification;

import net.minecraftforge.fml.client.FMLClientHandler;

/**
 * This file was created for NeoTech
 * <p/>
 * NeoTech is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis pauljoda
 * @since August 17, 2015
 */
public class NotificationHelper {

    public static void addNotification(Notification notification) {
        NotificationTickHandler.guiNotification.queueNotification(notification);
    }

    public static void openConfigurationGui() {
        FMLClientHandler.instance().showGuiScreen(new GuiNotificationConfig());
    }
}
