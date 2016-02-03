package com.dyonovan.neotech.common.blocks.misc

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.common.blocks.BaseBlock
import com.dyonovan.neotech.common.blocks.states.NeoStates
import com.dyonovan.neotech.common.tiles.misc.TileStar
import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.managers.BlockManager
import com.dyonovan.neotech.pipes.blocks.PipeProperties
import com.dyonovan.neotech.pipes.collections.WorldPipes
import net.minecraft.block.Block
import net.minecraft.block.material.{MapColor, Material}
import net.minecraft.block.state.{BlockState, IBlockState}
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{ItemDye, ItemStack, Item, EnumDyeColor}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.{AxisAlignedBB, EnumFacing, BlockPos}
import net.minecraft.world.{WorldServer, IBlockAccess, World}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

import scala.util.Random

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 1/23/2016
  */
class BlockStar(name: String) extends BaseBlock(Material.rock, name, classOf[TileStar]) {

    setCreativeTab(NeoTech.tabDecorations)
    setHardness(1.5F)

    def getName: String = name

    setLightLevel(1.0F)
    setDefaultState(this.blockState.getBaseState
            .withProperty(NeoStates.ON_BLOCK, 6.asInstanceOf[Integer]))

    override def isFullBlock: Boolean = false

    override def isFullCube: Boolean = false

    override def isOpaqueCube: Boolean = false

    override def onBlockPlaced(world: World, pos: BlockPos, facing: EnumFacing, hitX : Float, hitY : Float, hitZ : Float, meta : Int, placer : EntityLivingBase) : IBlockState = {
        var attachedSide = 6

        placer match {
            case player : EntityPlayer =>
                if(player.isSneaking)
                    return getDefaultState.withProperty(NeoStates.ON_BLOCK, attachedSide.asInstanceOf[Integer])
            case _ =>
        }

        if (attachedSide == 6 && world.getBlockState(pos.offset(facing.getOpposite)) != null && world.getBlockState(pos.offset(facing.getOpposite)).getBlock.isSideSolid(world, pos.offset(facing.getOpposite), facing)) {
            attachedSide = facing.getOpposite.ordinal()
        }

        if(attachedSide == 6) {
            for (dir <- EnumFacing.values()) {
                if (attachedSide == 6 && world.getBlockState(pos.offset(dir)) != null && world.getBlockState(pos.offset(dir)).getBlock.isSideSolid(world, pos.offset(dir), dir.getOpposite))
                    attachedSide = dir.ordinal()
            }
        }

        getDefaultState.withProperty(NeoStates.ON_BLOCK, attachedSide.asInstanceOf[Integer])
    }

    override def onBlockPlacedBy(world: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack) : Unit = {
        if(!world.isRemote && world.getTileEntity(pos) != null) {
            world.getTileEntity(pos).asInstanceOf[TileStar].color = EnumDyeColor.byMetadata(stack.getItemDamage).getMetadata
            world.markBlockForUpdate(pos)
        }
    }

    override def onBlockActivated(world: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) : Boolean = {
        playerIn.getCurrentEquippedItem match {
            case stack: ItemStack if stack.getItem.isInstanceOf[ItemDye] =>
                if (stack.getItemDamage != world.getBlockState(pos).getValue(PipeProperties.COLOR).getMetadata) {
                    world.getTileEntity(pos).asInstanceOf[TileStar].color = EnumDyeColor.byDyeDamage(stack.getItemDamage).getMetadata
                    world.markBlockForUpdate(pos)
                    return true
                }
                false
            case _ => false
        }
    }

    override def rotateBlock(world : World, pos : BlockPos, side : EnumFacing) : Boolean = {
        var attached = world.getBlockState(pos).getValue(NeoStates.ON_BLOCK)
        val tag = new NBTTagCompound
        if(world.getTileEntity(pos) != null)
            world.getTileEntity(pos).writeToNBT(tag)
        attached += 1
        if(attached > 6)
            attached = 0
        world.setBlockState(pos, getDefaultState.withProperty(NeoStates.ON_BLOCK, attached))
        if(tag != null && world.getTileEntity(pos) != null)
            world.getTileEntity(pos).readFromNBT(tag)
        true
    }

    /**
      * Convert the given metadata into a BlockState for this Block
      */
    override def getStateFromMeta(meta: Int): IBlockState = {
        getDefaultState.withProperty(NeoStates.ON_BLOCK, meta.asInstanceOf[Integer])
    }

    /**
      * Convert the BlockState into the correct metadata value
      */
    override def getMetaFromState(state: IBlockState): Int = {
        state.getValue(NeoStates.ON_BLOCK).asInstanceOf[Int]
    }

