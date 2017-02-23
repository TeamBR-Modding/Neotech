package com.teambrmodding.neotech.registries;

import com.google.gson.reflect.TypeToken;
import com.teambr.bookshelf.helper.LogHelper;
import com.teambr.bookshelf.util.ClientUtils;
import com.teambrmodding.neotech.Neotech;
import com.teambrmodding.neotech.managers.MetalManager;
import com.teambrmodding.neotech.registries.recipes.CrucibleRecipe;
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
public class CrucibleRecipeHandler extends AbstractRecipeHandler<CrucibleRecipe, ItemStack, FluidStack> {

    /**
     * Used to get the base name of the files
     *
     * @return The base name for the files
     */
    @Override
    public String getBaseName() {
        return "crucible";
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
    public TypeToken<ArrayList<CrucibleRecipe>> getTypeToken() {
        return new TypeToken<ArrayList<CrucibleRecipe>>() {
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
                return "addCrucibleRecipe";
            }

            @Override
            public int getRequiredPermissionLevel() {
                return 3;
            }

            @Override
            public String getCommandUsage(ICommandSender sender) {
                return "commands.addCrucibleRecipe.usage";
            }

            @Override
            public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                if (args.length != 3 || (args.length == 2 && !args[0].equalsIgnoreCase("hands")))
                    sender.addChatMessage(new TextComponentString(ClientUtils.translate(getCommandUsage(sender))));
                else if (args.length == 2 && args[0].equalsIgnoreCase("hands")) {
                    // Must be a player using the command as we need their hands
                    if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
                        EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity(); // Get the player

                        // Make sure both hands have a stack
                        if (player.getHeldItemMainhand() != null && player.getHeldItemOffhand() != null) {
                            ItemStack mainHandStack = player.getHeldItemMainhand();
                            ItemStack offHandStack = player.getHeldItemOffhand();

                            // Offhand must hold a fluid handler
                            if (mainHandStack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
                                IFluidHandler fluidHandler = mainHandStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);

                                // Cycle the tanks (usually one) to find a tank with a fluid
                                for (IFluidTankProperties tankInfo : fluidHandler.getTankProperties()) {
                                    FluidStack stackInTank = tankInfo.getContents();

                                    // Is a valid FluidStack
                                    if (stackInTank != null && stackInTank.getFluid() != null) {
                                        addRecipe(new CrucibleRecipe(getItemStackString(offHandStack), getFluidStackString(stackInTank)));
                                        sender.addChatMessage(new TextComponentString(getItemStackString(offHandStack) +
                                                " -> " + getFluidStackString(stackInTank) + " Added Successfully!"));
                                        saveToFile();
                                        return;
                                    }
                                }
                            }
                        }
                    }

                    // Conditions for hands usage not met
                    sender.addChatMessage(new TextComponentString(ClientUtils.translate(getCommandUsage(sender))));
                } else {
                    String itemStackInput = args[0];
                    String fluidStackOutput = args[1];

                    if (getItemStackFromString(itemStackInput) != null && getFluidStackFromString(fluidStackOutput) != null) {
                        addRecipe(new CrucibleRecipe(itemStackInput, fluidStackOutput));
                        sender.addChatMessage(new TextComponentString(itemStackInput +
                                " -> " + fluidStackOutput + " Added Successfully!"));
                        saveToFile();
                    } else
                        sender.addChatMessage(new TextComponentString(ClientUtils.translate(getCommandUsage(sender))));
                }
            }
        };
    }

    /**
     * Called when the file is not found, add all default recipes here
     */
    @Override
    protected void generateDefaultRecipes() {
        LogHelper.logger.info("[Neotech] Generating Crucible recipes...");

        // Metals
        for (Object key : MetalManager.metalRegistry.keySet()) {
            MetalManager.Metal metal = MetalManager.metalRegistry.get(key);
            if (FluidRegistry.isFluidRegistered(metal.getOreDict())) {
                //Block - 1296mb
                if (metal.getSolidBlock() != null)
                    addRecipe(new CrucibleRecipe(metal.getSolidBlock().getName(),
                            getFluidStackString(new FluidStack(FluidRegistry.getFluid(metal.getOreDict()), MetalManager.BLOCK_MB))));
                //Ore - 432mb
                if (metal.getOreBlock() != null)
                    addRecipe(new CrucibleRecipe(metal.getOreBlock().getName(),
                            getFluidStackString(new FluidStack(FluidRegistry.getFluid(metal.getOreDict()), MetalManager.ORE_MB))));
                //Ingot - 144mb
                if (metal.getIngot() != null)
                    addRecipe(new CrucibleRecipe(metal.getIngot().getName(),
                            getFluidStackString(new FluidStack(FluidRegistry.getFluid(metal.getOreDict()), MetalManager.INGOT_MB))));
                //Dust - 76mb
                if (metal.getDust() != null)
                    addRecipe(new CrucibleRecipe(metal.getDust().getName(),
                            getFluidStackString(new FluidStack(FluidRegistry.getFluid(metal.getOreDict()), MetalManager.DUST_MB))));
                //Nugget - 16mb
                if (metal.getNugget() != null)
                    addRecipe(new CrucibleRecipe(metal.getNugget().getName(),
                            getFluidStackString(new FluidStack(FluidRegistry.getFluid(metal.getOreDict()), MetalManager.NUGGET_MB))));
            }
        }

        // Iron
        addRecipe(new CrucibleRecipe("ingotIron",
                getFluidStackString(new FluidStack(FluidRegistry.getFluid("iron"), MetalManager.INGOT_MB))));
        addRecipe(new CrucibleRecipe("oreIron",
                getFluidStackString(new FluidStack(FluidRegistry.getFluid("dirtyiron"), MetalManager.ORE_MB))));
        addRecipe(new CrucibleRecipe("blockIron",
                getFluidStackString(new FluidStack(FluidRegistry.getFluid("iron"), MetalManager.BLOCK_MB))));

        // Gold
        addRecipe(new CrucibleRecipe("nuggetGold",
                getFluidStackString(new FluidStack(FluidRegistry.getFluid("gold"), MetalManager.NUGGET_MB))));
        addRecipe(new CrucibleRecipe("ingotGold",
                getFluidStackString(new FluidStack(FluidRegistry.getFluid("gold"), MetalManager.INGOT_MB))));
        addRecipe(new CrucibleRecipe("oreGold",
                getFluidStackString(new FluidStack(FluidRegistry.getFluid("dirtygold"), MetalManager.ORE_MB))));
        addRecipe(new CrucibleRecipe("blockGold",
                getFluidStackString(new FluidStack(FluidRegistry.getFluid("gold"), MetalManager.BLOCK_MB))));

        // Carbon
        addRecipe(new CrucibleRecipe(getItemStackString(new ItemStack(Items.COAL, 1, 1)),
                getFluidStackString(new FluidStack(FluidRegistry.getFluid("carbon"), MetalManager.INGOT_MB * 2))));
        addRecipe(new CrucibleRecipe(getItemStackString(new ItemStack(Items.COAL)),
                getFluidStackString(new FluidStack(FluidRegistry.getFluid("carbon"), MetalManager.INGOT_MB))));
        addRecipe(new CrucibleRecipe(getItemStackString(new ItemStack(Blocks.COAL_BLOCK)),
                getFluidStackString(new FluidStack(FluidRegistry.getFluid("carbon"), MetalManager.BLOCK_MB))));

        // Obsidian
        addRecipe(new CrucibleRecipe(getItemStackString(new ItemStack(Blocks.OBSIDIAN)),
                getFluidStackString(new FluidStack(FluidRegistry.getFluid("obsidian"), 288))));

        // Blaze
        addRecipe(new CrucibleRecipe(getItemStackString(new ItemStack(Items.BLAZE_ROD)),
                "blaze:" + MetalManager.NUGGET_MB));

        // Chorus
        addRecipe(new CrucibleRecipe(getItemStackString(new ItemStack(Items.CHORUS_FRUIT_POPPED)),
                "chorus:" + MetalManager.NUGGET_MB));

        // Wither
        addRecipe(new CrucibleRecipe(getItemStackString(new ItemStack(Items.NETHER_STAR)),
                "wither:" + MetalManager.NUGGET_MB));

        // Ice/Snowball to Water
        addRecipe(new CrucibleRecipe(getItemStackString(new ItemStack(Items.SNOWBALL)),
                getFluidStackString(new FluidStack(FluidRegistry.WATER, 144))));
        addRecipe(new CrucibleRecipe(getItemStackString(new ItemStack(Blocks.ICE)),
                getFluidStackString(new FluidStack(FluidRegistry.WATER, 1296))));
        addRecipe(new CrucibleRecipe(getItemStackString(new ItemStack(Blocks.PACKED_ICE)),
                getFluidStackString(new FluidStack(FluidRegistry.WATER, 1296))));

        // Stones to lava
        addRecipe(new CrucibleRecipe("cobblestone",
                getFluidStackString(new FluidStack(FluidRegistry.LAVA, 20))));
        addRecipe(new CrucibleRecipe("stone",
                getFluidStackString(new FluidStack(FluidRegistry.LAVA, 40))));

        saveToFile();
    }
}

