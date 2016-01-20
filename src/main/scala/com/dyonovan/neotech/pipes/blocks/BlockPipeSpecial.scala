package com.dyonovan.neotech.pipes.blocks

import java.util.Random

import com.dyonovan.neotech.NeoTech
import com.dyonovan.neotech.client.gui.machines.GuiMachineUpgrade
import com.dyonovan.neotech.common.blocks.traits.Upgradeable
import com.dyonovan.neotech.common.container.machines.ContainerMachineUpgrade
import com.dyonovan.neotech.lib.Reference
import com.dyonovan.neotech.managers.{BlockManager, ItemManager}
import com.dyonovan.neotech.pipes.gui.GuiExtractionMenu
import com.dyonovan.neotech.pipes.types.{ExtractionPipe, SimplePipe}
import com.teambr.bookshelf.Bookshelf
import com.teambr.bookshelf.client.gui.GuiColor
import com.teambr.bookshelf.common.tiles.traits.OpensGui
import com.teambr.bookshelf.traits.HasToolTip
import com.teambr.bookshelf.util.WorldUtils
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.block.state.{BlockState, IBlockState}
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{AxisAlignedBB, BlockPos, EnumFacing, EnumWorldBlockLayer}
import net.minecraft.world.{WorldServer, IBlockAccess, World}
import net.minecraftforge.client.MinecraftForgeClient
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.{SideOnly, Side}
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
class BlockPipeSpecial(val name : String, mat : Material, tileClass : Class[_ <: SimplePipe]) extends BlockContainer(mat) with OpensGui with HasToolTip {

    //Constructor
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

    protected override def createBlockState: BlockState = {
        new BlockState(this, PipeProperties.SPECIAL_UP, PipeProperties.SPECIAL_DOWN, PipeProperties.SPECIAL_NORTH, PipeProperties.SPECIAL_SOUTH, PipeProperties.SPECIAL_EAST, PipeProperties.SPECIAL_WEST)
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
    override def getStateFromMeta(meta: Int): IBlockState = {
        getDefaultState
    }

    /**
      * Convert the BlockState into the correct metadata value
      */
    override def getMetaFromState(state: IBlockState): Int = {
        0
    }

    override def breakBlock(worldIn: World, pos: BlockPos, state: IBlockState): Unit = {
        if(worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos).isInstanceOf[ExtractionPipe[_, _]]) {
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
        super.breakBlock(worldIn, pos, state)
    }

    override def setBlockBoundsBasedOnState(worldIn: IBlockAccess, pos: BlockPos) {
        var x1 = 0.25F
        var x2 = 1.0F - x1
        var y1 = 0.25F
        var y2 = 1.0F - y1
        var z1 = 0.25F
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

    def isPipeConnected(world: IBlockAccess, pos: BlockPos, facing: EnumFacing): Boolean = {
        world.getTileEntity(pos) match {
            case pipe: SimplePipe =>
                pipe.canConnect(facing) || pipe.isSpecialConnection(facing)
            case _ => false
        }
    }

    def countConnections(world: IBlockAccess, pos: BlockPos, facing: EnumFacing): Int = {
        world.getTileEntity(pos) match {
            case pipe: SimplePipe =>
                if (pipe.isSpecialConnection(facing) && pipe.canConnect(facing))
                    2
                else if (pipe.canConnect(facing))
                    1
                else
                    0
            case _ => 0
        }
    }

    override def addCollisionBoxesToList(worldIn: World, pos: BlockPos, state: IBlockState, mask: AxisAlignedBB, list: java.util.List[AxisAlignedBB], collidingEntity: Entity) {
        this.setBlockBoundsBasedOnState(worldIn, pos)
        super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity)
    }

    override def getRenderType: Int = 3

    override def isOpaqueCube: Boolean = false

    @SideOnly(Side.CLIENT)
    override def isTranslucent: Boolean = true

    override def isFullCube: Boolean = false

    @SideOnly(Side.CLIENT)
    override def getBlockLayer: EnumWorldBlockLayer = EnumWorldBlockLayer.CUTOUT

    override def canRenderInLayer(layer: EnumWorldBlockLayer): Boolean =
        layer == EnumWorldBlockLayer.TRANSLUCENT || layer == EnumWorldBlockLayer.CUTOUT

    override def createNewTileEntity(worldIn: World, meta: Int): TileEntity = tileClass.newInstance()

    override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        world.getTileEntity(new BlockPos(x, y, z)) match {
            case upgradeable: Upgradeable =>
                if (ID == 0) {
                    return new ContainerMachineUpgrade(player.inventory, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[Upgradeable])
                }
            case __ =>
        }
        null
    }

    @SideOnly(Side.CLIENT)
    override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
        world.getTileEntity(new BlockPos(x, y, z)) match {
            case upgradeable: Upgradeable =>
                if (ID == 0) {
                    return new GuiMachineUpgrade(player, world.getTileEntity(new BlockPos(x, y, z)).asInstanceOf[Upgradeable])
                }
            case _ =>
        }
        null
    }

    /**
      * Called when the block is activated
      *
      * If you want to override this but still call it, make sure you call
      *      super[OpensGui].onBlockActivated(...)
      */
    override def onBlockActivated(world : World, pos : BlockPos, state : IBlockState, playerIn : EntityPlayer, side : EnumFacing, hitX : Float, hitY : Float, hitZ : Float) : Boolean = {
        playerIn.getCurrentEquippedItem match {
            case stack : ItemStack if stack.getItem == ItemManager.wrench && playerIn.isSneaking =>
                if(!world.isRemote) {
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
                    world.markBlockForUpdate(pos)
                    return true
                } else {
                    playerIn.swingItem()
                    return true
                }

            case _ =>
        }

        if (world.isRemote && playerIn.inventory.getCurrentItem != null && playerIn.inventory.getCurrentItem.getItem == ItemManager.wrench &&
                world.getTileEntity(pos).isInstanceOf[ExtractionPipe[_, _]]) {
            FMLClientHandler.instance().showGuiScreen(new GuiExtractionMenu(world.getTileEntity(pos).asInstanceOf[ExtractionPipe[_, _]]))
            return true
        }
        false
    }

    override def getToolTip() : List[String] = {
        if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
            List[String](GuiColor.ORANGE + "Press <SHIFT> for possible upgrades...")
        else
            List[String](GuiColor.YELLOW + "Control Upgrade (Max 1): " + GuiColor.WHITE + "Adds Redstone Control.",
                GuiColor.YELLOW + "Expansion Upgrade (Max 1): " + GuiColor.WHITE + "Allows Mode Selection (Round Robin, etc).",
                GuiColor.YELLOW + "Hard Drive (Max 8): " + GuiColor.WHITE + "Increases amount extracted per tick.",
                GuiColor.YELLOW + "Processor Upgrade (Max 8): " + GuiColor.WHITE + "Increases Extraction Rate.")
    }
}
