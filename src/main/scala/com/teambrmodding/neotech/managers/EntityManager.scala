package com.teambrmodding.neotech.managers

import com.teambrmodding.neotech.common.entities.EntityNet
import com.teambrmodding.neotech.lib.Reference
import net.minecraftforge.fml.common.registry.EntityRegistry

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 2/12/2016
  */
object EntityManager {

    def preInit(): Unit = {
        EntityRegistry.registerModEntity(classOf[EntityNet], "mobNet", 0, Reference.MOD_ID, 32, 5, true)
    }
}
