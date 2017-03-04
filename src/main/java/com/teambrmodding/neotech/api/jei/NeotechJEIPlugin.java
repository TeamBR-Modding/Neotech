package com.teambrmodding.neotech.api.jei;

import com.teambr.bookshelf.client.gui.GuiBase;
import com.teambrmodding.neotech.api.jei.alloyer.JEIAlloyerRecipeCategory;
import com.teambrmodding.neotech.api.jei.alloyer.JEIAlloyerRecipeHandler;
import com.teambrmodding.neotech.api.jei.centrifuge.JEICentrifugeRecipeCategory;
import com.teambrmodding.neotech.api.jei.centrifuge.JEICentrifugeRecipeHandler;
import com.teambrmodding.neotech.api.jei.crucible.JEICrucibleRecipeCategory;
import com.teambrmodding.neotech.api.jei.crucible.JEICrucibleRecipeHandler;
import com.teambrmodding.neotech.api.jei.crusher.JEICrusherRecipeCategory;
import com.teambrmodding.neotech.api.jei.crusher.JEICrusherRecipeHandler;
import com.teambrmodding.neotech.api.jei.fluidGenerator.JEIFluidGeneratorCategory;
import com.teambrmodding.neotech.api.jei.fluidGenerator.JEIFluidGeneratorHandler;
import com.teambrmodding.neotech.api.jei.solidifier.JEISolidifierRecipeCategory;
import com.teambrmodding.neotech.api.jei.solidifier.JEISolidifierRecipeHandler;
import com.teambrmodding.neotech.client.gui.machines.generators.GuiFluidGenerator;
import com.teambrmodding.neotech.client.gui.machines.generators.GuiFurnaceGenerator;
import com.teambrmodding.neotech.client.gui.machines.processors.*;
import com.teambrmodding.neotech.common.blocks.machines.BlockMachine;
import com.teambrmodding.neotech.common.items.UpgradeItem;
import com.teambrmodding.neotech.common.tiles.traits.IUpgradeItem;
import com.teambrmodding.neotech.managers.BlockManager;
import mezz.jei.api.*;
import mezz.jei.api.gui.IAdvancedGuiHandler;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/5/2017
 */
@JEIPlugin
public class NeotechJEIPlugin implements IModPlugin {
    // JEI Helper
    public static IJeiHelpers jeiHelpers;

    // Category UUIDS
    public static final String ALLOYER_UUID    = "neotech.alloyer";
    public static final String CENTRIFUGE_UUID = "neotech.centrifuge";
    public static final String CRUCIBLE_UUID   = "neotech.crucible";
    public static final String CRUSHER_UUID    = "neotech.crusher";
    public static final String SOLIDIFIER_UUID = "neotech.solidifier";
    public static final String FLUID_GEN_UUID  = "neotech.fluidGenerator";

