package com.teambrmodding.neotech.common.tiles.traits;

import com.teambrmodding.neotech.managers.CapabilityLoadManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis <pauljoda>
 * @since 9/6/2016
 */
public interface IUpgradeItem {

    // Enum for categories
    enum ENUM_UPGRADE_CATEGORY {
        CPU,
        MEMORY,
        HDD,
        PSU,
        MISC,
        NONE
    }

    // Basic Variables for IDS

    // CPUs, should be used mainly as an speed upgrade
    String CPU_SINGLE_CORE = "CPU_1";
    String CPU_DUAL_CORE   = "CPU_2";
    String CPU_QUAD_CORE   = "CPU_3";
    String CPU_OCT_CORE    = "CPU_4";

    // Memory, MOAR
    String MEMORY_DDR1 = "DDR1";
    String MEMORY_DDR2 = "DDR2";
    String MEMORY_DDR3 = "DDR3";
    String MEMORY_DDR4 = "DDR4";

    // Hard Disk, more storage etc
    String HDD_64G  = "HDD_64";
    String HDD_256G = "HDD_256";
    String HDD_512G = "HDD_512";
    String HDD_1T   = "HDD_1T";

    // PSU, hold more power
    String PSU_250W = "PSU_250";
    String PSU_500W = "PSU_500";
    String PSU_750W = "PSU_750";
    String PSU_960W = "PSU_960";

    // IC2
    String TRANSFORMER = "Transformer";

    // Misc
    String EXPANSION_CARD  = "Expansion_Card";
    String REDSTONE_CIRCUIT = "Redstone_Circuit";
    String NETWORK_CARD = "Network_Card";

    /**
     * Get the id of this upgrade item
     * @return A unique ID, should be a static variable somewhere
     */
    String getID();

    /**
     * Used to get what category this upgrade is, this allows for tiered upgrades, only use if tiered
     * @return Category based of standard set, use NONE if not needed
     */
    ENUM_UPGRADE_CATEGORY getCategory();

    /**
     * Specify the multiplier for this object. Used commonly with tiered objects
     * @param stack The stack this object is in, to access stack size etc.
     * @return The multiplier for this object, machines can use differently
     */
    int getMultiplier(ItemStack stack);

    // Storage implementations are required, tho there is some flexibility here.
    // If you are the API provider you can also say that in order to use the default storage
    // the consumer MUST use the default impl, to allow you to access innards.
    // This is just a contract you will have to stipulate in the documentation of your cap.
    class Storage implements Capability.IStorage<IUpgradeItem> {
        @Override
        public NBTBase writeNBT(Capability<IUpgradeItem> capability, IUpgradeItem instance, EnumFacing side) {
            return null;
        }

        @Override
        public void readNBT(Capability<IUpgradeItem> capability, IUpgradeItem instance, EnumFacing side, NBTBase nbt) {
        }
    }

    // You MUST also supply a default implementation.
    // This is to make life easier on consumers.
    class UpgradeItemDefaultImp implements IUpgradeItem, ICapabilityProvider {
        private IUpgradeItem host;

        public UpgradeItemDefaultImp(IUpgradeItem in) {
            host = in;
        }

        @Override
        public String getID() {
            return host.getID();
        }

        @Override
        public ENUM_UPGRADE_CATEGORY getCategory() {
            return host.getCategory();
        }

        @Override
        public int getMultiplier(ItemStack stack) {
            return host.getMultiplier(stack);
        }

        @Override
        public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY;
        }

        @Override
        public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
            if(capability != null && capability == CapabilityLoadManager.UPGRADE_ITEM_CAPABILITY)
                return (T) this;
            return null;
        }
    }
}
