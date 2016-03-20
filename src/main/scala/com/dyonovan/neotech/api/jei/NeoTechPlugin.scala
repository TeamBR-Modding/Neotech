package com.dyonovan.neotech.api.jei

import java.awt.Rectangle
import java.util

import com.dyonovan.neotech.api.jei.alloyer.{JEIAlloyerRecipeMaker, JEIAlloyerRecipeHandler, JEIAlloyerRecipeCategory}
import com.dyonovan.neotech.api.jei.centrifuge.{JEICentrifugeRecipeMaker, JEICentrifugeRecipeHandler, JEICentrifugeRecipeCategory}
import com.dyonovan.neotech.api.jei.crucible.{JEICrucibleRecipeMaker, JEICrucibleRecipeHandler, JEICrucibleRecipeCategory}
import com.dyonovan.neotech.api.jei.crusher.{JEICrusherRecipeCategory, JEICrusherRecipeHandler, JEICrusherRecipeMaker}
import com.dyonovan.neotech.api.jei.grinder.{JEIGrinderRecipeMaker, JEIGrinderRecipeHandler, JEIGrinderRecipeCategory}
import com.dyonovan.neotech.api.jei.solidifier.{JEISolidifierRecipeMaker, JEISolidifierRecipeHandler, JEISolidifierRecipeCategory}
import com.dyonovan.neotech.common.container.misc.ContainerCrafter
import com.dyonovan.neotech.managers.{BlockManager, ItemManager}
import com.teambr.bookshelf.client.gui.GuiBase
import mezz.jei.api._
import mezz.jei.api.gui.IAdvancedGuiHandler
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
        registry.getRecipeTransferRegistry.addRecipeTransferHandler(classOf[ContainerCrafter], VanillaRecipeCategoryUid.CRAFTING,  2, 9, 20, 36)
        registry.addRecipeCategories(
            new JEICrusherRecipeCategory,
            new JEIGrinderRecipeCategory,
            new JEISolidifierRecipeCategory,
            new JEICrucibleRecipeCategory,
            new JEIAlloyerRecipeCategory,
            new JEICentrifugeRecipeCategory)
        registry.addRecipeHandlers(
            new JEICrusherRecipeHandler,
            new JEIGrinderRecipeHandler,
            new JEISolidifierRecipeHandler,
            new JEICrucibleRecipeHandler,
            new JEIAlloyerRecipeHandler,
            new JEICentrifugeRecipeHandler)

        registry.addRecipes(JEICrusherRecipeMaker.getRecipes)
        registry.addRecipes(JEIGrinderRecipeMaker.getRecipes)
        registry.addRecipes(JEISolidifierRecipeMaker.getRecipes)
        registry.addRecipes(JEICrucibleRecipeMaker.getRecipes)
        registry.addRecipes(JEIAlloyerRecipeMaker.getRecipes)
        registry.addRecipes(JEICentrifugeRecipeMaker.getRecipes)

        registry.addAdvancedGuiHandlers(new IAdvancedGuiHandler[GuiBase[_]] {
            override def getGuiContainerClass: Class[GuiBase[_]] = classOf[GuiBase[_]]

            override def getGuiExtraAreas(t: GuiBase[_]): util.List[Rectangle] = t.getCoveredAreas
        })

        //Descriptions
        registry.addDescription(new ItemStack(BlockManager.grinder), "neotech.grinder.description")
        registry.addDescription(new ItemStack(BlockManager.flushableChest), "neotech.flushableChest.description")
        registry.addDescription(new ItemStack(BlockManager.playerPlate), "neotech.playerPlate.description")
        registry.addDescription(new ItemStack(ItemManager.trashBag), "neotech.trashBag.description")

        //Blacklists
        NeoTechPlugin.jeiHelpers.getItemBlacklist.addItemToBlacklist(new ItemStack(ItemManager.upgradeMBFull))
        NeoTechPlugin.jeiHelpers.getItemBlacklist.addItemToBlacklist(new ItemStack(BlockManager.mechanicalPipe))
    }

    override def onRuntimeAvailable(iJeiRuntime: IJeiRuntime): Unit = { }
}
