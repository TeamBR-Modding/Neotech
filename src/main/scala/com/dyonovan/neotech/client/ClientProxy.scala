package com.dyonovan.neotech.client

import com.dyonovan.neotech.client.mesh.MeshDefinitions.{PipeSpecialModelMesh, PipeModelMesh, StarModelMesh}
import com.dyonovan.neotech.client.modelfactory.ModelFactory
import com.dyonovan.neotech.client.renderers._
import com.dyonovan.neotech.common.CommonProxy
import com.dyonovan.neotech.common.entities.EntityNet
import com.dyonovan.neotech.common.tiles.AbstractMachine
import com.dyonovan.neotech.common.tiles.misc.{TileAttractor, TileMobStand}
import com.dyonovan.neotech.common.tiles.storage.{TileDimStorage, TileFlushableChest, TileTank}
import com.dyonovan.neotech.events.RenderingEvents
import com.dyonovan.neotech.managers.{BlockManager, MetalManager}
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.entity.{Render, RenderManager}
import net.minecraft.client.resources.IReloadableResourceManager
import net.minecraft.item.{EnumDyeColor, Item}
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.{ClientRegistry, IRenderFactory, RenderingRegistry}

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
class ClientProxy extends CommonProxy {

    /**
      * Called during the preInit phase of the mod loading
      *
      * This is where you would register blocks and such
      */
    override def preInit() = {
        RenderingRegistry.registerEntityRenderingHandler(classOf[EntityNet], new IRenderFactory[EntityNet] {
            override def createRenderFor(manager: RenderManager): Render[_ >: EntityNet] = new RenderNet(manager)
        })

        ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(BlockManager.blockMiniatureStar), new StarModelMesh)
        for(dye <- EnumDyeColor.values())
            ModelLoaderHelper.registerItem(Item.getItemFromBlock(BlockManager.blockMiniatureStar),
                "blockMiniatureStar",
                "attached_side=6,color=" + dye.getName)

        ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(BlockManager.pipeBasicStructure), new PipeModelMesh)
        for(dye <- EnumDyeColor.values())
            ModelLoaderHelper.registerItem(Item.getItemFromBlock(BlockManager.pipeBasicStructure),
                "pipeStructure",
                "color=" + dye.getName + ",down=true,east=false,north=false,south=false,up=true,west=false")


        //Item Stuff
        ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(BlockManager.pipeItemInterface), new PipeSpecialModelMesh)
        ItemRenderManager.registerBlockModel(
            BlockManager.pipeItemInterface,
                "pipeItemBasicInterface",
            "down=2,east=0,north=0,south=0,up=1,west=0")

        //Fluid Stuff
        ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(BlockManager.pipeFluidInterface), new PipeSpecialModelMesh)
        ItemRenderManager.registerBlockModel(
            BlockManager.pipeFluidInterface,
            "pipeFluidBasicInterface",
            "down=2,east=0,north=0,south=0,up=1,west=0")

        //Energy
        ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(BlockManager.pipeEnergyInterface), new PipeSpecialModelMesh)
        ItemRenderManager.registerBlockModel(
            BlockManager.pipeEnergyInterface,
            "pipeEnergyBasicInterface",
            "down=2,east=0,north=0,south=0,up=1,west=0")

        ItemRenderManager.registerBlockModel(BlockManager.electricFurnace, "electricFurnace", "facing=north,isactive=false")
        ItemRenderManager.registerBlockModel(BlockManager.grinder, "grinder", "normal")
        ItemRenderManager.registerBlockModel(BlockManager.electricCrusher, "electricCrusher", "facing=north,isactive=false")
        ItemRenderManager.registerBlockModel(BlockManager.furnaceGenerator, "furnaceGenerator", "facing=north,isactive=false")
        ItemRenderManager.registerBlockModel(BlockManager.fluidGenerator, "fluidGenerator", "facing=north,isactive=false")
        ItemRenderManager.registerBlockModel(BlockManager.thermalBinder, "thermalBinder", "facing=north,isactive=false")
        ItemRenderManager.registerBlockModel(BlockManager.electricCrucible, "electricCrucible", "facing=north,isactive=false")
        ItemRenderManager.registerBlockModel(BlockManager.electricSolidifier, "electricSolidifier", "facing=north,isactive=false")
        ItemRenderManager.registerBlockModel(BlockManager.electricAlloyer, "alloyer", "facing=north,isactive=false")
        ItemRenderManager.registerBlockModel(BlockManager.electricCentrifuge, "centrifuge", "facing=north,isactive=false")
        ItemRenderManager.registerBlockModel(BlockManager.pump, "pump", "isactive=false")
        ItemRenderManager.registerBlockModel(BlockManager.treeFarm, "treeFarm", "isactive=false")
        ItemRenderManager.registerBlockModel(BlockManager.mechanicalPipe, "mechanicalPipe", "normal")
        ItemRenderManager.registerBlockModel(BlockManager.solarPanelT1, "solarPanelT1", "isactive=false")
        ItemRenderManager.registerBlockModel(BlockManager.solarPanelT2, "solarPanelT2", "isactive=false")
        ItemRenderManager.registerBlockModel(BlockManager.solarPanelT3, "solarPanelT3", "isactive=false")
        ItemRenderManager.registerBlockModel(BlockManager.basicRFStorage, "basicRFStorage", "normal")
        ItemRenderManager.registerBlockModel(BlockManager.advancedRFStorage, "advancedRFStorage", "normal")
        ItemRenderManager.registerBlockModel(BlockManager.eliteRFStorage, "eliteRFStorage", "normal")
        ItemRenderManager.registerBlockModel(BlockManager.creativeRFStorage, "creativeRFStorage", "normal")
        ItemRenderManager.registerBlockModel(BlockManager.ironTank, "ironTank", "normal")
        ItemRenderManager.registerBlockModel(BlockManager.goldTank, "goldTank", "normal")
        ItemRenderManager.registerBlockModel(BlockManager.diamondTank, "diamondTank", "normal")
        ItemRenderManager.registerBlockModel(BlockManager.creativeTank, "creativeTank", "normal")
        ItemRenderManager.registerBlockModel(BlockManager.voidTank, "voidTank", "normal")
        ItemRenderManager.registerBlockModel(BlockManager.blockCrafter, "blockCrafter", "normal")
        ItemRenderManager.registerBlockModel(BlockManager.blockMiniatureSun, "blockMiniatureSun", "attached_side=6")
        ItemRenderManager.registerBlockModel(BlockManager.playerPlate, "playerPlate", "powered=false")
        ItemRenderManager.registerBlockModel(BlockManager.chunkLoader, "chunkLoader", "normal")
        ItemRenderManager.registerBlockModel(BlockManager.dimStorage, "dimStorage", "facing=north,locked=false")
        ItemRenderManager.registerBlockModel(BlockManager.redstoneClock, "redstoneClock", "powered=false")
        ItemRenderManager.registerBlockModel(BlockManager.mobStand, "mobStand", "normal")
        ItemRenderManager.registerBlockModel(BlockManager.blockAttractor, "blockAttractor", "attached_side=up")
        ItemRenderManager.registerBlockModel(BlockManager.flushableChest, "flushableChest", "facing=north")

        MetalManager.registerModels()
    }

    /**
      * Called during the init phase of the mod loading
      *
      * Now that the items and such are loaded, use this chance to use them
      */
    override def init() = {
        ModelFactory.register()
        ItemRenderManager.registerItemRenderer()

        KeybindHandler.registerBindings()
        MinecraftForge.EVENT_BUS.register(ClientTickHandler)

        Minecraft.getMinecraft.getRenderItem.getItemModelMesher.getModelManager.getBlockModelShapes.registerBuiltInBlocks(BlockManager.flushableChest)
        ClientRegistry.bindTileEntitySpecialRenderer(classOf[TileFlushableChest], new TileFlushableChestRenderer[TileFlushableChest])

        ClientRegistry.bindTileEntitySpecialRenderer(classOf[TileTank], new TileTankFluidRenderer)

        ClientRegistry.bindTileEntitySpecialRenderer(classOf[AbstractMachine], new TileMachineIORenderer)

        ClientRegistry.bindTileEntitySpecialRenderer(classOf[TileDimStorage], new TileDimStorageRenderer)
        ClientRegistry.bindTileEntitySpecialRenderer(classOf[TileMobStand], new MobStandEntityRenderer[TileMobStand])
        ClientRegistry.bindTileEntitySpecialRenderer(classOf[TileAttractor], new AttractorEnderEyeRenderer)
    }

    /**
      * Called during the postInit phase of the mod loading
      *
      * Usually used to close things opened to load and check for conditions
      */
    override def postInit() = {
        MinecraftForge.EVENT_BUS.register(RenderingEvents)
        Minecraft.getMinecraft.getResourceManager.asInstanceOf[IReloadableResourceManager].registerReloadListener(RenderingEvents)
    }
}
