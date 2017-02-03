package com.teambrmodding.neotech.client.mesh

import com.teambrmodding.neotech.lib.Reference
import com.teambrmodding.neotech.managers.BlockManager
import net.minecraft.block.Block
import net.minecraft.client.renderer.ItemMeshDefinition
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.{EnumDyeColor, ItemStack}
import net.minecraft.util.ResourceLocation

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis "pauljoda"
  * @since 3/6/2016
  */
object MeshDefinitions {

    class StarModelMesh extends ItemMeshDefinition {
        override def getModelLocation(stack: ItemStack): ModelResourceLocation = {
            new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID + ":blockMiniatureStar"),
                "attached_side=6," + "color=" + EnumDyeColor.byMetadata(stack.getItemDamage).getName)
        }
    }

    class SimpleItemMeshDefinition(modelName : String, variants : String) extends ItemMeshDefinition {
        override def getModelLocation(stack: ItemStack): ModelResourceLocation =
            new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID +  ":items/" + modelName), variants)
    }
}
