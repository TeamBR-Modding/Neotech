package com.teambrmodding.neotech.client

import com.teambrmodding.neotech.client.mesh.MeshDefinitions.{SimpleItemMeshDefinition, StarModelMesh}
import com.teambrmodding.neotech.client.renderers.entity.RenderNet
import com.teambrmodding.neotech.client.renderers.tiles._
import com.teambrmodding.neotech.common.CommonProxy
import com.teambrmodding.neotech.common.entities.EntityNet
import com.teambrmodding.neotech.common.fluids.FluidBlockGas
import com.teambrmodding.neotech.common.metals.blocks.BlockFluidMetal
import com.teambrmodding.neotech.common.metals.items.ItemMetal
import com.teambrmodding.neotech.common.tiles.AbstractMachine
import com.teambrmodding.neotech.common.tiles.misc.TileMobStand
import com.teambrmodding.neotech.common.tiles.storage.tanks.TileIronTank
import com.teambrmodding.neotech.events.RenderingEvents
import com.teambrmodding.neotech.managers.{BlockManager, FluidManager, ItemManager, MetalManager}
import com.teambrmodding.neotech.tools.armor.ItemElectricArmor
import com.teambrmodding.neotech.tools.tools.BaseElectricTool
import com.teambrmodding.neotech.tools.upgradeitems.BaseUpgradeItem
import com.teambr.bookshelf.client.models.{BakedConnectedTextures, BakedDynItem}
import com.teambr.bookshelf.common.blocks.BlockConnectedTextures
import com.teambrmodding.neotech.common.tiles.traits.IUpgradeItem
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.color.{IBlockColor, IItemColor}
import net.minecraft.client.renderer.entity.{Render, RenderManager}
import net.minecraft.client.resources.IReloadableResourceManager
import net.minecraft.item.{EnumDyeColor, Item, ItemStack}
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.{ClientRegistry, IRenderFactory, RenderingRegistry}
import net.minecraftforge.fml.common.Loader

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
        for (dye <- EnumDyeColor.values())
            ModelLoaderHelper.registerItem(Item.getItemFromBlock(BlockManager.blockMiniatureStar),
                "blockMiniatureStar",
                "attached_side=6,color=" + dye.getName)

        // Item Models
        val itemIterator = Item.REGISTRY.iterator()
        while (itemIterator.hasNext) {
            itemIterator.next() match {
                case upgradeItem: BaseUpgradeItem =>
                    ModelLoader.setCustomModelResourceLocation(upgradeItem, 0, BakedDynItem.MODEL_RESOURCE_LOCATION)
                case tool: BaseElectricTool if !tool.isInstanceOf[ItemElectricArmor] =>
                    ModelLoader.setCustomModelResourceLocation(tool, 0, BakedDynItem.MODEL_RESOURCE_LOCATION)
                case _ =>
            }
        }

        // Block Models, for items
        val blockIterator = Block.REGISTRY.iterator()
        while (blockIterator.hasNext) {
            blockIterator.next() match {
                case connectedTextures: BlockConnectedTextures =>
                    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(connectedTextures), 0,
                        BakedConnectedTextures.MODEL_RESOURCE_LOCATION_NORMAL)
                case _ =>
            }
        }

        ItemRenderManager.registerBlockModel(BlockManager.electricFurnace, "electricFurnace", "facing=north,isactive=false")
        ItemRenderManager.registerBlockModel(BlockManager.electricCrusher, "electricCrusher", "facing=north,isactive=false")
        ItemRenderManager.registerBlockModel(BlockManager.furnaceGenerator, "furnaceGenerator", "facing=north,isactive=false")
        ItemRenderManager.registerBlockModel(BlockManager.fluidGenerator, "fluidGenerator", "facing=north,isactive=false")
        ItemRenderManager.registerBlockModel(BlockManager.thermalBinder, "thermalBinder", "facing=north,isactive=false")
        ItemRenderManager.registerBlockModel(BlockManager.electricCrucible, "electricCrucible", "facing=north,isactive=false")
        ItemRenderManager.registerBlockModel(BlockManager.electricSolidifier, "electricSolidifier", "facing=north,isactive=false")
        ItemRenderManager.registerBlockModel(BlockManager.electricAlloyer, "alloyer", "facing=north,isactive=false")
        ItemRenderManager.registerBlockModel(BlockManager.electricCentrifuge, "centrifuge", "facing=north,isactive=false")
        ItemRenderManager.registerBlockModel(BlockManager.treeFarm, "treeFarm", "isactive=false")
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
        ItemRenderManager.registerBlockModel(BlockManager.blockMiniatureSun, "blockMiniatureSun", "attached_side=6")
        ItemRenderManager.registerBlockModel(BlockManager.chunkLoader, "chunkLoader", "normal")
        ItemRenderManager.registerBlockModel(BlockManager.mobStand, "mobStand", "normal")

        MetalManager.registerModels()

        // Upgrades

        // CPUS
        // Single Core
        ModelLoader.setCustomMeshDefinition(ItemManager.processorSingleCore,
            new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.CPU_SINGLE_CORE.toLowerCase))
        ModelLoaderHelper.registerItem(ItemManager.processorSingleCore, "items/upgrades", "type=" + IUpgradeItem.CPU_SINGLE_CORE.toLowerCase)

        // Dual Core
        ModelLoader.setCustomMeshDefinition(ItemManager.processorDualCore,
            new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.CPU_DUAL_CORE.toLowerCase))
        ModelLoaderHelper.registerItem(ItemManager.processorDualCore, "items/upgrades", "type=" + IUpgradeItem.CPU_DUAL_CORE.toLowerCase)

        // Quad Core
        ModelLoader.setCustomMeshDefinition(ItemManager.processorQuadCore,
            new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.CPU_QUAD_CORE.toLowerCase))
        ModelLoaderHelper.registerItem(ItemManager.processorQuadCore, "items/upgrades", "type=" + IUpgradeItem.CPU_QUAD_CORE.toLowerCase)

        // Octa Core
        ModelLoader.setCustomMeshDefinition(ItemManager.processorOctCore,
            new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.CPU_OCT_CORE.toLowerCase))
        ModelLoaderHelper.registerItem(ItemManager.processorOctCore, "items/upgrades", "type=" + IUpgradeItem.CPU_OCT_CORE.toLowerCase)


        // Memory
        // DDR1
        ModelLoader.setCustomMeshDefinition(ItemManager.memoryDDR1,
            new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.MEMORY_DDR1.toLowerCase))
        ModelLoaderHelper.registerItem(ItemManager.memoryDDR1, "items/upgrades", "type=" + IUpgradeItem.MEMORY_DDR1.toLowerCase)

        // DDR2
        ModelLoader.setCustomMeshDefinition(ItemManager.memoryDDR2,
            new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.MEMORY_DDR2.toLowerCase))
        ModelLoaderHelper.registerItem(ItemManager.memoryDDR2, "items/upgrades", "type=" + IUpgradeItem.MEMORY_DDR2.toLowerCase)

        //DDR3
        ModelLoader.setCustomMeshDefinition(ItemManager.memoryDDR3,
            new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.MEMORY_DDR3.toLowerCase))
        ModelLoaderHelper.registerItem(ItemManager.memoryDDR3, "items/upgrades", "type=" + IUpgradeItem.MEMORY_DDR3.toLowerCase)

        //DDR4
        ModelLoader.setCustomMeshDefinition(ItemManager.memoryDDR4,
            new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.MEMORY_DDR4.toLowerCase))
        ModelLoaderHelper.registerItem(ItemManager.memoryDDR4, "items/upgrades", "type=" + IUpgradeItem.MEMORY_DDR4.toLowerCase)

        // Hard Drive
        // 64
        ModelLoader.setCustomMeshDefinition(ItemManager.hardDrive64G,
            new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.HDD_64G.toLowerCase))
        ModelLoaderHelper.registerItem(ItemManager.hardDrive64G, "items/upgrades", "type=" + IUpgradeItem.HDD_64G.toLowerCase)

        // 254
        ModelLoader.setCustomMeshDefinition(ItemManager.hardDrive254G,
            new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.HDD_256G.toLowerCase))
        ModelLoaderHelper.registerItem(ItemManager.hardDrive254G, "items/upgrades", "type=" + IUpgradeItem.HDD_256G.toLowerCase)

        // 512
        ModelLoader.setCustomMeshDefinition(ItemManager.hardDrive512G,
            new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.HDD_512G.toLowerCase))
        ModelLoaderHelper.registerItem(ItemManager.hardDrive512G, "items/upgrades", "type=" + IUpgradeItem.HDD_512G.toLowerCase)

        // 1t
        ModelLoader.setCustomMeshDefinition(ItemManager.hardDrive1T,
            new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.HDD_1T.toLowerCase))
        ModelLoaderHelper.registerItem(ItemManager.hardDrive1T, "items/upgrades", "type=" + IUpgradeItem.HDD_1T.toLowerCase)

        // PSU
        // 250
        ModelLoader.setCustomMeshDefinition(ItemManager.psu250W,
            new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.PSU_250W.toLowerCase))
        ModelLoaderHelper.registerItem(ItemManager.psu250W, "items/upgrades", "type=" + IUpgradeItem.PSU_250W.toLowerCase)

        // 500
        ModelLoader.setCustomMeshDefinition(ItemManager.psu500W,
            new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.PSU_500W.toLowerCase))
        ModelLoaderHelper.registerItem(ItemManager.psu500W, "items/upgrades", "type=" + IUpgradeItem.PSU_500W.toLowerCase)

        // 750
        ModelLoader.setCustomMeshDefinition(ItemManager.psu750W,
            new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.PSU_750W.toLowerCase))
        ModelLoaderHelper.registerItem(ItemManager.psu750W, "items/upgrades", "type=" + IUpgradeItem.PSU_750W.toLowerCase)

        // 960
        ModelLoader.setCustomMeshDefinition(ItemManager.psu960W,
            new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.PSU_960W.toLowerCase))
        ModelLoaderHelper.registerItem(ItemManager.psu960W, "items/upgrades", "type=" + IUpgradeItem.PSU_960W.toLowerCase)

        // Transformer
        if (Loader.isModLoaded("IC2")) {
            ModelLoader.setCustomMeshDefinition(ItemManager.transformer,
                new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.TRANSFORMER.toLowerCase))
            ModelLoaderHelper.registerItem(ItemManager.transformer, "items/upgrades", "type=" + IUpgradeItem.TRANSFORMER.toLowerCase)
        }

        // Expansion
        ModelLoader.setCustomMeshDefinition(ItemManager.expansion,
            new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.EXPANSION_CARD.toLowerCase))
        ModelLoaderHelper.registerItem(ItemManager.expansion, "items/upgrades", "type=" + IUpgradeItem.EXPANSION_CARD.toLowerCase)

        // Redstone Control
        ModelLoader.setCustomMeshDefinition(ItemManager.redstoneControl,
            new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.REDSTONE_CIRCUIT.toLowerCase))
        ModelLoaderHelper.registerItem(ItemManager.redstoneControl, "items/upgrades", "type=" + IUpgradeItem.REDSTONE_CIRCUIT.toLowerCase)

        // Network Card
        ModelLoader.setCustomMeshDefinition(ItemManager.networkCard,
            new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.NETWORK_CARD.toLowerCase))
        ModelLoaderHelper.registerItem(ItemManager.networkCard, "items/upgrades", "type=" + IUpgradeItem.NETWORK_CARD.toLowerCase)
    }

    /**
      * Called during the init phase of the mod loading
      *
      * Now that the items and such are loaded, use this chance to use them
      */
    override def init() = {
        ItemRenderManager.registerItemRenderer()

        KeybindHandler.registerBindings()
        MinecraftForge.EVENT_BUS.register(ClientTickHandler)

        // Register fluid colors
        for(metal <- MetalManager.metalRegistry.keySet()) {
            if(MetalManager.metalRegistry.get(metal).fluidBlock.isDefined) {
                Minecraft.getMinecraft.getBlockColors.registerBlockColorHandler(new IBlockColor {
                    override def colorMultiplier(state: IBlockState, world: IBlockAccess, pos: BlockPos, tintIndex: Int): Int = {
                        state.getBlock match {
                            case metal: BlockFluidMetal => metal.getBlockColor
                            case _ => 0xFFFFFF
                        }
                    }
                }, MetalManager.metalRegistry.get(metal).fluidBlock.get)
            }
        }

        Minecraft.getMinecraft.getBlockColors.registerBlockColorHandler(new IBlockColor {
            override def colorMultiplier(state: IBlockState, world: IBlockAccess, pos: BlockPos, tintIndex: Int): Int = {
                state.getBlock match {
                    case metal: FluidBlockGas => metal.getBlockColor
                    case _ => 0xFFFFFF
                }
            }
        }, FluidManager.blockOxygen)


        Minecraft.getMinecraft.getBlockColors.registerBlockColorHandler(new IBlockColor {
            override def colorMultiplier(state: IBlockState, world: IBlockAccess, pos: BlockPos, tintIndex: Int): Int = {
                state.getBlock match {
                    case metal: FluidBlockGas => metal.getBlockColor
                    case _ => 0xFFFFFF
                }
            }
        }, FluidManager.blockHydrogen)

        for(metal <- MetalManager.metalRegistry.keySet()) {
            // Dusts
            if(MetalManager.metalRegistry.get(metal).dust.isDefined)
                Minecraft.getMinecraft.getItemColors.registerItemColorHandler(new IItemColor {
                    override def getColorFromItemstack(stack: ItemStack, tintIndex: Int): Int = {
                        stack.getItem match {
                            case metal : ItemMetal => metal.getColorFromItemStack(stack)
                            case _ => 0xFFFFFF
                        }
                    }
                }, MetalManager.metalRegistry.get(metal).dust.get)

            // Ingots
            if(MetalManager.metalRegistry.get(metal).ingot.isDefined)
                Minecraft.getMinecraft.getItemColors.registerItemColorHandler(new IItemColor {
                    override def getColorFromItemstack(stack: ItemStack, tintIndex: Int): Int = {
                        stack.getItem match {
                            case metal : ItemMetal => metal.getColorFromItemStack(stack)
                            case _ => 0xFFFFFF
                        }
                    }
                }, MetalManager.metalRegistry.get(metal).ingot.get)

            // Nuggets
            if(MetalManager.metalRegistry.get(metal).nugget.isDefined)
                Minecraft.getMinecraft.getItemColors.registerItemColorHandler(new IItemColor {
                    override def getColorFromItemstack(stack: ItemStack, tintIndex: Int): Int = {
                        stack.getItem match {
                            case metal : ItemMetal => metal.getColorFromItemStack(stack)
                            case _ => 0xFFFFFF
                        }
                    }
                }, MetalManager.metalRegistry.get(metal).nugget.get)
        }

        ClientRegistry.bindTileEntitySpecialRenderer(classOf[TileIronTank], new TileTankFluidRenderer)

        ClientRegistry.bindTileEntitySpecialRenderer(classOf[AbstractMachine], new TileMachineIORenderer)

        ClientRegistry.bindTileEntitySpecialRenderer(classOf[TileMobStand], new MobStandEntityRenderer[TileMobStand])
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
