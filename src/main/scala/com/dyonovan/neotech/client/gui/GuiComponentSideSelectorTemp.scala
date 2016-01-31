package com.dyonovan.neotech.client.gui

import java.awt.Color
import javax.annotation.Nullable

import com.google.common.collect.Lists
import com.teambr.bookshelf.client.gui.component.BaseComponent
import com.teambr.bookshelf.client.gui.misc.{SidePicker, TrackballWrapper}
import com.teambr.bookshelf.util.{FakeBlockAccess, RenderUtils}
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.{GlStateManager, Tessellator}
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{EnumFacing, MathHelper}
import org.lwjgl.input.{Keyboard, Mouse}
import org.lwjgl.opengl.GL11

/**
  * This file was created for Bookshelf
  *
  * Bookshelf is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/31/2016
  *
  * This component will allow you to render a 3d version of the tile/block. It will give the user the ability to
  * see the block in the GUI and toggle sided interactions
  *
  * This class is based on the work by OpenModsLib https://github.com/OpenMods/OpenModsLib/blob/1.8.X/src/main/java/openmods/gui/component/GuiComponentSideSelector.java
  */
abstract class GuiComponentSideSelectorTemp(x : Int, y : Int, scale : Double, var blockState : IBlockState, tile : TileEntity, highlightSelectedSides : Boolean = true) extends
        BaseComponent(x, y) {

    val diameter = MathHelper.ceiling_double_int(scale * Math.sqrt(3))

    var toggleableSidesController : ToggleableSidesController = null

    var isInInitialPosition : Boolean = false

    val trackball = new TrackballWrapper(1, 40)

    var lastSideHovered : EnumFacing = null

    setToggleController()

    /**
      * You should set up a controller here. Without it, there will be no function
      */
    def setToggleController() : Unit

    /**
      * Called from constructor. Set up anything needed here
      */
    override def initialize(): Unit = {}

    /**
      * Called to render the component
      */
    override def render(guiLeft: Int, guiTop: Int, mouseX : Int, mouseY : Int): Unit = {
        if(!isInInitialPosition || Mouse.isButtonDown(2)) {
            val rve = Minecraft.getMinecraft.getRenderViewEntity
            trackball.setTransform(RenderUtils.createEntityRotateMatrix(rve))
            isInInitialPosition = true
        }

        val width  : Int = getWidth
        val height : Int = getHeight

        GL11.glPushMatrix()
        GL11.glTranslatef(xPos + width / 2, yPos + height / 2, diameter.toFloat)
        GL11.glScaled(scale, -scale, scale)
        trackball.update(mouseX - width, -(mouseY - height))
        if(tile != null)
            TileEntityRendererDispatcher.instance.renderTileEntityAt(tile, -0.5, -0.5, -0.5, 0.0F)
        if(blockState != null)
            drawBlock()

        val picker = new SidePicker(0.5)

        val selections = Lists.newArrayListWithCapacity[org.apache.commons.lang3.tuple.Pair[SidePicker.Side, Color]](6 + 1)
        val coord = picker.getNearestHit
        if(coord != null) selections.add(org.apache.commons.lang3.tuple.Pair.of(coord.side, toggleableSidesController.getColorForMode(coord.side.toEnumFacing)))

        if(highlightSelectedSides) {
            for(dir <- EnumFacing.values())
                selections.add(org.apache.commons.lang3.tuple.Pair.of(SidePicker.Side.fromEnumFacing(dir),
                    if(toggleableSidesController.getColorForMode(dir) != null)
                        toggleableSidesController.getColorForMode(dir)
                    else new Color(0, 0, 0, 0)))
        }

        if(selections != null) drawHighlights(selections)

        lastSideHovered = if(coord == null) null else coord.side.toEnumFacing

        GL11.glPopMatrix()
    }

    def drawBlock() : Unit = {
        val tessellator = Tessellator.getInstance()
        GlStateManager.pushMatrix()
        GlStateManager.translate(-0.5, -0.5, -0.5)
        GlStateManager.enableDepth()

        val wr = tessellator.getWorldRenderer
        wr.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK)

        val access = new FakeBlockAccess(blockState)

        val dispatcher = Minecraft.getMinecraft.getBlockRendererDispatcher
        val model = dispatcher.getModelFromBlockState(blockState, access, FakeBlockAccess.ORGIN)
        dispatcher.getBlockModelRenderer.renderModel(access, model, blockState, FakeBlockAccess.ORGIN, wr, false)
        wr.setTranslation(0.0D, 0.0D, 0.0D)
        tessellator.draw()
        GlStateManager.popMatrix()
    }

    def drawHighlights(selections : java.util.List[org.apache.commons.lang3.tuple.Pair[SidePicker.Side, Color]]): Unit = {
        GlStateManager.disableLighting()
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GlStateManager.enableBlend()
        GlStateManager.enableAlpha()
        GlStateManager.disableDepth()
        GlStateManager.disableTexture2D()

        GL11.glBegin(GL11.GL_QUADS)
        for(p <- 0 until selections.size()) {
            val pair = selections.get(p)
            if(pair.getRight != null)
                RenderUtils.setColor(pair.getRight)

            pair.getLeft match {
                case SidePicker.Side.XPos =>
                    GL11.glVertex3d(0.5, -0.5, -0.5)
                    GL11.glVertex3d(0.5, 0.5, -0.5)
                    GL11.glVertex3d(0.5, 0.5, 0.5)
                    GL11.glVertex3d(0.5, -0.5, 0.5)
                case SidePicker.Side.YPos =>
                    GL11.glVertex3d(-0.5, 0.5, -0.5)
                    GL11.glVertex3d(-0.5, 0.5, 0.5)
                    GL11.glVertex3d(0.5, 0.5, 0.5)
                    GL11.glVertex3d(0.5, 0.5, -0.5)
                case SidePicker.Side.ZPos =>
                    GL11.glVertex3d(-0.5, -0.5, 0.5)
                    GL11.glVertex3d(0.5, -0.5, 0.5)
                    GL11.glVertex3d(0.5, 0.5, 0.5)
                    GL11.glVertex3d(-0.5, 0.5, 0.5)
                case SidePicker.Side.XNeg =>
                    GL11.glVertex3d(-0.5, -0.5, -0.5)
                    GL11.glVertex3d(-0.5, -0.5, 0.5)
                    GL11.glVertex3d(-0.5, 0.5, 0.5)
                    GL11.glVertex3d(-0.5, 0.5, -0.5)
                case SidePicker.Side.YNeg =>
                    GL11.glVertex3d(-0.5, -0.5, -0.5)
                    GL11.glVertex3d(0.5, -0.5, -0.5)
                    GL11.glVertex3d(0.5, -0.5, 0.5)
                    GL11.glVertex3d(-0.5, -0.5, 0.5)
                case SidePicker.Side.ZNeg =>
                    GL11.glVertex3d(-0.5, -0.5, -0.5)
                    GL11.glVertex3d(-0.5, 0.5, -0.5)
                    GL11.glVertex3d(0.5, 0.5, -0.5)
                    GL11.glVertex3d(0.5, -0.5, -0.5)
                case _ =>
            }
        }

        GL11.glEnd()

        GlStateManager.disableBlend()
        GlStateManager.enableDepth()
        GlStateManager.enableTexture2D()
    }

    def setBlockState(newState : IBlockState) : Unit = blockState = newState

    /**
      * Called when the mouse is pressed
      *
      * @param x Mouse X Position
      * @param y Mouse Y Position
      * @param button Mouse Button
      */
    override def mouseDown(x: Int, y: Int, button: Int) {
        lastSideHovered = null
    }

    /**
      * Called when the mouse button is over the component and released
      *
      * @param x Mouse X Position
      * @param y Mouse Y Position
      * @param button Mouse Button
      */
    override def mouseUp(x: Int, y: Int, button: Int) {
        if(button == 0 && lastSideHovered != null)
            toggleableSidesController.onSideToggled(lastSideHovered, if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) 1 else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) 2 else 0)
    }

    /**
      * Called after base render, is already translated to guiLeft and guiTop, just move offset
      */
    override def renderOverlay(guiLeft: Int, guiTop: Int, mouseX : Int, mouseY : Int): Unit = {}

    /**
      * Used to find how wide this is
      *
      * @return How wide the component is
      */
    override def getWidth: Int = diameter

    /**
      * Used to find how tall this is
      *
      * @return How tall the component is
      */
    override def getHeight: Int = diameter

    /**
      * The class that holds the logic for the side changing. This will allow the modder to give a little more control
      * over the behavior of this selector and give to ability to have more than one state per side
      */
    abstract class ToggleableSidesController {

        /**
          * This is called any time the user presses a side to toggle the mode. You should change your variable
          * here to the next value as well as handle any changes to the Tile or whatever.
          *
          * @param side The side that was selected
          * @param modifier 0 : Normal Click (you should toggle to the next mode)
          *                 1 : Shift Click (you should set to default or disabled)
          *                 2 : Control Click (you should go backward)
          */
        def onSideToggled(side : EnumFacing, modifier : Int)

        /**
          * This is used to color the highlight. Use whatever mode you have for each side and get the color that should
          * be displayed.
          * As a general rule:
          *     NULL    : Disabled
          *     BLUE    : Input
          *     ORANGE  : OUTPUT
          *     GREEN   : BOTH
          *
          * However you are free to use your own implementation as you feel needed
          *
          * @param side The side that needs a color
          * @return The color that should be rendered, or null for no color
          */
        @Nullable
        def getColorForMode(side : EnumFacing) : Color
    }
}
