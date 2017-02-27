package com.teambrmodding.neotech.client.models;

import com.teambrmodding.neotech.lib.Reference;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/27/2017
 */
public class ModelFactory {
    // Public instance of event
    public static ModelFactory INSTANCE = new ModelFactory();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void bakeModels(ModelBakeEvent event) {
        // Tanks

        // Basic Tank Model
        ModelResourceLocation basicTank = new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID, "basicTank"), "inventory");
        event.getModelRegistry().putObject(basicTank,
                new ModelItemFluidStorage(event.getModelRegistry().getObject(basicTank)));

        ModelResourceLocation advancedTank = new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID, "advancedTank"), "inventory");
        event.getModelRegistry().putObject(advancedTank,
                new ModelItemFluidStorage(event.getModelRegistry().getObject(advancedTank)));

        ModelResourceLocation eliteTank = new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID, "eliteTank"), "inventory");
        event.getModelRegistry().putObject(eliteTank,
                new ModelItemFluidStorage(event.getModelRegistry().getObject(eliteTank)));
    }
}
