package com.teambrmodding.neotech.registries;

import com.teambr.bookshelf.helper.LogHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * Used as the base class for all recipes handler recipes
 *
 * I : Recipe Input
 * O : Recipe Output
 *
 * @author Paul Davis - pauljoda
 * @since 2/14/2017
 */
public abstract class AbstractRecipe<I, O> {

    /**
     * Used to get the output of this recipe
     *
     * @param input The input object
     * @return The output object, can be null
     */
    @Nullable
    public abstract O getOutput(I input);

    /**
     * Is the input valid for an output
     *
     * @param input The input object
     * @return True if there is an output
     */
    public abstract boolean isValidInput(I input);

    /*******************************************************************************************************************
     * Helper Methods                                                                                                  *
     *******************************************************************************************************************/

    /**
     * Used to get the string form of an ItemStack
     *
     * @param itemStack The stack to translate
     * @return A string version of the stack in format MODID:ITEMID:DAMAGE:STACK_SIZE or ORE_DICT_TAG
     */
    public String getItemStackString(@Nonnull ItemStack itemStack) {
        if(OreDictionary.getOreIDs(itemStack).length > 0) { // Do we have an ore dict tag for this?
            return OreDictionary.getOreName(OreDictionary.getOreIDs(itemStack)[0]); // Return the ore dict tag, always preferred
        }
        return itemStack.getItem().getRegistryName().toString() + ":" + itemStack.getItemDamage() + ":" + itemStack.stackSize;
    }

    /**
     * Used to get a stack from a string
     *
     * @param itemString The item string in format MODID:ITEMID:DAMAGE:STACK_SIZE
     * @return The stack for the string
     */
    @Nullable
    public static ItemStack getItemStackFromString(String itemString) {
        if(itemString == null || itemString.isEmpty())
            return null;

        String[] name = itemString.split(":");
        int damage = 0;
        int stackSize = 1;
        switch (name.length) {
            case 4: // Stack size defined
                stackSize = Integer.valueOf(name[3]);
            case 3:
                damage = Integer.valueOf(name[2]); // Damage Defined
            case 2: // Create the stack
                List<ItemStack> ores = OreDictionary.getOres(name[0], false);
                if(!ores.isEmpty())
                    return new ItemStack(ores.get(0).getItem(), Integer.parseInt(name[1]),
                            ores.get(0).getItemDamage() != OreDictionary.WILDCARD_VALUE ? ores.get(0).getItemDamage() : 0);
                else
                    return new ItemStack(Item.REGISTRY.getObject(new ResourceLocation(name[0], name[1])), stackSize, damage);
            case 1: // Not a defined item already, search OreDict
                List<ItemStack> itemOreTag = OreDictionary.getOres(name[0], false);
                if(!itemOreTag.isEmpty())
                    return new ItemStack(itemOreTag.get(0).getItem(), stackSize,
                            itemOreTag.get(0).getItemDamage() != OreDictionary.WILDCARD_VALUE ? itemOreTag.get(0).getItemDamage() : 0);
            default :
                LogHelper.logger.error("[Neotech] Unable to get stack from string: " + itemString);
                return null;
        }
    }

    /**
     * Checks if the passed string allows for normal match or OreDictionary match
     * @param recipeStack The recipe stack, formated MOD:ITEM:DAMAGE:STACK or ORE_DICT
     * @param inputStack The stack to compare to
     * @return True if the input matches our defined stack
     */
    public boolean isItemStackValidForRecipeStack(String recipeStack, ItemStack inputStack) {
        ItemStack convertedStack = getItemStackFromString(recipeStack);

        if(convertedStack == null)
            return false;

        boolean oreDict = false;
        if(OreDictionary.doesOreNameExist(recipeStack)) { // Is input ore dict tag
            int[] otherStackIDS = OreDictionary.getOreIDs(inputStack); // The list of ids for input
            if(otherStackIDS.length > 0) { // Input must have ore dict to match
                for(int oreID : otherStackIDS) { // Cycle Ore Dict ids
                    if(OreDictionary.containsMatch(false,
                            OreDictionary.getOres(OreDictionary.getOreName(oreID)), convertedStack)) { // Checks if there is a stack matching this
                        oreDict = true; // Found a match
                        break; // No need to continue
                    }
                }
            }
        }

        return oreDict ||
                convertedStack.isItemEqual(inputStack) && // Our item matches the input
                        convertedStack.stackSize <= inputStack.stackSize && // Input must be equal or larger
                        (convertedStack.getItemDamage() == -1 || convertedStack.getItemDamage() == inputStack.getItemDamage()) && // Our damage matches the input
                        ItemStack.areItemStackTagsEqual(convertedStack, inputStack); // Make sure tags are equal

    }

    /**
     * Converts a FluidStack into a string form
     *
     * @param fluidStack The stack
     * @return The string in form FLUID:AMOUNT
     */
    public String getFluidStackString(FluidStack fluidStack) {
        return FluidRegistry.getFluidName(fluidStack) + ":" + fluidStack.amount;
    }

    /**
     * Converts the string form of a fluid into a stack
     *
     * @param fluidString The string in format FLUID:AMOUNT
     * @return The FluidStack for the string
     */
    @Nullable
    public FluidStack getFluidStackFromString(String fluidString) {
        String[] fluidStringSplit = fluidString.split(":");
        if(fluidStringSplit.length != 2)
            return null;
        return FluidRegistry.getFluidStack(fluidStringSplit[0], Integer.valueOf(fluidStringSplit[1]));
    }
}
