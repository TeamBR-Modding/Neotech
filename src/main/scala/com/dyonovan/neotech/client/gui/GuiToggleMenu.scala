package com.dyonovan.neotech.client.gui

import com.dyonovan.neotech.client.KeybindHandler
import com.dyonovan.neotech.network.{UpdateToolTag, PacketDispatcher}
import com.dyonovan.neotech.tools.ToolHelper
import com.dyonovan.neotech.utils.ClientUtils
import com.google.common.collect.ImmutableSet
import com.teambr.bookshelf.helper.GuiHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.{GlStateManager, RenderHelper}
import net.minecraft.client.settings.KeyBinding
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import org.lwjgl.input.{Keyboard, Mouse}
import org.lwjgl.opengl.GL11
import org.lwjgl.util.vector.Vector2f

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis "pauljoda"
  * @since 3/4/2016
  */
class GuiToggleMenu(stack : ItemStack) extends GuiScreen {

    var timeIn = 0
    var upgrades = getUpgrades
    var selectedUpgrade = -1

    override def drawScreen(mx : Int, my : Int, partialTicks : Float): Unit = {
        super.drawScreen(mx, my, partialTicks)

        GlStateManager.pushMatrix()
        GlStateManager.disableTexture2D()

        val x = width  / 2
        val y = height / 2
        val maxRadius = 60

        val mouseIn = true
        val angle = mouseAngle(x, y, mx, my)

        val highlight = 5

        GlStateManager.enableBlend()
        GlStateManager.shadeModel(GL11.GL_SMOOTH)
        val segments = upgrades.length
        var totalDeg = 0F
        val degPer = 360F / segments.toFloat

        val stringPosition = new ArrayBuffer[Array[Int]]

        for(seg <- 0 until segments) {
            val mouseOverSection = mouseIn && angle > totalDeg && angle < totalDeg + degPer
            var radius = Math.max(0F, Math.min((timeIn + partialTicks - seg * 6F / segments) * 40F, maxRadius))

            GL11.glBegin(GL11.GL_TRIANGLE_FAN)
            var gs = 0.25F
            if(seg % 2 == 0)
               gs += 0.1F
            var r = if(upgrades(seg)._3) gs else 200
            var g = if(!upgrades(seg)._3) gs else 200
            val b = 0
            var a = 0.4F
            if(mouseOverSection) {
                selectedUpgrade = seg
                r = if(upgrades(seg)._3) r else 255
                g = if(!upgrades(seg)._3) g else 255
                a = 0.5F
            }
            GlStateManager.color(r, g, b, a)
            GL11.glVertex2i(x, y)
            var i = degPer
            while(i >= 0) {
                val rad = ((i + totalDeg) / 180F * Math.PI).toFloat
                val xp = x + Math.cos(rad) * radius
                val yp = y + math.sin(rad) * radius
                if(i == (degPer / 2).toInt)
                    stringPosition += Array(seg, xp.toInt, yp.toInt, if(mouseOverSection) 'n' else 'r')
                GL11.glVertex2d(xp, yp)
                i -= 1
            }
            totalDeg += degPer

            GL11.glVertex2i(x, y)
            GL11.glEnd()

            if(mouseOverSection)
                radius -= highlight
        }

        GlStateManager.shadeModel(GL11.GL_FLAT)
        GlStateManager.enableTexture2D()

        for(pos <- stringPosition) {
            val slot = upgrades.get(pos(0))
            val xp = pos(1)
            val yp = pos(2)
            val c = pos(3).toChar

            val displayStack = slot._2
            if(displayStack != null) {
                var xsp = xp - 4
                var ysp = yp
                val name = "\u00a7" + c + slot._1
                val width = fontRendererObj.getStringWidth(name)

                val mod = 0.6
                val xdp = ((xp - x) * mod + x).toInt
                val ydp = ((yp - y) * mod + y).toInt

                RenderHelper.enableGUIStandardItemLighting()
                GlStateManager.pushMatrix()
                GlStateManager.translate(xdp - 15, ydp - 15, 2)
                GlStateManager.scale(2, 2, 2)
                Minecraft.getMinecraft.getRenderItem.renderItemIntoGUI(displayStack, 0, 0)
                GlStateManager.popMatrix()
                RenderHelper.disableStandardItemLighting()

                if(xsp < x)
                    xsp -= width - 8
                if(ysp < y)
                    ysp -= 9

                fontRendererObj.drawStringWithShadow(name, xsp, ysp, 0xFFFFFF)
            }
        }

        GlStateManager.popMatrix()
    }

