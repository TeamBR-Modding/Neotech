package com.dyonovan.jatm.crafting;

import com.dyonovan.jatm.helpers.OreDictionaryHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class OreProcessingRegistry {

    public static ArrayList<OreProcessingRecipe> recipes = new ArrayList<>();

    public static class OreProcessingRecipe
    {
        public final ItemStack input;
        public final ItemStack output;

        OreProcessingRecipe(ItemStack in, ItemStack out)
        {
            this.input = in;
            this.output = out;
        }
        public boolean matches (ItemStack input)
        {
            return ItemStack.areItemStacksEqual(this.input, input);
        }
        public ItemStack getResult ()
        {
            return output.copy();
        }
    }

    public static void addOreProcessingRecipe(Object input, Object output)
    {
        ItemStack inputItem, outputItem;

        if (input instanceof ItemStack)
            inputItem = (ItemStack) input;
        else if (input instanceof Item)
            inputItem = new ItemStack((Item) input, 1, 0);
        else if (input instanceof Block)
            inputItem = new ItemStack((Block) input, 1, 0);
        else
            throw new RuntimeException("Ore Recipe is invalid");
        if (output instanceof ItemStack)
            outputItem = (ItemStack) output;
        else if (output instanceof Item)
            outputItem = new ItemStack((Item) output, 1, 0);
        else if (output instanceof Block)
            outputItem = new ItemStack((Block) output, 1, 0);
        else
            throw new RuntimeException("Ore recipe is invalid");
        recipes.add(new OreProcessingRecipe(inputItem, outputItem));
    }

    public static ItemStack getOutput (ItemStack input)
    {
        if(OreDictionaryHelper.getOreOutput(input) != null)
        {
            return OreDictionaryHelper.getOreOutput(input);
        }
        for (OreProcessingRecipe r : recipes)
        {
            if (r.input.isItemEqual(input))
            //if (r.matches(input))
                return r.getResult();
        }
        return null;
    }
}
