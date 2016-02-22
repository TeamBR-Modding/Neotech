package com.dyonovan.neotech.client

import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.managers.{BlockManager, ItemManager}
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
        registerItem(Item.getItemFromBlock(BlockManager.grinder))
        registerItem(Item.getItemFromBlock(BlockManager.electricFurnace))
        registerItem(Item.getItemFromBlock(BlockManager.electricCrusher))
        registerItem(Item.getItemFromBlock(BlockManager.furnaceGenerator))
        registerItem(Item.getItemFromBlock(BlockManager.fluidGenerator))
        registerItem(Item.getItemFromBlock(BlockManager.thermalBinder))
        registerItem(Item.getItemFromBlock(BlockManager.electricCrucible))
        registerItem(Item.getItemFromBlock(BlockManager.electricSolidifier))
        registerItem(Item.getItemFromBlock(BlockManager.electricAlloyer))
        registerItem(Item.getItemFromBlock(BlockManager.electricCentrifuge))
        registerItem(Item.getItemFromBlock(BlockManager.pump))
        registerItem(Item.getItemFromBlock(BlockManager.treeFarm))
        registerItem(Item.getItemFromBlock(BlockManager.mechanicalPipe))
        registerItem(Item.getItemFromBlock(BlockManager.solarPanelT1))
        registerItem(Item.getItemFromBlock(BlockManager.solarPanelT2))
        registerItem(Item.getItemFromBlock(BlockManager.solarPanelT3))
        registerItem(Item.getItemFromBlock(BlockManager.basicRFStorage))
        registerItem(Item.getItemFromBlock(BlockManager.advancedRFStorage))
        registerItem(Item.getItemFromBlock(BlockManager.eliteRFStorage))
        registerItem(Item.getItemFromBlock(BlockManager.creativeRFStorage))
        registerItem(Item.getItemFromBlock(BlockManager.ironTank))
        registerItem(Item.getItemFromBlock(BlockManager.goldTank))
        registerItem(Item.getItemFromBlock(BlockManager.diamondTank))
        registerItem(Item.getItemFromBlock(BlockManager.creativeTank))
        registerItem(Item.getItemFromBlock(BlockManager.voidTank))
        registerItem(Item.getItemFromBlock(BlockManager.blockCrafter))
        registerItem(Item.getItemFromBlock(BlockManager.blockMiniatureSun))
        registerItem(Item.getItemFromBlock(BlockManager.playerPlate))
        registerItem(Item.getItemFromBlock(BlockManager.chunkLoader))
        registerItem(Item.getItemFromBlock(BlockManager.flushableChest))
        registerItem(Item.getItemFromBlock(BlockManager.dimStorage))
        registerItem(Item.getItemFromBlock(BlockManager.redstoneClock))
        registerItem(Item.getItemFromBlock(BlockManager.mobStand))
        registerItem(Item.getItemFromBlock(BlockManager.blockAttractor))
        registerItem(ItemManager.upgradeMBFull)
        registerItem(ItemManager.upgradeMBEmpty)
        registerItem(ItemManager.upgradeHardDrive)
        registerItem(ItemManager.upgradeControl)
        registerItem(ItemManager.upgradeProcessor)
        registerItem(ItemManager.upgradeExpansion)
        registerItem(ItemManager.wrench)
        registerItem(ItemManager.trashBag)
        registerItem(ItemManager.spawnerMover)
        registerItem(ItemManager.mobGun)
        registerItem(ItemManager.mobNet)
        registerItem(ItemManager.electroMagnet)
        registerItem(ItemManager.basicRFBattery)
        registerItem(ItemManager.advancedRFBattery)
        registerItem(ItemManager.eliteRFBattery)

        registerPipesAndColored()
    }

    def registerItem(item: Item): Unit = {
        Minecraft.getMinecraft.getRenderItem.getItemModelMesher.register(item, 0,
            new ModelResourceLocation(item.getUnlocalizedName.substring(5), "inventory"))
    }

    def registerPipesAndColored() : Unit = {
        //Colored
        for(color <- EnumDyeColor.values()) {
            Minecraft.getMinecraft.getRenderItem.getItemModelMesher.register(Item.getItemFromBlock(BlockManager.pipeBasicStructure), color.getMetadata,
                new ModelResourceLocation(Reference.MOD_ID + ":pipeStructure_" + color.getName, "inventory"))
            Minecraft.getMinecraft.getRenderItem.getItemModelMesher.register(Item.getItemFromBlock(BlockManager.blockMiniatureStar), color.getMetadata,
                new ModelResourceLocation(Reference.MOD_ID + ":blockMiniatureStar_" + color.getName, "inventory"))
        }

        //Speed
        registerItem(Item.getItemFromBlock(BlockManager.pipeBasicSpeedStructure))
        registerItem(Item.getItemFromBlock(BlockManager.pipeAdvancedSpeedStructure))
        registerItem(Item.getItemFromBlock(BlockManager.pipeEliteSpeedStructure))

        //Item Stuff
        registerItem(Item.getItemFromBlock(BlockManager.pipeItemInterface))

        //Fluid Stuff
        registerItem(Item.getItemFromBlock(BlockManager.pipeFluidInterface))

        //Energy
        registerItem(Item.getItemFromBlock(BlockManager.pipeEnergyInterface))
    }
}