    override def mouseClicked(mouseX : Int, mouseY : Int, mouseButton : Int) : Unit = {
        super.mouseClicked(mouseX, mouseY, mouseButton)

        if(selectedUpgrade != -1 && selectedUpgrade < upgrades.length) {
            val upgrade = upgrades.get(selectedUpgrade)
            val active = !upgrade._3
            val tagList = ToolHelper.getModifierTagList(stack)

            if(tagList != null && tagList.tagCount() > 0) {
                for(x <- 0 until tagList.tagCount()) {
                    val tag = tagList.getCompoundTagAt(x)
                    if(tag.getString("ModifierID").equalsIgnoreCase(upgrade._4)) {
                        tag.setBoolean("Active", active)
                        GuiHelper.playButtonSound
                    }
                }
            }

            upgrades = getUpgrades

            PacketDispatcher.net.sendToServer(new UpdateToolTag(Minecraft.getMinecraft.thePlayer.inventory.currentItem, stack.getTagCompound))
        }
    }

    override def updateScreen(): Unit = {
        if(!isKeyDown(KeybindHandler.radialMenu)) {
            Minecraft.getMinecraft.displayGuiScreen(null)
        }

        val set = ImmutableSet.of(mc.gameSettings.keyBindForward, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindSneak, mc.gameSettings.keyBindSprint, mc.gameSettings.keyBindJump)
        for(k <- set)
            KeyBinding.setKeyBindState(k.getKeyCode, isKeyDown(k))

        timeIn += 1
    }

    def isKeyDown(keybind : KeyBinding) : Boolean = {
        val key = keybind.getKeyCode
        if(key < 0) {
            val button = 100 + key
            return Mouse.isButtonDown(button)
        }
        Keyboard.isKeyDown(key)
    }

    def getUpgrades : ArrayBuffer[(String, ItemStack, Boolean, String)] = {
        val buffer = new ArrayBuffer[(String, ItemStack, Boolean, String)]()

        val tagList = ToolHelper.getModifierTagList(stack)
        if(tagList != null && tagList.tagCount() > 0) {
            for(x <- 0 until tagList.tagCount()) {
                val tag = tagList.getCompoundTagAt(x)
                if(tag.hasKey("Active")) {
                    val name = ClientUtils.translate(tag.getString("ModifierID"))
                    val stack = getStackForDisplay(tag.getString("ModifierID"))
                    val active = tag.getBoolean("Active")
                    val id = tag.getString("ModifierID")
                    val tuple = (name, stack, active, id)
                    buffer += tuple
                }
            }
        }

        buffer
    }

    def getStackForDisplay(id : String) : ItemStack = {
        id match {
            case "aoe"      => new ItemStack(Blocks.piston)
            case "lighting" => new ItemStack(Blocks.torch)
            case _          => new ItemStack(Blocks.air)
        }
    }

    def mouseAngle(x : Int, y : Int, mx : Int, my : Int) : Float = {
        val baseVec = new Vector2f(1F, 0F)
        val mouseVec = new Vector2f(mx - x, my - y)
        val angle : Float =
            (Math.acos(Vector2f.dot(baseVec, mouseVec) / (baseVec.length() * mouseVec.length())) * (180F / Math.PI)).toFloat
        if(my < y) 360F - angle else angle
    }

    override def doesGuiPauseGame = false
}
