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
class ModelTank extends ISmartBlockModel with ISmartItemModel{

    var topIcon: TextureAtlasSprite = null
    val glass: TextureAtlasSprite = Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite("minecraft:blocks/glass")
    val faceBakery = new FaceBakery
    var fluidHeight: Float = 0.0F
    var renderFluid: Fluid = null
    var isItem: Boolean = _

    def this(renderHeight: Float, fluid: Fluid, icon: TextureAtlasSprite) {
        this()
        this.fluidHeight = renderHeight
        this.renderFluid = fluid
        this.topIcon = icon
        if (topIcon == null)
            topIcon = Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite("minecraft:blocks/iron_block")
        isItem = true
    }

    override def handleBlockState(s: IBlockState): IBakedModel = {
        s match {
            case state: DummyState =>
                if(state.blockAccess.getTileEntity(state.pos) != null) {
                    fluidHeight = state.blockAccess.getTileEntity(state.pos).asInstanceOf[TileTank].getFluidLevelScaled
                    renderFluid = state.blockAccess.getTileEntity(state.pos).asInstanceOf[TileTank].getCurrentFluid
                    topIcon = state.blockAccess.getTileEntity(state.pos).asInstanceOf[TileTank].getTierIcon
                }
                if (topIcon == null)
                    topIcon = Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite("minecraft:blocks/iron_block")
                isItem = false
                new ModelTank(fluidHeight, renderFluid, topIcon)
            case _ => null
        }
    }

    override def handleItemState(stack: ItemStack): IBakedModel = {
        var height = 0.01F
        var fluid: Fluid = null
        var top: TextureAtlasSprite = null
        if (stack.hasTagCompound) {
            val liquidTag = stack.getTagCompound.getString("FluidName")

            if (liquidTag != null) {
                val liquid = FluidStack.loadFluidStackFromNBT(stack.getTagCompound)
                if (liquid != null && liquid.getFluid != null) {
                    fluid = liquid.getFluid
                    if (stack.getItem == Item.getItemFromBlock(BlockManager.ironTank))
                        height = Math.min((16 * liquid.amount) / (FluidContainerRegistry.BUCKET_VOLUME * 8), 15.99F)
                    else if (stack.getItem == Item.getItemFromBlock(BlockManager.goldTank))
                        height = Math.min((16 * liquid.amount) / (FluidContainerRegistry.BUCKET_VOLUME * 16), 15.99F)
                    else if (stack.getItem == Item.getItemFromBlock(BlockManager.diamondTank))
                        height = Math.min((16 * liquid.amount) / (FluidContainerRegistry.BUCKET_VOLUME * 64), 15.99F)
                    else if (stack.getItem == Item.getItemFromBlock(BlockManager.creativeTank))
                        height = Math.min((16 * liquid.amount) / (FluidContainerRegistry.BUCKET_VOLUME * 8), 15.99F)
                    else
                        height = 16
                }
            }
        }

        if (stack.getItem == Item.getItemFromBlock(BlockManager.ironTank))
            top = Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite("minecraft:blocks/iron_block")
        else if (stack.getItem == Item.getItemFromBlock(BlockManager.goldTank))
            top = Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite("minecraft:blocks/gold_block")
        else if (stack.getItem == Item.getItemFromBlock(BlockManager.diamondTank))
            top = Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite("minecraft:blocks/diamond_block")
        else if (stack.getItem == Item.getItemFromBlock(BlockManager.creativeTank))
            top = Minecraft.getMinecraft.getTextureMapBlocks.getAtlasSprite("minecraft:blocks/emerald_block")

        new ModelTank(height, fluid, top)
    }

    override def isBuiltInRenderer: Boolean = false

    override def getItemCameraTransforms: ItemCameraTransforms = {
        new ItemCameraTransforms(MovedUp, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT)
    }

    //override def getTexture: TextureAtlasSprite = glass
    override def getParticleTexture: TextureAtlasSprite = glass

    override def isAmbientOcclusion: Boolean = false

