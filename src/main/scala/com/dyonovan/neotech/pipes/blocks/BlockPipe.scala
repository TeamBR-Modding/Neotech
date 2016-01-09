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
class BlockPipe(val name : String, mat : Material, val colored : Boolean, tileClass : Class[_ <: SimplePipe]) extends BlockContainer(mat) {

    //Constructor
    setUnlocalizedName(Reference.MOD_ID + ":" + name)
    setCreativeTab(NeoTech.tabPipes)
    setHardness(1.5F)
    setLightOpacity(0)
    if(colored)
        setDefaultState(this.blockState.getBaseState.withProperty(PipeProperties.COLOR, EnumDyeColor.WHITE)
                .withProperty(PipeProperties.UP, false.asInstanceOf[java.lang.Boolean])
                .withProperty(PipeProperties.DOWN, false.asInstanceOf[java.lang.Boolean])
                .withProperty(PipeProperties.NORTH, false.asInstanceOf[java.lang.Boolean])
                .withProperty(PipeProperties.EAST, false.asInstanceOf[java.lang.Boolean])
                .withProperty(PipeProperties.SOUTH, false.asInstanceOf[java.lang.Boolean])
                .withProperty(PipeProperties.WEST, false.asInstanceOf[java.lang.Boolean]))
    else
        setDefaultState(this.blockState.getBaseState
                .withProperty(PipeProperties.UP, false.asInstanceOf[java.lang.Boolean])
                .withProperty(PipeProperties.DOWN, false.asInstanceOf[java.lang.Boolean])
                .withProperty(PipeProperties.NORTH, false.asInstanceOf[java.lang.Boolean])
                .withProperty(PipeProperties.EAST, false.asInstanceOf[java.lang.Boolean])
                .withProperty(PipeProperties.SOUTH, false.asInstanceOf[java.lang.Boolean])
                .withProperty(PipeProperties.WEST, false.asInstanceOf[java.lang.Boolean]))

    override def onBlockPlaced(world: World, pos: BlockPos, facing: EnumFacing, hitX : Float, hitY : Float, hitZ : Float, meta : Int, placer : EntityLivingBase) : IBlockState = {
        if(colored)
            getDefaultState.withProperty(PipeProperties.COLOR, EnumDyeColor.byMetadata(meta))
        else
            getDefaultState
    }

    override def onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) : Boolean = {
        if(colored) {
            playerIn.getCurrentEquippedItem match {
                case stack: ItemStack if stack.getItem.isInstanceOf[ItemDye] =>
                    if (stack.getItemDamage != worldIn.getBlockState(pos).getValue(PipeProperties.COLOR).asInstanceOf[EnumDyeColor].getMetadata) {
                        worldIn.setBlockState(pos, state.withProperty(PipeProperties.COLOR, EnumDyeColor.byDyeDamage(stack.getItemDamage)))
                        if (!playerIn.capabilities.isCreativeMode) {
                            playerIn.getCurrentEquippedItem.stackSize -= 1
                        }
                        return true
                    }
                    return false
                case _ => return false
            }
        }
        false
    }
    /**
     * Get the damage value that this Block should drop
     */
    override def damageDropped (state: IBlockState) : Int = {
        if(colored)
            state.getValue(PipeProperties.COLOR).asInstanceOf[EnumDyeColor].getMetadata
        else
            0
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @SideOnly(Side.CLIENT)
    override def getSubBlocks(itemIn: Item, tab: CreativeTabs, list: java.util.List[ItemStack]) {
        if(colored) {
            for (color <- EnumDyeColor.values()) {
                list.asInstanceOf[java.util.List[ItemStack]].add(new ItemStack(itemIn, 1, color.getMetadata))
            }
        } else
            super.getSubBlocks(itemIn, tab, list)
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     */
    override def getMapColor(state: IBlockState): MapColor = {
        if(colored)
            state.getValue(PipeProperties.COLOR).asInstanceOf[EnumDyeColor].getMapColor
        else
            MapColor.grayColor
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    override def getStateFromMeta(meta: Int): IBlockState = {
        if(colored)
            this.getDefaultState.withProperty(PipeProperties.COLOR, EnumDyeColor.byMetadata(meta))
        else
            getDefaultState
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    override def getMetaFromState(state: IBlockState): Int = {
        if(colored)
            state.getValue(PipeProperties.COLOR).asInstanceOf[EnumDyeColor].getMetadata
        else
            0
    }


    protected override def createBlockState: BlockState = {
        if(colored)
            new BlockState(this, PipeProperties.COLOR, PipeProperties.UP, PipeProperties.DOWN, PipeProperties.NORTH, PipeProperties.SOUTH, PipeProperties.EAST, PipeProperties.WEST)
        else
            new BlockState(this, PipeProperties.UP, PipeProperties.DOWN, PipeProperties.NORTH, PipeProperties.SOUTH, PipeProperties.EAST, PipeProperties.WEST)
    }

    override def getActualState (state: IBlockState, worldIn: IBlockAccess, pos: BlockPos) : IBlockState=  {
        state.withProperty(PipeProperties.UP, isPipeConnected(worldIn, pos, EnumFacing.UP).asInstanceOf[java.lang.Boolean])
                .withProperty(PipeProperties.DOWN, isPipeConnected(worldIn, pos, EnumFacing.DOWN).asInstanceOf[java.lang.Boolean])
                .withProperty(PipeProperties.NORTH, isPipeConnected(worldIn, pos, EnumFacing.NORTH).asInstanceOf[java.lang.Boolean])
                .withProperty(PipeProperties.EAST, isPipeConnected(worldIn, pos, EnumFacing.EAST).asInstanceOf[java.lang.Boolean])
                .withProperty(PipeProperties.SOUTH, isPipeConnected(worldIn, pos, EnumFacing.SOUTH).asInstanceOf[java.lang.Boolean])
                .withProperty(PipeProperties.WEST, isPipeConnected(worldIn, pos, EnumFacing.WEST).asInstanceOf[java.lang.Boolean])
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
        if(isPipeConnected(worldIn, pos, EnumFacing.WEST)) {
            x1 = 0.0F
        }

        if(isPipeConnected(worldIn, pos, EnumFacing.EAST)) {
            x2 = 1.0F
        }

        if(isPipeConnected(worldIn, pos, EnumFacing.NORTH)) {
            z1 = 0.0F
        }

        if(isPipeConnected(worldIn, pos, EnumFacing.SOUTH)) {
            z2 = 1.0F
        }

        if(isPipeConnected(worldIn, pos, EnumFacing.DOWN)) {
            y1 = 0.0F
        }

        if(isPipeConnected(worldIn, pos, EnumFacing.UP)) {
            y2 = 1.0F
        }

        this.setBlockBounds(x1, y1, z1, x2, y2, z2)
    }

    def isPipeConnected(world: IBlockAccess, pos: BlockPos, facing: EnumFacing) : Boolean = {
        world.getTileEntity(pos) match {
            case pipe : SimplePipe =>
                pipe.canConnect(facing)
            case _ => false
        }
    }

    override def addCollisionBoxesToList(worldIn : World, pos : BlockPos, state : IBlockState, mask : AxisAlignedBB, list : java.util.List[AxisAlignedBB], collidingEntity : Entity) {
        this.setBlockBoundsBasedOnState(worldIn, pos)
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity)
    }

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
