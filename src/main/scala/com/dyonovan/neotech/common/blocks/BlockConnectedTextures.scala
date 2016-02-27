package com.dyonovan.neotech.common.blocks

import com.dyonovan.neotech.client.{ConnectedTextureBlocks, TextureManager}
import com.dyonovan.neotech.collections.ConnectedTextures
import net.minecraft.block.Block
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.{BlockState, IBlockState}
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.util.{BlockPos, EnumFacing, EnumWorldBlockLayer}
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.property.{IUnlistedProperty, ExtendedBlockState}

import scala.collection.mutable.ArrayBuffer

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
trait BlockConnectedTextures extends Block with CreatesTextures {

    // Methods to move textures to lower class, handle others here
    def NoCornersTextureLocation   : String
    def AntiCornersTextureLocation : String
    def CornersTextureLocation     : String
    def HorizontalTextureLocation  : String
    def VerticalTextureLocation    : String

    /**
      * The name of this block, eg neotech:phantomGlass
      */
    def name : String = getUnlocalizedName

    /**
      * Define true if you are a clear texture
      *
      * @return
      */
    def isClear : Boolean

    def getNormal : ModelResourceLocation = new ModelResourceLocation(name.split("tile.")(1), "normal")
    def getInventory : ModelResourceLocation = new ModelResourceLocation(name.split("tile.")(1), "inventory")

    ConnectedTextureBlocks.blocks += this

    lazy val connectedTextures = new ConnectedTextures(TextureManager.getTexture(NoCornersTextureLocation),
        TextureManager.getTexture(AntiCornersTextureLocation), TextureManager.getTexture(CornersTextureLocation),
        TextureManager.getTexture(HorizontalTextureLocation), TextureManager.getTexture(VerticalTextureLocation))

    /**
      * Register the textures we need to create
      */
    for(texture <- getTexturesToStitch)
        TextureManager.registerTexture(texture)

    /**
      * Used to define the strings needed
      */
    def getTexturesToStitch: ArrayBuffer[String] = ArrayBuffer(NoCornersTextureLocation, AntiCornersTextureLocation,
        CornersTextureLocation, HorizontalTextureLocation, VerticalTextureLocation)

    /**
      * Used to get the connected textures object for this block
      *
      * @return
      */
    def getConnectedTextures : ConnectedTextures = if(connectedTextures != null) connectedTextures else {
        new ConnectedTextures(TextureManager.getTexture(NoCornersTextureLocation),
            TextureManager.getTexture(AntiCornersTextureLocation), TextureManager.getTexture(CornersTextureLocation),
            TextureManager.getTexture(HorizontalTextureLocation), TextureManager.getTexture(VerticalTextureLocation))
    }

    /**
      * Used to check if we are able to connect textures with the block
      *
      * @param block The block to check
      * @return True if can connect
      */
    def canTextureConnect(block : Block) : Boolean = block == this

