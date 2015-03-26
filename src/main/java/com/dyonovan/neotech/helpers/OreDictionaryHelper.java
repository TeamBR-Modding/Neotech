/* Thanks to pauljoda for the code */

package com.dyonovan.neotech.helpers;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictionaryHelper {

    public static ItemStack getOreOutput(ItemStack input)
    {
        ItemStack output = null;
        if(OreDictionary.getOreIDs(input).length > 0)
        {
            int oreId = OreDictionary.getOreIDs(input)[0];
            String oreName = OreDictionary.getOreName(oreId);
            if(oreName.contains("ore"))
            {
                String baseName = oreName.split("ore", 50)[1];
                String dustName = "dust".concat(baseName);
                if (!OreDictionary.getOres(dustName).isEmpty())
                {
                    output = OreDictionary.getOres(dustName).get(0).copy();
                    output.stackSize++;
                }
            }
        }
        return output;
    }
}
