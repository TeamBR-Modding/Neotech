package com.dyonovan.neotech.client

import com.dyonovan.neotech.client.mesh.MeshDefinitions.StarModelMesh
import com.dyonovan.neotech.client.modelfactory.ModelFactory
import com.dyonovan.neotech.client.renderers.entity.RenderNet
import com.dyonovan.neotech.client.renderers.tiles._
import com.dyonovan.neotech.common.CommonProxy
import com.dyonovan.neotech.common.entities.EntityNet
import com.dyonovan.neotech.common.fluids.FluidBlockGas
import com.dyonovan.neotech.common.metals.blocks.BlockFluidMetal
import com.dyonovan.neotech.common.metals.items.ItemMetal
import com.dyonovan.neotech.common.tiles.AbstractMachine
import com.dyonovan.neotech.common.tiles.misc.TileMobStand
import com.dyonovan.neotech.common.tiles.storage.TileFlushableChest
import com.dyonovan.neotech.common.tiles.storage.tanks.TileIronTank
import com.dyonovan.neotech.events.{GuiEvents, RenderingEvents}
import com.dyonovan.neotech.managers.{BlockManager, FluidManager, MetalManager}
import com.dyonovan.neotech.tools.armor.ItemElectricArmor
import com.dyonovan.neotech.tools.tools.BaseElectricTool
import com.dyonovan.neotech.tools.upgradeitems.BaseUpgradeItem
import com.teambr.bookshelf.client.models.{BakedConnectedTextures, BakedDynItem}
import com.teambr.bookshelf.common.blocks.BlockConnectedTextures
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
        MinecraftForge.EVENT_BUS.register(GuiEvents)

        RenderingRegistry.registerEntityRenderingHandler(classOf[EntityNet], new IRenderFactory[EntityNet] {
            override def createRenderFor(manager: RenderManager): Render[_ >: EntityNet] = new RenderNet(manager)
        })

        ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(BlockManager.blockMiniatureStar), new StarModelMesh)
        for(dye <- EnumDyeColor.values())
            ModelLoaderHelper.registerItem(Item.getItemFromBlock(BlockManager.blockMiniatureStar),
                "blockMiniatureStar",
                "attached_side=6,color=" + dye.getName)

        // Item Models
        val itemIterator = Item.REGISTRY.iterator()
        while(itemIterator.hasNext) {
            itemIterator.next() match {
                case upgradeItem : BaseUpgradeItem =>
                    ModelLoader.setCustomModelResourceLocation(upgradeItem, 0, BakedDynItem.MODEL_RESOURCE_LOCATION)
                case tool : BaseElectricTool if !tool.isInstanceOf[ItemElectricArmor] =>
                    ModelLoader.setCustomModelResourceLocation(tool, 0, BakedDynItem.MODEL_RESOURCE_LOCATION)
                case _ =>
            }
        }

        // Block Models, for items
        val blockIterator = Block.REGISTRY.iterator()
        while(blockIterator.hasNext) {
            blockIterator.next() match {
                case connectedTextures : BlockConnectedTextures =>
                        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(connectedTextures), 0,
                            BakedConnectedTextures.MODEL_RESOURCE_LOCATION_NORMAL)
                case _ =>
            }
        }

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
        ItemRenderManager.registerBlockModel(BlockManager.blockCrafter, "blockCrafter", "normal")
        ItemRenderManager.registerBlockModel(BlockManager.blockMiniatureSun, "blockMiniatureSun", "attached_side=6")
        ItemRenderManager.registerBlockModel(BlockManager.playerPlate, "playerPlate", "powered=false")
        ItemRenderManager.registerBlockModel(BlockManager.chunkLoader, "chunkLoader", "normal")
        ItemRenderManager.registerBlockModel(BlockManager.redstoneClock, "redstoneClock", "powered=false")
        ItemRenderManager.registerBlockModel(BlockManager.mobStand, "mobStand", "normal")
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

        Minecraft.getMinecraft.getRenderItem.getItemModelMesher.getModelManager.getBlockModelShapes.registerBuiltInBlocks(BlockManager.flushableChest)
        ClientRegistry.bindTileEntitySpecialRenderer(classOf[TileFlushableChest], new TileFlushableChestRenderer[TileFlushableChest])

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
