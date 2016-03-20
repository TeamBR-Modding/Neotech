package com.dyonovan.neotech.universe.items

import java.util

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.managers.ItemManager
import com.dyonovan.neotech.universe.entities.{EntitySun, EnumSunType}
import com.dyonovan.neotech.utils.ClientUtils
import com.teambr.bookshelf.common.items.traits.ItemModelProvider
import com.teambr.bookshelf.loadables.CreatesTextures
import net.minecraft.entity.Entity
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.world.World

import scala.collection.mutable.ArrayBuffer

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis "pauljoda"
  * @since 3/14/2016
  */
class SunItem(name : String) extends Item with ItemModelProvider with CreatesTextures {

    setUnlocalizedName(Reference.MOD_ID + ":" + name)
    setCreativeTab(NeoTech.tabNeoTech)

    override def getTexturesToStitch: ArrayBuffer[String] =
        ArrayBuffer(
            ClientUtils.prefixResource("items/universe/inertSun"),
            ClientUtils.prefixResource("items/universe/blueDwarf"),
            ClientUtils.prefixResource("items/universe/smallSun"),
            ClientUtils.prefixResource("items/universe/largeSun"),
            ClientUtils.prefixResource("items/universe/redGiant"))

    /**
      * Creates a list of strings to register and render, ItemStack aware
      *
      * @return An ArrayBuffer of strings, order matters index == layer
      */
    override def getTextures(stack : ItemStack) : java.util.List[String] = {
        val list = new util.ArrayList[String]()
        stack.getItem match {
            case ItemManager.inertSun =>  list.add(ClientUtils.prefixResource("items/universe/inertSun"))
            case ItemManager.blueDwarf => list.add(ClientUtils.prefixResource("items/universe/blueDwarf"))
            case ItemManager.smallSun =>  list.add(ClientUtils.prefixResource("items/universe/smallSun"))
            case ItemManager.largeSun =>  list.add(ClientUtils.prefixResource("items/universe/largeSun"))
            case ItemManager.redGiant =>  list.add(ClientUtils.prefixResource("items/universe/redGiant"))
            case _ =>
        }
        list
    }

    /**
      * Determines if this Item has a special entity for when they are in the world.
      * Is called when a EntityItem is spawned in the world, if true and Item#createCustomEntity
      * returns non null, the EntityItem will be destroyed and the new Entity will be added to the world.
      *
      * @param stack The current item stack
      * @return True of the item has a custom entity, If true, Item#createCustomEntity will be called
      */
    override def hasCustomEntity(stack: ItemStack): Boolean = true

    /**
      * This function should return a new entity to replace the dropped item.
      * Returning null here will not kill the EntityItem and will leave it to function normally.
      * Called when the item it placed in a world.
      *
      * @param world     The world object
      * @param location  The EntityItem object, useful for getting the position of the entity
      * @param itemstack The current item stack
      * @return A new Entity object to spawn or null
      */
    override def createEntity(world: World, location: Entity, itemstack: ItemStack): Entity = {
        val sunType = {
            itemstack.getItem match {
                case ItemManager.inertSun => EnumSunType.INERT
                case ItemManager.blueDwarf => EnumSunType.BLUE_DWARF
                case ItemManager.smallSun => EnumSunType.SMALL_SUN
                case ItemManager.largeSun => EnumSunType.LARGE_SUN
                case ItemManager.redGiant => EnumSunType.RED_GIANT
                case _ => EnumSunType.INERT
            }
        }
        val sun = new EntitySun(world, sunType)
        sun.setPosition(location.posX, location.posY, location.posZ)
        sun
    }

    override def isTool: Boolean = false
}
