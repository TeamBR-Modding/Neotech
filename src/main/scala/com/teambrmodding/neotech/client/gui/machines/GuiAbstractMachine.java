package com.teambrmodding.neotech.client.gui.machines;

import com.teambr.bookshelf.client.gui.GuiBase;
import com.teambr.bookshelf.client.gui.GuiColor;
import com.teambr.bookshelf.client.gui.component.BaseComponent;
import com.teambr.bookshelf.client.gui.component.control.GuiComponentButton;
import com.teambr.bookshelf.client.gui.component.control.GuiComponentSideSelector;
import com.teambr.bookshelf.client.gui.component.display.*;
import com.teambr.bookshelf.common.tiles.InventoryHandler;
import com.teambr.bookshelf.util.ClientUtils;
import com.teambrmodding.neotech.common.container.machines.ContainerAbstractMachine;
import com.teambrmodding.neotech.common.tiles.AbstractMachine;
import com.teambrmodding.neotech.common.tiles.traits.IUpgradeItem;
import com.teambrmodding.neotech.managers.ItemManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This file was created for NeoTech
 * <p>
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis - pauljoda
 * @since 2/16/2017
 */
public abstract class GuiAbstractMachine<C extends ContainerAbstractMachine> extends GuiBase<C> {
    protected AbstractMachine machine;
    protected EntityPlayer player;
    protected C inventory;

    // This object holds an instance of the previous inventory, calls to check for changes
    protected InventoryHandler lastInventory = new InventoryHandler() {
        @Override
        protected int getInitialSize() {
            return 6;
        }

        @Override
        protected boolean isItemValidForSlot(int index, ItemStack stack) {
            return true;
        }

        @Override
        public void setVariable(int id, double value) {

        }

        @Override
        public Double getVariable(int id) {
            return 0.0;
        }
    };

    /**
     * Main constructor for Guis
     *
     * @param inventory The container
     * @param width     The width of the gui
     * @param height    The height of the gui
     * @param title     The title of the gui
     * @param texture   The location of the background texture
     */
    public GuiAbstractMachine(C inventory, int width, int height, String title, ResourceLocation texture, AbstractMachine machine, EntityPlayer player) {
        super(inventory, width, height, title, texture);
        this.inventory = inventory;
        this.machine = machine;
        this.player = player;

        lastInventory.copyFrom(machine.upgradeInventory);
        addComponents();
    }

    /*******************************************************************************************************************
     * GuiBase                                                                                                         *
     *******************************************************************************************************************/

    /**
     * Adds the tabs to the right. Overwrite this if you want tabs on your GUI
     *
     * @param tabs List of tabs, put GuiTabs in
     */
    @Override
    protected void addRightTabs(GuiTabCollection tabs) {
        addRightTabsLocal(tabs, true);
    }

