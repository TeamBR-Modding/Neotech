package com.dyonovan.neotech.pipes.blocks

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.pipes.types.SimplePipe
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.{MapColor, Material}
import net.minecraft.block.state.{BlockState, IBlockState}
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.item.{ItemDye, EnumDyeColor, Item, ItemStack}
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{AxisAlignedBB, BlockPos, EnumFacing, EnumWorldBlockLayer}
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

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
class BlockPipe(val name : String, mat : Material, tileClass : Class[_ <: SimplePipe]) extends BlockContainer(mat) {

    //Constructor
    setUnlocalizedName(Reference.MOD_ID + ":" + name)
    setCreativeTab(NeoTech.tabPipes)
    setHardness(1.5F)
    setLightOpacity(0)
    setDefaultState(this.blockState.getBaseState.withProperty(PipeProperties.COLOR, EnumDyeColor.WHITE)
            .withProperty(PipeProperties.UP, false)
            .withProperty(PipeProperties.DOWN, false)
            .withProperty(PipeProperties.NORTH, false)
            .withProperty(PipeProperties.EAST, false)
            .withProperty(PipeProperties.SOUTH, false)
            .withProperty(PipeProperties.WEST, false))

    override def onBlockPlaced(world: World, pos: BlockPos, facing: EnumFacing, hitX : Float, hitY : Float, hitZ : Float, meta : Int, placer : EntityLivingBase) : IBlockState = {
        getDefaultState.withProperty(PipeProperties.COLOR, EnumDyeColor.byMetadata(meta))
    }

    override def onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) : Boolean = {
        playerIn.getCurrentEquippedItem match {
            case stack: ItemStack if stack.getItem.isInstanceOf[ItemDye] =>
                if (stack.getItemDamage != worldIn.getBlockState(pos).getValue(PipeProperties.COLOR).asInstanceOf[EnumDyeColor].getMetadata) {
                    worldIn.setBlockState(pos, state.withProperty(PipeProperties.COLOR, EnumDyeColor.byDyeDamage(stack.getItemDamage)))
                    if(!playerIn.capabilities.isCreativeMode) {
                        playerIn.getCurrentEquippedItem.stackSize -= 1
                    }
                    return true
                }
                false
            case _ => false
        }
    }
    /**
     * Get the damage value that this Block should drop
     */
    override def damageDropped (state: IBlockState) : Int =
        state.getValue(PipeProperties.COLOR).asInstanceOf[EnumDyeColor].getMetadata

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @SideOnly(Side.CLIENT)
    override def getSubBlocks(itemIn: Item, tab: CreativeTabs, list: java.util.List[_]) {
        for(color <- EnumDyeColor.values()) {
            list.asInstanceOf[java.util.List[ItemStack]].add(new ItemStack(itemIn, 1, color.getMetadata))
        }
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     */
    override def getMapColor(state: IBlockState): MapColor =
        state.getValue(PipeProperties.COLOR).asInstanceOf[EnumDyeColor].getMapColor

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    override def getStateFromMeta(meta: Int): IBlockState =
        this.getDefaultState.withProperty(PipeProperties.COLOR, EnumDyeColor.byMetadata(meta))

    /**
     * Convert the BlockState into the correct metadata value
     */
    override def getMetaFromState(state: IBlockState): Int =
        state.getValue(PipeProperties.COLOR).asInstanceOf[EnumDyeColor].getMetadata


    protected override def createBlockState: BlockState =
        new BlockState(this, PipeProperties.COLOR, PipeProperties.UP, PipeProperties.DOWN, PipeProperties.NORTH, PipeProperties.SOUTH, PipeProperties.EAST, PipeProperties.WEST)

    @SideOnly(Side.CLIENT)
    override def shouldSideBeRendered (worldIn: IBlockAccess, pos: BlockPos, side: EnumFacing) : Boolean =
        true

    override def getActualState (state: IBlockState, worldIn: IBlockAccess, pos: BlockPos) : IBlockState=  {
        state.withProperty(PipeProperties.UP, isCableConnected(worldIn, pos, EnumFacing.UP))
                .withProperty(PipeProperties.DOWN, isCableConnected(worldIn, pos, EnumFacing.DOWN))
                .withProperty(PipeProperties.NORTH, isCableConnected(worldIn, pos, EnumFacing.NORTH))
                .withProperty(PipeProperties.EAST, isCableConnected(worldIn, pos, EnumFacing.EAST))
                .withProperty(PipeProperties.SOUTH, isCableConnected(worldIn, pos, EnumFacing.SOUTH))
                .withProperty(PipeProperties.WEST, isCableConnected(worldIn, pos, EnumFacing.WEST))
    }

    override def breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) : Unit = {
        worldIn.getTileEntity(pos) match {
            case pipe : SimplePipe =>
                pipe.onPipeBroken()
            case _ =>
        }
    }

    override def setBlockBoundsBasedOnState(worldIn : IBlockAccess, pos : BlockPos) {
        var x1 = 0.25F
        var x2 = 1.0F - x1
        var y1 = 0.25F
        var y2 = 1.0F - y1
        var z1 = 0.25F
        var z2 = 1.0F - z1
        if(isCableConnected(worldIn, pos, EnumFacing.WEST)) {
            x1 = 0.0F
        }

        if(isCableConnected(worldIn, pos, EnumFacing.EAST)) {
            x2 = 1.0F
        }

        if(isCableConnected(worldIn, pos, EnumFacing.NORTH)) {
            z1 = 0.0F
        }

        if(isCableConnected(worldIn, pos, EnumFacing.SOUTH)) {
            z2 = 1.0F
        }

        if(isCableConnected(worldIn, pos, EnumFacing.DOWN)) {
            y1 = 0.0F
        }

        if(isCableConnected(worldIn, pos, EnumFacing.UP)) {
            y2 = 1.0F
        }

        this.setBlockBounds(x1, y1, z1, x2, y2, z2)
    }

    def isCableConnected(world: IBlockAccess, pos: BlockPos, facing: EnumFacing) : Boolean = {
        world.getTileEntity(pos) match {
            case pipe : SimplePipe =>
                pipe.canConnect(facing)
            case _ => false
        }
    }

    override def addCollisionBoxesToList(worldIn : World, pos : BlockPos, state : IBlockState, mask : AxisAlignedBB, list : java.util.List[_], collidingEntity : Entity) {
        this.setBlockBoundsBasedOnState(worldIn, pos)
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity)
    }

    //TODO: Implement Colored networks
    /* override def colorMultiplier(worldIn : IBlockAccess, pos : BlockPos, renderPass : Int) : Int = {
         0xFFFFFF
     }*/

    override def getRenderType : Int = 3

    override def isOpaqueCube : Boolean = false

    @SideOnly(Side.CLIENT)
    override def isTranslucent : Boolean = true

    override def isFullCube : Boolean = false

    @SideOnly(Side.CLIENT)
    override def getBlockLayer : EnumWorldBlockLayer = EnumWorldBlockLayer.CUTOUT

    override def canRenderInLayer(layer : EnumWorldBlockLayer) : Boolean =
        layer == EnumWorldBlockLayer.TRANSLUCENT || layer == EnumWorldBlockLayer.CUTOUT

    override def createNewTileEntity(worldIn : World, meta : Int) : TileEntity = tileClass.newInstance()
}
