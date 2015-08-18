package com.dyonovan.neotech.notification;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

/**
 * This file was created for NeoTech
 * <p/>
 * NeoTech is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis pauljoda
 * @since August 17, 2015
 */

public class NotificationKeyBinding {
    public static KeyBinding menu;
    public static void init() {
        menu = new KeyBinding("Notification Configuration", Keyboard.KEY_N, "Notifications");
        ClientRegistry.registerKeyBinding(menu);
    }
}
