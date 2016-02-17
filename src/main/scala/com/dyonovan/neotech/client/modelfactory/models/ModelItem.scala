package com.dyonovan.neotech.client.modelfactory.models

import com.google.common.collect.ImmutableList
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ItemLayerModel

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/17/2016
  */
class ModelItem(resourceLocation: ResourceLocation) extends ItemLayerModel(ImmutableList.of[ResourceLocation](resourceLocation)) {}