    /**
      * Kinds long, but the way to get the connection array for the face
      */
    def getConnectionArrayForFace(world: IBlockAccess, pos: BlockPos, facing: EnumFacing): Array[Boolean] = {
        val connections = new Array[Boolean](16)
        if (world.isAirBlock(pos.offset(facing)) || (!world.getBlockState(pos.offset(facing)).getBlock.isOpaqueCube &&
                !canTextureConnect(world.getBlockState(pos.offset(facing)).getBlock))) {
            facing match {
                case EnumFacing.UP =>
                    connections(0) = canTextureConnect(world.getBlockState(pos.add(-1, 0, -1)).getBlock)
                    connections(1) = canTextureConnect(world.getBlockState(pos.add(0, 0, -1)).getBlock)
                    connections(2) = canTextureConnect(world.getBlockState(pos.add(1, 0, -1)).getBlock)
                    connections(3) = canTextureConnect(world.getBlockState(pos.add(-1, 0, 0)).getBlock)
                    connections(4) = canTextureConnect(world.getBlockState(pos.add(1, 0, 0)).getBlock)
                    connections(5) = canTextureConnect(world.getBlockState(pos.add(-1, 0, 1)).getBlock)
                    connections(6) = canTextureConnect(world.getBlockState(pos.add(0, 0, 1)).getBlock)
                    connections(7) = canTextureConnect(world.getBlockState(pos.add(1, 0, 1)).getBlock)
                    connections(8) = !world.isAirBlock(pos.add(-1, 1, -1)) && world.getBlockState(pos.add(-1, 1, -1)).getBlock.isOpaqueCube
                    connections(9) = !world.isAirBlock(pos.add(0, 1, -1)) && world.getBlockState(pos.add(0, 1, -1)).getBlock.isOpaqueCube
                    connections(10) = !world.isAirBlock(pos.add(1, 1, -1)) && world.getBlockState(pos.add(1, 1, -1)).getBlock.isOpaqueCube
                    connections(11) = !world.isAirBlock(pos.add(-1, 1, 0)) && world.getBlockState(pos.add(-1, 1, 0)).getBlock.isOpaqueCube
                    connections(12) = !world.isAirBlock(pos.add(1, 1, 0)) && world.getBlockState(pos.add(1, 1, 0)).getBlock.isOpaqueCube
                    connections(13) = !world.isAirBlock(pos.add(-1, 1, 1)) && world.getBlockState(pos.add(-1, 1, 1)).getBlock.isOpaqueCube
                    connections(14) = !world.isAirBlock(pos.add(0, 1, 1)) && world.getBlockState(pos.add(0, 1, 1)).getBlock.isOpaqueCube
                    connections(15) = !world.isAirBlock(pos.add(1, 1, 1)) && world.getBlockState(pos.add(1, 1, 1)).getBlock.isOpaqueCube
                    return connections
                case EnumFacing.DOWN =>
                    connections(0) = canTextureConnect(world.getBlockState(pos.add(-1, 0, 1)).getBlock)
                    connections(1) = canTextureConnect(world.getBlockState(pos.add(0, 0, 1)).getBlock)
                    connections(2) = canTextureConnect(world.getBlockState(pos.add(1, 0, 1)).getBlock)
                    connections(3) = canTextureConnect(world.getBlockState(pos.add(-1, 0, 0)).getBlock)
                    connections(4) = canTextureConnect(world.getBlockState(pos.add(1, 0, 0)).getBlock)
                    connections(5) = canTextureConnect(world.getBlockState(pos.add(-1, 0, -1)).getBlock)
                    connections(6) = canTextureConnect(world.getBlockState(pos.add(0, 0, -1)).getBlock)
                    connections(7) = canTextureConnect(world.getBlockState(pos.add(1, 0, -1)).getBlock)
                    connections(8) = !world.isAirBlock(pos.add(-1, -1, 1)) && world.getBlockState(pos.add(-1, -1, 1)).getBlock.isOpaqueCube
                    connections(9) = !world.isAirBlock(pos.add(0, -1, 1)) && world.getBlockState(pos.add(0, -1, 1)).getBlock.isOpaqueCube
                    connections(10) = !world.isAirBlock(pos.add(1, -1, 1)) && world.getBlockState(pos.add(1, -1, 1)).getBlock.isOpaqueCube
                    connections(11) = !world.isAirBlock(pos.add(-1, -1, 0)) && world.getBlockState(pos.add(-1, -1, 0)).getBlock.isOpaqueCube
                    connections(12) = !world.isAirBlock(pos.add(1, -1, 0)) && world.getBlockState(pos.add(1, -1, 0)).getBlock.isOpaqueCube
                    connections(13) = !world.isAirBlock(pos.add(-1, -1, -1)) && world.getBlockState(pos.add(-1, -1, -1)).getBlock.isOpaqueCube
                    connections(14) = !world.isAirBlock(pos.add(0, -1, -1)) && world.getBlockState(pos.add(0, -1, -1)).getBlock.isOpaqueCube
                    connections(15) = !world.isAirBlock(pos.add(1, -1, -1)) && world.getBlockState(pos.add(1, -1, -1)).getBlock.isOpaqueCube
                    return connections
                case EnumFacing.NORTH =>
                    connections(0) = canTextureConnect(world.getBlockState(pos.add(1, 1, 0)).getBlock)
                    connections(1) = canTextureConnect(world.getBlockState(pos.add(0, 1, 0)).getBlock)
                    connections(2) = canTextureConnect(world.getBlockState(pos.add(-1, 1, 0)).getBlock)
                    connections(3) = canTextureConnect(world.getBlockState(pos.add(1, 0, 0)).getBlock)
                    connections(4) = canTextureConnect(world.getBlockState(pos.add(-1, 0, 0)).getBlock)
                    connections(5) = canTextureConnect(world.getBlockState(pos.add(1, -1, 0)).getBlock)
                    connections(6) = canTextureConnect(world.getBlockState(pos.add(0, -1, 0)).getBlock)
                    connections(7) = canTextureConnect(world.getBlockState(pos.add(-1, -1, 0)).getBlock)
                    connections(8) = !world.isAirBlock(pos.add(1, 1, -1)) && world.getBlockState(pos.add(1, 1, -1)).getBlock.isOpaqueCube
                    connections(9) = !world.isAirBlock(pos.add(0, 1, -1)) && world.getBlockState(pos.add(0, 1, -1)).getBlock.isOpaqueCube
                    connections(10) = !world.isAirBlock(pos.add(-1, 1, -1)) && world.getBlockState(pos.add(-1, 1, -1)).getBlock.isOpaqueCube
                    connections(11) = !world.isAirBlock(pos.add(1, 0, -1)) && world.getBlockState(pos.add(1, 0, -1)).getBlock.isOpaqueCube
                    connections(12) = !world.isAirBlock(pos.add(-1, 0, -1)) && world.getBlockState(pos.add(-1, 0, -1)).getBlock.isOpaqueCube
                    connections(13) = !world.isAirBlock(pos.add(1, -1, -1)) && world.getBlockState(pos.add(1, -1, -1)).getBlock.isOpaqueCube
                    connections(14) = !world.isAirBlock(pos.add(0, -1, -1)) && world.getBlockState(pos.add(0, -1, -1)).getBlock.isOpaqueCube
                    connections(15) = !world.isAirBlock(pos.add(-1, -1, -1)) && world.getBlockState(pos.add(-1, -1, -1)).getBlock.isOpaqueCube
                    return connections
                case EnumFacing.SOUTH =>
                    connections(0) = canTextureConnect(world.getBlockState(pos.add(-1, 1, 0)).getBlock)
                    connections(1) = canTextureConnect(world.getBlockState(pos.add(0, 1, 0)).getBlock)
                    connections(2) = canTextureConnect(world.getBlockState(pos.add(1, 1, 0)).getBlock)
                    connections(3) = canTextureConnect(world.getBlockState(pos.add(-1, 0, 0)).getBlock)
                    connections(4) = canTextureConnect(world.getBlockState(pos.add(1, 0, 0)).getBlock)
                    connections(5) = canTextureConnect(world.getBlockState(pos.add(-1, -1, 0)).getBlock)
                    connections(6) = canTextureConnect(world.getBlockState(pos.add(0, -1, 0)).getBlock)
                    connections(7) = canTextureConnect(world.getBlockState(pos.add(1, -1, 0)).getBlock)
                    connections(8) = !world.isAirBlock(pos.add(-1, 1, 1)) && world.getBlockState(pos.add(-1, 1, 1)).getBlock.isOpaqueCube
                    connections(9) = !world.isAirBlock(pos.add(0, 1, 1)) && world.getBlockState(pos.add(0, 1, 1)).getBlock.isOpaqueCube
                    connections(10) = !world.isAirBlock(pos.add(1, 1, 1)) && world.getBlockState(pos.add(1, 1, 1)).getBlock.isOpaqueCube
                    connections(11) = !world.isAirBlock(pos.add(-1, 0, 1)) && world.getBlockState(pos.add(-1, 0, 1)).getBlock.isOpaqueCube
                    connections(12) = !world.isAirBlock(pos.add(1, 0, 1)) && world.getBlockState(pos.add(1, 0, 1)).getBlock.isOpaqueCube
                    connections(13) = !world.isAirBlock(pos.add(-1, -1, 1)) && world.getBlockState(pos.add(-1, -1, 1)).getBlock.isOpaqueCube
                    connections(14) = !world.isAirBlock(pos.add(0, -1, 1)) && world.getBlockState(pos.add(0, -1, 1)).getBlock.isOpaqueCube
                    connections(15) = !world.isAirBlock(pos.add(1, -1, 1)) && world.getBlockState(pos.add(1, -1, 1)).getBlock.isOpaqueCube
                    return connections
                case EnumFacing.WEST =>
                    connections(0) = canTextureConnect(world.getBlockState(pos.add(0, 1, -1)).getBlock)
                    connections(1) = canTextureConnect(world.getBlockState(pos.add(0, 1, 0)).getBlock)
                    connections(2) = canTextureConnect(world.getBlockState(pos.add(0, 1, 1)).getBlock)
                    connections(3) = canTextureConnect(world.getBlockState(pos.add(0, 0, -1)).getBlock)
                    connections(4) = canTextureConnect(world.getBlockState(pos.add(0, 0, 1)).getBlock)
                    connections(5) = canTextureConnect(world.getBlockState(pos.add(0, -1, -1)).getBlock)
                    connections(6) = canTextureConnect(world.getBlockState(pos.add(0, -1, 0)).getBlock)
                    connections(7) = canTextureConnect(world.getBlockState(pos.add(0, -1, 1)).getBlock)
                    connections(8) = !world.isAirBlock(pos.add(-1, 1, -1)) && world.getBlockState(pos.add(-1, 1, -1)).getBlock.isOpaqueCube
                    connections(9) = !world.isAirBlock(pos.add(-1, 1, 0)) && world.getBlockState(pos.add(-1, 1, 0)).getBlock.isOpaqueCube
                    connections(10) = !world.isAirBlock(pos.add(-1, 1, 1)) && world.getBlockState(pos.add(-1, 1, 1)).getBlock.isOpaqueCube
                    connections(11) = !world.isAirBlock(pos.add(-1, 0, -1)) && world.getBlockState(pos.add(-1, 0, -1)).getBlock.isOpaqueCube
                    connections(12) = !world.isAirBlock(pos.add(-1, 0, 1)) && world.getBlockState(pos.add(-1, 0, 1)).getBlock.isOpaqueCube
                    connections(13) = !world.isAirBlock(pos.add(-1, -1, -1)) && world.getBlockState(pos.add(-1, -1, -1)).getBlock.isOpaqueCube
                    connections(14) = !world.isAirBlock(pos.add(-1, -1, 0)) && world.getBlockState(pos.add(-1, -1, 0)).getBlock.isOpaqueCube
                    connections(15) = !world.isAirBlock(pos.add(-1, -1, 1)) && world.getBlockState(pos.add(-1, -1, 1)).getBlock.isOpaqueCube
                    return connections
                case EnumFacing.EAST =>
                    connections(0) = canTextureConnect(world.getBlockState(pos.add(0, 1, 1)).getBlock)
                    connections(1) = canTextureConnect(world.getBlockState(pos.add(0, 1, 0)).getBlock)
                    connections(2) = canTextureConnect(world.getBlockState(pos.add(0, 1, -1)).getBlock)
                    connections(3) = canTextureConnect(world.getBlockState(pos.add(0, 0, 1)).getBlock)
                    connections(4) = canTextureConnect(world.getBlockState(pos.add(0, 0, -1)).getBlock)
                    connections(5) = canTextureConnect(world.getBlockState(pos.add(0, -1, 1)).getBlock)
                    connections(6) = canTextureConnect(world.getBlockState(pos.add(0, -1, 0)).getBlock)
                    connections(7) = canTextureConnect(world.getBlockState(pos.add(0, -1, -1)).getBlock)
                    connections(8) = !world.isAirBlock(pos.add(1, 1, 1)) && world.getBlockState(pos.add(1, 1, 1)).getBlock.isOpaqueCube
                    connections(9) = !world.isAirBlock(pos.add(1, 1, 0)) && world.getBlockState(pos.add(1, 1, 0)).getBlock.isOpaqueCube
                    connections(10) = !world.isAirBlock(pos.add(1, 1, -1)) && world.getBlockState(pos.add(1, 1, -1)).getBlock.isOpaqueCube
                    connections(11) = !world.isAirBlock(pos.add(1, 0, 1)) && world.getBlockState(pos.add(1, 0, 1)).getBlock.isOpaqueCube
                    connections(12) = !world.isAirBlock(pos.add(1, 0, -1)) && world.getBlockState(pos.add(1, 0, -1)).getBlock.isOpaqueCube
                    connections(13) = !world.isAirBlock(pos.add(1, -1, 1)) && world.getBlockState(pos.add(1, -1, 1)).getBlock.isOpaqueCube
                    connections(14) = !world.isAirBlock(pos.add(1, -1, 0)) && world.getBlockState(pos.add(1, -1, 0)).getBlock.isOpaqueCube
                    connections(15) = !world.isAirBlock(pos.add(1, -1, -1)) && world.getBlockState(pos.add(1, -1, -1)).getBlock.isOpaqueCube
                    return connections
                case _ => return connections
            }
        }
        connections
    }

    /**
      * Used to say what our block state is
      */
    override def createBlockState(): BlockState = {
        val listed: Array[IProperty[_]] = new Array(0)
        val unlisted = new Array[IUnlistedProperty[_]](0)
        new ExtendedBlockState(this, listed, unlisted)
    }

    override def getExtendedState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState = {
        new ConnectedTexturesState(pos, world, state.getBlock.asInstanceOf[BlockConnectedTextures], state.getBlock)
    }

    override def getRenderType = 3
    override def isOpaqueCube = !isClear
    override def isTranslucent = isClear
    override def isFullCube = !isClear

    /**
      * Used to define what layer to render in
      */
    override def canRenderInLayer(layer: EnumWorldBlockLayer) : Boolean =
        layer == EnumWorldBlockLayer.CUTOUT
}