    @Override
    public void register(IModRegistry registry) {
        jeiHelpers = registry.getJeiHelpers();

        // Add Categories
        registry.addRecipeCategories(
                new JEICrusherRecipeCategory(),
                new JEISolidifierRecipeCategory(),
                new JEICrucibleRecipeCategory(),
                new JEIAlloyerRecipeCategory(),
                new JEICentrifugeRecipeCategory(),
                new JEIFluidGeneratorCategory());

        // Add Handlers
        registry.addRecipeHandlers(
                new JEICrusherRecipeHandler(),
                new JEISolidifierRecipeHandler(),
                new JEICrucibleRecipeHandler(),
                new JEIAlloyerRecipeHandler(),
                new JEICentrifugeRecipeHandler(),
                new JEIFluidGeneratorHandler());

        // Fill in recipes
        registry.addRecipes(JEIAlloyerRecipeCategory.buildRecipeList());
        registry.addRecipes(JEICentrifugeRecipeCategory.buildRecipeList());
        registry.addRecipes(JEICrucibleRecipeCategory.buildRecipeList());
        registry.addRecipes(JEICrusherRecipeCategory.buildRecipeList());
        registry.addRecipes(JEISolidifierRecipeCategory.buildRecipeList());
        registry.addRecipes(JEIFluidGeneratorCategory.buildRecipeList());

        // Furnace
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockManager.electricFurnace), VanillaRecipeCategoryUid.SMELTING);
        registry.addRecipeClickArea(GuiElectricFurnace.class, 81, 35, 23, 17, VanillaRecipeCategoryUid.SMELTING);

        // Furnace Generator
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockManager.furnaceGenerator), VanillaRecipeCategoryUid.FUEL);
        registry.addRecipeClickArea(GuiFurnaceGenerator.class, 78, 35, 14, 14, VanillaRecipeCategoryUid.FUEL);

        // Fluid Generator
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockManager.fluidGenerator), FLUID_GEN_UUID);
        registry.addRecipeClickArea(GuiFluidGenerator.class, 78, 35, 14, 14, FLUID_GEN_UUID);

        // Crusher
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockManager.electricCrusher), CRUSHER_UUID);
        registry.addRecipeClickArea(GuiElectricCrusher.class, 79, 34, 24, 17, CRUSHER_UUID);

        // Solidifier
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockManager.electricSolidifier), SOLIDIFIER_UUID);
        registry.addRecipeClickArea(GuiSolidifier.class, 97, 35, 23, 17, SOLIDIFIER_UUID);

        // Crucible
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockManager.electricCrucible), CRUCIBLE_UUID);
        registry.addRecipeClickArea(GuiCrucible.class, 81, 35, 23, 17, CRUCIBLE_UUID);

        // Alloyer
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockManager.electricAlloyer), ALLOYER_UUID);
        registry.addRecipeClickArea(GuiAlloyer.class, 81, 35, 23, 17, ALLOYER_UUID);

        // Centrifuge
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockManager.electricCentrifuge), CENTRIFUGE_UUID);
        registry.addRecipeClickArea(GuiCentrifuge.class, 94, 35, 23, 17, CENTRIFUGE_UUID);

        // Move JEI item pane around tabs
        registry.addAdvancedGuiHandlers(new IAdvancedGuiHandler<GuiBase>() {
            @Nonnull
            @Override
            public Class<GuiBase> getGuiContainerClass() {
                return GuiBase.class;
            }

            @Nullable
            @Override
            public List<Rectangle> getGuiExtraAreas(@Nonnull GuiBase guiContainer) {
                return guiContainer.getCoveredAreas();
            }

            @Nullable
            @Override
            public Object getIngredientUnderMouse(@Nonnull GuiBase guiContainer, int mouseX, int mouseY) {
                return null;
            }
        });

        // Add descriptions for machines
        for (Object block : Block.REGISTRY) {
            if (block instanceof BlockMachine) {
                BlockMachine machine = (BlockMachine) block;
                registry.addDescription(new ItemStack(machine), "neotech." + machine.name + ".desc");
            }
        }

        // Batteries
        List<ItemStack> storageList = new ArrayList<>();
        storageList.add(new ItemStack(BlockManager.basicEnergyStorage));
        storageList.add(new ItemStack(BlockManager.advancedEnergyStorage));
        storageList.add(new ItemStack(BlockManager.eliteEnergyStorage));
        registry.addDescription(storageList,
                "neotech.energyStorage.desc");

        // Fluid Storage
        List<ItemStack> fluidStorageList = new ArrayList<>();
        fluidStorageList.add(new ItemStack(BlockManager.basicTank));
        fluidStorageList.add(new ItemStack(BlockManager.advancedTank));
        fluidStorageList.add(new ItemStack(BlockManager.eliteTank));
        registry.addDescription(fluidStorageList, "neotech.fluidStorage.desc");

        // Add descriptions for items
        for(Object item : Item.REGISTRY) {
            // Upgrades
            if(item instanceof UpgradeItem) {
                UpgradeItem upgradeItem = (UpgradeItem) item;
                if(upgradeItem.getCategory() == IUpgradeItem.ENUM_UPGRADE_CATEGORY.MISC)
                    registry.addDescription(new ItemStack(upgradeItem), "neotech." + upgradeItem.getID() + ".desc");
                else
                    registry.addDescription(new ItemStack(upgradeItem), "neotech." + upgradeItem.getCategory() + ".desc");
            }
        }
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry){ }

    @Override
    public void registerIngredients(IModIngredientRegistration registry) { }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) { }
}
