package com.dyonovan.neotech.api.jei.crusher

import java.util
import java.util.Collections

import mezz.jei.api.recipe.BlankRecipeWrapper
import net.minecraft.item.ItemStack

/**
  * Created by Dyonovan on 1/13/2016.
  */
class CrusherRecipeJEI(in: ItemStack, out: ItemStack, sec: ItemStack, per: Int) extends BlankRecipeWrapper {

    var input: ItemStack = in
    var output: ItemStack = out
    var secondary: ItemStack = sec
    var secPercent: Int = per

    override def getInputs: java.util.List[ItemStack] = Collections.singletonList(input)

    override def getOutputs: java.util.List[ItemStack] = {
        val outputs = new util.ArrayList[ItemStack]()
        outputs.add(output)
        outputs.add(secondary)
        outputs
    }
}
