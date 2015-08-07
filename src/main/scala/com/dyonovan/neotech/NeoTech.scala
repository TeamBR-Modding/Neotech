package com.dyonovan.neotech

import com.dyonovan.neotech.common.CommonProxy
import com.dyonovan.neotech.lib.Reference
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.{FMLPostInitializationEvent, FMLInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.{Mod, SidedProxy}


/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since August 07, 2015
  */
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION,
    dependencies = Reference.DEPENDENCIES, modLanguage = "scala")
object NeoTech {

    @SidedProxy(clientSide = "com.dyonovan.neotech.client.ClientProxy",
                serverSide = "com.dyonovan.neotech.common.CommonProsy")
    var proxy : CommonProxy = null

    var tabNeoTech: CreativeTabs = new CreativeTabs("tabNeoTech") {
        override def getTabIconItem: Item = Item.getItemFromBlock(Blocks.furnace)
    }

    @EventHandler def preInit(event : FMLPreInitializationEvent) = {
        proxy.preInit()
    }

    @EventHandler def init(event : FMLInitializationEvent) =  {
        proxy.init()
    }

    @EventHandler def postInit(event : FMLPostInitializationEvent) = {
        proxy.postInit()
    }
 }
