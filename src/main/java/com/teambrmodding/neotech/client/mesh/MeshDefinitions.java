package com.teambrmodding.neotech.client.mesh;

import com.teambrmodding.neotech.lib.Reference;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/11/2017
 */
public class MeshDefinitions {
    public static class SimpleItemMeshDefinition implements ItemMeshDefinition {
        protected String modelName;
        protected String variants;

        public SimpleItemMeshDefinition(String modelname, String vars) {
            modelName = modelname;
            variants = vars;
        }

        @Override
        public ModelResourceLocation getModelLocation(ItemStack stack) {
            return new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID + ":items/" + modelName), variants);
        }
    }

    public static class ModelLocationWrapper implements ItemMeshDefinition {
        private ModelResourceLocation location;

        public ModelLocationWrapper(ModelResourceLocation location) {
            this.location = location;
        }

        @Override
        public ModelResourceLocation getModelLocation(ItemStack stack) {
            return location;
        }
    }
}