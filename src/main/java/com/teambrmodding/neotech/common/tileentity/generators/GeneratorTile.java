package com.teambrmodding.neotech.common.tileentity.generators;

import com.teambr.nucleus.common.tiles.InventorySided;
import com.teambrmodding.neotech.common.container.GeneratorContainer;
import com.teambrmodding.neotech.lib.Reference;
import com.teambrmodding.neotech.managers.TileEntityManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nullable;

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
public class GeneratorTile extends InventorySided implements INamedContainerProvider {

    public static final int BASE_ENERGY_TICK = 100;
    public static final int INPUT_SLOT       = 0;

    public static final int TANK             = 0;

    public GeneratorTile() {
        super(TileEntityManager.generator);
    }

    @Override
    public void setVariable(int id, double value) {

    }

    @Override
    public Double getVariable(int id) {
        return null;
    }

    @Override
    protected int getInventorySize() {
        return 1;
    }

    @Override
    protected boolean isItemValidForSlot(int index, ItemStack stack) {
        return false;
    }

    @Override
    public int[] getSlotsForFace(Direction face) {
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack itemStackIn, Direction dir) {
        return false;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent(Reference.MOD_ID + ".generator");
    }

    @Nullable
    @Override
    public Container createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new GeneratorContainer(windowID, playerInventory, this);
    }
}