    override def getGeneralQuads: util.List[_] = {
        val list: util.ArrayList[BakedQuad] = new util.ArrayList[BakedQuad]()
        val uv = new BlockFaceUV(Array(0.0F, 0.0F, 16.0F, 16.0F), 0)
        val face = new BlockPartFace(null, 0, "", uv)
        val modelRot = ModelRotation.X0_Y0
        val scale = true

        if(MinecraftForgeClient.getRenderLayer == EnumWorldBlockLayer.CUTOUT || isItem) {
            //Top and Bottom (Iron)
            list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(16.0F, 0.0F, 16.0F), face, topIcon, EnumFacing.DOWN, modelRot, null, scale, true))
            list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.01F, 0.0F), new Vector3f(16.0F, 0.01F, 16.0F), face, topIcon, EnumFacing.DOWN.getOpposite, modelRot, null, scale, true))
            list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 16.0F, 0.0F), new Vector3f(16.0F, 16.0F, 16.0F), face, topIcon, EnumFacing.UP, modelRot, null, scale, true))
            list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 15.99F, 0.0F), new Vector3f(16.0F, 15.99F, 16.0F), face, topIcon, EnumFacing.UP.getOpposite, modelRot, null, scale, true))

            //Sides (Glass)
            list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(16.0F, 16.0F, 0.0F), face, glass, EnumFacing.NORTH, modelRot, null, scale, true))
            list.add(faceBakery.makeBakedQuad(new Vector3f(16.0F, 16.0F, 0.01F), new Vector3f(0.0F, 0.0F, 0.01F), face, glass, EnumFacing.NORTH.getOpposite, modelRot, null, scale, true))
            list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.0F, 16.0F), new Vector3f(16.0F, 16.0F, 16.0F), face, glass, EnumFacing.SOUTH, modelRot, null, scale, true))
            list.add(faceBakery.makeBakedQuad(new Vector3f(16.0F, 16.0F, 15.99F), new Vector3f(0.0F, 0.0F, 15.99F), face, glass, EnumFacing.SOUTH.getOpposite, modelRot, null, scale, true))
            list.add(faceBakery.makeBakedQuad(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 16.0F, 16.0F), face, glass, EnumFacing.WEST, modelRot, null, scale, true))
            list.add(faceBakery.makeBakedQuad(new Vector3f(0.01F, 16.0F, 16.0F), new Vector3f(0.01F, 0.0F, 0.0F), face, glass, EnumFacing.WEST.getOpposite, modelRot, null, scale, true))
            list.add(faceBakery.makeBakedQuad(new Vector3f(16.0F, 0.0F, 0.0F), new Vector3f(16.0F, 16.0F, 16.0F), face, glass, EnumFacing.EAST, modelRot, null, scale, true))
            list.add(faceBakery.makeBakedQuad(new Vector3f(15.99F, 16.0F, 16.0F), new Vector3f(15.99F, 0.0F, 0.0F), face, glass, EnumFacing.EAST.getOpposite, modelRot, null, scale, true))

            //Render Fluid
            if (renderFluid != null && (MinecraftForgeClient.getRenderLayer == EnumWorldBlockLayer.TRANSLUCENT || isItem)) {
                list.add(faceBakery.makeBakedQuad(new Vector3f(0.01F, fluidHeight, 0.01F), new Vector3f(15.99F, fluidHeight, 15.99F), face, renderFluid.getIcon, EnumFacing.UP, modelRot, null, scale, true))
                list.add(faceBakery.makeBakedQuad(new Vector3f(0.01F, 0.001F, 0.01F), new Vector3f(15.999F, 0.001F, 15.999F), face, renderFluid.getIcon, EnumFacing.DOWN, modelRot, null, scale, true))

                list.add(faceBakery.makeBakedQuad(new Vector3f(0.01F, 0.01F, 0.01F), new Vector3f(15.99F, fluidHeight, 0.01F), face, renderFluid.getIcon, EnumFacing.NORTH, modelRot, null, scale, true))
                list.add(faceBakery.makeBakedQuad(new Vector3f(0.01F, 0.01F, 15.99F), new Vector3f(15.99F, fluidHeight, 15.99F), face, renderFluid.getIcon, EnumFacing.SOUTH, modelRot, null, scale, true))
                list.add(faceBakery.makeBakedQuad(new Vector3f(0.01F, 0.01F, 0.01F), new Vector3f(0.01F, fluidHeight, 15.99F), face, renderFluid.getIcon, EnumFacing.WEST, modelRot, null, scale, true))
                list.add(faceBakery.makeBakedQuad(new Vector3f(15.99F, 0.01F, 0.01F), new Vector3f(15.99F, fluidHeight, 15.99F), face, renderFluid.getIcon, EnumFacing.EAST, modelRot, null, scale, true))
            }
        }
        list
    }

    override def isGui3d: Boolean = true

    override def getFaceQuads(p_177551_1_ : EnumFacing): util.List[_] = new util.ArrayList[Nothing]()

    def MovedUp: ItemTransformVec3f = {
        new ItemTransformVec3f(new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(-0.05F, 0.05F, -0.15F), new Vector3f(-0.5F, -0.5F, -0.5F))
    }
}
