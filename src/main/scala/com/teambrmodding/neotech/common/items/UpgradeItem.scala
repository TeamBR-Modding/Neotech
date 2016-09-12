package com.teambrmodding.neotech.common.items

import com.teambrmodding.neotech.common.tiles.traits.IUpgradeItem
import com.teambrmodding.neotech.common.tiles.traits.IUpgradeItem.ENUM_UPGRADE_CATEGORY

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 9/10/2016
  */
class UpgradeItem(id : String, category : ENUM_UPGRADE_CATEGORY, stackSize : Int) extends BaseItem(id, stackSize) with IUpgradeItem {

    /**
      * Get the id of this upgrade item
      *
      * @return A unique ID, should be a static variable somewhere
      */
    override def getID: String = id

    /**
      * Used to get what category this upgrade is, this allows for tiered upgrades, only use if tiered
      *
      * @return Category based of standard set, use NONE if not needed
      */
    override def getCategory: ENUM_UPGRADE_CATEGORY = category
}
