package com.dyonovan.neotech.common.blocks.traits

import com.dyonovan.neotech.tools.ToolHelper.ToolType.ToolType

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
trait ThermalBinderItem {

    val acceptableUpgrades: java.util.ArrayList[String]

    def getToolType   : ToolType

    def isAcceptableUpgrade(toolType: ToolType, upgradeName: String): Boolean = {
        acceptableUpgrades.contains(upgradeName)
    }
}
