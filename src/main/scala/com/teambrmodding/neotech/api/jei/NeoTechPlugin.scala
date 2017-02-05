package com.teambrmodding.neotech.api.jei

import java.awt.Rectangle
import java.util

import com.teambr.bookshelf.client.gui.GuiBase
import com.teambrmodding.neotech.api.jei.alloyer.{JEIAlloyerRecipeCategory, JEIAlloyerRecipeHandler, JEIAlloyerRecipeMaker, JEIAlloyerRecipeWrapper}
import com.teambrmodding.neotech.api.jei.centrifuge.{JEICentrifugeRecipeCategory, JEICentrifugeRecipeHandler, JEICentrifugeRecipeMaker}
import com.teambrmodding.neotech.api.jei.crucible.{JEICrucibleRecipeCategory, JEICrucibleRecipeHandler, JEICrucibleRecipeMaker}
import com.teambrmodding.neotech.api.jei.crusher.{JEICrusherRecipeCategory, JEICrusherRecipeHandler, JEICrusherRecipeMaker}
import com.teambrmodding.neotech.api.jei.solidifier.{JEISolidifierRecipeCategory, JEISolidifierRecipeHandler, JEISolidifierRecipeMaker}
import com.teambrmodding.neotech.managers.BlockManager
import mezz.jei.api._
import mezz.jei.api.gui.IAdvancedGuiHandler
import mezz.jei.api.ingredients.IModIngredientRegistration
import mezz.jei.api.recipe.VanillaRecipeCategoryUid
import net.minecraft.item.ItemStack

/**
  * Created by Dyonovan on 1/9/2016.
  */
object NeoTechPlugin {
    var jeiHelpers: IJeiHelpers = _
}

@JEIPlugin
class NeoTechPlugin extends IModPlugin {

    override def register(registry: IModRegistry): Unit = {
        NeoTechPlugin.jeiHelpers = registry.getJeiHelpers

        //Crafter Shift Right Click
        registry.addRecipeCategories(
            new JEICrusherRecipeCategory,
            new JEISolidifierRecipeCategory,
            new JEICrucibleRecipeCategory,
            new JEIAlloyerRecipeCategory,
            new JEICentrifugeRecipeCategory)
        registry.addRecipeHandlers(
            new JEICrusherRecipeHandler,
            new JEISolidifierRecipeHandler,
            new JEICrucibleRecipeHandler,
            new JEIAlloyerRecipeHandler,
            new JEICentrifugeRecipeHandler)

        registry.addRecipes(JEIAlloyerRecipeWrapper.buildRecipeList())

        registry.addRecipes(JEICrusherRecipeMaker.getRecipes)
        registry.addRecipes(JEISolidifierRecipeMaker.getRecipes)
        registry.addRecipes(JEICrucibleRecipeMaker.getRecipes)
        registry.addRecipes(JEICentrifugeRecipeMaker.getRecipes)

        // Furnace
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockManager.electricFurnace), VanillaRecipeCategoryUid.SMELTING)

        // Furnace Generator
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockManager.furnaceGenerator), VanillaRecipeCategoryUid.FUEL)

        // Crusher
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockManager.electricCrusher), NeotechRecipeCategoryUID.CRUSHER)

        // Solidifier
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockManager.electricSolidifier), NeotechRecipeCategoryUID.SOLIDIFIER)

        // Crucible
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockManager.electricCrucible), NeotechRecipeCategoryUID.CRUCIBLE)

        // Alloyer
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockManager.electricAlloyer), NeotechRecipeCategoryUID.ALLOYER)

        // Centrifuge
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockManager.electricCentrifuge), NeotechRecipeCategoryUID.CENTRIFUGE)

        registry.addAdvancedGuiHandlers(new IAdvancedGuiHandler[GuiBase[_]] {
            override def getGuiContainerClass: Class[GuiBase[_]] = classOf[GuiBase[_]]
            override def getGuiExtraAreas(t: GuiBase[_]): util.List[Rectangle] = t.getCoveredAreas
            override def getIngredientUnderMouse(guiContainer: GuiBase[_], mouseX: Int, mouseY: Int): AnyRef = {
               null
            }
        })
    }

    override def onRuntimeAvailable(iJeiRuntime: IJeiRuntime): Unit = { }

    override def registerItemSubtypes(subtypeRegistry: ISubtypeRegistry): Unit = {}

    override def registerIngredients(registry: IModIngredientRegistration): Unit = {}
}
