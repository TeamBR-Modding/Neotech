package com.teambrmodding.neotech.managers;

import com.teambrmodding.neotech.common.item.DiskBase;
import com.teambrmodding.neotech.lib.Reference;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

/**
 * This file was created for AssistedProgression
 * <p>
 * AssistedProgression is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author James Rogers - Dyonovan
 * @since 9/3/2019
 */
@ObjectHolder(Reference.MOD_ID)
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemManager {

    // CreativeTab
    public static ItemGroup itemGroupNeotech;

    /*******************************************************************************************************************
     * BlockItems                                                                                                      *
     *******************************************************************************************************************/

    @ObjectHolder("machine")
    public static Item machine;

    /*******************************************************************************************************************
     * Items                                                                                                           *
     *******************************************************************************************************************/

    @ObjectHolder("disk_os")
    public static Item disk_os;



    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        // CreativeTab
        itemGroupNeotech = new ItemGroup(Reference.MOD_ID) {
            @Override
            public ItemStack createIcon() {
                return new ItemStack(Items.BARREL);
            }
        };

        // BlockItems
        registerBlockItemForBlock(event.getRegistry(), BlockManager.machine);

        // Items
        event.getRegistry().register(new DiskBase("disk_os"));
    }

    /*******************************************************************************************************************
     * Helpers                                                                                                         *
     *******************************************************************************************************************/

    /**
     * Registers the BlockItem variant for this block
     * @param registry The item registry
     * @param block The block to create
     */

    @SuppressWarnings("ConstantConditions")
    public static void registerBlockItemForBlock(IForgeRegistry<Item> registry, Block block) {
        Item itemBlock = new BlockItem(block, new Item.Properties().group(itemGroupNeotech));
        itemBlock.setRegistryName(block.getRegistryName());
        registry.register(itemBlock);
    }
}
