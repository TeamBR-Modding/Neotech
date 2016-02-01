package com.dyonovan.neotech.pipes.gui

import java.awt.Color
import javax.annotation.Nullable

import com.dyonovan.neotech.managers.BlockManager
import com.dyonovan.neotech.pipes.types.AdvancedPipe
import com.teambr.bookshelf.client.gui.GuiBase
import com.teambr.bookshelf.client.gui.component.BaseComponent
import com.teambr.bookshelf.client.gui.component.control.GuiComponentSideSelector
import com.teambr.bookshelf.client.gui.component.display.{GuiComponentText, GuiTabCollection}
import com.teambr.bookshelf.common.container.ContainerGeneric
import com.teambr.bookshelf.common.tiles.traits.Syncable
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

import scala.collection.mutable.ArrayBuffer

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/21/2016
  */
class GuiAdvancedPipeConnections(tileEntity : AdvancedPipe, tile : Syncable) extends GuiBase[ContainerGeneric](new ContainerGeneric, 150, 150, "Connections") {
    override def addComponents(): Unit = {
        components += new GuiComponentSideSelector(20, 20, 40, tileEntity.getWorld.getBlockState(tileEntity.getPos), tileEntity, true) {
            override def setToggleController(): Unit = {
                toggleableSidesController = new ToggleableSidesController {

                    override def onSideToggled(side: EnumFacing, modifier: Int): Unit = {
                        tileEntity.setVariable(AdvancedPipe.IO_FIELD_ID, side.ordinal())
                        tileEntity.sendValueToServer(AdvancedPipe.IO_FIELD_ID, side.ordinal())
                        setBlockState(tileEntity.getWorld.getBlockState(tileEntity.getPos))
                    }

                    @Nullable
                    override def getColorForMode(side: EnumFacing): Color = {
                        tileEntity.getColor(tileEntity.getModeForSide(side))
                    }
                }
            }
        }
    }

    override def addRightTabs(tabs : GuiTabCollection) = {
        val selectorTab = new ArrayBuffer[BaseComponent]
        selectorTab += new GuiComponentText("I/O Mode", 29, 6)
        selectorTab += new GuiComponentSideSelector(20, 20, 40, tileEntity.getWorld.getBlockState(tileEntity.getPos), tileEntity, true) {
            override def setToggleController(): Unit = {
                toggleableSidesController = new ToggleableSidesController {

                    override def onSideToggled(side: EnumFacing, modifier: Int): Unit = {
                        tileEntity.setVariable(AdvancedPipe.IO_FIELD_ID, side.ordinal())
                        tileEntity.sendValueToServer(AdvancedPipe.IO_FIELD_ID, side.ordinal())
                        setBlockState(tileEntity.getWorld.getBlockState(tileEntity.getPos))
                    }

                    @Nullable
                    override def getColorForMode(side: EnumFacing): Color = {
                        tileEntity.getColor(tileEntity.getModeForSide(side))
                    }
                }
            }
        }
        tabs.addTab(selectorTab.toList, 100, 100, new Color(150, 150, 150), new ItemStack(BlockManager.electricCrusher))
    }
}
