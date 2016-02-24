package com.dyonovan.neotech.common.blocks.traits

import java.util

import com.dyonovan.neotech.tools.tools.ToolType.ToolType
import com.dyonovan.neotech.tools.upgradeitems.ToolType
import com.dyonovan.neotech.tools.upgradeitems.ToolType.ToolType
import com.dyonovan.neotech.tools.upgradeitems.ToolType.ToolType
import net.minecraft.init.Items
import net.minecraft.item.Item

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 2/23/2016
  */
trait ThermalBinderItems {

    val acceptableTools: java.util.ArrayList[String]

    def isAcceptableUpgrade(toolType: ToolType, upgradeName: String): Boolean = {
        acceptableTools.contains(upgradeName)
    }
}
