package com.dyonovan.neotech.api.nei

import codechicken.nei.recipe.GuiCraftingRecipe
import com.dyonovan.neotech.common.container.machines.{ContainerElectricFurnace, ContainerElectricCrusher}
import net.minecraft.inventory.Container

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 17, 2015
 */
class NEICallback {
    def onArrowClicked(gui : Container) : Unit = {
        gui match {
            case _: ContainerElectricCrusher => GuiCraftingRecipe.openRecipeGui("ecrusher")
            case _: ContainerElectricFurnace => GuiCraftingRecipe.openRecipeGui("furnace")
            case _ =>
        }
    }
}
