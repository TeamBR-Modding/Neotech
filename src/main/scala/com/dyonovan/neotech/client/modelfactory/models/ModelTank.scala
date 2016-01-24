package com.dyonovan.neotech.client.modelfactory.models

import java.util

import com.dyonovan.neotech.collections.DummyState
import com.dyonovan.neotech.common.tiles.storage.TileTank
import com.dyonovan.neotech.managers.BlockManager
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model._
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.{IBakedModel, ModelRotation}
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util.{EnumFacing, EnumWorldBlockLayer}
import net.minecraftforge.client.MinecraftForgeClient
import net.minecraftforge.client.model.{ISmartBlockModel, ISmartItemModel}
import net.minecraftforge.fluids.{Fluid, FluidContainerRegistry, FluidStack}
import org.lwjgl.util.vector.Vector3f

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since August 17, 2015
  */
class ModelTank(baseModel: IBakedModel) extends ISmartItemModel {

    val faceBakery = new FaceBakery
    var fluidHeight: Float = 0.0F
    var renderFluid: Fluid = null

    override def handleItemState(stack: ItemStack): IBakedModel = {
        fluidHeight = 2.01F
        renderFluid = null
        if (stack.hasTagCompound) {
            val liquidTag = stack.getTagCompound.getString("FluidName")

            if (liquidTag != null) {
                val liquid = FluidStack.loadFluidStackFromNBT(stack.getTagCompound)
                if (liquid != null && liquid.getFluid != null) {
                    renderFluid = liquid.getFluid
                    if (stack.getItem == Item.getItemFromBlock(BlockManager.ironTank))
                        fluidHeight = Math.min((16 * liquid.amount) / (FluidContainerRegistry.BUCKET_VOLUME * 8), 13.99F)
                    else if (stack.getItem == Item.getItemFromBlock(BlockManager.goldTank))
                        fluidHeight = Math.min((16 * liquid.amount) / (FluidContainerRegistry.BUCKET_VOLUME * 16), 13.99F)
                    else if (stack.getItem == Item.getItemFromBlock(BlockManager.diamondTank))
                        fluidHeight = Math.min((16 * liquid.amount) / (FluidContainerRegistry.BUCKET_VOLUME * 64), 13.99F)
                    else if (stack.getItem == Item.getItemFromBlock(BlockManager.creativeTank))
                        fluidHeight = Math.min((16 * liquid.amount) / (FluidContainerRegistry.BUCKET_VOLUME * 8), 13.99F)
                    else
                        fluidHeight = 16
                }
            }
        }
        this
    }

    override def isBuiltInRenderer: Boolean = false

    override def getItemCameraTransforms: ItemCameraTransforms = {
        baseModel.getItemCameraTransforms
    }

    override def isAmbientOcclusion: Boolean = true

    override def getGeneralQuads: util.List[BakedQuad] = {
        val list: util.ArrayList[BakedQuad] = new util.ArrayList[BakedQuad]()
        val uv = new BlockFaceUV(Array(0.0F, 0.0F, 16.0F, 16.0F), 0)
        val face = new BlockPartFace(null, 0, "", uv)
        val modelRot = ModelRotation.X0_Y0
        val scale = true

        list.addAll(baseModel.getGeneralQuads)

        if(renderFluid != null) {
            list.add(faceBakery.makeBakedQuad(new Vector3f(2.01F, fluidHeight, 2.01F), new Vector3f(13.99F, fluidHeight, 13.99F), face, Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite(renderFluid.getStill.toString), EnumFacing.UP, modelRot, null, scale, true))
            list.add(faceBakery.makeBakedQuad(new Vector3f(2.01F, 0.001F, 2.01F), new Vector3f(13.999F, 0.001F, 13.999F), face, Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite(renderFluid.getStill.toString), EnumFacing.DOWN, modelRot, null, scale, true))

            list.add(faceBakery.makeBakedQuad(new Vector3f(2.01F, 2.01F, 2.01F), new Vector3f(13.99F, fluidHeight, 2.01F), face, Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite(renderFluid.getStill.toString), EnumFacing.NORTH, modelRot, null, scale, true))
            list.add(faceBakery.makeBakedQuad(new Vector3f(2.01F, 2.01F, 13.99F), new Vector3f(13.99F, fluidHeight, 13.99F), face, Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite(renderFluid.getStill.toString), EnumFacing.SOUTH, modelRot, null, scale, true))
            list.add(faceBakery.makeBakedQuad(new Vector3f(2.01F, 2.01F, 2.01F), new Vector3f(2.01F, fluidHeight, 13.99F), face, Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite(renderFluid.getStill.toString), EnumFacing.WEST, modelRot, null, scale, true))
            list.add(faceBakery.makeBakedQuad(new Vector3f(13.99F, 2.01F, 2.01F), new Vector3f(13.99F, fluidHeight, 13.99F), face, Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite(renderFluid.getStill.toString), EnumFacing.EAST, modelRot, null, scale, true))
        }
        list
    }

    override def isGui3d: Boolean = true

    override def getFaceQuads(face : EnumFacing): util.List[BakedQuad] = baseModel.getFaceQuads(face)

    override def getParticleTexture: TextureAtlasSprite = baseModel.getParticleTexture
}
