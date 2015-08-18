package com.dyonovan.neotech.helpers;

import com.dyonovan.neotech.notification.NotificationHelper;
import com.dyonovan.neotech.notification.NotificationKeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

/**
 * This file was created for NeoTech
 * <p/>
 * NeoTech is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis pauljoda
 * @since August 17, 2015
 */
public class KeyInputHelper {
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if(NotificationKeyBinding.menu.isPressed())
            NotificationHelper.openConfigurationGui();
    }
}
