package com.teambrmodding.neotech.collections;

import com.teambr.nucleus.util.ClientUtils;

import java.awt.*;

/**
 * This file was created for AssistedProgression
 * <p>
 * AssistedProgression is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author James Rogers - Dyonovan
 * @since 9/4/2019
 */
public enum EnumInputOutputMode {

    DEFAULT          ("iomode.default", 0, 255, 255, 255, 15),         // Allows for pipes, but no automatic
    INPUT_ALL        ("iomode.inputAll", 1, 0, 102, 255, 150),         // Sets for all input locations
    INPUT_PRIMARY    ("iomode.inputPrimary", 2, 3, 214, 212, 150),     // Only first input
    INPUT_SECONDARY  ("iomode.inputSecondary", 3, 148, 7, 149, 150),   // Only second input
    OUTPUT_ALL       ("iomode.outputAll", 4, 255, 102, 0, 150),        // Sets for all output locations
    OUTPUT_PRIMARY   ("iomode.outputPrimary", 5, 238, 9, 52, 150),     // Only first output
    OUTPUT_SECONDARY ("iomode.outputSecondary", 6, 248, 245, 35, 150), // Only second output
    ALL_MODES        ("iomode.both", 7, 0, 150, 0, 150),               // Allows for input/output
    DISABLED         ("iomode.disabled", 8, 180, 180, 180, 150);       // No connection allowed, no automatic

    // Unlocalized Name
    private String name;

    // Integer Value
    private int id;

    // Color
    private Color cachedColor;

    EnumInputOutputMode(String unlocalName, int identifier, int r, int g, int b, int a) {
        name = unlocalName;
        id = identifier;
        cachedColor = new Color(r, g, b, a);
    }

    /**
     * The unlocalized name
     */
    public String getName() {
        return name;
    }

    /**
     * Used to get the name that should be displayed in GUI
     * @return The display name
     */
    public String getDisplayName() {
        return ClientUtils.translate(name);
    }

    /**
     * Used to get the correct mode from an integer
     * @param identifier The value to convert
     * @return The mode for that value
     */
    public static EnumInputOutputMode getModeFromInt(int identifier) {
        for(EnumInputOutputMode mode : values()) {
            if(mode.id == identifier)
                return mode;
        }
        return DEFAULT;
    }

    /**
     * Used to get the integer value
     * @return The value
     */
    public int getIntValue() {
        return id;
    }

    /**
     * Used to get the color to render on the Side Selector
     * @return The color, with alpha attached
     */
    public Color getHighlightColor() {
        return cachedColor;
    }
}
