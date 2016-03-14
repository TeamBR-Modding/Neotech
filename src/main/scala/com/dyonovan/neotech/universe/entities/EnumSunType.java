package com.dyonovan.neotech.universe.entities;

import net.minecraft.nbt.NBTTagCompound;

import java.awt.*;

/**
 * This file was created for NeoTech
 * <p/>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis "pauljoda"
 * @since 3/14/2016
 */
public enum EnumSunType {
    INERT("Inert Sun", 0.15F, -0.01F, 150, 150, 150),
    BLUE_DWARF("Blue Dwarf", 0.35F, 0.02F, 102, 153, 255);

    /**
     * Display Name
     */
    private String name;

    /**
     * The default size of this sun
     */
    private float defaultRadius;
    private float secondLayerOffset;

    /**
     * Used to cache the color, no need to keep making it
     */
    private Color cachedColor;

    EnumSunType(String type, float radius, float layerTwoOffset, int r, int g, int b) {
        name = type;
        defaultRadius = radius;
        secondLayerOffset = layerTwoOffset;
        cachedColor = new Color(r, g, b);
    }

    /**
     * Used to write the info needed to read from the tag
     * @param tag The tag
     */
    public void writeInfoToTag(NBTTagCompound tag) {
        tag.setString("EnumSunType", name);
    }

    /**
     * Used to get the info from the tag
     * @param tag The tag
     * @return What type was written
     */
    public EnumSunType getTypeFromTag(NBTTagCompound tag) {
        for(EnumSunType type : values())
            if(type.name.equalsIgnoreCase(tag.getString("EnumSunType")))
                return type;
        return BLUE_DWARF;
    }

    /**
     * Used to get the default radius of this sun
     * @return The default radius
     */
    public float getDefaultRadius() {
        return defaultRadius;
    }

    /**
     * Used to get the offset radius of this sun
     * @return The offset radius
     */
    public float getSecondLayerOffset() {
        return secondLayerOffset;
    }

    /**
     * Used to get what color to define as
     * @return The color of this object
     */
    public Color getColor() {
        return cachedColor;
    }
}
