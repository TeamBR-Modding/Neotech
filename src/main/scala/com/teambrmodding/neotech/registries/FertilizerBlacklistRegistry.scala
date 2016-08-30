package com.teambrmodding.neotech.registries

import java.util

import net.minecraft.block.Block

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since 1/22/2016
  */
object FertilizerBlacklistRegistry {

    lazy val blackList = new util.ArrayList[Block]()

    def init(): Unit = {
        if (ConfigRegistry.fertBlacklist != null && ConfigRegistry.fertBlacklist.nonEmpty) {
            for (string: String <- ConfigRegistry.fertBlacklist.toList) {
                if (Block.getBlockFromName(string).isInstanceOf[Block]) {
                    addToBlacklist(Block.getBlockFromName(string))
                }
            }
        }
    }

    def addToBlacklist(block: Block) = if (!blackList.contains(block)) blackList.add(block)

    def removeFromBlacklist(block: Block) = if (blackList.contains(block)) blackList.remove(block)

    def isBlacklisted(block: Block): Boolean = {
        if (blackList.contains(block)) true else false
    }
}
