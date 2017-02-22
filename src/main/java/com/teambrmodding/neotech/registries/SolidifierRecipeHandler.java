package com.teambrmodding.neotech.registries;

import com.google.gson.reflect.TypeToken;
import com.teambr.bookshelf.helper.LogHelper;
import com.teambr.bookshelf.util.ClientUtils;
import com.teambrmodding.neotech.Neotech;
import com.teambrmodding.neotech.managers.MetalManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;

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
public class SolidifierRecipeHandler extends
        AbstractRecipeHandler<SolidifierRecipeHandler.SolidifierRecipe, Pair<SolidifierRecipeHandler.SolidifierMode, FluidStack>, ItemStack> {

    /**
     * Used to get the base name of the files
     *
     * @return The base name for the files
     */
    @Override
    public String getBaseName() {
        return "solidifier";
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
    public TypeToken<ArrayList<SolidifierRecipe>> getTypeToken() {
        return new TypeToken<ArrayList<SolidifierRecipe>>() {};
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
                return "addSolidifierRecipe";
            }

            @Override
            public int getRequiredPermissionLevel() {
                return 3;
            }

            @Override
            public String getCommandUsage(ICommandSender sender) {
                return "commands.addSolidifierRecipe.usage";
            }

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                if(args.length != 3 || (args.length == 2 && !args[0].equalsIgnoreCase("hands")))
                    sender.addChatMessage(new TextComponentString(ClientUtils.translate(getCommandUsage(sender))));
                else if(args.length == 2 && args[0].equalsIgnoreCase("hands")) { // Allow user to hold recipe

                    String mode = args[1];
                    SolidifierMode requiredMode = null;
                    if(mode.equalsIgnoreCase("BLOCK"))
                        requiredMode = SolidifierMode.BLOCK_MODE;
                    else if(mode.equalsIgnoreCase("INGOT"))
                        requiredMode = SolidifierMode.INGOT_MODE;
                    else if(mode.equalsIgnoreCase("NUGGET"))
                        requiredMode = SolidifierMode.NUGGET_MODE;
                    else {
                        // Conditions for hands usage not met
                        sender.addChatMessage(new TextComponentString(ClientUtils.translate(getCommandUsage(sender))));
                        return;
                    }

                    // Must be a player using the command as we need their hands
                    if(sender.getCommandSenderEntity() instanceof EntityPlayer) {
                        EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity(); // Get the player

                        // Make sure both hands have a stack
                        if(player.getHeldItemMainhand() != null && player.getHeldItemOffhand() != null) {
                            ItemStack mainHandStack = player.getHeldItemMainhand();
                            ItemStack offHandStack  = player.getHeldItemOffhand();

                            // Offhand must hold a fluid handler
                            if(offHandStack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
                                IFluidHandler fluidHandler = offHandStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);

                                // Cycle the tanks (usually one) to find a tank with a fluid
                                for(IFluidTankProperties tankInfo : fluidHandler.getTankProperties()) {
                                    FluidStack stackInTank = tankInfo.getContents();

                                    // Is a valid FluidStack
                                    if(stackInTank != null && stackInTank.getFluid() != null) {
                                        addRecipe(new SolidifierRecipe(requiredMode, getItemStackString(mainHandStack), getFluidStackString(stackInTank)));
                                        sender.addChatMessage(new TextComponentString(getItemStackString(mainHandStack) + " -> " +
                                                getFluidStackString(stackInTank) + " Added Successfully!"));
                                        saveToFile();
                                        return;
                                    }
                                }
                            }
                        }
                    }

                    // Conditions for hands usage not met
                    sender.addChatMessage(new TextComponentString(ClientUtils.translate(getCommandUsage(sender))));
                } else { // Not hands, has three args
                    String fluidStackInput = args[1];
                    String itemStackOutput = args[2];

                    String mode = args[0];
                    SolidifierMode requiredMode = null;
                    if(mode.equalsIgnoreCase("BLOCK"))
                        requiredMode = SolidifierMode.BLOCK_MODE;
                    else if(mode.equalsIgnoreCase("INGOT"))
                        requiredMode = SolidifierMode.INGOT_MODE;
                    else if(mode.equalsIgnoreCase("NUGGET"))
                        requiredMode = SolidifierMode.NUGGET_MODE;
                    else {
                        // Conditions for hands usage not met
                        sender.addChatMessage(new TextComponentString(ClientUtils.translate(getCommandUsage(sender))));
                        return;
                    }

                    if(getFluidStackFromString(fluidStackInput) != null && getItemStackFromString(itemStackOutput) != null) {
                        addRecipe(new SolidifierRecipe(requiredMode, fluidStackInput, itemStackOutput));
                        sender.addChatMessage(new TextComponentString(fluidStackInput + " -> " + itemStackOutput + " Added Successfully!"));
                        saveToFile();
                    } else
                        sender.addChatMessage(new TextComponentString(ClientUtils.translate(getCommandUsage(sender))));
                }
            }
        };
    }

    @Override
    protected void generateDefaultRecipes() {
        LogHelper.logger.info("[Neotech] Generating Solidifier Recipes...");

        // Metals
        for(String key : MetalManager.metalRegistry.keySet()) {
            MetalManager.Metal metal = MetalManager.getMetal(key);
            if(FluidRegistry.isFluidRegistered(metal.getOreDict())) {
                // Block
                if(metal.getSolidBlock() != null)
                    addRecipe(new SolidifierRecipe(SolidifierMode.BLOCK_MODE, metal.getOreDict() + ":" + MetalManager.BLOCK_MB,
                            getItemStackString(new ItemStack(metal.getSolidBlock(), 1))));

                // Ingot
                if(metal.getIngot() != null)
                    addRecipe(new SolidifierRecipe(SolidifierMode.INGOT_MODE, metal.getOreDict() + ":" + MetalManager.INGOT_MB,
                            getItemStackString(new ItemStack(metal.getIngot(), 1))));

                // Nugget
                if(metal.getNugget() != null)
                    addRecipe(new SolidifierRecipe(SolidifierMode.NUGGET_MODE, metal.getOreDict() + ":" + MetalManager.NUGGET_MB,
                            getItemStackString(new ItemStack(metal.getNugget(), 1))));
            }
        }

        // Vanilla Stuff

        //Iron
        addRecipe(new SolidifierRecipe(SolidifierMode.BLOCK_MODE, "iron:" + MetalManager.BLOCK_MB, "blockIron"));
        addRecipe(new SolidifierRecipe(SolidifierMode.INGOT_MODE, "iron:" + MetalManager.INGOT_MB, "ingotIron"));

        // Gold
        addRecipe(new SolidifierRecipe(SolidifierMode.BLOCK_MODE,  "gold:" + MetalManager.BLOCK_MB , "blockGold"));
        addRecipe(new SolidifierRecipe(SolidifierMode.INGOT_MODE,  "gold:" + MetalManager.INGOT_MB,  "ingotGold"));
        addRecipe(new SolidifierRecipe(SolidifierMode.NUGGET_MODE, "gold:" + MetalManager.NUGGET_MB, "nuggetGold"));

        // Carbon
        addRecipe(new SolidifierRecipe(SolidifierMode.BLOCK_MODE, "carbon:" + MetalManager.BLOCK_MB,
                getItemStackString(new ItemStack(Blocks.COAL_BLOCK, 1))));
        addRecipe(new SolidifierRecipe(SolidifierMode.INGOT_MODE, "carbon:" + MetalManager.INGOT_MB,
                getItemStackString(new ItemStack(Items.COAL, 1))));

        // Obsidian
        addRecipe(new SolidifierRecipe(SolidifierMode.BLOCK_MODE, "obsidian:288",
                getItemStackString(new ItemStack(Blocks.OBSIDIAN, 1))));

        // Ghast Tear
        addRecipe(new SolidifierRecipe(SolidifierMode.INGOT_MODE, "ghasttear:" + MetalManager.INGOT_MB,
                getItemStackString(new ItemStack(Items.GHAST_TEAR))));

        // Chorus
        addRecipe(new SolidifierRecipe(SolidifierMode.BLOCK_MODE, "chorus:" + MetalManager.NUGGET_MB,
                getItemStackString(new ItemStack(Items.CHORUS_FRUIT_POPPED))));

        // Wither
        addRecipe(new SolidifierRecipe(SolidifierMode.NUGGET_MODE, "wither:" + MetalManager.NUGGET_MB,
                getItemStackString(new ItemStack(Items.NETHER_STAR))));

        saveToFile();
    }

    /**
     * Enumeration to handle what mode to be in
     */
    public enum SolidifierMode {
        BLOCK_MODE(MetalManager.BLOCK_MB, "blockIron", "neotech.text.blockMode"),
        INGOT_MODE(MetalManager.INGOT_MB, "ingotIron", "neotech.text.ingotMode"),
        NUGGET_MODE(MetalManager.NUGGET_MB, "nuggetIron", "neotech.text.nuggetMode");

        private String displayStack, displayName;
        private int requiredAmount;

        SolidifierMode(int requiredAmount, String displayStack, String displayName) {
            this.requiredAmount = requiredAmount;
            this.displayStack = displayStack;
            this.displayName = displayName;
        }

        /**
         * Gets the name to display translated
         * @return The display name
         */
        public String getDisplayName() {
            return ClientUtils.translate(displayName);
        }

        /**
         * Get how many mb this mode requires
         * @return The amount of mb
         */
        public int getRequiredAmount() {
            return requiredAmount;
        }

        /**
         * Used to get what display as the stack
         * @return The display stack
         */
        public ItemStack getDisplayStack() {
            return getItemStackFromString(displayStack);
        }

        /**
         * Get the next mode in the list, used for toggling and cycling options
         * @return The next mode
         */
        public SolidifierMode getNextMode() {
            if(ordinal() + 1 < SolidifierMode.values().length)
                return SolidifierMode.values()[ordinal() + 1];
            else
                return SolidifierMode.values()[0];
        }
    }

    public static class SolidifierRecipe extends AbstractRecipe<Pair<SolidifierMode, FluidStack>, ItemStack> {
        public String inputFluidStack, outputItemStack;
        public SolidifierMode requiredMode;

        /**
         * Creates recipe
         * @param inputFluidStack Input Fluid Stack
         * @param outputItemStack Output ItemStack
         */
        public SolidifierRecipe(SolidifierMode mode, String inputFluidStack, String outputItemStack) {
            this.requiredMode = mode;
            this.inputFluidStack = inputFluidStack;
            this.outputItemStack = outputItemStack;
        }

        /***************************************************************************************************************
         * AbstractRecipe                                                                                              *
         ***************************************************************************************************************/

        /**
         * Used to get the output of this recipe
         *
         * @param input The input object
         * @return The output object
         */
        @Nullable
        @Override
        public ItemStack getOutput(Pair<SolidifierMode, FluidStack> input) {
            if(input == null || input.getRight().getFluid() == null)
                return null;

            if(isValidInput(input))
                return getItemStackFromString(outputItemStack);

            return null;
        }

        /**
         * Is the input valid for an output
         *
         * @param input The input object
         * @return True if there is an output
         */
        @Override
        public boolean isValidInput(Pair<SolidifierMode, FluidStack> input) {
            return input.getLeft() == requiredMode &&
                    !(input == null || input.getRight().getFluid() == null) &&
                    getFluidStackFromString(inputFluidStack).getFluid().getName().equalsIgnoreCase(input.getRight().getFluid().getName()) &&
                    input.getRight().amount >= getFluidStackFromString(inputFluidStack).amount;
        }
    }
}
