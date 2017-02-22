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
 * @since 2/15/2017
 */
public class FluidFuelRecipeHandler extends AbstractRecipeHandler<FluidFuelRecipeHandler.FluidFuelRecipe, FluidStack, Pair<Integer, Integer>> {

    /**
     * Used to get the base name of the files
     *
     * @return The base name for the files
     */
    @Override
    public String getBaseName() {
        return "fluidFuelValues";
    }

    /**
     * This is the current version of the registry, if you update this it will cause the registry to be redone
     *
     * @return Current version number
     */
    @Override
    public int getVersion() {
        return 0;
    }

    /**
     * Used to get the default folder location
     *
     * @return The folder location
     */
    @Override
    public String getBaseFolderLocation() {
        return Neotech.configFolderLocation;
    }

    /**
     * Used to get what type token to read from file (Generics don't handle well)
     *
     * @return A defined type token
     */
    @Override
    public TypeToken<ArrayList<FluidFuelRecipe>> getTypeToken() {
        return new TypeToken<ArrayList<FluidFuelRecipe>>() {};
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
                return "addFluidFuelValues";
            }

            @Override
            public int getRequiredPermissionLevel() {
                return 3;
            }

            @Override
            public String getCommandUsage(ICommandSender sender) {
                return "commands.addFluidFuelValues.usage";
            }

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                if(args.length < 3)
                    sender.addChatMessage(new TextComponentString(ClientUtils.translate(getCommandUsage(sender))));
                else if(getFluidStackFromString(args[0]) != null &&
                        getFluidStackFromString(args[0]).getFluid() != null) {
                    addRecipe(new FluidFuelRecipe(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2])));
                    sender.addChatMessage(new TextComponentString(args[0] + " -> " + args[1] + " Added Successfully!"));
                    saveToFile();
                    return;
                }
                sender.addChatMessage(new TextComponentString(ClientUtils.translate(getCommandUsage(sender))));
            }
        };
    }

    @Override
    protected void generateDefaultRecipes() {
        LogHelper.logger.info("[Neotech] Generating Fluid Fuel Values...");
        addRecipe(new FluidFuelRecipe("lava:100", 640, 20));
        addRecipe(new FluidFuelRecipe("hydrogen:100", 200, 200));
        addRecipe(new FluidFuelRecipe("ghasttear:" + MetalManager.INGOT_MB, 350, 286));
        addRecipe(new FluidFuelRecipe("chorus:" + MetalManager.INGOT_MB, 500, 800));
        addRecipe(new FluidFuelRecipe("wither:" + MetalManager.INGOT_MB, 1000, 1000));
        saveToFile();
    }

    public static class FluidFuelRecipe extends AbstractRecipe<FluidStack, Pair<Integer, Integer>> {
        public String fluidStackInput;
        public int burnTime, burnRate;

        /**
         * Creates a recipe
         * @param fluidStackInput The input
         * @param burnTime        How many ticks it burns
         * @param burnRate        How many RF/Tick to generate
         */
        public FluidFuelRecipe(String fluidStackInput, int burnTime, int burnRate) {
            this.fluidStackInput = fluidStackInput;
            this.burnTime = burnTime;
            this.burnRate = burnRate;
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
        public Pair<Integer, Integer> getOutput(FluidStack input) {
            if(input == null || input.getFluid() == null)
                return null;

            if(isValidInput(input))
                return Pair.of(burnTime, burnRate);

            return null;
        }

        /**
         * Is the input valid for an output
         *
         * @param input The input object
         * @return True if there is an output
         */
        @Override
        public boolean isValidInput(FluidStack input) {
            return input != null && input.getFluid() != null &&
                    input.getFluid().getName().equalsIgnoreCase(getFluidStackFromString(fluidStackInput).getFluid().getName());
        }
    }
}
