package com.dyonovan.neotech.api.jei.crucible

import java.util
import java.util.Collections

import com.dyonovan.neotech.managers.RecipeManager
import com.dyonovan.neotech.registries.CrucibleRecipeHandler
import com.teambr.bookshelf.helper.LogHelper
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

import scala.collection.JavaConversions._

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 2/22/2016
  */
object JEICrucibleRecipeMaker {

    def getRecipes: util.List[JEICrucibleRecipe] = {
        val recipes = new util.ArrayList[JEICrucibleRecipe]()
        val crucible = RecipeManager.getHandler[CrucibleRecipeHandler](RecipeManager.Crucible).recipes
        for (recipe <- crucible) {
            val inputList = OreDictionary.getOres(recipe.ore)
            val output = recipe.getFluidFromString(recipe.output)
            if (output != null) {
                if (!inputList.isEmpty)
                    recipes.add(new JEICrucibleRecipe(inputList, output))
                else {
                    val input = recipe.getItemStackFromString(recipe.input)
                    if (input != null) {
                        val list: util.List[ItemStack] = Collections.singletonList(input)
                        recipes.add(new JEICrucibleRecipe(list, recipe.getFluidFromString(recipe.output)))
                    } else LogHelper.severe("[NeoTech] CrucibleRecipe json is corrupt! Please delete and recreate!")
                }
            } else LogHelper.severe("[NeoTech] CrucibleRecipe json is corrupt! Please delete and recreate!")
        }
        recipes
    }

}
