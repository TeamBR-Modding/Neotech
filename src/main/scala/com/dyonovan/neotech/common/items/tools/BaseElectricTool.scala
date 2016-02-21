package com.dyonovan.neotech.common.items.tools

import com.dyonovan.neotech.NeoTech
import com.teambr.bookshelf.common.items.traits.ItemBattery

/**
  * This file was created for Bookshelf API
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 2/21/2016
  */
trait BaseElectricTool extends ItemBattery {

    setCreativeTab(NeoTech.tabNeoTech)
    setMaxStackSize(1)

    override var capacity: Int = 2500
    override var maxExtract: Int = 100
    override var maxReceive: Int = 100

    def getName: String = { getUnlocalizedName }
}
