package com.dyonovan.neotech.registries

import java.io.File
import java.util

import com.dyonovan.neotech.NeoTech
import com.google.gson.reflect.TypeToken
import com.teambr.bookshelf.helper.LogHelper
import com.teambr.bookshelf.util.JsonUtils
import net.minecraftforge.fluids.FluidRegistry

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 21, 2015
 */
object FluidFuelValues {

    var values = new util.HashMap[String, Integer]()

    /**
     * Add the values
     */
    def init() {
        if (!loadFromFile)
            generateDefaults()
        else
            LogHelper.info("Fluid Fuel Values loaded successfully")
    }

    /**
     * Load the values from the file
     * @return True if successful
     */
    def loadFromFile(): Boolean = {
        LogHelper.info("Loading Fluid Fuel Values...")
        values = JsonUtils.readFromJson[util.LinkedHashMap[String, Integer]](new TypeToken[util.LinkedHashMap[String, Integer]]() {
        }, NeoTech.configFolderLocation + File.separator + "Registries" + File.separator + "fluidFuelValues.json")
        if (values == null) {
            values = new util.HashMap[String, Integer]()
        }
        !values.isEmpty
    }

    /**
     * Save the current registry to a file
     */
    def saveToFile(): Unit = {
        if (!values.isEmpty) {
            JsonUtils.writeToJson(values, NeoTech.configFolderLocation + File.separator + "Registries" + File
                    .separator + "fluidFuelValues.json")
        }
    }

    /**
     * Used to generate the default values
     */
    def generateDefaults(): Unit = {
        values.put(FluidRegistry.LAVA.getName, 6400)
        saveToFile()
    }

    /**
     * Add a value to a fluid
     * @param name The name of the fluid
     * @param value The new burn time
     */
    def addFluidFuel(name: String, value: Int) {
        values.put(name, value)
        saveToFile()
    }

    /**
     * Get the fluid fuel value
     * @param name The name of the fluid
     * @return The burn time
     */
    def getFluidFuelValue(name: String): Int = {
        if (values.containsKey(name)) {
            return values.get(name)
        }
        0
    }

    /**
     * Checks if we have something for this fluid
     * @param name The name of the fluid
     * @return True if we have something for it
     */
    def isFluidFuel(name: String): Boolean = {
        values.containsKey(name)
    }
}