    /**
     * Local holding for adding tabs, allows upgrade tab to stay open
     * @param tabs The tabs object
     * @param redoUpgrades Regen upgrade tab
     */
    protected void addRightTabsLocal(GuiTabCollection tabs, boolean redoUpgrades) {
        if(machine != null) {
            // Create the upgrade tab
            if(redoUpgrades) {
                List<BaseComponent> motherBoardComponents = new ArrayList<>();

                // Add title
                motherBoardComponents.add(new GuiComponentText(this, 26, 6,
                        GuiColor.ORANGE + ClientUtils.translate("neotech.text.upgrade"), Color.DARK_GRAY));

                // Add tab
                GuiTab motherBoardTab = tabs.addTab(motherBoardComponents, 100, 65, 176, 0, new ItemStack(ItemManager.processorOctCore)).getTabs().get(0);

                // Fill with Slots
                int slotID = 0;
                int xStart = 25;
                int yStart = 20;
                for(int row = 0; row < 3; row++) {
                    for(int column = 0; column < 2; column++) {
                        int slotX = xStart + (row * 18);
                        int slotY = yStart + (column * 18);
                        motherBoardTab.addChild(new GuiComponentTabSlotHolder(this, slotX, slotY,
                                inventory.upgradeSlots.get(slotID), 170 + slotX, 2 + slotY,
                                201 + (row * 18), 27 + (column * 18), motherBoardTab));
                        slotID++;
                    }
                }
            }

            // Add Restone Tab
            if(machine != null && machine.hasUpgradeByID(IUpgradeItem.REDSTONE_CIRCUIT)) {
                List<BaseComponent> redstoneTabComponents = new ArrayList<>();

                // Label
                redstoneTabComponents.add(new GuiComponentText(this, 20, 7,
                        GuiColor.ORANGE + ClientUtils.translate("neotech.text.redstoneMode"), Color.DARK_GRAY));

                // Button Display Current Mode
                redstoneTabComponents.add(new GuiComponentButton(this, 25, 20, 176, 160,
                        50, 20, machine.getRedstoneModeName()) {
                    @Override
                    protected void doAction() {}

                    /**
                     * Called after base render, is already translated to guiLeft and guiTop, just move offset
                     *
                     * @param guiLeft
                     * @param guiTop
                     * @param mouseX
                     * @param mouseY
                     */
                    @Override
                    public void renderOverlay(int guiLeft, int guiTop, int mouseX, int mouseY) {
                        setLabel(machine.getRedstoneModeName());
                        super.renderOverlay(guiLeft, guiTop, mouseX, mouseY);
                    }
                });

                // Left Toggle
                redstoneTabComponents.add(new GuiComponentButton(this, 10, 20, 227, 65,
                        14, 22, null) {
                    @Override
                    protected void doAction() {
                        machine.moveRedstoneMode(-1);
                        machine.sendValueToServer(AbstractMachine.REDSTONE_FIELD_ID, machine.redstone);
                    }
                });

                // Right Toggle
                redstoneTabComponents.add(new GuiComponentButton(this, 76, 20, 242, 65,
                        14, 22, null) {
                    @Override
                    protected void doAction() {
                        machine.moveRedstoneMode(1);
                        machine.sendValueToServer(AbstractMachine.REDSTONE_FIELD_ID, machine.redstone);
                    }
                });

                // Add the tab
                tabs.addTab(redstoneTabComponents, 100, 50, 176, 24, new ItemStack(Items.REDSTONE));
            }

            // Add IO Configure
            if(machine != null && machine.shouldHandleIO() && machine.hasUpgradeByID(IUpgradeItem.NETWORK_CARD)) {
                List<BaseComponent> selectorTabComponents = new ArrayList<>();

                // Add Label
                selectorTabComponents.add(new GuiComponentText(this, 29, 6,
                        GuiColor.ORANGE + ClientUtils.translate("neotech.text.ioConfig"), Color.DARK_GRAY));

                // Side Selector
                selectorTabComponents.add(new GuiComponentSideSelector(this, 15, 20, 40,
                        machine.getWorld().getBlockState(machine.getPos()), machine, true, true) {
                    @Override
                    protected void onSideToggled(EnumFacing side, int modifier) {
                        machine.setVariable(AbstractMachine.IO_FIELD_ID, side.ordinal());
                        machine.sendValueToServer(AbstractMachine.IO_FIELD_ID, side.ordinal());
                        setBlockState(machine.getWorld().getBlockState(machine.getPos()));
                    }

                    @Nullable
                    @Override
                    protected Color getColorForMode(EnumFacing side) {
                        return machine.getModeForSide(side).getHighlightColor();
                    }
                });

                tabs.addTab(selectorTabComponents, 100, 100, 176, 48, new ItemStack(machine.getBlockType()));
            }
        }
    }

    /**
     * Add the tabs to the left. Overwrite this if you want tabs on your GUI
     *
     * @param tabs List of tabs, put GuiReverseTabs in
     */
    @Override
    protected void addLeftTabs(GuiTabCollection tabs) {
        List<BaseComponent> infoTabComponents = new ArrayList<>();
        infoTabComponents.add(new GuiComponentText(this, 10, 7,
                GuiColor.YELLOW + ClientUtils.translate("neotech.text.information"), Color.DARK_GRAY));
        infoTabComponents.add(new GuiComponentLongText(this, 10, 20, 100, 65,
                224, 0, machine.getDescription(), 50));
        tabs.addReverseTab(infoTabComponents, 120, 100, 200, 0, new ItemStack(machine.getBlockType()));
    }

    /**
     * Called to draw the background
     * <p>
     * Usually used to create the base on which to render things
     *
     * @param partialTicks partial ticks
     * @param mouseX       The mouse X
     * @param mouseY       The mouse Y
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        if(machine.hasChangedFromLast(lastInventory)) {
            GuiTab motherBoardTab = rightTabs.getTabs().get(0);
            rightTabs.getTabs().clear();
            rightTabs.getTabs().add(motherBoardTab);
            addRightTabsLocal(rightTabs, false);
            leftTabs.getTabs().clear();
            addLeftTabs(leftTabs);
        }
        lastInventory.copyFrom(machine.upgradeInventory);

        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }
}
