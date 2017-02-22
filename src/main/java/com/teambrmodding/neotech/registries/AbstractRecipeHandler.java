package com.teambrmodding.neotech.registries;

import com.google.gson.reflect.TypeToken;
import com.teambr.bookshelf.helper.LogHelper;
import com.teambr.bookshelf.util.JsonUtils;
import com.teambrmodding.neotech.registries.recipes.AbstractRecipe;
import net.minecraft.command.CommandBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * R : Recipe Object, this should be what holds all the information for recipes you need
 * I : Input Object, the input for this recipe handler
 * O : Output Object, the output for this recipe handler
 *
 * @author Paul Davis - pauljoda
 * @since 2/14/2017
 */
public abstract class AbstractRecipeHandler<R extends AbstractRecipe<I, O>, I, O> {
    //Variables
    public List<R> recipes;

    /*******************************************************************************************************************
     * Abstract Methods                                                                                                *
     *******************************************************************************************************************/

    /**
     * Used to get the base name of the files
     *
     * @return The base name for the files
     */
    public abstract String getBaseName();

    /**
     * This is the current version of the registry, if you update this it will cause the registry to be redone
     *
     * @return Current version number
     */
    public abstract int getVersion();

    /**
     * Used to get the default folder location
     *
     * @return The folder location
     */
    public abstract String getBaseFolderLocation();

    /**
     * Used to get what type token to read from file (Generics don't handle well)
     *
     * @return A defined type token
     */
    public abstract TypeToken<ArrayList<R>> getTypeToken();

    /**
     * Get the command to add values to the registry
     *
     * @return A new command
     */
    public abstract CommandBase getCommand();

    /**
     * Called when the file is not found, add all default recipes here
     */
    protected abstract void generateDefaultRecipes();

    /*******************************************************************************************************************
     * AbstractRecipeHandler                                                                                           *
     *******************************************************************************************************************/

    /**
     * Called to load the handler, it will try to load the file and if not call generateDefaultRecipes
     */
    public AbstractRecipeHandler<R, I, O> loadHandler() {
        checkConfig();
        if(!loadFromFile())
            generateDefaultRecipes();
        else
            LogHelper.logger.info("[Neotech]" + getBaseName() + " Recipes loaded successfully");
        return this;
    }

    /**
     * Load the values from the file
     *
     * @return True if successful
     */
    protected boolean loadFromFile() {
        LogHelper.logger.info("[Neotech] Loading " + getBaseName() + " Recipes...");
        recipes = JsonUtils.readFromJson(getTypeToken(),
                getBaseFolderLocation() + File.separator + "Registries" + File.separator + getBaseName() + "Recipes.json");
        if(recipes == null)
            recipes = new ArrayList<>();
        return !recipes.isEmpty();
    }

    /**
     * Save the current registry to a file
     */
    protected void saveToFile() {
        if(!recipes.isEmpty())
            JsonUtils.writeToJson(recipes, getBaseFolderLocation() +
                    File.separator + "Registries" + File.separator + getBaseName() + "Recipes.json");
    }

    /**
     * Used to add a recipe to the handler
     *
     * @param recipe The Recipe object
     */
    public AbstractRecipeHandler<R, I, O> addRecipe(R recipe) {
        recipes.add(recipe);
        return this;
    }

    /**
     * Used to get the output for the handler
     *
     * @param input Input
     * @return Output object
     */
    @Nullable
    public O getOutput(I input) {
        if(input == null) // Safety Check
            return null;

        // Check for registered
        for(R recipe : recipes) {
            if(recipe.getOutput(input) != null)
                return recipe.getOutput(input);
        }

        return null;
    }

    /**
     * Used to check if the input is valid
     *
     * @param input The input
     * @return True if valid by a recipe
     */
    public boolean isValidInput(I input) {
        if(input == null) // Safety check
            return false;

        for(R recipe : recipes) {
            if(recipe.isValidInput(input))
                return true;
        }

        return false;
    }

    /**
     * Gets the recipe object for the given input
     * @param input The input
     * @return The recipe that matches
     */
    @Nullable
    public R getRecipe(I input) {
        for(R recipe : recipes) {
            if(recipe.isValidInput(input))
                return recipe;
        }
        return null;
    }

    /*******************************************************************************************************************
     * Config Methods                                                                                                  *
     *******************************************************************************************************************/

    // Local instance of config
    private Configuration config =
            new Configuration(new File(getBaseFolderLocation() + File.separator + "Registries" + File.separator + getBaseName() + "Recipes.cfg"));

    /**
     * Checks the config for user values and writes new version if need be
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    protected void checkConfig() {
        config.load();

        boolean autoUpdate =
                config.getBoolean("autoupdate", "recipeconfig", true,
                        "Set to false to disable auto updating");

        int lastVersion =
                config.getInt("version", "recipeconfig", getVersion(), 0, Integer.MAX_VALUE,
                        "This is the last checked version number");

        config.save(); // Write values

        if(autoUpdate && lastVersion != getVersion()) {
            config.load();

            File oldFile = new File(getBaseFolderLocation() +
                    File.separator + "Registries" + File.separator + getBaseName() + "Recipes.json");

            File newFile = new File(getBaseFolderLocation() +
                    File.separator + "Registries" + File.separator + getBaseName() + "RecipesOLD-" + lastVersion + ".json");

            if(!newFile.exists()) {
                oldFile.renameTo(newFile);

                config.getCategory("recipeconfig").get("version").set(getVersion());
            }
            config.save();

        }
    }

    /*******************************************************************************************************************
     * Class Helper Methods                                                                                            *
     *******************************************************************************************************************/

    // Duplicate for ease when adding recipes

    /**
     * Used to get the string form of an ItemStack
     *
     * @param itemStack The stack to translate
     * @return A string version of the stack in format MODID:ITEMID:DAMAGE:STACK_SIZE
     */
    public static String getItemStackString(@Nonnull ItemStack itemStack) {
        return AbstractRecipe.getItemStackString(itemStack);
    }

    /**
     * Used to get a stack from a string
     *
     * @param itemString The item string in format MODID:ITEMID:DAMAGE:STACK_SIZE
     * @return The stack for the string
     */
    @Nullable
    public static ItemStack getItemStackFromString(String itemString) {
        return AbstractRecipe.getItemStackFromString(itemString);
    }

    /**
     * Converts a FluidStack into a string form
     *
     * @param fluidStack The stack
     * @return The string in form FLUID:AMOUNT
     */
    public static String getFluidStackString(FluidStack fluidStack) {
        return AbstractRecipe.getFluidStackString(fluidStack);
    }

    /**
     * Converts the string form of a fluid into a stack
     *
     * @param fluidString The string in format FLUID:AMOUNT
     * @return The FluidStack for the string
     */
    @Nullable
    public static FluidStack getFluidStackFromString(String fluidString) {
        return AbstractRecipe.getFluidStackFromString(fluidString);
    }
}
