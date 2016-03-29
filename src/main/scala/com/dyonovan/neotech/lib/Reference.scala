package com.dyonovan.neotech.lib

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 07, 2015
 */
object Reference {
    final val MOD_NAME     = "NeoTech"
    final val MOD_ID       = "neotech"
    final val VERSION      = "3.0.3"//@VERSION@"
    final val DEPENDENCIES = "after:tconstruct;required-after:Forge@[11.16.0.1811,);required-after:bookshelfapi@[3.0.2,);" //required-after:mcmultipart@[1.0.7,)
    final val UPDATE_JSON  = "https://raw.githubusercontent.com/TeamBR-Modding/NeoTech/1.9/update.json"

    final val CONFIG_WORLD = "World Generation"
    final val CONFIG_FERT_BLACKLIST = "Fertilizer Blacklist"
    final val CONFIG_VERSION_CHECK = "Show if NeoTech is Outdated?"
    final var CONFIG_CHUNKLOADER = "ChunkLoader"
    final var CONFIG_ELECTROMAGNET = "ElectroMagnet"
    final var CONFIG_CLIENT = "Client Config Options"
}
