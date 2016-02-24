package com.dyonovan.neotech.tools.upgradeitems

import com.dyonovan.neotech.managers.ItemManager

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
object UpgradeItemManager {
    val upgradeSilkTouch   = new ItemModifierSilkTouch
    val upgradeFortune     =  new ItemModifierFortune
    val upgradeMiningLevel = new ItemModifierMiningLevel
    val upgradeMiningSpeed = new ItemModifierMiningSpeed

    val upgradeSharpness   = new ItemModifierSharpness
    val upgradeSmite       = new ItemModifierSmite

    def preInit(): Unit = {
        ItemManager.registerItem(upgradeSilkTouch, upgradeSilkTouch.getUpgradeName)
        ItemManager.registerItem(upgradeFortune, upgradeFortune.getUpgradeName)
        ItemManager.registerItem(upgradeMiningLevel, upgradeMiningLevel.getUpgradeName)
        ItemManager.registerItem(upgradeMiningSpeed, upgradeMiningSpeed.getUpgradeName)
        ItemManager.registerItem(upgradeSharpness, upgradeSharpness.getUpgradeName)
        ItemManager.registerItem(upgradeSmite, upgradeSmite.getUpgradeName)
    }
}
