package com.dyonovan.neotech.pipes.blocks

import java.util
import java.util.Random

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.managers.{BlockManager, ItemManager}
import com.dyonovan.neotech.pipes.collections.WorldPipes
import com.dyonovan.neotech.pipes.tiles.structure.StructurePipe
import com.dyonovan.neotech.pipes.types.SimplePipe
import com.teambr.bookshelf.loadables.ILoadActionProvider
import net.minecraft.block.material.{MapColor, Material}
import net.minecraft.block.state.{BlockStateContainer, IBlockState}
import net.minecraft.block.{Block, BlockContainer}
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.init.Blocks
import net.minecraft.item._
import net.minecraft.util._
import net.minecraft.util.math.{AxisAlignedBB, BlockPos}
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
class BlockPipe(val name : String, mat : Material, val colored : Boolean, tileClass : Class[_ <: StructurePipe])
        extends BlockContainer(mat) with ILoadActionProvider {

    /*******************************************************************************************************************
      * Constructor                                                                                                    *
      ******************************************************************************************************************/

    setUnlocalizedName(Reference.MOD_ID + ":" + name)
    setCreativeTab(NeoTech.tabPipes)
    setHardness(1.5F)
    setLightOpacity(0)

    // For colored, we need to add in the default color of white, these aren't really needed but better safe that sorry
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

    /*******************************************************************************************************************
      * Block State                                                                                                    *
      ******************************************************************************************************************/

    /**
      * Used to create the block state of the pipes
      */
    override def createBlockState : BlockStateContainer = {
        new BlockStateContainer(this, PipeProperties.COLOR, PipeProperties.UP, PipeProperties.DOWN,
            PipeProperties.EAST, PipeProperties.WEST, PipeProperties.NORTH, PipeProperties.SOUTH)
        /*val listed = new ArrayBuffer[IProperty[_]]()
        if(colored)
            listed += PipeProperties.COLOR
        listed += PipeProperties.UP
        listed += PipeProperties.DOWN
        listed += PipeProperties.EAST
        listed += PipeProperties.WEST
        listed += PipeProperties.NORTH
        listed += PipeProperties.SOUTH
        val unListed = new ArrayBuffer[IUnlistedProperty[_]]()
        unListed += BlockMultipartContainer.PROPERTY_MULTIPART_CONTAINER
        new ExtendedBlockState(this, listed.toArray, unListed.toArray)*/
    }

    /**
      * We can't store all the info we want on the state, so access the info from the world on demand
      */
    override def getActualState (state: IBlockState, worldIn: IBlockAccess, pos: BlockPos) : IBlockState=  {
        state.withProperty(PipeProperties.UP, isPipeConnected(worldIn, pos, EnumFacing.UP).asInstanceOf[java.lang.Boolean])
                .withProperty(PipeProperties.DOWN, isPipeConnected(worldIn, pos, EnumFacing.DOWN).asInstanceOf[java.lang.Boolean])
                .withProperty(PipeProperties.NORTH, isPipeConnected(worldIn, pos, EnumFacing.NORTH).asInstanceOf[java.lang.Boolean])
                .withProperty(PipeProperties.EAST, isPipeConnected(worldIn, pos, EnumFacing.EAST).asInstanceOf[java.lang.Boolean])
                .withProperty(PipeProperties.SOUTH, isPipeConnected(worldIn, pos, EnumFacing.SOUTH).asInstanceOf[java.lang.Boolean])
                .withProperty(PipeProperties.WEST, isPipeConnected(worldIn, pos, EnumFacing.WEST).asInstanceOf[java.lang.Boolean])
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
            state.getValue(PipeProperties.COLOR).getMetadata
        else
            0
    }

    /*******************************************************************************************************************
      * Block Methods                                                                                                  *
      ******************************************************************************************************************/

    /**
      * Called when we need to create a new tile class
      */
    override def createNewTileEntity(worldIn : World, meta : Int) : StructurePipe = tileClass.newInstance()

    /**
      * Called the moment the block is placed, for us we need to set the property to the default values if colored
      */
    override def onBlockPlaced(world: World, pos: BlockPos, facing: EnumFacing,
                               hitX : Float, hitY : Float, hitZ : Float, meta : Int, placer : EntityLivingBase) : IBlockState = {
        if(colored)
            getDefaultState.withProperty(PipeProperties.COLOR, EnumDyeColor.byMetadata(meta))
        else
            getDefaultState
    }

    /**
      * Called when the block is clicked on, if we are colored or using a wrench perform relevant actions
      */
    override def onBlockActivated(world: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer,
                                         hand: EnumHand, heldItem: ItemStack, side: EnumFacing,
                                         hitX: Float, hitY: Float, hitZ: Float) : Boolean = {
        heldItem match {
            case stack : ItemStack if stack.getItem == ItemManager.wrench =>
                if(!world.isRemote) {
                    val random = new Random
                    val stack = new ItemStack(world.getBlockState(pos)
                            .getBlock.getItemDropped(
                        world.getBlockState(pos), random, 0),
                        1,
                        world.getBlockState(pos).getValue(PipeProperties.COLOR).getMetadata)
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
                    }
                    world.setBlockToAir(pos)
                    world.setBlockState(pos, Blocks.AIR.getDefaultState, 3)
                    return true
                } else
                    playerIn.swingArm(hand)

            case _ =>
        }

        if(colored) {
            heldItem match {
                case stack: ItemStack if stack.getItem.isInstanceOf[ItemDye] =>
                    if (stack.getItemDamage != world.getBlockState(pos).getValue(PipeProperties.COLOR).getMetadata) {
                        world.setBlockState(pos, state.withProperty(PipeProperties.COLOR, EnumDyeColor.byDyeDamage(stack.getItemDamage)))
                        if (!playerIn.capabilities.isCreativeMode) {
                            heldItem.stackSize -= 1
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
      * Notify pipe to execute code when broken
      */
    override def breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) : Unit = {
        worldIn.getTileEntity(pos) match {
            case pipe : SimplePipe =>
                pipe.onPipeBroken()
            case _ =>
        }
        super.breakBlock(worldIn, pos, state)
    }

    /**
      * Send update to pipes in grid to reform their cache
      */
    override def neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block): Unit = {
        if (!worldIn.isRemote)
            WorldPipes.notifyPipes()
    }

    /*******************************************************************************************************************
      * Block Info Methods                                                                                             *
      ******************************************************************************************************************/
    var BB = new AxisAlignedBB(4F / 16F, 4F / 16F, 4F / 16F, 12F / 16F, 12F / 16F, 12F / 16F)

    override def getSelectedBoundingBox(state: IBlockState, world: World, pos: BlockPos): AxisAlignedBB = {
        state.getBoundingBox(world, pos)
    }

    /**
      * Used to set the bounding box based on the current state
      */
    override def getBoundingBox(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): AxisAlignedBB = {
        var x1 = 5F / 16F
        var x2 = 1.0F - x1
        var y1 = x1
        var y2 = 1.0F - y1
        var z1 = x1
        var z2 = 1.0F - z1
        if(isPipeConnected(worldIn, pos, EnumFacing.WEST))
            x1 = 0.0F
        if(isPipeConnected(worldIn, pos, EnumFacing.EAST))
            x2 = 1.0F
        if(isPipeConnected(worldIn, pos, EnumFacing.NORTH))
            z1 = 0.0F
        if(isPipeConnected(worldIn, pos, EnumFacing.SOUTH))
            z2 = 1.0F
        if(isPipeConnected(worldIn, pos, EnumFacing.DOWN))
            y1 = 0.0F
        if(isPipeConnected(worldIn, pos, EnumFacing.UP))
            y2 = 1.0F
        this.setBlockBounds(x1, y1, z1, x2, y2, z2)
    }

    def setBlockBounds(x1 : Float, y1 : Float, z1 : Float, x2 : Float, y2 : Float, z2 : Float): AxisAlignedBB = {
        BB = new AxisAlignedBB(x1, y1, z1, x2, y2, z2)
        BB
    }

    /**
      * Checks pipe connection status
      */
    def isPipeConnected(world: IBlockAccess, pos: BlockPos, facing: EnumFacing) : Boolean = {
        world.getTileEntity(pos) match {
            case pipe : SimplePipe =>
                pipe.canConnect(facing)
            case _ => false
        }
    }

    /**
      * Add collision, allows player to get close to pipe bounds
      */
    override def addCollisionBoxToList(state: IBlockState, worldIn: World, pos: BlockPos, mask : AxisAlignedBB, list : java.util.List[AxisAlignedBB], collidingEntity : Entity) = {
        this.getBoundingBox(state, worldIn, pos)
        super.addCollisionBoxToList(state, worldIn, pos, mask, list, collidingEntity)
    }

    override def getDrops(world: IBlockAccess, pos: BlockPos, state: IBlockState, fortune: Int): java.util.List[ItemStack] = {
        val list = new util.ArrayList[ItemStack]()
        if(colored) {
            list.add(new ItemStack(
                BlockManager.pipeBasicStructure,
                1,
                state.getValue(PipeProperties.COLOR).getMetadata))
        }
        list
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

    /*******************************************************************************************************************
      * Client Block Info                                                                                              *
      ******************************************************************************************************************/

    /**
      * Get the MapColor for this Block and the given BlockState
      */
    override def getMapColor(state: IBlockState): MapColor = {
        if(colored)
            state.getValue(PipeProperties.COLOR).getMapColor
        else
            MapColor.GRAY
    }

    /**
      * Tells minecraft to use a default model
      *
      * @return
      */
    override def getRenderType(state : IBlockState) : EnumBlockRenderType = EnumBlockRenderType.MODEL

    /**
      * We are clear, the following are all needed for best clear performance, rendering on Translucent layer
      * allows for alpha pixels (for things like acceleration pipes)
      */
    override def isOpaqueCube(state: IBlockState) : Boolean = false
    @SideOnly(Side.CLIENT)
    override def isTranslucent(state: IBlockState) : Boolean = true
    override def isFullCube(state: IBlockState) : Boolean = false
    @SideOnly(Side.CLIENT)
    override def getBlockLayer : BlockRenderLayer = BlockRenderLayer.SOLID
    override def canRenderInLayer(layer : BlockRenderLayer) : Boolean =
        layer == BlockRenderLayer.SOLID

    override def performLoadAction(event: AnyRef, isClient: Boolean): Unit = {
        /*event match {
            case modelBake: ModelBakeEvent =>
                for(modelLocation <- modelBake.getModelRegistry.asInstanceOf[RegistrySimple[ModelResourceLocation, IBakedModel]].getKeys) {
                    if(modelLocation.getResourceDomain.equalsIgnoreCase(Reference.MOD_ID) &&
                            modelLocation.getResourcePath.contains("pipeStructure")) {
                        // Create Multipart world obj
                        modelBake.getModelRegistry.putObject(modelLocation,
                            new ModelMultipartContainer(modelBake.getModelRegistry.getObject(modelLocation),
                                new Predicate[BlockRenderLayer] {
                                    override def apply(input: BlockRenderLayer): Boolean = true
                                }))
                    }
                }
            case _ =>
        }*/
    }
}
