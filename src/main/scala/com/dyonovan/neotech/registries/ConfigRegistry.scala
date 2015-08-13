package com.dyonovan.neotech.registries

import java.io.File

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.lib.Reference
import net.minecraftforge.common.config.Configuration

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 12, 2015
 */
object ConfigRegistry {

    var config = new Configuration(new File(NeoTech.configFolderLocation + File.separator + "NeoTech.cfg"))

    var genTin = true
    var genCopper = true
    var copperMin = 40
    var copperMax = 70
    var copperSize = 12
    var copperPerChunk = 6
    var tinMin = 40
    var tinMax = 70
    var tinSize = 12
    var tinPerChunk = 6



    def preInit(): Unit = {
        config.load()

        genCopper       = config.get(Reference.CONFIG_ORE_GENERATION, "Generate Copper", true).getBoolean
        genTin       = config.get(Reference.CONFIG_ORE_GENERATION, "Generate Tin", true).getBoolean

        copperMin       = config.get(Reference.CONFIG_COPPER_GENERATION, "Copper Min Level", 40).getInt
        copperMax       = config.get(Reference.CONFIG_COPPER_GENERATION, "Copper Max Level", 70).getInt
        copperSize      = config.get(Reference.CONFIG_COPPER_GENERATION, "Copper Vein Size", 12).getInt
        copperPerChunk  = config.get(Reference.CONFIG_COPPER_GENERATION, "Copper Veins per Chunk", 6).getInt

        tinMin       = config.get(Reference.CONFIG_TIN_GENERATION, "Tin Min Level", 20).getInt
        tinMax       = config.get(Reference.CONFIG_TIN_GENERATION, "Tin Max Level", 50).getInt
        tinSize      = config.get(Reference.CONFIG_TIN_GENERATION, "Tin Vein Size", 12).getInt
        tinPerChunk  = config.get(Reference.CONFIG_TIN_GENERATION, "Tin Veins per Chunk", 6).getInt

        config.save()

        //Check to make sure Registry Directory is made
        val path: File = new File(NeoTech.configFolderLocation + File.separator + "Registries")
        if (!path.exists) path.mkdirs
    }
}
