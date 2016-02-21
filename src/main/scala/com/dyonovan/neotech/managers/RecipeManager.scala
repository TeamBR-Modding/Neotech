package com.dyonovan.neotech.managers

import java.util

import com.dyonovan.neotech.registries._

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/20/2016
  */
object RecipeManager {
    sealed trait RecipeType { def name : String }
    case object Crucible extends RecipeType { val name = "crucible" }
    case object Solidifier extends RecipeType { val name = "solidifier" }

    lazy val recipeHandlers = new util.HashMap[RecipeType, AbstractRecipeHandler[_, _, _]]()

    def preInit() : Unit = {
        CraftingRecipeManager.preInit()
    }

    def init() : Unit = {
        recipeHandlers.put(Crucible,   new CrucibleRecipeRegistry().loadHandler())
        recipeHandlers.put(Solidifier, new SolidifierRegistry().loadHandler())
        CrusherRecipeRegistry.init()
        FluidFuelValues.init()
    }

    /**
      * Used to get a handler Type
      *
      * @param handlerType The handler Type
      * @tparam H The Handler Object
      * @return The Handler
      */
    def getHandler[H <: AbstractRecipeHandler[_, _, _]](handlerType : RecipeType) : H =
        recipeHandlers.get(handlerType).asInstanceOf[H]

    /**
      * Used to get Handles by name
      *
      * @param name The name of the handler, eg "Crusher"
      * @tparam H The recipe handler type
      * @return The recipe Handler
      */
    def getHandler[H <: AbstractRecipeHandler[_, _, _]](name : String)  : Option[H] = {
        for(x <- recipeHandlers.keySet().toArray) {
            val handler = recipeHandlers.get(x).asInstanceOf[RecipeType]
            if(handler.name.equalsIgnoreCase(name))
                return Option(recipeHandlers.get(handler).asInstanceOf[H])
        }
        None
    }
}
