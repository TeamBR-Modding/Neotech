package com.dyonovan.neotech.managers

import com.dyonovan.neotech.lib.Reference
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.item.{EnumDyeColor, Item}

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
object ItemRenderManager {

    def registerItemRenderer(): Unit = {
        registerItem(ItemManager.dustGold)
        registerItem(ItemManager.dustIron)
        registerItem(ItemManager.dustCopper)
        registerItem(ItemManager.dustTin)
        registerItem(ItemManager.dustBronze)
        registerItem(ItemManager.ingotCopper)
        registerItem(ItemManager.ingotTin)
        registerItem(ItemManager.ingotBronze)
        registerItem(Item.getItemFromBlock(BlockManager.electricFurnace))
        registerItem(Item.getItemFromBlock(BlockManager.electricCrusher))
        registerItem(Item.getItemFromBlock(BlockManager.furnaceGenerator))
        registerItem(Item.getItemFromBlock(BlockManager.blockCopper))
        registerItem(Item.getItemFromBlock(BlockManager.blockTin))
        registerItem(Item.getItemFromBlock(BlockManager.oreCopper))
        registerItem(Item.getItemFromBlock(BlockManager.oreTin))
        registerItem(Item.getItemFromBlock(BlockManager.basicRFStorage))
        registerItem(Item.getItemFromBlock(BlockManager.advancedRFStorage))
        registerItem(Item.getItemFromBlock(BlockManager.eliteRFStorage))
        registerItem(Item.getItemFromBlock(BlockManager.creativeRFStorage))
        registerItem(Item.getItemFromBlock(BlockManager.blockCrafter))
        registerItem(ItemManager.upgradeMBFull)
        registerItem(ItemManager.upgradeMBEmpty)
        registerItem(ItemManager.upgradeCapacity)
        registerItem(ItemManager.upgradeControl)
        registerItem(ItemManager.upgradeEfficiency)
        registerItem(ItemManager.upgradeSecondary)

        registerPipes()
    }

    def registerItem(item: Item): Unit = {
        Minecraft.getMinecraft.getRenderItem.getItemModelMesher.register(item, 0,
            new ModelResourceLocation(item.getUnlocalizedName.substring(5), "inventory"))
    }

    def registerPipes() : Unit = {
        //Colored
        for(color <- EnumDyeColor.values()) {
            Minecraft.getMinecraft.getRenderItem.getItemModelMesher.register(Item.getItemFromBlock(BlockManager.pipeBasicStructure), color.getMetadata,
                new ModelResourceLocation(Reference.MOD_ID + ":pipeStructure_" + color.getName, "inventory"))
        }

        //Speed
        registerItem(Item.getItemFromBlock(BlockManager.pipeBasicSpeedStructure))

        //Item Stuff
        registerItem(Item.getItemFromBlock(BlockManager.pipeItemSource))
        registerItem(Item.getItemFromBlock(BlockManager.pipeItemSink))

        //Fluid Stuff
        registerItem(Item.getItemFromBlock(BlockManager.pipeFluidSource))
        registerItem(Item.getItemFromBlock(BlockManager.pipeFluidSink))

        //Energy
        registerItem(Item.getItemFromBlock(BlockManager.pipeEnergySource))
        registerItem(Item.getItemFromBlock(BlockManager.pipeEnergySink))
    }
}
