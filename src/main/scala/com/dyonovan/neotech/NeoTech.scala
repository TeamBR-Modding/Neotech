package com.dyonovan.neotech

import java.io.File

import com.dyonovan.neotech.api.igw.IGWSupportNotifier
import com.dyonovan.neotech.common.CommonProxy
import com.dyonovan.neotech.events.EventManager
import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.managers._
import com.dyonovan.neotech.network.PacketDispatcher
import com.dyonovan.neotech.registries._
import com.dyonovan.neotech.world.{ChunkLoaderManager, NeotechWorldGenerator}
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraftforge.common.{MinecraftForge, ForgeVersion, ForgeChunkManager}
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.common.{FMLModContainer, Loader, Mod, SidedProxy}
import org.apache.logging.log4j.LogManager

import scala.collection.JavaConversions._

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
    dependencies = Reference.DEPENDENCIES, modLanguage = "scala", updateJSON = Reference.UPDATE_JSON)
object NeoTech {

    //The logger. For logging
    final val logger = LogManager.getLogger(Reference.MOD_NAME)

    var configFolderLocation : String = ""

    @SidedProxy(clientSide = "com.dyonovan.neotech.client.ClientProxy",
                serverSide = "com.dyonovan.neotech.common.CommonProxy")
    var proxy : CommonProxy = null

    val tabNeoTech: CreativeTabs = new CreativeTabs("tabNeoTech") {
        override def getTabIconItem: Item = Item.getItemFromBlock(BlockManager.eliteRFStorage)
    }

    val tabPipes = new CreativeTabs("tabNeoTechPipes") {
        override def getTabIconItem : Item = Item.getItemFromBlock(BlockManager.pipeItemInterface)
    }

    val tabDecorations = new CreativeTabs("tabNeoTechDecorations") {
        override def getTabIconItem : Item = Item.getItemFromBlock(BlockManager.blockMiniatureStar)
    }

    @EventHandler def preInit(event : FMLPreInitializationEvent) = {
        configFolderLocation = event.getModConfigurationDirectory.getAbsolutePath + File.separator + "NeoTech"
        ConfigRegistry.preInit()
        BlockManager.preInit()
        ItemManager.preInit()
        proxy.preInit()
        GameRegistry.registerWorldGenerator(new NeotechWorldGenerator, 2)
        CraftingRecipeManager.preInit()
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new ItemGuiManager)
    }

    @EventHandler def init(event : FMLInitializationEvent) =  {
        FertilizerBlacklistRegistry.init()
        CrusherRecipeRegistry.init()
        FluidFuelValues.init()
        PacketDispatcher.initPackets()
        EventManager.init()
        proxy.init()
    }

    @EventHandler def postInit(event : FMLPostInitializationEvent) = {
        proxy.postInit()
        ForgeChunkManager.setForcedChunkLoadingCallback(this, new ChunkLoaderManager)
        new IGWSupportNotifier
    }
 }
