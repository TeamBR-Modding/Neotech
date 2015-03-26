package com.dyonovan.neotech.common.container.machine;

import com.dyonovan.neotech.common.container.BaseContainer;
import com.dyonovan.neotech.common.tileentity.machine.TileEntityCrafter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

public class ContainerCrafter extends BaseContainer {
    protected TileEntityCrafter crafter;

    public InventoryCrafting craftingGrid1;
    public InventoryCrafting craftingGrid2;

    public IInventory craftResult1 = new InventoryCraftResult();
    public IInventory craftResult2 = new InventoryCraftResult();

    public ContainerCrafter(InventoryPlayer playerInv, TileEntityCrafter tile) {
        this.crafter = tile;
        craftingGrid1 = new DummyCraftingInventory(tile, this);
        craftingGrid2 = new DummyCraftingInventory(tile, this, 10);

        this.addSlotToContainer(new SlotCrafting(playerInv.player, craftingGrid1, craftResult1, 0, 80, 31));
        this.addSlotToContainer(new SlotCrafting(playerInv.player, craftingGrid2, craftResult2, 1, 80, 59));

        addCraftingGrid(craftingGrid1, 0,  8, 27, 3, 3);
        addCraftingGrid(craftingGrid2, 0, 116, 27, 3, 3);

        bindPlayerInventory(playerInv, 8, 84);

        onCraftMatrixChanged(craftingGrid1);
        onCraftMatrixChanged(craftingGrid2);
    }

    public void onCraftMatrixChanged(IInventory inv) {
        if(inv == craftingGrid1)
            craftResult1.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(craftingGrid1, crafter.getWorld()));
        if(inv == craftingGrid2)
            craftResult2.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(craftingGrid2, crafter.getWorld()));
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slot) {
        return !(slot instanceof SlotCrafting);
    }


    public void addCraftingGrid(IInventory inventory, int startSlot, int x, int y, int width, int height) {
        int i = 0;
        for(int h = 0; h < height; h++) {
            for(int w = 0; w < width; w++) {
                this.addSlotToContainer(new Slot(inventory, startSlot + i++, x + (w * 18), y + (h * 18)));
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    public static class DummyCraftingInventory extends InventoryCrafting {

        protected final TileEntityCrafter tile;
        protected final int offset;
        protected final Container container;
        protected ItemStack[] stacks;

        public DummyCraftingInventory(TileEntityCrafter tile, Container container) {
            this(tile, container, 0);
        }

        public DummyCraftingInventory(TileEntityCrafter tile, Container container, int offset) {
            super(null, 3, 3);
            stacks = new ItemStack[9];
            this.tile = tile;
            this.offset = offset;
            this.container = container;
        }

        private void onCraftingChanged() {
            container.onCraftMatrixChanged(this);
        }

        @Override
        public int getSizeInventory() {
            return 9;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return slot >= getSizeInventory() ? null : tile.getStackInSlot(slot + offset);
        }

        @Override
        public ItemStack getStackInRowAndColumn(int row, int column) {
            if (row >= 0 && row < 3) {
                int k = row + column * 3;
                return getStackInSlot(k);
            } else
                return null;
        }

        @Override
        public ItemStack getStackInSlotOnClosing(int slot) {
            return tile.getStackInSlotOnClosing(slot + offset);
        }

        @Override
        public ItemStack decrStackSize(int slot, int size) {
            ItemStack stack = tile.decrStackSize(slot + offset, size);
            onCraftingChanged();
            return stack;
        }

        @Override
        public void setInventorySlotContents(int slot, ItemStack stack) {
            tile.setInventorySlotContents(slot + offset, stack);
            onCraftingChanged();
        }
    }
}
