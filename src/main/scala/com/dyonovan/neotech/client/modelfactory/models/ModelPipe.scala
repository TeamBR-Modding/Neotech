package com.dyonovan.neotech.client.modelfactory.models

import java.util
import javax.vecmath.Vector3f

import com.dyonovan.neotech.client.modelfactory.ModelFactory
import com.dyonovan.neotech.managers.BlockManager
import com.dyonovan.neotech.pipes.blocks.BlockPipe
import com.dyonovan.neotech.pipes.types.SimplePipe
import com.teambr.bookshelf.common.blocks.properties.TileAwareState
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model._
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.{ ModelRotation, IBakedModel }
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.client.model.{ ISmartBlockModel, ISmartItemModel }

/**
 * This file was created for NeoTech
 *
 * NeoTech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author Paul Davis pauljoda
 * @since August 14, 2015
 */
class ModelPipe extends ISmartBlockModel with ISmartItemModel {
    val faceBakery = new FaceBakery
    val uv = new BlockFaceUV(Array[Float](0.0F, 0.0F, 16.0F, 16.0F), 0)
    val face = new BlockPartFace(null, 0, "", uv)
    var blockPipe : BlockPipe = null
    var pipeTile : SimplePipe = null
    var min = 4.0F
    var max = 12.0F

    def this(block : BlockPipe, tile: SimplePipe) {
        this()
        blockPipe = block
        pipeTile = tile
    }


    override def getFaceQuads(facing : EnumFacing) : util.List[_] = new util.ArrayList[Nothing]()

    override def getGeneralQuads : util.List[_] = {
        val list = new util.ArrayList[BakedQuad]()

        for(dir <- EnumFacing.values()) {
            //Draw the center bit, front and back
            list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, max), face, getTexture, dir, ModelRotation.X0_Y0, null, true, true))
            list.add(faceBakery.makeBakedQuad(new Vector3f(max, max, max), new Vector3f(min, min, min), face, getTexture, dir.getOpposite, ModelRotation.X0_Y0, null, true, true))
        }

        if(pipeTile != null) { //Exists in world
            if (pipeTile.canConnect(EnumFacing.DOWN))
                drawPipeConnection(ModelRotation.X270_Y0, list)
            if (pipeTile.canConnect(EnumFacing.UP))
                drawPipeConnection(ModelRotation.X90_Y0, list)
            if (pipeTile.canConnect(EnumFacing.NORTH))
                drawPipeConnection(ModelRotation.X180_Y0, list)
            if (pipeTile.canConnect(EnumFacing.SOUTH))
                drawPipeConnection(ModelRotation.X0_Y0, list)
            if (pipeTile.canConnect(EnumFacing.EAST.getOpposite))
                drawPipeConnection(ModelRotation.X0_Y90, list)
            if (pipeTile.canConnect(EnumFacing.WEST.getOpposite))
                drawPipeConnection(ModelRotation.X0_Y270, list)
        } else { //Is an item
            drawPipeConnection(ModelRotation.X270_Y0, list)
            drawPipeConnection(ModelRotation.X90_Y0, list)
        }

        list
    }

    def drawPipeConnection(modelRot : ModelRotation, list : util.ArrayList[BakedQuad]) {
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, max, max), new Vector3f(max, max, 16.0F), face, getTexture, EnumFacing.UP, modelRot, null, true, true))
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, max), new Vector3f(max, min, 16.0F), face, getTexture, EnumFacing.DOWN, modelRot, null, true, true))
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, max), new Vector3f(min, max, 16.0F), face, getTexture, EnumFacing.WEST, modelRot, null, true, true))
        list.add(faceBakery.makeBakedQuad(new Vector3f(max, min, max), new Vector3f(max, max, 16.0F), face, getTexture, EnumFacing.EAST, modelRot, null, true, true))

        //Enable the back to be seen
        list.add(faceBakery.makeBakedQuad(new Vector3f(max, max, 16.0F), new Vector3f(min, max, max), face, getTexture, EnumFacing.DOWN, modelRot, null, true, true))
        list.add(faceBakery.makeBakedQuad(new Vector3f(max, min, 16.0F), new Vector3f(min, min, max), face, getTexture, EnumFacing.UP, modelRot, null, true, true))
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, max, 16.0F), new Vector3f(min, min, max), face, getTexture, EnumFacing.EAST, modelRot, null, true, true))
        list.add(faceBakery.makeBakedQuad(new Vector3f(max, max, 16.0F), new Vector3f(max, min, max), face, getTexture, EnumFacing.WEST, modelRot, null, true, true))

    }

    override def isAmbientOcclusion: Boolean = true

    override def isGui3d: Boolean = true

    override def isBuiltInRenderer: Boolean = false

    val MovedUp = new ItemTransformVec3f(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(-0.05F, 0.05F, -0.15F), new Vector3f(-0.5F, -0.5F, -0.5F))
    override def getItemCameraTransforms : ItemCameraTransforms = {
        new ItemCameraTransforms(MovedUp, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT)
    }

    override def getTexture : TextureAtlasSprite =  {
        blockPipe match {
            case BlockManager.pipeItemBasicSource =>
                ModelFactory.BASIC_ITEM_SOURCE
            case BlockManager.pipeItemBasicSink =>
                ModelFactory.BASIC_ITEM_SINK
            case _ =>
                ModelFactory.STRUCTURE_PIPE
        }
    }

    override def handleBlockState(state : IBlockState) : IBakedModel =  {
        state match {
            case tileAware : TileAwareState => new ModelPipe(state.getBlock.asInstanceOf[BlockPipe], tileAware.tile.asInstanceOf[SimplePipe])
            case _ =>  new ModelPipe

        }
    }

    override def handleItemState(stack : ItemStack) : IBakedModel = new ModelPipe(Block.getBlockFromItem(stack.getItem).asInstanceOf[BlockPipe], null)
}
