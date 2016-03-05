package com.dyonovan.neotech.client.gui

import com.dyonovan.neotech.client.{ClientTickHandler, KeybindHandler}
import com.dyonovan.neotech.network.{PacketDispatcher, UpdateToolTag}
import com.dyonovan.neotech.tools.ToolHelper
import com.google.common.collect.ImmutableSet
import com.teambr.bookshelf.helper.GuiHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.{GlStateManager, RenderHelper}
import net.minecraft.client.settings.KeyBinding
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
class GuiToggleMenu extends GuiScreen {

    var timeIn = 0
    var upgrades = ClientTickHandler.getUpgrades
    var selectedUpgrade = -1
    lazy val player = Minecraft.getMinecraft.thePlayer

    override def drawScreen(mx : Int, my : Int, partialTicks : Float): Unit = {
        super.drawScreen(mx, my, partialTicks)
        upgrades = ClientTickHandler.getUpgrades

        GlStateManager.pushMatrix()
        GlStateManager.disableTexture2D()

        val x = width  / 2
        val y = height / 2
        val maxRadius = 60

        val mouseIn = true
        val angle = mouseAngle(x, y, mx, my)
        val distance = mouseDistance(x, y, mx, my)

        val highlight = 5

        GlStateManager.enableBlend()
        GlStateManager.shadeModel(GL11.GL_SMOOTH)
        val segments = upgrades.length
        var totalDeg = 0F
        val degPer = 360F / segments.toFloat

        val stringPosition = new ArrayBuffer[Array[Int]]

        var wasSelected = false
        for(seg <- 0 until segments) {
            val mouseOverSection = distance <= maxRadius && (mouseIn && angle > totalDeg && angle < totalDeg + degPer)
            var radius = Math.max(0F, Math.min((timeIn + partialTicks - seg * 6F / segments) * 40F, maxRadius))

            GL11.glBegin(GL11.GL_TRIANGLE_FAN)
            var gs = 0.25F
            if(seg % 2 == 0)
                gs += 0.1F
            var r = if(upgrades(seg)._2) gs else 200
            var g = if(!upgrades(seg)._2) gs else 200
            val b = 0
            var a = 0.4F
            if(mouseOverSection) {
                selectedUpgrade = seg
                wasSelected = true
                r = if(upgrades(seg)._2) r else 255
                g = if(!upgrades(seg)._2) g else 255
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

        if(!wasSelected)
            selectedUpgrade = -1

        GlStateManager.shadeModel(GL11.GL_FLAT)
        GlStateManager.enableTexture2D()

        for(pos <- stringPosition) {
            val slot = upgrades.get(pos(0))
            val xp = pos(1)
            val yp = pos(2)
            val c = pos(3).toChar

            val displayStack = slot._5
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
                GlStateManager.translate(xdp - 10, ydp - 10, 2)
                GlStateManager.scale(1.25, 1.25, 1.25)
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
            val tuple = upgrades.get(selectedUpgrade)
            val active = !tuple._2

            val tagList = ToolHelper.getModifierTagList(tuple._5)
            if(tagList != null && tagList.tagCount() > 0) {
                for(x <- 0 until tagList.tagCount()) {
                    val tag = tagList.getCompoundTagAt(x)
                    if (tag.getString("ModifierID").equalsIgnoreCase(tuple._3)) {
                        tag.setBoolean("Active", active)
                        GuiHelper.playButtonSound

                        PacketDispatcher.net.sendToServer(new UpdateToolTag(
                            tuple._4,
                            tuple._5.getTagCompound))
                    }
                }
            }

            upgrades = ClientTickHandler.getUpgrades
        }
    }

    override def updateScreen(): Unit = {
        if(!isKeyDown(KeybindHandler.radialMenu)) {
            Minecraft.getMinecraft.displayGuiScreen(null)
        }

        val set = ImmutableSet.of(mc.gameSettings.keyBindForward, mc.gameSettings.keyBindLeft,
            mc.gameSettings.keyBindBack, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindSneak,
            mc.gameSettings.keyBindSprint, mc.gameSettings.keyBindJump)
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

    def mouseAngle(x : Int, y : Int, mx : Int, my : Int) : Float = {
        val baseVec = new Vector2f(1F, 0F)
        val mouseVec = new Vector2f(mx - x, my - y)
        val angle : Float =
            (Math.acos(Vector2f.dot(baseVec, mouseVec) / (baseVec.length() * mouseVec.length())) * (180F / Math.PI)).toFloat
        if(my < y) 360F - angle else angle
    }

    def mouseDistance(x : Int, y : Int, mx : Int, my : Int) : Int =
        Math.abs(Math.sqrt(((mx - x) * (mx - x)) + ((my - y) * (my - y)))).toInt


    override def doesGuiPauseGame = false
}
