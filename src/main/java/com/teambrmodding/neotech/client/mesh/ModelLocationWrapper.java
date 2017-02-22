package com.teambrmodding.neotech.client.mesh;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

/**
* This file was created for NeoTech
* <p>
* NeoTech is licensed under the
* Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
* http://creativecommons.org/licenses/by-nc-sa/4.0/
*
* @author Paul Davis - pauljoda
* @since 2/22/2017
*/
public class ModelLocationWrapper implements ItemMeshDefinition {
    private ModelResourceLocation location;

    public ModelLocationWrapper(ModelResourceLocation location) {
        this.location = location;
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack stack) {
        return location;
    }
}
