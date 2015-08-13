package com.dyonovan.neotech.registries

import java.io.File

import com.dyonovan.neotech.NeoTech
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

    def preInit(): Unit = {
        config.load()

        config.save()

        //Check to make sure Registry Directory is made
        val path: File = new File(NeoTech.configFolderLocation + File.separator + "Registries")
        if (!path.exists) path.mkdirs
    }
}
