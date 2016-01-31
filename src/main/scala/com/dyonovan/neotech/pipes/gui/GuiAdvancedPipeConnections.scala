package com.dyonovan.neotech.pipes.gui

import java.awt.Color
import javax.annotation.Nullable
import com.dyonovan.neotech.pipes.types.AdvancedPipe
import com.teambr.bookshelf.client.gui.component.BaseComponent
import com.teambr.bookshelf.client.gui.component.control.{GuiComponentSideSelector, GuiComponentTexturedButton}
import com.teambr.bookshelf.client.gui.component.display.{GuiComponentText, GuiTabCollection}
import com.teambr.bookshelf.client.gui.{GuiBase, GuiColor}
import com.teambr.bookshelf.common.container.ContainerGeneric
import com.teambr.bookshelf.common.tiles.traits.Syncable
import net.minecraft.init.Blocks
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
class GuiAdvancedPipeConnections(tileEntity : AdvancedPipe, tile : Syncable) extends GuiBase[ContainerGeneric](new ContainerGeneric, 75, 150, "Connections") {
    override def addComponents(): Unit = {
        components += new GuiComponentText(" FIX FOR MINECRAFT ", -100, -10000000)

        for(dir <- EnumFacing.values()) {
            components += new GuiComponentText(dir.getName.toUpperCase, 7, 20 * dir.ordinal() + 20)
            components += new GuiComponentTexturedButton(50, 20 * dir.ordinal() + 15,
                tileEntity.getUVForMode(tileEntity.getModeForSide(dir))._1,
                tileEntity.getUVForMode(tileEntity.getModeForSide(dir))._2,
                16, 16, 20, 20) {
                override def doAction(): Unit = {
                    tileEntity.setVariable(AdvancedPipe.IO_FIELD_ID, dir.ordinal())
                    tileEntity.sendValueToServer(AdvancedPipe.IO_FIELD_ID, dir.ordinal())
                }

                override def render(i : Int, j : Int, x : Int, y : Int) = {
                    setUV(tileEntity.getUVForMode(tileEntity.getModeForSide(dir)))
                    super.render(i, j, x, y)
                }

                override def getDynamicToolTip(mouseX: Int, mouseY: Int): ArrayBuffer[String] = {
                    val tip = new ArrayBuffer[String]()
                    tip += GuiColor.YELLOW + dir.getName.toUpperCase + ": " + GuiColor.WHITE + tileEntity.getDisplayNameForIOMode(tileEntity.getModeForSide(dir))
                    tip
                }
            }
        }
    }

    override def addRightTabs(tabs : GuiTabCollection): Unit = {
        val selectorTab = new ArrayBuffer[BaseComponent]
        selectorTab += new GuiComponentSideSelectorTemp(20, 20, 40, tileEntity.getWorld.getBlockState(tileEntity.getPos), tileEntity, true) {
            override def setToggleController(): Unit = {
                toggleableSidesController = new ToggleableSidesController {

                    override def onSideToggled(side: EnumFacing, modifier: Int): Unit = {
                        tileEntity.setVariable(AdvancedPipe.IO_FIELD_ID, side.ordinal())
                        tileEntity.sendValueToServer(AdvancedPipe.IO_FIELD_ID, side.ordinal())

                    }

                    @Nullable
                    override def getColorForMode(side: EnumFacing): Color = {
                        tileEntity.getColor(tileEntity.getModeForSide(side))
                    }
                }
            }
        }
        tabs.addTab(selectorTab.toList, 100, 100, new Color(255, 255, 255), new ItemStack(Blocks.piston))
    }
}
