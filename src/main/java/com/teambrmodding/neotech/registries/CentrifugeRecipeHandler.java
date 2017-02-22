package com.teambrmodding.neotech.registries;

import com.google.gson.reflect.TypeToken;
import com.teambr.bookshelf.helper.LogHelper;
import com.teambr.bookshelf.util.ClientUtils;
import com.teambrmodding.neotech.Neotech;
import com.teambrmodding.neotech.managers.MetalManager;
import com.teambrmodding.neotech.registries.recipes.CentrifugeRecipe;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/14/2017
 */
public class CentrifugeRecipeHandler extends AbstractRecipeHandler<CentrifugeRecipe, FluidStack, Pair<FluidStack, FluidStack>> {

    /**
     * Used to get the base name of the files
     *
     * @return The base name for the files
     */
    @Override
    public String getBaseName() {
        return "centrifuge";
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
    public TypeToken<ArrayList<CentrifugeRecipe>> getTypeToken() {
        return new TypeToken<ArrayList<CentrifugeRecipe>>() {
        };
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
                return "addCentrifugeRecipe";
            }

            @Override
            public int getRequiredPermissionLevel() {
                return 3;
            }

            @Override
            public String getCommandUsage(ICommandSender sender) {
                return "commands.addCentrifugeRecipe.usage";
            }

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                if (args.length < 3)
                    sender.addChatMessage(new TextComponentString(ClientUtils.translate("commands.addCentrifugeRecipe.usage")));
                else {
                    String input = args[0];
                    String outputOne = args[1];
                    String outputTwo = args[2];

                    if (getFluidStackFromString(input) != null && getFluidStackFromString(outputOne) != null && getFluidStackFromString(outputTwo) != null) {
                        addRecipe(new CentrifugeRecipe(input, outputOne, outputTwo));
                        sender.addChatMessage(new TextComponentString(input + " -> " + outputOne + " + " + outputTwo + " Added Successfully!"));
                        saveToFile();
                    } else
                        sender.addChatMessage(new TextComponentString(input + " -> " + outputOne + " + " + outputTwo + " Failed To Add"));
                }
            }
        };
    }

    /**
     * Called when the file is not found, add all default recipes here
     */
    @Override
    protected void generateDefaultRecipes() {
        LogHelper.logger.info("[Neotech] Loading Default Centrifuge Recipes...");

        // Obsidian
        addRecipe(new CentrifugeRecipe("obsidian:288", "water:1000", "lava:1000"));

        // Steel
        addRecipe(new CentrifugeRecipe("steel:144", "iron:144", "carbon:144"));

        // Bronze
        addRecipe(new CentrifugeRecipe("bronze:576", "copper:432", "tin:144"));

        // Hydrogen - Oxygen
        addRecipe(new CentrifugeRecipe("water:750", "hydrogen:500", "oxygen:250"));

        // Metals
        for (Object o : MetalManager.metalRegistry.keySet()) {
            MetalManager.Metal metal = MetalManager.metalRegistry.get(o);
            String dirtyName = "dirty" + metal.getOreDict();
            if (MetalManager.getMetal(dirtyName) != null && FluidRegistry.isFluidRegistered(dirtyName))
                addRecipe(new CentrifugeRecipe(dirtyName + ":144", metal.getOreDict() + ":144", "lava:16"));
        }

        saveToFile();
    }
}

