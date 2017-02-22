package com.teambrmodding.neotech.managers;

import com.teambrmodding.neotech.registries.*;
import gnu.trove.map.hash.THashMap;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ServerCommandManager;

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/15/2017
 */
public class RecipeManager {

    // The list of all recipe handlers
    public static THashMap<RecipeType, AbstractRecipeHandler> recipeHandlers = new THashMap<>();

    /**
     * Called to register things
     */
    public static void preInit() {
        CraftingRecipeManager.preInit();
    }

    /**
     * Builds the recipe registry
     */
    public static void init() {
        recipeHandlers.put(RecipeType.FLUID_FUELS, new FluidFuelRecipeHandler().loadHandler());
        recipeHandlers.put(RecipeType.CRUSHER,     new CrusherRecipeHandler().loadHandler());
        recipeHandlers.put(RecipeType.CRUCIBLE,    new CrucibleRecipeHandler().loadHandler());
        recipeHandlers.put(RecipeType.SOLIDIFIER,  new SolidifierRecipeHandler().loadHandler());
        recipeHandlers.put(RecipeType.ALLOYER,     new AlloyerRecipeHandler().loadHandler());
        recipeHandlers.put(RecipeType.CENTRIFUGE,  new CentrifugeRecipeHandler().loadHandler());
    }

    /**
     * Loads all the commands from the handlers
     * @param manager The manager to register to
     */
    public static void initCommands(ServerCommandManager manager) {
        for(RecipeType recipeType : recipeHandlers.keySet()) {
            CommandBase command = recipeHandlers.get(recipeType).getCommand();
            if(command != null)
                manager.registerCommand(command);
        }
    }

    /*******************************************************************************************************************
     * Helper Methods                                                                                                  *
     *******************************************************************************************************************/

    /**
     * Used to get a recipe handler
     * @param recipeType The recipe type
     * @return The recipe handler
     */
    public static  <H extends AbstractRecipeHandler> H getHandler(RecipeType recipeType) {
        return (H) recipeHandlers.get(recipeType);
    }

    public enum RecipeType {
        FLUID_FUELS("fluid_fuels"),
        CRUSHER("crusher"),
        CRUCIBLE("crucible"),
        SOLIDIFIER("solidifier"),
        ALLOYER("alloyer"),
        CENTRIFUGE("centrifuge");

        private String name;

        RecipeType(String name) {
            this.name = name;
        }

        /**
         * Get the name of this recipe type
         * @return The name
         */
        public String getName() {
            return name;
        }
    }
}
