package com.dyonovan.neotech.client;

import com.dyonovan.neotech.lib.Reference;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

/**
 * This file was created for NeoTech
 * <p/>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis "pauljoda"
 * @since 3/6/2016
 */
public class ModelLoaderHelper {

    public static void registerItem(Item item, String name, String variants) {
        ModelLoader.registerItemVariants(item,
                new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID(), name), variants));
    }
}
