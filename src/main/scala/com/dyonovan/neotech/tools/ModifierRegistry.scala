package com.dyonovan.neotech.tools

import java.util

import com.dyonovan.neotech.tools.modifier.Modifier

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/22/2016
  */
object ModifierRegistry {

    lazy val listOfModifiers = new util.ArrayList[Modifier]()

    def getModifier(name : String) : Modifier = {
        for(mod <- listOfModifiers.toArray)
            if(mod.asInstanceOf[Modifier].getIdentifier.equalsIgnoreCase(name))
                return mod.asInstanceOf[Modifier]
        null
    }
}