    override def getActualState (state: IBlockState, worldIn: IBlockAccess, pos: BlockPos) : IBlockState = {
        state.withProperty(PipeProperties.COLOR, EnumDyeColor.byMetadata(worldIn.getTileEntity(pos).asInstanceOf[TileStar].color))
    }

    /**
      * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
      */
    @SideOnly(Side.CLIENT)
    override def getSubBlocks(itemIn: Item, tab: CreativeTabs, list: java.util.List[ItemStack]) {
        for (color <- EnumDyeColor.values()) {
            list.asInstanceOf[java.util.List[ItemStack]].add(new ItemStack(itemIn, 1, color.getMetadata))
        }
    }

    /**
      * Get the MapColor for this Block and the given BlockState
      */
    override def getMapColor(state: IBlockState): MapColor = {
        state.getValue(PipeProperties.COLOR).getMapColor
    }

    protected override def createBlockState: BlockState = {
        new BlockState(this, PipeProperties.COLOR, NeoStates.ON_BLOCK)
    }

    override def setBlockBoundsBasedOnState(worldIn : IBlockAccess, pos : BlockPos): Unit = {
        if(worldIn.getBlockState(pos).getValue(NeoStates.ON_BLOCK).asInstanceOf[Int] == 6) {
            this.setBlockBounds(6F / 16F, 6F / 16F, 6F / 16F, 10F / 16F, 10F / 16F, 10F / 16F)
        } else {
            EnumFacing.getFront(worldIn.getBlockState(pos).getValue(NeoStates.ON_BLOCK).asInstanceOf[Int]) match {
                case EnumFacing.UP =>
                    this.setBlockBounds(6F / 16F, 12F / 16F, 6F / 16F, 10F / 16F, 16F / 16F, 10F / 16F)
                case EnumFacing.DOWN =>
                    this.setBlockBounds(6F / 16F, 0F / 16F, 6F / 16F, 10F / 16F, 4 / 16F, 10F / 16F)
                case EnumFacing.SOUTH =>
                    this.setBlockBounds(6F / 16F, 6F / 16F, 12F / 16F, 10F / 16F, 10F / 16F, 16F / 16F)
                case EnumFacing.NORTH =>
                    this.setBlockBounds(6F / 16F, 6F / 16F, 0F / 16F, 10F / 16F, 10F / 16F, 4F / 16F)
                case EnumFacing.EAST =>
                    this.setBlockBounds(12F / 16F, 6F / 16F, 6 / 16F, 16F / 16F, 10F / 16F, 10F / 16F)
                case EnumFacing.WEST =>
                    this.setBlockBounds(0F / 16F, 6F / 16F, 6F / 16F, 4F / 16F, 10F / 16F, 10F / 16F)
                case _ =>
                    this.setBlockBounds(6F / 16F, 6F / 16F, 0F / 16F, 10F / 16F, 10F / 16F, 10F / 16F)
            }
        }
    }

    def facingToInt(facing : EnumFacing) : Int = facing.ordinal()

    override def addCollisionBoxesToList(worldIn : World, pos : BlockPos, state : IBlockState, mask : AxisAlignedBB, list : java.util.List[AxisAlignedBB], collidingEntity : Entity) {
        this.setBlockBoundsBasedOnState(worldIn, pos)
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity)
    }

    override def getRenderType : Int = 3

    /***
      * Overwritten as we want to also drop the mother board
      */
    override def breakBlock(world: World, pos: BlockPos, state : IBlockState): Unit = {
        world match {
            case _: WorldServer => //We are on a server
                world.getTileEntity(pos) match {
                    case star : TileStar =>
                        val stack = new ItemStack(BlockManager.blockMiniatureStar, 1, star.color)
                        val random = new Random()

                        if(stack != null && stack.stackSize > 0) {
                            val rx = random.nextFloat * 0.8F + 0.1F
                            val ry = random.nextFloat * 0.8F + 0.1F
                            val rz = random.nextFloat * 0.8F + 0.1F

                            val itemEntity = new EntityItem(world,
                                pos.getX + rx, pos.getY + ry, pos.getZ + rz,
                                new ItemStack(stack.getItem, stack.stackSize, stack.getItemDamage))

                            if (stack.hasTagCompound)
                                itemEntity.getEntityItem.setTagCompound(stack.getTagCompound)

                            val factor = 0.05F

                            itemEntity.motionX = random.nextGaussian * factor
                            itemEntity.motionY = random.nextGaussian * factor + 0.2F
                            itemEntity.motionZ = random.nextGaussian * factor
                            world.spawnEntityInWorld(itemEntity)

                            stack.stackSize = 0
                        }
                    case _ =>
                }
        }
    }

    override def getItemDropped(state: IBlockState, rand: java.util.Random, fortune: Int) : Item = {
        null
    }
}
