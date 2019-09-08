package com.teambrmodding.neotech.managers;

import com.teambrmodding.neotech.lib.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.TableLootEntry;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

/**
 * This file was created for AssistedProgression
 * <p>
 * AssistedProgression is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author James Rogers - Dyonovan
 * @since 9/8/2019
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class LootManager {

    @SubscribeEvent
    public void lootLoad(LootTableLoadEvent event) {
        if (event.getName().toString().equals("minecraft:chests/simple_dungeon")) {
            event.getTable().addPool(getInjectPool("simple_dungeon"));
        }
    }

    private static LootPool getInjectPool(String entryName) {
        return LootPool.builder()
                .addEntry(getInjectEntry(entryName, 1))
                .bonusRolls(0, 1)
                .build();
    }

    private static LootEntry.Builder getInjectEntry(String name, int weight) {
        ResourceLocation table = new ResourceLocation(Reference.MOD_ID, "inject/" + name);
        return TableLootEntry.builder(table).weight(weight);
    }
}
