package com.dyonovan.neotech.client.modelfactory.models

import java.util

import com.dyonovan.neotech.common.blocks.{BlockConnectedTextures, ConnectedTexturesState}
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model._
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.{IBakedModel, ModelRotation}
import net.minecraft.item.ItemStack
import net.minecraft.util.{BlockPos, EnumFacing}
import net.minecraft.world.IBlockAccess
import net.minecraftforge.client.model.{TRSRTransformation, ISmartBlockModel, ISmartItemModel}
import org.lwjgl.util.vector.Vector3f

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis "pauljoda"
  * @since 2/26/2016
  */
class ModelConnectedTextures extends ISmartBlockModel with ISmartItemModel {
    lazy val faceBakery = new FaceBakery
    var block : BlockConnectedTextures = null
    var world : IBlockAccess = null
    var pos : BlockPos = null

    override def handleBlockState(state: IBlockState): IBakedModel = {
        state match {
            case connectedState : ConnectedTexturesState =>
                world = connectedState.world
                pos = connectedState.pos
                block = connectedState.block
                this
            case _ => this
        }
    }

    override def handleItemState(stack: ItemStack): IBakedModel = {
        block = Block.getBlockFromItem(stack.getItem).asInstanceOf[BlockConnectedTextures]
        this
    }

    override def getFaceQuads(facing : EnumFacing): util.List[BakedQuad] = {
        val bakedQuads = new util.ArrayList[BakedQuad]()
        if(world != null && world.getBlockState(pos.offset(facing)).getBlock != block)
            drawFace(block.getConnectionArrayForFace(world, pos, facing), bakedQuads, lookUpRotationForFace(facing), facing.getOpposite)
        else if(world == null)
            drawFace(Array[Boolean](false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false), bakedQuads, lookUpRotationForFace(facing), facing)

        bakedQuads
    }

    def drawFace(connections : Array[Boolean], list : java.util.List[BakedQuad], rot : ModelRotation, facing : EnumFacing) {
        val face : BlockPartFace = new BlockPartFace(null, 0, "", new BlockFaceUV(Array[Float](0.0F, 0.0F, 16.0F, 16.0F), 0))
        val scale : Boolean = true

        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.0F, 16.0F), new Vector3f(8.0F, 8.0F, 16.0F), face, block.getConnectedTextures.getTextureForCorner(6, connections), EnumFacing.SOUTH, rot, null, scale, true))
        list.add(faceBakery.makeBakedQuad(new Vector3f(8.0F, 0.0F, 16.0F), new Vector3f(16.0F, 8.0F, 16.0F), face, block.getConnectedTextures.getTextureForCorner(7, connections), EnumFacing.SOUTH, rot, null, scale, true))
        list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 8.0F, 16.0F), new Vector3f(8.0F, 16.0F, 16.0F), face, block.getConnectedTextures.getTextureForCorner(4, connections), EnumFacing.SOUTH, rot, null, scale, true))
        list.add(faceBakery.makeBakedQuad(new Vector3f(8.0F, 8.0F, 16.0F), new Vector3f(16.0F, 16.0F, 16.0F), face, block.getConnectedTextures.getTextureForCorner(5, connections), EnumFacing.SOUTH, rot, null, scale, true))

        if (block != null && block.isTranslucent && world != null) {
            list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.0F, 16.0F), new Vector3f(8.0F, 8.0F, 15.999F), face, block.getConnectedTextures.getTextureForCorner(6, connections), EnumFacing.NORTH, rot, null, scale, true))
            list.add(faceBakery.makeBakedQuad(new Vector3f(8.0F, 0.0F, 16.0F), new Vector3f(16.0F, 8.0F, 15.999F), face, block.getConnectedTextures.getTextureForCorner(7, connections), EnumFacing.NORTH, rot, null, scale, true))
            list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 8.0F, 16.0F), new Vector3f(8.0F, 16.0F, 15.999F), face, block.getConnectedTextures.getTextureForCorner(4, connections), EnumFacing.NORTH, rot, null, scale, true))
            list.add(faceBakery.makeBakedQuad(new Vector3f(8.0F, 8.0F, 16.0F), new Vector3f(16.0F, 16.0F, 15.999F), face, block.getConnectedTextures.getTextureForCorner(5, connections), EnumFacing.NORTH, rot, null, scale, true))
        }
    }

    def lookUpRotationForFace(face : EnumFacing) : ModelRotation = {
        face match {
            case EnumFacing.UP =>
                ModelRotation.X90_Y0
            case EnumFacing.DOWN =>
                ModelRotation.X270_Y0
            case EnumFacing.NORTH =>
                ModelRotation.X0_Y180
            case EnumFacing.EAST =>
                ModelRotation.X0_Y270
            case EnumFacing.SOUTH =>
                ModelRotation.X0_Y0
            case EnumFacing.WEST =>
                ModelRotation.X0_Y90
            case _ =>
                ModelRotation.X0_Y0
        }
    }

    override def getGeneralQuads: util.List[BakedQuad] = new util.ArrayList[BakedQuad]()
    override def getParticleTexture: TextureAtlasSprite = block.getConnectedTextures.corners
    override def isBuiltInRenderer: Boolean = false
    override def getItemCameraTransforms: ItemCameraTransforms = {
        val tRSRTransformation = TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
            new javax.vecmath.Vector3f(-0.325F, -0.2F, -0.5F),
            TRSRTransformation.quatFromYXZDegrees(new javax.vecmath.Vector3f(0F, 0.25F, 0F)),
            new javax.vecmath.Vector3f(0.375F, 0.375F, 0.375F),
            null))
        new ItemCameraTransforms( tRSRTransformation.toItemTransform, ItemTransformVec3f.DEFAULT,
            ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT)
    }
    override def isAmbientOcclusion: Boolean = true
    override def isGui3d: Boolean = true
}
