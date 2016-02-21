package com.dyonovan.neotech.api.jei.solidifier

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 2/20/2016
  */
object SolidiferRecipeMaker {

    /*def getRecipes: java.util.List[SolidiferRecipeJEI] = {
        val recipes = new util.ArrayList[SolidiferRecipeJEI]()
        val solidifer = SolidifierRegistry.getSolidiferRecipes

        for (r <- solidifer.toArray()) {
            val recipe = r.asInstanceOf[SolidifierRecipe]

            val s = recipe.ore
            var amount = 0
            breakable {
                for (i <- 0 until s.length) {
                    if (s.charAt(i) == s.charAt(i).toUpper) {
                        s.substring(0, i) match {
                            case "block" => amount = 1296
                            case "ore" => amount = 288
                            case "ingot" => amount = 144
                            case "dust" => amount = 72
                            case "nugget" => amount = 16
                            case _ =>
                        }
                        break
                    }
                }
            }

            val fluid = SolidifierRegistry.getFluidFromString(recipe.input)
            val output = SolidifierRegistry.getItemStackFromString(recipe.output)
            recipes.add(new SolidiferRecipeJEI(fluid, amount, output))
        }

        recipes
    }*/

}
