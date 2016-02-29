package com.dyonovan.neotech.client;

import net.minecraft.block.properties.IProperty;
import net.minecraftforge.common.property.IUnlistedProperty;

/**
 * This file was created for NeoTech
 * <p/>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis "pauljoda"
 * @since 2/29/2016
 */
public class UnlistedPropertyWrapper<V> implements IUnlistedProperty<V> {

    protected IProperty property;

    public UnlistedPropertyWrapper(IProperty wrapped) {
        property = wrapped;
    }

    @Override
    public String getName() {
        return property.getName();
    }

    @Override
    public boolean isValid(V value) {
        return property.getAllowedValues().contains(value);
    }

    @Override
    public Class<V> getType() {
        return property.getValueClass();
    }

    @Override
    public String valueToString(V value) {
        return value.toString();
    }
}
