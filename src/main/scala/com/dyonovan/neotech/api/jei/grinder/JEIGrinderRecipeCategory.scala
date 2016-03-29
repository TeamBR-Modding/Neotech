package com.dyonovan.neotech.api.jei.grinder

import com.dyonovan.neotech.api.jei.NeoTechPlugin
import com.dyonovan.neotech.lib.Reference
import com.teambr.bookshelf.api.jei.drawables.SlotDrawable
import mezz.jei.api.gui.{IDrawable, IGuiItemStackGroup, IRecipeLayout}
import mezz.jei.api.recipe.{IRecipeCategory, IRecipeWrapper}
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.translation.I18n

/**
  * Created by Dyonovan on 1/16/2016.
  */
class JEIGrinderRecipeCategory extends IRecipeCategory {

    val location = new ResourceLocation(Reference.MOD_ID, "textures/gui/jei/jei.png")
    val inputSlot = new SlotDrawable(80, 0)
    val outputSlot1 = new SlotDrawable(62, 22)
    val outputSlot2 = new SlotDrawable(80, 22)
    val outputSlot3 = new SlotDrawable(98, 22)

    override def getBackground: IDrawable = NeoTechPlugin.jeiHelpers.getGuiHelper.createDrawable(location, 0, 0, 170, 60)

    override def setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: IRecipeWrapper): Unit = {

        val stacks: IGuiItemStackGroup = recipeLayout.getItemStacks
        stacks.init(0, true, 80, 0)
        stacks.init(1, false, 62, 22)

        recipeWrapper match {
            case grinderRecipeWrapper: JEIGrinderRecipe =>
                recipeLayout.getItemStacks.set(0, grinderRecipeWrapper.getInputs)
                recipeLayout.getItemStacks.set(1, grinderRecipeWrapper.getOutputs)
            case _ =>
        }
    }

    override def drawAnimations(minecraft: Minecraft): Unit = {}

    override def drawExtras(minecraft: Minecraft): Unit = {
        inputSlot.draw(minecraft)
        outputSlot1.draw(minecraft)
        outputSlot2.draw(minecraft)
        outputSlot3.draw(minecraft)
    }

    override def getTitle: String = I18n.translateToLocal("tile.neotech:grinder.name")

    override def getUid: String = Reference.MOD_ID + ":grinder"
}
