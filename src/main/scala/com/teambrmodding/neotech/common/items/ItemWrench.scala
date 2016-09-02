package com.teambrmodding.neotech.common.items

import com.teambr.bookshelf.common.items.traits.IToolWrench

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/9/2016
  */
class ItemWrench extends BaseItem("wrench", 1) with IToolWrench {
    override def isFull3D: Boolean = true
}
