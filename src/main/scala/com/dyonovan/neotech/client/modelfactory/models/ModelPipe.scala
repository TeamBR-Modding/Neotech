package com.dyonovan.neotech.client.modelfactory.models

import java.util
import javax.vecmath.Vector3f

import com.dyonovan.neotech.client.modelfactory.ModelFactory
import com.dyonovan.neotech.pipes.blocks.BlockPipe
import com.dyonovan.neotech.pipes.tiles.TilePipe
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
    var pipeTile : TilePipe = null
    var min = 5.0F
    var max = 11.0F
    val connections = Array(false, true, false, false, false, false)

    def this(block : BlockPipe, tile: TilePipe) {
        this()
        blockPipe = block
        pipeTile = tile
    }


    override def getFaceQuads(facing : EnumFacing) : util.List[_] = new util.ArrayList[Nothing]()

    override def getGeneralQuads : util.List[_] = {
        val list = new util.ArrayList[BakedQuad]()

       for(dir <- EnumFacing.values()) {
           list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, min), new Vector3f(max, max, max), face, getTexture, dir, ModelRotation.X0_Y0, null, true, true))

           if (connections(EnumFacing.DOWN.ordinal()))
               drawPipeConnection(ModelRotation.X270_Y0, list)
           if (connections(EnumFacing.UP.ordinal()))
               drawPipeConnection(ModelRotation.X90_Y0, list)
           if (connections(EnumFacing.NORTH.ordinal()))
               drawPipeConnection(ModelRotation.X180_Y0, list)
           if (connections(EnumFacing.SOUTH.ordinal()))
               drawPipeConnection(ModelRotation.X0_Y0, list)
           if (connections(EnumFacing.WEST.ordinal()))
               drawPipeConnection(ModelRotation.X0_Y90, list)
           if (connections(EnumFacing.EAST.ordinal()))
               drawPipeConnection(ModelRotation.X0_Y270, list)
       }

        list
    }

    def drawPipeConnection(modelRot : ModelRotation, list : util.ArrayList[BakedQuad]) {
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, max), new Vector3f(max, min, 16.0F), face, getTexture, EnumFacing.NORTH, modelRot, null, true, true))
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, max), new Vector3f(max, min, 16.0F), face, getTexture, EnumFacing.SOUTH, modelRot, null, true, true))
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, max), new Vector3f(max, min, 16.0F), face, getTexture, EnumFacing.UP, modelRot, null, true, true))
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, max), new Vector3f(max, min, 16.0F), face, getTexture, EnumFacing.DOWN, modelRot, null, true, true))
        list.add(faceBakery.makeBakedQuad(new Vector3f(min, min, max), new Vector3f(min, max, 16.0F), face, getTexture, EnumFacing.WEST, modelRot, null, true, true))
        list.add(faceBakery.makeBakedQuad(new Vector3f(max, min, max), new Vector3f(max, max, 16.0F), face, getTexture, EnumFacing.EAST, modelRot, null, true, true))
    }

    override def isAmbientOcclusion: Boolean = true

    override def isGui3d: Boolean = true

    override def isBuiltInRenderer: Boolean = false

    val MovedUp = new ItemTransformVec3f(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(-0.05F, 0.05F, -0.15F), new Vector3f(-0.5F, -0.5F, -0.5F))
    override def getItemCameraTransforms : ItemCameraTransforms = {
        new ItemCameraTransforms(MovedUp, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT)
    }

    override def getTexture : TextureAtlasSprite = ModelFactory.BASIC_ITEM_TEXTURE

    override def handleBlockState(state : IBlockState) : IBakedModel =  {
        state match {
            case tileAware : TileAwareState => new ModelPipe(state.getBlock.asInstanceOf[BlockPipe], tileAware.tile.asInstanceOf[TilePipe])
            case _ =>  new ModelPipe

        }
    }

    override def handleItemState(stack : ItemStack) : IBakedModel = new ModelPipe(Block.getBlockFromItem(stack.getItem).asInstanceOf[BlockPipe], null)
}
