package com.teambrmodding.neotech.managers;

import com.teambrmodding.neotech.client.ItemRenderManager;
import com.teambrmodding.neotech.client.ModelLoaderHelper;
import com.teambrmodding.neotech.client.mesh.ModelLocationWrapper;
import com.teambrmodding.neotech.client.mesh.SimpleItemMeshDefinition;
import com.teambrmodding.neotech.common.metals.blocks.BlockFluidMetal;
import com.teambrmodding.neotech.common.metals.blocks.BlockMetalOre;
import com.teambrmodding.neotech.common.metals.fluids.FluidMetal;
import com.teambrmodding.neotech.common.metals.items.ItemMetal;
import com.teambrmodding.neotech.lib.Reference;
import gnu.trove.map.hash.THashMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/14/2017
 */
public class MetalManager {

    // Stores all registered metals
    public static THashMap<String, Metal> metalRegistry = new THashMap<>();

    // The fluid amounts for various objects
    public static final int BLOCK_MB  = 1296;
    public static final int ORE_MB    = 432;
    public static final int INGOT_MB  = 144;
    public static final int DUST_MB   = 72;
    public static final int NUGGET_MB = 16;

    // Metal Names
    public static final String COPPER       = "copper";
    public static final String DIRTY_COPPER = "dirtycopper";
    public static final String TIN          = "tin";
    public static final String DIRTY_TIN    = "dirtytin";
    public static final String LEAD         = "lead";
    public static final String DIRTY_LEAD   = "dirtylead";
    public static final String SILVER       = "silver";
    public static final String DIRTY_SILVER = "dirtysilver";
    public static final String GOLD         = "gold";
    public static final String DIRTY_GOLD   = "dirtygold";
    public static final String IRON         = "iron";
    public static final String DIRTY_IRON   = "dirtyiron";
    public static final String BRONZE       = "bronze";
    public static final String STEEL        = "steel";
    public static final String CARBON       = "carbon";
    public static final String OBSIDIAN     = "obsidian";
    public static final String BLAZE        = "blaze";
    public static final String CHORUS       = "chorus";
    public static final String WITHER       = "wither";

    // Alloys
    public static final String TORMENTED  = "tormented";
    public static final String OUTLANDISH = "outlandish";
    public static final String NEODYMIUM  = "neodymium";

    // A list of metals that Tinker's Construct adds, we want to play nice if possible
    private static final List<String> tinkersMetals = Arrays.asList("brass", "copper", "tin", "bronze", "zinc", "lead",
            "nickel", "silver", "electrum", "steel", "aluminium");

