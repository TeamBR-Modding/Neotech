package com.dyonovan.neotech.utils

import java.text.NumberFormat
import java.util.Locale
import javax.annotation.Nullable

import com.dyonovan.neotech.lib.Reference
import com.teambr.bookshelf.helper.LogHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.util.{StatCollector, ResourceLocation}
import net.minecraftforge.fml.common.registry.GameData
import org.lwjgl.input.Keyboard

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/22/2016
  */
object ClientUtils {

    lazy val MODID    = Reference.MOD_ID             // Used to hold a reference locally
    lazy val RESOURCE = MODID.toLowerCase(Locale.US) // Easier access to resource path

    /**
      * Used to translate the text to a given language
      *
      * @param text The text to translate
      * @return The translated text
      */
    def translate(text : String) : String =
        StatCollector.translateToLocal(text)

    /**
      * Used to translate a number to a standard format based on Locale
      *
      * @param number Number to format
      * @return A formated number string, eg 1,000,000
      */
    def formatNumber(number : Double) : String =
        NumberFormat.getNumberInstance(Locale.forLanguageTag(Minecraft.getMinecraft.gameSettings.language)).format(number)

    /**
      * Prefixes the given string with a MODID:, this should be used when using resource location. Set doLowerCase to
      * true to have it formatted to lowercase
      *
      * @param standardResource The standard name, eg electricFurnace
      * @return The resource return, eg neotech:electricFurnace
      */
    def prefixResource(standardResource : String, doLowerCase : Boolean = false) : String =
        String.format("%s:%s", RESOURCE, if(doLowerCase) standardResource.toLowerCase(Locale.US) else standardResource)

    /**
      * Used to get the resource location for our mod without needing to put the ID
      *
      * @param standardName The name, eg items/wrench
      * @return The resource location for this string
      */
    def getResource(standardName : String): ResourceLocation =
        new ResourceLocation(RESOURCE, standardName)

    /**
      * Used to get a model resource for a given resource and type
      *
      * @param resource The resource, omit mod id (it will be added)
      * @param modelType Model type, inventory or normal
      * @return The model resource location for this input
      */
    def getModelResource(resource : String, modelType : String): ModelResourceLocation =
        new ModelResourceLocation(prefixResource(resource), modelType)

    /**
      * Used to get the item resource location
      *
      * @param item The item to get
      * @return The resource for that item, null if not found/registered
      */
    @Nullable
    def getItemLocation(item : Item) : ResourceLocation = {
        // Get the GameObject for the item
        val gameObject = GameData.getItemRegistry.getNameForObject(item)

        //The item is not registered, you should not do this!
        if(gameObject == null) {
            LogHelper.severe("Item %s is not registered silly", item.getUnlocalizedName)
            return null
        }
        gameObject
    }

    /**
      * Checks for CTRL key, Macs use Command so this will enable that key as well
      * @return True if CTRL is pressed
      */
    def isCtrlPressed : Boolean = {
        var standardControl = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)
        //Check for Macs
        if(!standardControl && Minecraft.isRunningOnMac) standardControl = Keyboard.isKeyDown(Keyboard.KEY_LMETA) || Keyboard.isKeyDown(Keyboard.KEY_RMETA)
        standardControl
    }

    /**
      * Checks for the Shift key pressed
      * @return True if pressed
      */
    def isShiftPressed : Boolean = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)
}
