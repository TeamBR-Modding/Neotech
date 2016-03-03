package com.dyonovan.neotech

import java.io.File

import com.dyonovan.neotech.collections.CreativeTabMetals
import com.dyonovan.neotech.common.CommonProxy
import com.dyonovan.neotech.events.EventManager
import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.managers._
import com.dyonovan.neotech.network.PacketDispatcher
import com.dyonovan.neotech.registries._
import com.dyonovan.neotech.world.{ChunkLoaderManager, NeotechWorldGenerator}
import net.minecraft.block.Block
import net.minecraft.command.ServerCommandManager
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.server.MinecraftServer
import net.minecraftforge.common.ForgeChunkManager
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event._
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.common.{Mod, SidedProxy}
import org.apache.logging.log4j.LogManager

import collection.JavaConversions._

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

    //Lets us make some buckets
    FluidRegistry.enableUniversalBucket()

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

    val tabTools = new CreativeTabs("tabNeoTechTools") {
        override def getTabIconItem: Item = ItemManager.electricPickaxe
    }

    val tabMetals = new CreativeTabMetals

    @EventHandler def preInit(event : FMLPreInitializationEvent) = {
        configFolderLocation = event.getModConfigurationDirectory.getAbsolutePath + File.separator + "NeoTech"
        ConfigRegistry.preInit()
        BlockManager.preInit()
        ItemManager.preInit()
        FluidManager.preInit()
        MetalManager.registerDefaultMetals()
        EntityManager.preInit()
        RecipeManager.preInit()
        proxy.preInit()
        GameRegistry.registerWorldGenerator(new NeotechWorldGenerator, 2)
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new ItemGuiManager)
    }

    @EventHandler def init(event : FMLInitializationEvent) =  {
        FertilizerBlacklistRegistry.init()
        RecipeManager.init()
        PacketDispatcher.initPackets()
        EventManager.init()
        proxy.init()
    }

    @EventHandler def postInit(event : FMLPostInitializationEvent) = {
        proxy.postInit()
        ForgeChunkManager.setForcedChunkLoadingCallback(this, new ChunkLoaderManager)
    }

    @EventHandler def imcMessages(event: FMLInterModComms.IMCEvent): Unit  = {
        val messages = event.getMessages
        for (msg <- messages) {
            if (msg.key.equalsIgnoreCase("blacklistFertilizer")) {
                val block = Block.getBlockFromName(msg.getStringValue)
                if (block != null) FertilizerBlacklistRegistry.addToBlacklist(block)
            }
        }
    }

    @EventHandler def serverLoad(event : FMLServerStartingEvent): Unit = {
        RecipeManager.initCommands(MinecraftServer.getServer.getCommandManager.asInstanceOf[ServerCommandManager])
    }
 }
