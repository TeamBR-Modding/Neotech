package com.dyonovan.neotech.api.jei

import com.dyonovan.neotech.api.jei.crusher.{CrusherRecipeHandler, CrusherRecipeCatagory}
import com.dyonovan.neotech.managers.BlockManager
import com.dyonovan.neotech.registries.CrusherRecipeRegistry
import mezz.jei.api._
import net.minecraft.item.ItemStack

/**
  * Created by Dyonovan on 1/9/2016.
  */
object NeoTechPlugin {
    var jeiHelpers: IJeiHelpers = _
}

@JEIPlugin
class NeoTechPlugin extends IModPlugin {

    override def onRecipeRegistryAvailable(recipeRegistry: IRecipeRegistry): Unit = { }

    override def register(registry: IModRegistry): Unit = {
        registry.addRecipeCategories(new CrusherRecipeCatagory)
        registry.addRecipeHandlers(new CrusherRecipeHandler)

        registry.addRecipes(CrusherRecipeRegistry.getRecipes)

        //Descriptions
        registry.addDescription(new ItemStack(BlockManager.grinder), "neotech.grinder.description")
    }

    override def onItemRegistryAvailable(itemRegistry: IItemRegistry): Unit = { }

    override def onJeiHelpersAvailable(jeiHelpers: IJeiHelpers): Unit = { NeoTechPlugin.jeiHelpers = jeiHelpers }
}
