package com.teambrmodding.neotech.pipes.blocks

import java.util.Random

import com.teambrmodding.neotech.NeoTech
import com.teambrmodding.neotech.lib.Reference
import com.teambrmodding.neotech.managers.ItemManager
import com.teambrmodding.neotech.pipes.collections.WorldPipes
import com.teambrmodding.neotech.pipes.container.ContainerAdvancedPipeMenu
import com.teambrmodding.neotech.pipes.gui.GuiAdvancedPipeMenu
import com.teambrmodding.neotech.pipes.types.{AdvancedPipe, SimplePipe}
import com.teambr.bookshelf.client.gui.GuiColor
import com.teambr.bookshelf.loadables.ILoadActionProvider
import com.teambr.bookshelf.traits.HasToolTip
import com.teambrmodding.neotech.common.tiles.traits.Upgradeable
import net.minecraft.block.material.Material
import net.minecraft.block.state.{BlockStateContainer, IBlockState}
import net.minecraft.block.{Block, BlockContainer}
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util._
import net.minecraft.util.math.{AxisAlignedBB, BlockPos}
import net.minecraft.world.{IBlockAccess, World, WorldServer}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import org.lwjgl.input.Keyboard


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
class BlockPipeSpecial(val name : String, mat : Material, tileClass : Class[_ <: AdvancedPipe]) extends BlockContainer(mat)
        with HasToolTip with ILoadActionProvider {

    /*******************************************************************************************************************
      * Constructor                                                                                                    *
      ******************************************************************************************************************/

    setUnlocalizedName(Reference.MOD_ID + ":" + name)
    setCreativeTab(NeoTech.tabPipes)
    setHardness(1.5F)
    setLightOpacity(0)
    setDefaultState(this.blockState.getBaseState
            .withProperty(PipeProperties.SPECIAL_UP, 0.asInstanceOf[Integer])
            .withProperty(PipeProperties.SPECIAL_DOWN, 0.asInstanceOf[Integer])
            .withProperty(PipeProperties.SPECIAL_NORTH, 0.asInstanceOf[Integer])
            .withProperty(PipeProperties.SPECIAL_EAST, 0.asInstanceOf[Integer])
            .withProperty(PipeProperties.SPECIAL_SOUTH, 0.asInstanceOf[Integer])
            .withProperty(PipeProperties.SPECIAL_WEST, 0.asInstanceOf[Integer]))

    /*******************************************************************************************************************
      * Block State                                                                                                    *
      ******************************************************************************************************************/

    /**
      * Used to create the block state of the pipes
      */
    protected override def createBlockState: BlockStateContainer = {
        new BlockStateContainer(this, PipeProperties.SPECIAL_UP,
            PipeProperties.SPECIAL_DOWN,
            PipeProperties.SPECIAL_NORTH,
            PipeProperties.SPECIAL_SOUTH,
            PipeProperties.SPECIAL_EAST,
            PipeProperties.SPECIAL_WEST)
        /*val listed = new ArrayBuffer[IProperty[_]]()
        listed += PipeProperties.SPECIAL_UP
        listed += PipeProperties.SPECIAL_DOWN
        listed += PipeProperties.SPECIAL_NORTH
        listed += PipeProperties.SPECIAL_SOUTH
        listed += PipeProperties.SPECIAL_EAST
        listed += PipeProperties.SPECIAL_WEST
        val unListed = new ArrayBuffer[IUnlistedProperty[_]]()
        unListed += BlockMultipartContainer.PROPERTY_MULTIPART_CONTAINER
        new ExtendedBlockState(this, listed.toArray, unListed.toArray)*/
    }

    override def getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState = {
        state.withProperty(PipeProperties.SPECIAL_UP, countConnections(worldIn, pos, EnumFacing.UP).asInstanceOf[Integer])
                .withProperty(PipeProperties.SPECIAL_DOWN, countConnections(worldIn, pos, EnumFacing.DOWN).asInstanceOf[Integer])
                .withProperty(PipeProperties.SPECIAL_NORTH, countConnections(worldIn, pos, EnumFacing.NORTH).asInstanceOf[Integer])
                .withProperty(PipeProperties.SPECIAL_EAST, countConnections(worldIn, pos, EnumFacing.EAST).asInstanceOf[Integer])
                .withProperty(PipeProperties.SPECIAL_SOUTH, countConnections(worldIn, pos, EnumFacing.SOUTH).asInstanceOf[Integer])
                .withProperty(PipeProperties.SPECIAL_WEST, countConnections(worldIn, pos, EnumFacing.WEST).asInstanceOf[Integer])
    }

    /**
      * Convert the given metadata into a BlockState for this Block
      */
    override def getStateFromMeta(meta: Int): IBlockState = getDefaultState

    /**
      * Convert the BlockState into the correct metadata value
      */
    override def getMetaFromState(state: IBlockState): Int = 0

    /*******************************************************************************************************************
      * Block Methods                                                                                                  *
      ******************************************************************************************************************/

    override def createNewTileEntity(worldIn: World, meta: Int): AdvancedPipe = tileClass.newInstance()

    override def breakBlock(worldIn: World, pos: BlockPos, state: IBlockState): Unit = {
        if(worldIn.getTileEntity(pos) != null) {
            worldIn match {
                case _: WorldServer => //We are on a server
                    worldIn.getTileEntity(pos) match {
                        case upgradeable: Upgradeable =>
                            if (upgradeable.upgradeInventory.getStackInSlot(0) != null) {
                                val stack = upgradeable.upgradeInventory.getStackInSlot(0)
                                val random = new Random

                                if (stack != null && stack.stackSize > 0) {
                                    val rx = random.nextFloat * 0.8F + 0.1F
                                    val ry = random.nextFloat * 0.8F + 0.1F
                                    val rz = random.nextFloat * 0.8F + 0.1F

                                    val itemEntity = new EntityItem(worldIn,
                                        pos.getX + rx, pos.getY + ry, pos.getZ + rz,
                                        new ItemStack(stack.getItem, stack.stackSize, stack.getItemDamage))

                                    if (stack.hasTagCompound)
                                        itemEntity.getEntityItem.setTagCompound(stack.getTagCompound)

                                    val factor = 0.05F

                                    itemEntity.motionX = random.nextGaussian * factor
                                    itemEntity.motionY = random.nextGaussian * factor + 0.2F
                                    itemEntity.motionZ = random.nextGaussian * factor
                                    worldIn.spawnEntityInWorld(itemEntity)

                                    stack.stackSize = 0
                                }
                            }
                    }
            }

            worldIn.getTileEntity(pos) match {
                case pipe: SimplePipe =>
                    pipe.onPipeBroken()
                case _ =>
            }
        }
    }


    /**
      * Called when the block is activated
      *
      * If you want to override this but still call it, make sure you call
      *      super[OpensGui].onBlockActivated(...)
      */
    override def onBlockActivated(world: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer,
                                  hand: EnumHand, heldItem: ItemStack, side: EnumFacing,
                                  hitX: Float, hitY: Float, hitZ: Float) : Boolean = {
        heldItem match {
            case stack : ItemStack if stack.getItem == ItemManager.wrench =>
                if(!world.isRemote && playerIn.isSneaking) {
                    val random = new Random
                    val stack = new ItemStack(world.getBlockState(pos).getBlock.getItemDropped(world.getBlockState(pos), random, 0), 1, damageDropped(world.getBlockState(pos)))
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
                    return true
                } else if(!playerIn.isSneaking) {
                    playerIn.openGui(NeoTech, 0, world, pos.getX, pos.getY, pos.getZ)
                    playerIn.swingArm(hand)
                    return true
                }

            case _ =>
        }
        false
    }

    var BB = new AxisAlignedBB(4F / 16F, 0F, 4F / 16F, 12F / 16F, 1F, 12F / 16F)

    /*override def getSelectedBoundingBoxDefault(state: IBlockState, world: World, pos: BlockPos): AxisAlignedBB = {
        state.getBoundingBox(world, pos)
    }*/

    override def getBoundingBox(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): AxisAlignedBB = {
        var x1 = 5F / 16F
        var x2 = 1.0F - x1
        var y1 = x1
        var y2 = 1.0F - y1
        var z1 = x1
        var z2 = 1.0F - z1
        if (countConnections(worldIn, pos, EnumFacing.WEST) > 0) {
            x1 = 0.0F
        }

        if (countConnections(worldIn, pos, EnumFacing.EAST) > 0) {
            x2 = 1.0F
        }

        if (countConnections(worldIn, pos, EnumFacing.NORTH) > 0) {
            z1 = 0.0F
        }

        if (countConnections(worldIn, pos, EnumFacing.SOUTH) > 0) {
            z2 = 1.0F
        }

        if (countConnections(worldIn, pos, EnumFacing.DOWN) > 0) {
            y1 = 0.0F
        }

        if (countConnections(worldIn, pos, EnumFacing.UP) > 0) {
            y2 = 1.0F
        }

        this.setBlockBounds(x1, y1, z1, x2, y2, z2)
    }

    def setBlockBounds(x1 : Float, y1 : Float, z1 : Float, x2 : Float, y2 : Float, z2 : Float): AxisAlignedBB = {
        BB = new AxisAlignedBB(x1, y1, z1, x2, y2, z2)
        BB
    }

    def isPipeConnected(world: IBlockAccess, pos: BlockPos, facing: EnumFacing): Boolean = {
        world.getTileEntity(pos) match {
            case pipe: SimplePipe =>
                pipe.canConnect(facing) || pipe.isSpecialConnection(facing)
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

    def countConnections(world: IBlockAccess, pos: BlockPos, facing: EnumFacing): Int = {
        world.getTileEntity(pos) match {
            case pipe: AdvancedPipe =>
                if (pipe.canConnectExtract(facing) && pipe.canConnect(facing) && world.getTileEntity(pos.offset(facing)) != null
                        && !world.getTileEntity(pos.offset(facing)).isInstanceOf[SimplePipe])
                    2
                else if (pipe.canConnect(facing) && pipe.canConnectSink(facing))
                    1
                else
                    0
            case _ => 0
        }
    }

    override def neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block): Unit = {
        if (!worldIn.isRemote)
            WorldPipes.notifyPipes()
    }

    def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        if(player.inventory.getCurrentItem != null && player.inventory.getCurrentItem.getItem == ItemManager.wrench &&
                world.getTileEntity(new BlockPos(x, y, z)).isInstanceOf[AdvancedPipe])
            return new ContainerAdvancedPipeMenu(player.inventory, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[AdvancedPipe])
        null
    }

    @SideOnly(Side.CLIENT)
    def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        if(player.inventory.getCurrentItem != null && player.inventory.getCurrentItem.getItem == ItemManager.wrench &&
                world.getTileEntity(new BlockPos(x, y, z)).isInstanceOf[AdvancedPipe])
            return new GuiAdvancedPipeMenu(player, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[AdvancedPipe])
        null
    }

    /*******************************************************************************************************************
      * Client Block Info                                                                                              *
      ******************************************************************************************************************/

    override def getRenderType(state : IBlockState) : EnumBlockRenderType = EnumBlockRenderType.MODEL
    override def isOpaqueCube(state: IBlockState): Boolean = false
    @SideOnly(Side.CLIENT)
    override def isTranslucent(state: IBlockState): Boolean = true
    override def isFullCube(state: IBlockState): Boolean = false
    @SideOnly(Side.CLIENT)
    override def getBlockLayer: BlockRenderLayer = BlockRenderLayer.SOLID
    override def canRenderInLayer(layer: BlockRenderLayer): Boolean =
        layer == BlockRenderLayer.SOLID

    override def getToolTip() : List[String] = {
        if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
            List[String](GuiColor.ORANGE + "Press <SHIFT> for possible upgrades...")
        else
            List[String](GuiColor.YELLOW + "Control Upgrade (Max 1): " + GuiColor.WHITE + "Adds Redstone Control.",
                GuiColor.YELLOW + "Expansion Upgrade (Max 1): " + GuiColor.WHITE + "Allows Mode Selection (Round Robin, etc).",
                GuiColor.YELLOW + "Hard Drive (Max 8): " + GuiColor.WHITE + "Increases amount extracted per tick.",
                GuiColor.YELLOW + "Processor Upgrade (Max 8): " + GuiColor.WHITE + "Increases Extraction Rate.")
    }

    override def performLoadAction(event: AnyRef, isClient: Boolean): Unit = {
        /*event match {
            case modelBake: ModelBakeEvent =>
                for(modelLocation <- modelBake.getModelRegistry.asInstanceOf[RegistrySimple[ModelResourceLocation, IBakedModel]].getKeys) {
                    if(modelLocation.getResourceDomain.equalsIgnoreCase(Reference.MOD_ID) &&
                            modelLocation.getResourcePath.contains("BasicInterface")) {
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
