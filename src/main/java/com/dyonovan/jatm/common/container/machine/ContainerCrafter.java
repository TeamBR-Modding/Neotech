package com.dyonovan.jatm.common.container.machine;

import com.dyonovan.jatm.common.tileentity.machine.TileEntityCrafter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.crafting.CraftingManager;

public class ContainerCrafter extends Container {
    protected TileEntityCrafter crafter;

    public InventoryCrafting craftingGrid1;
    public InventoryCrafting craftingGrid2;

    public ContainerCrafter(InventoryPlayer playerInv, TileEntityCrafter tile) {
        this.crafter = tile;
        craftingGrid1 = new InventoryCrafting(this, 3, 3);
        craftingGrid2 = new InventoryCrafting(this, 3, 3);

        this.addSlotToContainer(new SlotCrafting(playerInv.player, craftingGrid1, crafter, 9, 160, 31));
        this.addSlotToContainer(new SlotCrafting(playerInv.player, craftingGrid2, crafter, 18, 160, 60));

        addCraftingGrid(craftingGrid1,  0,  88, 27, 3, 3);
        addCraftingGrid(craftingGrid2, 10, 196, 27, 3, 3);

        addPlayerInventory(playerInv, 88, 84);

        onCraftMatrixChanged(craftingGrid1);
        onCraftMatrixChanged(craftingGrid2);
    }

    public void onCraftMatrixChanged(IInventory inv) {
        crafter.setInventorySlotContents(9,
                CraftingManager.getInstance().findMatchingRecipe(this.craftingGrid1, crafter.getWorld()));
        crafter.setInventorySlotContents(18,
                CraftingManager.getInstance().findMatchingRecipe(this.craftingGrid2, crafter.getWorld()));
        copyToTile();
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        copyToTile();
    }

    protected void copyToTile() {
        for (int i = 0; i < 9; i++) {
            crafter.setInventorySlotContents(i, craftingGrid1.getStackInSlot(i));
            crafter.setInventorySlotContents(i + 10, craftingGrid2.getStackInSlot(i));
        }
    }

    public void addPlayerInventory(InventoryPlayer inv, int x, int y) {
        for(int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(inv, j + (i + 1) * 9, x + j * 18, y + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(inv, i, 8 + i * 18, y + 58));
        }
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
}
