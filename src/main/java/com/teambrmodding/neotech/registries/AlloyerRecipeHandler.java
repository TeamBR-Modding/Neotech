package com.teambrmodding.neotech.registries;

import com.google.gson.reflect.TypeToken;
import com.teambr.bookshelf.helper.LogHelper;
import com.teambr.bookshelf.util.ClientUtils;
import com.teambrmodding.neotech.Neotech;
import com.teambrmodding.neotech.managers.MetalManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/14/2017
 */
public class AlloyerRecipeHandler extends AbstractRecipeHandler<AlloyerRecipeHandler.AlloyerRecipe, Pair<FluidStack, FluidStack>, FluidStack> {

    /**
     * Used to get the base name of the files
     *
     * @return The base name
     */
    @Override
    public String getBaseName() {
        return "alloyer";
    }

    /**
     * This is the current version of the registry, if you update this it will cause the registry to be redone
     *
     * @return The current version
     */
    @Override
    public int getVersion() {
        return 0;
    }

    /**
     * Used to get the default folder location
     *
     * @return The folder loc
     */
    @Override
    public String getBaseFolderLocation() {
        return Neotech.configFolderLocation;
    }

    /**
     * Used to get what type token to read from file (Generics don't handle well)
     *
     * @return The type token
     */
    @Override
    public TypeToken<ArrayList<AlloyerRecipe>> getTypeToken() {
        return new TypeToken<ArrayList<AlloyerRecipe>>() {};
    }

    /**
     * Get the command to add values to the registry
     *
     * @return A new command
     */
    @Override
    public CommandBase getCommand() {
        return new CommandBase() {

            @Override
            public String getCommandName() {
                return "addAlloyRecipe";
            }

            @Override
            public int getRequiredPermissionLevel() {
                return 3;
            }

            @Override
            public String getCommandUsage(ICommandSender sender) {
                return "commands.addAlloyRecipe";
            }

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                if(args.length < 3)
                    sender.addChatMessage(new TextComponentString(ClientUtils.translate("commands.addAlloyRecipe.usage")));
                else {
                    String input  = args[0];
                    String input2 = args[1];
                    String output = args[2];

                    if(getFluidStackFromString(input) != null && getFluidStackFromString(input2) != null && getFluidStackFromString(output) != null) {
                        addRecipe(new AlloyerRecipe(input, input2, output));
                        sender.addChatMessage(new TextComponentString(input + " + " + input2 + " -> " + output + " Added successfully"));
                        saveToFile();
                    } else
                        sender.addChatMessage(new TextComponentString(input + " + " + input2 + " -> " + output + " Failed Adding"));
                }
            }
        };
    }

    /**
     * Called when the file is not found, add all default recipes here
     */
    @Override
    protected void generateDefaultRecipes() {
        LogHelper.logger.info("[Neotech] Loading Default Alloyer Recipes...");

        // Obsidian
        addRecipe(new AlloyerRecipe("water:1000", "lava:1000", "obsidian:288"));

        // Steel
        addRecipe(new AlloyerRecipe("iron:144", "carbon:144", "steel:144"));

        // Bronze
        addRecipe(new AlloyerRecipe("copper:432", "tin:144", "bronze:576"));

        addRecipe(new AlloyerRecipe("ghasttear:" + MetalManager.INGOT_MB, "iron:" + MetalManager.INGOT_MB,
                "tormented:" + MetalManager.INGOT_MB));

        saveToFile();
    }

    /**
     * Used to test if a single stack is valid
     * @param input The input
     * @return Is valid
     */
    public boolean isValidSingle(FluidStack input) {
        for(AlloyerRecipe recipe : recipes)
            if(recipe.isValidSingleInput(input))
                return true;
        return false;
    }

    public class AlloyerRecipe extends AbstractRecipe<Pair<FluidStack, FluidStack>, FluidStack> {
        // Variables
        public String fluidStackOne, fluidStackTwo, fluidStackOutput;

        /**
         * Creates A recipe object
         *
         * All fluids should be format FLUID:AMOUNT
         *
         * @param fluidOne The first fluid
         * @param fluidTwo The second fluid
         * @param fluidOutput The fluid output
         */
        public AlloyerRecipe(String fluidOne, String fluidTwo, String fluidOutput) {
            this.fluidStackOne = fluidOne;
            this.fluidStackTwo = fluidTwo;
            this.fluidStackOutput = fluidOutput;
        }

        /**
         * Used to check if a single fluid is valid for the recipes
         *
         * @param input FluidStack
         * @return Boolean
         */
        public boolean isValidSingleInput(FluidStack input) {
            return input != null &&
                    (getFluidStackFromString(fluidStackOne).getFluid().getName().equalsIgnoreCase(input.getFluid().getName()) ||
                            getFluidStackFromString(fluidStackTwo).getFluid().getName().equalsIgnoreCase(input.getFluid().getName())) &&
                    (getFluidStackFromString(fluidStackOne).amount <= input.amount || getFluidStackFromString(fluidStackTwo).amount <= input.amount);
        }

        /***************************************************************************************************************
         * AbstractRecipe                                                                                              *
         ***************************************************************************************************************/

        /**
         * Used to get the output of this recipe
         *
         * @param input The input object
         * @return The output object, can be null
         */
        @Nullable
        @Override
        public FluidStack getOutput(Pair<FluidStack, FluidStack> input) {
            if(isValidInput(input))
                return getFluidStackFromString(fluidStackOutput);
            return null;
        }

        /**
         * Is the input valid for an output
         *
         * @param input The input object
         * @return True if there is an output
         */
        @Override
        public boolean isValidInput(Pair<FluidStack, FluidStack> input) {
            return !(input.getLeft() == null || input.getRight() == null ||
                    input.getLeft().getFluid() == null || input.getRight().getFluid() == null) &&
                    ((getFluidStackFromString(fluidStackOne).getFluid().getName().equalsIgnoreCase(input.getLeft().getFluid().getName()) &&
                            getFluidStackFromString(fluidStackTwo).getFluid().getName().equalsIgnoreCase(input.getRight().getFluid().getName()) &&
                            getFluidStackFromString(fluidStackOne).amount <= input.getLeft().amount &&
                            getFluidStackFromString(fluidStackTwo).amount <= input.getRight().amount) ||
                            (getFluidStackFromString(fluidStackTwo).getFluid().getName().equalsIgnoreCase(input.getLeft().getFluid().getName()) &&
                                    getFluidStackFromString(fluidStackOne).getFluid().getName().equalsIgnoreCase(input.getRight().getFluid().getName()) &&
                                    getFluidStackFromString(fluidStackTwo).amount <= input.getLeft().amount &&
                                    getFluidStackFromString(fluidStackOne).amount <= input.getRight().amount));
        }
    }
}
