package com.dyonovan.neotech.api.jei.grinder

import java.awt.Color
import java.util.Collections

import mezz.jei.api.recipe.BlankRecipeWrapper
import net.minecraft.client.Minecraft
import net.minecraft.item.ItemStack
import net.minecraft.util.text.translation.I18n

/**
  * Created by Dyonovan on 1/16/2016.
  */
class JEIGrinderRecipe(in: ItemStack, out: ItemStack) extends BlankRecipeWrapper {

    var input: ItemStack = in
    var output: ItemStack = out

    override def getInputs: java.util.List[ItemStack] = Collections.singletonList(input)

    override def getOutputs: java.util.List[ItemStack] = Collections.singletonList(output)

    override def drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) = {
        val fr = minecraft.fontRendererObj
        val grinding = I18n.translateToLocal("neotech.grinder.grinding")
        val output = I18n.translateToLocal("neotech.grinder.output")

        fr.drawString(grinding, 33 - fr.getStringWidth(grinding) / 2, 4, Color.gray.getRGB)
        fr.drawString(output, 33 - fr.getStringWidth(output) / 2, 26, Color.gray.getRGB)
    }
}
