package com.teambrmodding.neotech.client;

import com.teambr.bookshelf.common.CommonProxy;
import com.teambrmodding.neotech.client.mesh.MeshDefinitions.SimpleItemMeshDefinition;
import com.teambrmodding.neotech.client.renderers.tiles.TileMachineIORenderer;
import com.teambrmodding.neotech.client.renderers.tiles.TileTankFluidRenderer;
import com.teambrmodding.neotech.common.fluids.FluidBlockGas;
import com.teambrmodding.neotech.common.metals.blocks.BlockFluidMetal;
import com.teambrmodding.neotech.common.metals.items.ItemMetal;
import com.teambrmodding.neotech.common.tiles.AbstractMachine;
import com.teambrmodding.neotech.common.tiles.storage.tanks.TileBasicTank;
import com.teambrmodding.neotech.common.tiles.traits.IUpgradeItem;
import com.teambrmodding.neotech.managers.BlockManager;
import com.teambrmodding.neotech.managers.FluidManager;
import com.teambrmodding.neotech.managers.ItemManager;
import com.teambrmodding.neotech.managers.MetalManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.annotation.Nullable;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/11/2017
 */
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        ItemRenderManager.registerBlockModel(BlockManager.electricFurnace, "electricFurnace", "facing=north,isactive=false");
        ItemRenderManager.registerBlockModel(BlockManager.electricCrusher, "electricCrusher", "facing=north,isactive=false");
        ItemRenderManager.registerBlockModel(BlockManager.furnaceGenerator, "furnaceGenerator", "facing=north,isactive=false");
        ItemRenderManager.registerBlockModel(BlockManager.fluidGenerator, "fluidGenerator", "facing=north,isactive=false");
        ItemRenderManager.registerBlockModel(BlockManager.electricCrucible, "electricCrucible", "facing=north,isactive=false");
        ItemRenderManager.registerBlockModel(BlockManager.electricSolidifier, "electricSolidifier", "facing=north,isactive=false");
        ItemRenderManager.registerBlockModel(BlockManager.electricAlloyer, "alloyer", "facing=north,isactive=false");
        ItemRenderManager.registerBlockModel(BlockManager.electricCentrifuge, "centrifuge", "facing=north,isactive=false");
        ItemRenderManager.registerBlockModel(BlockManager.treeFarm, "treeFarm", "isactive=false");
        ItemRenderManager.registerBlockModel(BlockManager.solarPanelT1, "solarPanelT1", "isactive=false");
        ItemRenderManager.registerBlockModel(BlockManager.solarPanelT2, "solarPanelT2", "isactive=false");
        ItemRenderManager.registerBlockModel(BlockManager.solarPanelT3, "solarPanelT3", "isactive=false");
        ItemRenderManager.registerBlockModel(BlockManager.basicRFStorage, "basicRFStorage", "normal");
        ItemRenderManager.registerBlockModel(BlockManager.advancedRFStorage, "advancedRFStorage", "normal");
        ItemRenderManager.registerBlockModel(BlockManager.eliteRFStorage, "eliteRFStorage", "normal");
        ItemRenderManager.registerBlockModel(BlockManager.creativeRFStorage, "creativeRFStorage", "normal");
        ItemRenderManager.registerBlockModel(BlockManager.ironTank, "ironTank", "normal");
        ItemRenderManager.registerBlockModel(BlockManager.goldTank, "goldTank", "normal");
        ItemRenderManager.registerBlockModel(BlockManager.diamondTank, "diamondTank", "normal");
        ItemRenderManager.registerBlockModel(BlockManager.creativeTank, "creativeTank", "normal");
        ItemRenderManager.registerBlockModel(BlockManager.voidTank, "voidTank", "normal");

        MetalManager.registerModels();

        // Upgrades

        // CPUS
        // Single Core
        ModelLoader.setCustomMeshDefinition(ItemManager.processorSingleCore,
                new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.CPU_SINGLE_CORE.toLowerCase()));
        ModelLoaderHelper.registerItem(ItemManager.processorSingleCore, "items/upgrades", "type=" + IUpgradeItem.CPU_SINGLE_CORE.toLowerCase());

        // Dual Core
        ModelLoader.setCustomMeshDefinition(ItemManager.processorDualCore,
                new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.CPU_DUAL_CORE.toLowerCase()));
        ModelLoaderHelper.registerItem(ItemManager.processorDualCore, "items/upgrades", "type=" + IUpgradeItem.CPU_DUAL_CORE.toLowerCase());

        // Quad Core
        ModelLoader.setCustomMeshDefinition(ItemManager.processorQuadCore,
                new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.CPU_QUAD_CORE.toLowerCase()));
        ModelLoaderHelper.registerItem(ItemManager.processorQuadCore, "items/upgrades", "type=" + IUpgradeItem.CPU_QUAD_CORE.toLowerCase());

        // Octa Core
        ModelLoader.setCustomMeshDefinition(ItemManager.processorOctCore,
                new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.CPU_OCT_CORE.toLowerCase()));
        ModelLoaderHelper.registerItem(ItemManager.processorOctCore, "items/upgrades", "type=" + IUpgradeItem.CPU_OCT_CORE.toLowerCase());


        // Memory
        // DDR1
        ModelLoader.setCustomMeshDefinition(ItemManager.memoryDDR1,
                new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.MEMORY_DDR1.toLowerCase()));
        ModelLoaderHelper.registerItem(ItemManager.memoryDDR1, "items/upgrades", "type=" + IUpgradeItem.MEMORY_DDR1.toLowerCase());

        // DDR2
        ModelLoader.setCustomMeshDefinition(ItemManager.memoryDDR2,
                new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.MEMORY_DDR2.toLowerCase()));
        ModelLoaderHelper.registerItem(ItemManager.memoryDDR2, "items/upgrades", "type=" + IUpgradeItem.MEMORY_DDR2.toLowerCase());

        //DDR3
        ModelLoader.setCustomMeshDefinition(ItemManager.memoryDDR3,
                new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.MEMORY_DDR3.toLowerCase()));
        ModelLoaderHelper.registerItem(ItemManager.memoryDDR3, "items/upgrades", "type=" + IUpgradeItem.MEMORY_DDR3.toLowerCase());

        //DDR4
        ModelLoader.setCustomMeshDefinition(ItemManager.memoryDDR4,
                new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.MEMORY_DDR4.toLowerCase()));
        ModelLoaderHelper.registerItem(ItemManager.memoryDDR4, "items/upgrades", "type=" + IUpgradeItem.MEMORY_DDR4.toLowerCase());

        // Hard Drive
        // 64
        ModelLoader.setCustomMeshDefinition(ItemManager.hardDrive64G,
                new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.HDD_64G.toLowerCase()));
        ModelLoaderHelper.registerItem(ItemManager.hardDrive64G, "items/upgrades", "type=" + IUpgradeItem.HDD_64G.toLowerCase());

        // 254
        ModelLoader.setCustomMeshDefinition(ItemManager.hardDrive256G,
                new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.HDD_256G.toLowerCase()));
        ModelLoaderHelper.registerItem(ItemManager.hardDrive256G, "items/upgrades", "type=" + IUpgradeItem.HDD_256G.toLowerCase());

        // 512
        ModelLoader.setCustomMeshDefinition(ItemManager.hardDrive512G,
                new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.HDD_512G.toLowerCase()));
        ModelLoaderHelper.registerItem(ItemManager.hardDrive512G, "items/upgrades", "type=" + IUpgradeItem.HDD_512G.toLowerCase());

        // 1t
        ModelLoader.setCustomMeshDefinition(ItemManager.hardDrive1T,
                new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.HDD_1T.toLowerCase()));
        ModelLoaderHelper.registerItem(ItemManager.hardDrive1T, "items/upgrades", "type=" + IUpgradeItem.HDD_1T.toLowerCase());

        // PSU
        // 250
        ModelLoader.setCustomMeshDefinition(ItemManager.psu250W,
                new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.PSU_250W.toLowerCase()));
        ModelLoaderHelper.registerItem(ItemManager.psu250W, "items/upgrades", "type=" + IUpgradeItem.PSU_250W.toLowerCase());

        // 500
        ModelLoader.setCustomMeshDefinition(ItemManager.psu500W,
                new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.PSU_500W.toLowerCase()));
        ModelLoaderHelper.registerItem(ItemManager.psu500W, "items/upgrades", "type=" + IUpgradeItem.PSU_500W.toLowerCase());

        // 750
        ModelLoader.setCustomMeshDefinition(ItemManager.psu750W,
                new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.PSU_750W.toLowerCase()));
        ModelLoaderHelper.registerItem(ItemManager.psu750W, "items/upgrades", "type=" + IUpgradeItem.PSU_750W.toLowerCase());

        // 960
        ModelLoader.setCustomMeshDefinition(ItemManager.psu960W,
                new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.PSU_960W.toLowerCase()));
        ModelLoaderHelper.registerItem(ItemManager.psu960W, "items/upgrades", "type=" + IUpgradeItem.PSU_960W.toLowerCase());

        // Transformer
        if (Loader.isModLoaded("IC2")) {
            ModelLoader.setCustomMeshDefinition(ItemManager.transformer,
                    new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.TRANSFORMER.toLowerCase()));
            ModelLoaderHelper.registerItem(ItemManager.transformer, "items/upgrades", "type=" + IUpgradeItem.TRANSFORMER.toLowerCase());
        }

        // Expansion
        ModelLoader.setCustomMeshDefinition(ItemManager.expansion,
                new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.EXPANSION_CARD.toLowerCase()));
        ModelLoaderHelper.registerItem(ItemManager.expansion, "items/upgrades", "type=" + IUpgradeItem.EXPANSION_CARD.toLowerCase());

        // Redstone Control
        ModelLoader.setCustomMeshDefinition(ItemManager.redstoneControl,
                new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.REDSTONE_CIRCUIT.toLowerCase()));
        ModelLoaderHelper.registerItem(ItemManager.redstoneControl, "items/upgrades", "type=" + IUpgradeItem.REDSTONE_CIRCUIT.toLowerCase());

        // Network Card
        ModelLoader.setCustomMeshDefinition(ItemManager.networkCard,
                new SimpleItemMeshDefinition("upgrades", "type=" + IUpgradeItem.NETWORK_CARD.toLowerCase()));
        ModelLoaderHelper.registerItem(ItemManager.networkCard, "items/upgrades", "type=" + IUpgradeItem.NETWORK_CARD.toLowerCase());
    }

    /**
     * Called during the init phase of the mod loading
     *
     * Now that the items and such are loaded, use this chance to use them
     */
    public static void init() {
        ItemRenderManager.registerItemRenderer();

        // Tile Renders
        ClientRegistry.bindTileEntitySpecialRenderer(TileBasicTank.class, new TileTankFluidRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(AbstractMachine.class, new TileMachineIORenderer());

        // Register Fluid Colors
        for(String metalString : MetalManager.metalRegistry().keySet()) {
            if(MetalManager.metalRegistry().get(metalString).fluidBlock().isDefined()) {
                Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new IBlockColor() {
                    @Override
                    public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) {
                        if(state.getBlock() instanceof BlockFluidMetal) {
                            BlockFluidMetal metal = (BlockFluidMetal) state.getBlock();
                            return metal.getBlockColor();
                        }
                        return 0xFFFFFF;
                    }
                }, MetalManager.metalRegistry().get(metalString).fluidBlock().get());
            }
        }

        // Gas Colors
        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new IBlockColor() {
            @Override
            public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) {
                if(state.getBlock() instanceof FluidBlockGas) {
                    FluidBlockGas gas = (FluidBlockGas) state.getBlock();
                    return gas.getBlockColor();
                }
                return 0xFFFFFF;
            }
        }, FluidManager.blockOxygen(), FluidManager.blockHydrogen());

        // Metal Items
        for(String metal : MetalManager.metalRegistry().keySet()) {
            // Dusts
            if(MetalManager.metalRegistry().get(metal).dust().isDefined()) {
                Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() {
                    @Override
                    public int getColorFromItemstack(ItemStack stack, int tintIndex) {
                        if(stack.getItem() instanceof ItemMetal) {
                            ItemMetal itemMetal = (ItemMetal) stack.getItem();
                            return itemMetal.getColorFromItemStack(stack);
                        }
                        return 0xFFFFFF;
                    }
                }, MetalManager.metalRegistry().get(metal).dust().get());
            }

            // Ingots
            if(MetalManager.metalRegistry().get(metal).ingot().isDefined()) {
                Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() {
                    @Override
                    public int getColorFromItemstack(ItemStack stack, int tintIndex) {
                        if(stack.getItem() instanceof ItemMetal) {
                            ItemMetal itemMetal = (ItemMetal) stack.getItem();
                            return itemMetal.getColorFromItemStack(stack);
                        }
                        return 0xFFFFFF;
                    }
                }, MetalManager.metalRegistry().get(metal).ingot().get());
            }

            // Nuggets
            if(MetalManager.metalRegistry().get(metal).nugget().isDefined()) {
                Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() {
                    @Override
                    public int getColorFromItemstack(ItemStack stack, int tintIndex) {
                        if(stack.getItem() instanceof ItemMetal) {
                            ItemMetal itemMetal = (ItemMetal) stack.getItem();
                            return itemMetal.getColorFromItemStack(stack);
                        }
                        return 0xFFFFFF;
                    }
                }, MetalManager.metalRegistry().get(metal).nugget().get());
            }
        }
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {}
}
