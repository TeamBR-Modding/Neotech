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

    var generateFluids = true

    var genTin = true
    var genCopper = true
    var genLead = true
    var copperMin = 40
    var copperMax = 70
    var copperSize = 12
    var copperPerChunk = 6
    var tinMin = 40
    var tinMax = 70
    var tinSize = 12
    var tinPerChunk = 6
    var leadMin = 20
    var leadMax = 30
    var leadSize = 10
    var leadPerChunk = 4
    var genSilver = true
    var silverMin = 0
    var silverMax = 20
    var silverSize = 8
    var silverPerChunk = 3
    var fertBlacklist: Array[String] = _
    var versionCheck = true
    var chunkLoaderMax = 3
    var totalRFEM = 25000


    def preInit(): Unit = {
        config.load()

        generateFluids  = config.getBoolean(Reference.CONFIG_WORLD, "generateFluids", true, "Generate Fluids")

        genCopper       = config.get(Reference.CONFIG_WORLD, "copperEnable", true, "Generate Copper").getBoolean
        copperMin       = config.get(Reference.CONFIG_WORLD, "copperMin", 40, "Copper Min Level").getInt
        copperMax       = config.get(Reference.CONFIG_WORLD, "copperMax", 70, "Copper Max Level").getInt
        copperSize      = config.get(Reference.CONFIG_WORLD, "copperVeinSize", 12, "Copper Vein Size").getInt
        copperPerChunk  = config.get(Reference.CONFIG_WORLD, "copperVeinsPerChunk", 6, "Copper Veins per Chunk").getInt

        genTin       = config.get(Reference.CONFIG_WORLD, "tinEnable", true, "Generate Tin").getBoolean
        tinMin       = config.get(Reference.CONFIG_WORLD, "tinMin", 20, "Tin Min Level").getInt
        tinMax       = config.get(Reference.CONFIG_WORLD, "tinMax", 50, "Tin Max Level").getInt
        tinSize      = config.get(Reference.CONFIG_WORLD, "tinVeinSize", 12, "Tin Vein Size").getInt
        tinPerChunk  = config.get(Reference.CONFIG_WORLD, "tinVeinsPerChunk", 6, "Tin Veins per Chunk").getInt

        genLead       = config.get(Reference.CONFIG_WORLD, "leadEnable", true, "Generate Lead").getBoolean
        leadMin       = config.get(Reference.CONFIG_WORLD, "leadMin", 20, "Lead Min Level").getInt
        leadMax       = config.get(Reference.CONFIG_WORLD, "leadMax", 50, "Lead Max Level").getInt
        leadSize      = config.get(Reference.CONFIG_WORLD, "leadVeinSize", 12, "Lead Vein Size").getInt
        leadPerChunk  = config.get(Reference.CONFIG_WORLD, "leadVeinsPerChunk", 6, "Lead Veins per Chunk").getInt

        genSilver       = config.get(Reference.CONFIG_WORLD, "silverEnable", true, "Generate Silver").getBoolean
        silverMin       = config.get(Reference.CONFIG_WORLD, "silverMin", 20, "Silver Min Level").getInt
        silverMax       = config.get(Reference.CONFIG_WORLD, "silverMax", 50, "Silver Max Level").getInt
        silverSize      = config.get(Reference.CONFIG_WORLD, "silverVeinSize", 12, "Silver Vein Size").getInt
        silverPerChunk  = config.get(Reference.CONFIG_WORLD, "silverVeinsPerChunk", 6, "Silver Veins per Chunk").getInt

        fertBlacklist= config.get(Reference.CONFIG_FERT_BLACKLIST, "Blocks to Blacklist from Being Fertilized",
                        Array(""),"Format MODID:BLOCKNAME 1 per Line").getStringList

        versionCheck = config.get(Reference.CONFIG_VERSION_CHECK, "Version Check", true).getBoolean()

        chunkLoaderMax = config.get(Reference.CONFIG_CHUNKLOADER, "chunkLoaderMax", 3,
                            "Max Chunks (squared) to keep loaded per Chunk Loader").getInt

        totalRFEM = config.get(Reference.CONFIG_ELECTROMAGNET, "EMrfTotal", 25000,
                            "Total RF ElectroMagnet Stores").getInt()

        config.save()

        //Check to make sure Registry Directory is made
        val path: File = new File(NeoTech.configFolderLocation + File.separator + "Registries")
        if (!path.exists) path.mkdirs
    }
}
