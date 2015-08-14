package com.dyonovan.neotech.registries;

import com.dyonovan.neotech.common.tiles.AbstractMachine;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.api.modsupport.ILightWeightPowerAcceptor;
import cyano.poweradvantage.api.modsupport.LightWeightPowerRegistry;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.InterfaceList;
import net.minecraftforge.fml.common.Optional.Method;

/**
 * This file was created for NeoTech
 * <p/>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Dyonovan
 * @since August 14, 2015
 */
@InterfaceList({
        @Interface(iface = "cyano.poweradvantage.api.ConduitType", modid = "poweradvantage", striprefs = true),
        @Interface(iface = "cyano.poweradvantage.api.modsupport.ILightWeightPowerAcceptor", modid = "poweradvantage", striprefs = true),
        @Interface(iface = "cyano.poweradvantage.api.modsupport.LightWeightPowerRegistry", modid = "poweradvantage", striprefs = true)})
public class PowerAdvantageRegistry {

    @Method(modid = "poweradvantage")
    public static void registerPA(Block block) {
        LightWeightPowerRegistry.registerLightWeightPowerAcceptor(block, new ILightWeightPowerAcceptor() {

            @Override
            public boolean canAcceptEnergyType(ConduitType powerType) {
                    return ConduitType.areSameType(powerType, "electricity");
            }
            @Override
            public float addEnergy(TileEntity tileEntity, float v, ConduitType conduitType) {
                AbstractMachine tile = (AbstractMachine) tileEntity;
                    return tile.receiveEnergy(null, Math.round(v), false);
            }
            @Override
            public float getEnergyDemand(TileEntity tileEntity, ConduitType conduitType) {
                AbstractMachine tile = (AbstractMachine) tileEntity;
                    return (tile.getMaxEnergyStored(null) - tile.getEnergyStored(null));
            }
        });
    }
}
