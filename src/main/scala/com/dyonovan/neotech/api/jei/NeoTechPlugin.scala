package com.dyonovan.neotech.api.jei

import mezz.jei.api._

/**
  * Created by Dyonovan on 1/9/2016.
  */
object NeoTechPlugin {
    var jeiHelpers: IJeiHelpers = _
}

@JEIPlugin
class NeoTechPlugin extends IModPlugin {

    override def onRecipeRegistryAvailable(recipeRegistry: IRecipeRegistry): Unit = ???

    override def register(registry: IModRegistry): Unit = ???

    override def onItemRegistryAvailable(itemRegistry: IItemRegistry): Unit = ???

    override def onJeiHelpersAvailable(jeiHelpers: IJeiHelpers): Unit = { NeoTechPlugin.jeiHelpers = jeiHelpers }
}