    /**
     * Registers all the models
     */
    @SuppressWarnings("SuspiciousMethodCalls")
    @SideOnly(Side.CLIENT)
    public static void registerModels() {
        for (Object o : metalRegistry.keySet()) {
            Metal metal = metalRegistry.get(o);

            // Blocks
            if (metal.getSolidBlock() != null)
                ItemRenderManager.registerBlockModel(metal.getSolidBlock(), metal.getSolidBlock().getName().toLowerCase(), "normal");

            // Ore
            if (metal.getOreBlock() != null)
                ItemRenderManager.registerBlockModel(metal.getOreBlock(), metal.getOreBlock().getName().toLowerCase(), "normal");

            // Ingot
            if (metal.getIngot() != null) {
                ModelLoader.setCustomMeshDefinition(metal.getIngot(),
                        new SimpleItemMeshDefinition("metalitem", "type=ingot"));
                ModelLoaderHelper.registerItem(metal.getIngot(), "items/metalItem", "type=ingot");
            }

            // Dust
            if (metal.getDust() != null) {
                ModelLoader.setCustomMeshDefinition(metal.getDust(),
                        new SimpleItemMeshDefinition("metalitem", "type=dust"));
                ModelLoaderHelper.registerItem(metal.getDust(), "items/metalItem", "type=dust");
            }

            // Nugget
            if (metal.getNugget() != null) {
                ModelLoader.setCustomMeshDefinition(metal.getNugget(),
                        new SimpleItemMeshDefinition("metalitem", "type=nugget"));
                ModelLoaderHelper.registerItem(metal.getNugget(), "items/metalItem", "type=nugget");
            }

            // Fluid
            if(metal.getFluidBlock() != null) {
                final Item item = Item.getItemFromBlock(metal.getFluidBlock());
                assert item != null;

               // ModelBakery.registerItemVariants(item);

                ModelResourceLocation modelResourceLocation =
                        new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID, "fluid_" +
                                metal.getFluidBlock().getFluid().getName().toLowerCase()), "inventory");
                ModelLoader.setCustomMeshDefinition(item, new ModelLocationWrapper(modelResourceLocation));
                ModelLoader.setCustomModelResourceLocation(item, 0, modelResourceLocation);
                ModelLoader.setCustomStateMapper(metal.getFluidBlock(), new StateMapperBase() {
                    @Override
                    protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                        return new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID, "fluid_" +
                                metal.getFluidBlock().getFluid().getName().toLowerCase()), "fluid");
                    }
                });
            }
        }
    }

    public static void registerDefaultMetals() {
        // Copper
        registerMetal(COPPER, 1, 0xFFc27a47, 0xFFc27a49,
                true, false, true,
                true, true,
                true, true, true);
        registerMetal(DIRTY_COPPER, 1, 0xFFc27a49, 0xFFc27a49,
                true, true, true,
                false, false,
                false, false, false);

        // Tin
        registerMetal(TIN, 1, 0xFFe7eadd, 0xFFe7eadd,
                true, false, true,
                true, true,
                true, true, true);
        registerMetal(DIRTY_TIN, 1, 0xFFe7eadd, 0xFFe7eadd,
                true, true, true,
                false, false,
                false, false, false);

        // Lead
        registerMetal(LEAD, 2, 0xFF796a78, 0xFF796a78,
                true, false, true,
                true, true,
                true, true, true);
        registerMetal(DIRTY_LEAD, 1, 0xFF796a78, 0xFF796a78,
                true, true, true,
                false, false,
                false, false, false);

        // Silver
        registerMetal(SILVER, 2, 0xFFc5d8de, 0xFFc5d8de,
                true, false, true,
                true, true,
                true, true, true);
        registerMetal(DIRTY_SILVER, 1, 0xFFc5d8de, 0xFFc5d8de,
                true, true, true,
                false, false,
                false, false, false);

        // Iron
        registerMetal(IRON, 1, 0xFFd8d8d8, 0xFFb71b1b,
                true, false, true,
                false, false,
                false, true, true);
        registerMetal(DIRTY_IRON, 1, 0xFFd8d8d8, 0xFFb71b1b,
                true, true, true,
                false, false,
                false, false, false);

        // Gold
        registerMetal(GOLD, 1, 0xFFdede00, 0xFFdede00,
                true, false, true,
                false, false,
                false, true, false);
        registerMetal(DIRTY_GOLD, 1, 0xFFdede00, 0xFFdede00,
                true, true, true,
                false, false,
                false, false, false);

        // Bronze
        registerMetal(BRONZE, 2, 0xFFcd9520, 0xFFcd9520,
                true, false, true,
                false, true,
                true, true, true);

        // Steel
        registerMetal(STEEL, 2, 0xFF646464, 0xFF646464,
                true, false, true,
                false, true,
                true, true, true);

        // Obsidian
        registerMetal(OBSIDIAN, 1, 0xFF583962, 0xFF583962,
                true, false, true,
                false, false, false, false, false);

        // Carbon
        registerMetal(CARBON, 1, 0xFF202020, 0xFF202020,
                true, true, true,
                false, false, false, false, false);

        // Blaze
        registerMetal(BLAZE, 1, 0xFFbf5a00, 0xFFbf5a00,
                true, true, true,
                false, false, false, false, false);

        // Chorus
        registerMetal(CHORUS, 1, 0xFF9b6f9b, 0xFF9b6f9b,
                true, true, true,
                false, false, false, false, false);

        // Wither
        registerMetal(WITHER, 1, 0xFFfdffa8, 0xFFfdffa8,
                true, true, true,
                false, false, false, false, false);


        // Tormented
        registerMetal(TORMENTED, 1, 0xFF774747, 0xFF774747,
                true, false, true,
                false, true, true, true, true);

        // Outlandish
        registerMetal(OUTLANDISH, 1, 0xFF613e8f, 0xFF613e8f,
                true, true, true,
                false, true, true, true, true);

        // Neodymium
        registerMetal(NEODYMIUM, 1, 0xFF801f1f, 0xFF801f1f,
                true, true, true,
                false, true, true, true, true);
    }

    public static Metal registerMetal(String metalName, int miningLevel, int color, int fluidColor,
                                      boolean hasFluid, boolean isDirtyFluid, boolean hasFluidBlock,
                                      boolean hasOre, boolean hasSolidBlock,
                                      boolean hasIngot, boolean hasDust, boolean hasNugget) {
        //--------------------------------------------------------------------------------------------------------------
        // Fluid                                                                                                       -
        //--------------------------------------------------------------------------------------------------------------

        Fluid fluid = null;
        if(hasFluid && !isTinkers(metalName))
            fluid = createFluidMetal(fluidColor, isDirtyFluid, metalName, "neotech:blocks/metal");

        BlockFluidMetal fluidBlock = null;
        if(hasFluidBlock && fluid != null)
            fluidBlock = registerFluidBlock(fluid, new BlockFluidMetal(fluid));

        //--------------------------------------------------------------------------------------------------------------
        // Blocks                                                                                                      -
        //--------------------------------------------------------------------------------------------------------------

        String oreName = "ore" + Character.toUpperCase(metalName.charAt(0)) + metalName.substring(1);

        BlockMetalOre oreBlock = null;
        if(hasOre)
            oreBlock = BlockManager.registerBlock(new BlockMetalOre(oreName, miningLevel), oreName);

        String blockName = "block" + Character.toUpperCase(metalName.charAt(0)) + metalName.substring(1);

        BlockMetalOre solidBlock = null;
        if(hasSolidBlock)
            solidBlock = BlockManager.registerBlock(new BlockMetalOre(blockName, 1),
                    blockName);

        //--------------------------------------------------------------------------------------------------------------
        // Items                                                                                                       -
        //--------------------------------------------------------------------------------------------------------------

        String ingotName = "ingot" + Character.toUpperCase(metalName.charAt(0)) + metalName.substring(1);
        ItemMetal ingot = null;
        if(hasIngot)
            ingot = ItemManager.registerItem(new ItemMetal(ingotName, color, 64), ingotName);

        String dustName = "dust" + Character.toUpperCase(metalName.charAt(0)) + metalName.substring(1);
        ItemMetal dust = null;
        if(hasDust)
            dust = ItemManager.registerItem(new ItemMetal(dustName, color, 64), dustName);

        String nuggetName = "nugget" + Character.toUpperCase(metalName.charAt(0)) + metalName.substring(1);
        ItemMetal nugget = null;
        if(hasNugget)
            nugget = ItemManager.registerItem(new ItemMetal(nuggetName, color, 64), nuggetName);

        //--------------------------------------------------------------------------------------------------------------
        // Metal                                                                                                       -
        //--------------------------------------------------------------------------------------------------------------

        Metal metal = new Metal(metalName, fluid, fluidBlock,
                oreBlock, solidBlock,
                ingot, dust, nugget);

        metalRegistry.put(metalName.toLowerCase(), metal);

        //--------------------------------------------------------------------------------------------------------------
        // Crafting                                                                                                    -
        //--------------------------------------------------------------------------------------------------------------

        // Ore to ingot
        if(metal.getOreBlock() != null && metal.getIngot() != null)
            GameRegistry.addSmelting(metal.getOreBlock(), new ItemStack(metal.getIngot(), 1), 0.5F);

        // Dust to ingot
        if(metal.getIngot() != null && metal.getDust() != null)
            GameRegistry.addSmelting(metal.getDust(), new ItemStack(metal.getIngot(), 1), 0.5F);

        // Nugget - Ingot
        if(metal.getIngot() != null && metal.getNugget() != null) {
            GameRegistry.addShapelessRecipe(new ItemStack(metal.getNugget(), 9), metal.getIngot());
            GameRegistry.addRecipe(new ItemStack(metal.getIngot(), 1),
                    "III",
                    "III",
                    "III", 'I', metal.getNugget());
        }

        // Ingot - Block
        if (metal.getIngot() != null && metal.getSolidBlock() != null) {
            GameRegistry.addShapelessRecipe(new ItemStack(metal.getIngot(), 9), metal.getSolidBlock());
            GameRegistry.addRecipe(new ItemStack(metal.getSolidBlock(), 1),
                    "III",
                    "III",
                    "III", 'I', metal.getIngot());
        }

        return metal;
    }

    /**
     * Get a metal from the registry
     * @param name The metal name
     * @return The metal object
     */
    @Nullable
    public static Metal getMetal(String name) {
        if(metalRegistry.containsKey(name.toLowerCase()))
            return metalRegistry.get(name.toLowerCase());
        else
            return null;
    }

    /**
     * Used to create a fluid for this metal
     *
     * @param color        The color of the fluid, format 0xAARRGGBB
     * @param name         The name of the fluid eg. copper
     * @param iconLocation The texture base eg. neotech:blocks/metal
     * @return The fluid created, null if not registered
     */
    public static Fluid createFluidMetal(int color, boolean isDirtyMetal, String name, String iconLocation) {
        String texture = iconLocation;
        if(isDirtyMetal)
            texture += "_dirty";

        ResourceLocation stillIcon   = new ResourceLocation(texture + "_still");
        ResourceLocation flowingIcon = new ResourceLocation(texture + "_flow");

        Fluid fluid = new FluidMetal(color, name, stillIcon, flowingIcon);
        FluidRegistry.registerFluid(fluid);
        if(!FluidRegistry.getBucketFluids().contains(fluid))
            FluidRegistry.addBucketForFluid(fluid);

        return fluid;
    }

    /**
     * Registers the Fluid Block to the fluid
     *
     * @param fluid The fluid
     * @param block The fluid block
     * @return The block passed back
     */
    public static BlockFluidMetal registerFluidBlock(Fluid fluid, BlockFluidMetal block) {
        if(fluid != null) {
            fluid.setBlock(block);
            BlockManager.registerBlock(block);
        }
        return block;
    }

    /**
     * Helper method to test if fluid is a tinkers fluid
     * @param fluid The fluid
     * @return True if in tinkers, don't register fluid
     */
    public static boolean isTinkers(String fluid) {
        return Loader.isModLoaded("tconstruct") && tinkersMetals.contains(fluid);
    }

    public static class Metal {
        // Variables
        protected String oreDict;
        protected Fluid fluid;
        protected BlockFluidMetal fluidBlock;
        protected BlockMetalOre oreBlock, solidBlock;
        protected ItemMetal ingot, dust, nugget;


        /**
         * Used to hold all information about a metal, some elements may be null, always check
         *
         * @param oreDict    The ore dict tag of the metal
         * @param fluid      The fluid of the metal
         * @param fluidBlock The fluid block of the metal
         * @param oreBlock   The Ore Block or the metal
         * @param solidBlock The solid block of the metal
         * @param ingot      The ingot of the metal
         * @param dust       The dust of the metal
         * @param nugget     The nugget of the metal
         */
        public Metal(@Nullable String oreDict,
                     @Nullable Fluid fluid, @Nullable BlockFluidMetal fluidBlock,
                     @Nullable BlockMetalOre oreBlock, @Nullable BlockMetalOre solidBlock,
                     @Nullable ItemMetal ingot, @Nullable ItemMetal dust, @Nullable ItemMetal nugget) {
            this.oreDict = oreDict;
            this.fluid = fluid;
            this.fluidBlock = fluidBlock;
            this.oreBlock = oreBlock;
            this.solidBlock = solidBlock;
            this.ingot = ingot;
            this.dust = dust;
            this.nugget = nugget;
        }

        /***************************************************************************************************************
         * Accessors/Mutators                                                                                          *
         ***************************************************************************************************************/

        public String getOreDict() {
            return oreDict;
        }

        public Fluid getFluid() {
            return fluid;
        }

        public BlockFluidMetal getFluidBlock() {
            return fluidBlock;
        }

        public BlockMetalOre getOreBlock() {
            return oreBlock;
        }

        public BlockMetalOre getSolidBlock() {
            return solidBlock;
        }

        public ItemMetal getIngot() {
            return ingot;
        }

        public ItemMetal getDust() {
            return dust;
        }

        public ItemMetal getNugget() {
            return nugget;
        }
    }
}
