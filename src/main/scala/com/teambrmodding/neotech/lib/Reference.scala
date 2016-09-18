package com.teambrmodding.neotech.lib

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
    final val VERSION      = "3.3.0"//@VERSION@"
    final val DEPENDENCIES = "after:tconstruct;required-after:Forge@[12.18.1.2011,);after:JEI@[3.7.8.234,);required-after:bookshelfapi@[3.3.0,);after:IC2;after:Tesla" //required-after:mcmultipart@[1.0.7,)
    final val UPDATE_JSON  = "https://raw.githubusercontent.com/TeamBR-Modding/NeoTech/1.9/update.json"

    final val CONFIG_WORLD = "World Generation"
    final val CONFIG_FERT_BLACKLIST = "Fertilizer Blacklist"
    final val CONFIG_VERSION_CHECK = "Show if NeoTech is Outdated?"
    final var CONFIG_CHUNKLOADER = "ChunkLoader"
    final var CONFIG_ELECTROMAGNET = "ElectroMagnet"
    final var CONFIG_CLIENT = "Client Config Options"
}
