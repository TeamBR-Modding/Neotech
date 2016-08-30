package com.teambrmodding.neotech.collections

import com.teambrmodding.neotech.managers.ItemManager
import net.minecraft.item.ItemStack

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 24, 2015
 */
object UpgradeBoard {
    def getBoardFromStack(stack: ItemStack): UpgradeBoard = {
        if (stack.hasTagCompound && stack.getItem == ItemManager.upgradeMBFull) {
            val tag = stack.getTagCompound
            return new UpgradeBoard(tag.getInteger("HardDrive"), tag.getInteger("Processor"), tag.getBoolean("Control"),
                tag.getBoolean("Expansion"))
        }
        null
    }
}

class UpgradeBoard {

    var hardDrive = 0
    var processor = 0
    var control = false
    var expansion = false

    def this(hardDrive: Int, processor: Int, control: Boolean, expansion: Boolean) {
        this()
        this.hardDrive = hardDrive
        this.processor = processor
        this.control = control
        this.expansion = expansion
    }

    def getHardDriveCount: Int = {
        this.hardDrive
    }

    def setHardDriveCount(count: Int): Unit = {
        this.hardDrive = count
    }

    def hasControl: Boolean = {
        this.control
    }

    def setHasControl(value: Boolean): Unit = {
        this.control = value
    }

    def getProcessorCount: Int = {
        this.processor
    }

    def setProcessorCount(count: Int): Unit = {
        this.processor = count
    }

    def hasExpansion: Boolean = {
        this.expansion
    }

    def setHasExpansion(value: Boolean): Unit = {
        this.expansion = value
    }
}
