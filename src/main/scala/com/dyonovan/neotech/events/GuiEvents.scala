package com.dyonovan.neotech.events

import com.dyonovan.neotech.pipes.tiles.item.ItemInterfacePipe
import gnu.trove.map.hash.THashMap
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.{GuiInventory, GuiContainer}
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.inventory.IInventory
import net.minecraft.util.{EnumFacing, ResourceLocation}
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import net.minecraftforge.items.IItemHandler

import scala.collection.JavaConversions._

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis "pauljoda"
  * @since 3/15/2016
  */
@SideOnly(Side.CLIENT)
object GuiEvents {

    lazy val loadedPipes = new THashMap[ItemInterfacePipe, EnumFacing]()
    lazy val overlay = new ResourceLocation("minecraft", "textures/gui/widgets.png")

    @SubscribeEvent
    def guiLoaded(event : GuiScreenEvent.InitGuiEvent): Unit = {
        loadedPipes.clear()
        event.gui match {
            case containerGui: GuiContainer if !containerGui.isInstanceOf[GuiInventory] =>
                val lookingAt = Minecraft.getMinecraft.objectMouseOver.getBlockPos
                if(lookingAt != null) {
                    Minecraft.getMinecraft.theWorld.getTileEntity(lookingAt) match {
                        case inventory : IInventory  =>
                            for(dir <- EnumFacing.values()) {
                                Minecraft.getMinecraft.theWorld.getTileEntity(lookingAt.offset(dir)) match {
                                    case pipe : ItemInterfacePipe if pipe.canConnectExtract(dir.getOpposite) =>
                                        loadedPipes.put(pipe, dir.getOpposite)
                                    case _ =>
                                }
                            }
                        case inventory : IItemHandler  =>
                            for(dir <- EnumFacing.values()) {
                                Minecraft.getMinecraft.theWorld.getTileEntity(lookingAt.offset(dir)) match {
                                    case pipe : ItemInterfacePipe if pipe.canConnectExtract(dir.getOpposite) =>
                                        loadedPipes.put(pipe, dir.getOpposite)
                                    case _ =>
                                }
                            }
                        case _ =>
                    }
                }
            case _ =>
        }
    }

    @SubscribeEvent
    def guiDraw(event : GuiScreenEvent.BackgroundDrawnEvent): Unit = {
        if(!loadedPipes.isEmpty) {
            event.gui match {
                case containerGui: GuiContainer =>
                    GlStateManager.pushMatrix()
                    GlStateManager.enableBlend()
                    Minecraft.getMinecraft.renderEngine.bindTexture(overlay)
                    GlStateManager.translate(containerGui.guiLeft, containerGui.guiTop, 1)
                    for (x <- loadedPipes.keySet()) {
                        val index = x.slotMap.get(loadedPipes.get(x))
                        if (index >= 0 && index < containerGui.inventorySlots.getInventory.size()) {
                            val displayX = containerGui.inventorySlots.getSlot(index).xDisplayPosition
                            val displayY = containerGui.inventorySlots.getSlot(index).yDisplayPosition
                            containerGui.drawTexturedModalRect(displayX - 3, displayY - 3, 1, 23, 22, 22)
                        }
                    }
                    GlStateManager.popMatrix()
                case _ =>
            }
        }
    }
}
