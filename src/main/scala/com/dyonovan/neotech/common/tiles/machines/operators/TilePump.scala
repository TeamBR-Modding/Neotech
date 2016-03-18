package com.dyonovan.neotech.common.tiles.machines.operators

import java.util
import java.util.Comparator

import com.dyonovan.neotech.managers.BlockManager
import com.dyonovan.neotech.utils.ClientUtils
import com.teambr.bookshelf.client.gui.GuiColor
import com.teambr.bookshelf.common.tiles.traits.{EnergyHandler, FluidHandler, UpdatingTile}
import net.minecraft.block.BlockLiquid
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fluids._

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis <pauljoda>
  * @since 2/4/2016
  */
class TilePump extends UpdatingTile with FluidHandler with EnergyHandler {

    lazy val RANGE = 50

    var pumpingFrom = new BlockPos(pos)
    var isBuildingCache = false
    lazy val cache : util.Queue[BlockPos] = new util.PriorityQueue[BlockPos](new Comparator[BlockPos] {
        override def compare(o1: BlockPos, o2: BlockPos): Int =
            -o1.distanceSq(pumpingFrom.getX, pumpingFrom.getY, pumpingFrom.getZ)
                    .compareTo(o2.distanceSq(pumpingFrom.getX, pumpingFrom.getY, pumpingFrom.getZ))
    })

    def costToOperate = 1000

    def operationDelay = 20

    var tickerPump = operationDelay
    override def onServerTick() : Unit = {
        tickerPump -= 1
        if(!isBuildingCache && tickerPump <= 0 && energyStorage.getEnergyStored >= costToOperate) {
            tickerPump = operationDelay
            buildPipeline()
        }
        tryOutput()
    }

    def buildPipeline() : Unit = {
        val position = findBlockUnderPipeline()
        if(worldObj.isAirBlock(position)) {
            worldObj.setBlockState(position, BlockManager.mechanicalPipe.getDefaultState)
            energyStorage.extractEnergy(costToOperate, false)
            return
        }
        if(worldObj.getBlockState(position).getBlock != BlockManager.mechanicalPipe) {
            if(cache.isEmpty)
                buildCache(position)
            else
                pumpNext()
            return
        }
        worldObj.setBlockState(position, BlockManager.mechanicalPipe.getDefaultState)
    }

    def findBlockUnderPipeline() : BlockPos = {
        var position = new BlockPos(pos)
        position = position.offset(EnumFacing.DOWN)
        if(worldObj.isAirBlock(position)) {
            return position
        }
        while(!worldObj.isAirBlock(position)) {
            if(worldObj.getBlockState(position).getBlock != BlockManager.mechanicalPipe)
                return position
            position = position.offset(EnumFacing.DOWN)
        }
        position
    }

    def isValidSourceBlock(position : BlockPos) : Boolean = {
        if(tanks(INPUT_TANK).getFluidAmount < tanks(INPUT_TANK).getCapacity) {
            val shouldMatch = tanks(INPUT_TANK).getFluid != null
            worldObj.getBlockState(position).getBlock match {
                case fluid: IFluidBlock =>
                    return fluid.canDrain(worldObj, position) && fluid.drain(worldObj, position, false) != null &&
                            fluid.drain(worldObj, position, false).amount > 0 &&
                            (if(shouldMatch) tanks(INPUT_TANK).getFluid.getFluid == fluid.getFluid else true)
                case water: Blocks.water.type if worldObj.getBlockState(position).getValue(BlockLiquid.LEVEL).intValue == 0 =>
                    return if (shouldMatch) tanks(INPUT_TANK).getFluid.getFluid == FluidRegistry.WATER else true
                case lava: Blocks.lava.type if worldObj.getBlockState(position).getValue(BlockLiquid.LEVEL).intValue == 0 =>
                    return if (shouldMatch) tanks(INPUT_TANK).getFluid.getFluid == FluidRegistry.LAVA else true
                case _ =>
            }
        }
        false
    }

    def buildCache(startPos : BlockPos) : Unit = {
        isBuildingCache = true
        val stack : util.Stack[BlockPos] = new util.Stack[BlockPos]()
        stack.push(startPos)
        pumpingFrom = findBlockUnderPipeline().offset(EnumFacing.UP)
        while(!stack.isEmpty) {
            val lookingPosition = stack.pop()
            if(isValidSourceBlock(lookingPosition)) {
                for(dir <- EnumFacing.VALUES) {
                    val attachedPosition = lookingPosition.offset(dir)
                    if(!cache.contains(attachedPosition) &&
                            isValidSourceBlock(attachedPosition) &&
                            attachedPosition.distanceSq(pumpingFrom.getX, pumpingFrom.getY, pumpingFrom.getZ) <= RANGE * RANGE) {
                        stack.push(attachedPosition)
                        cache.add(attachedPosition)
                    }
                }
            }
        }
        isBuildingCache = false
    }

    def pumpNext() : Unit = {
        pumpBlock(cache.poll())
    }

    def pumpBlock(position : BlockPos) : Boolean = {
        if(tanks(INPUT_TANK).getFluidAmount < tanks(INPUT_TANK).getCapacity) {
            worldObj.getBlockState(position).getBlock match {
                case fluid: IFluidBlock =>
                    val drained = fluid.drain(worldObj, position, false)
                    if (drained != null && drained.amount > 0) {
                        val fluidStack =  fluid.drain(worldObj, position, true)
                        fill(EnumFacing.DOWN, fluidStack, doFill = true)
                        worldObj.setBlockState(position, Blocks.stone.getDefaultState)
                        energyStorage.extractEnergy(costToOperate, false)
                        return true
                    }
                case water: Blocks.water.type if worldObj.getBlockState(position).getValue(BlockLiquid.LEVEL).intValue == 0 =>
                    val fluidStack = new FluidStack(FluidRegistry.WATER, 1000)
                    if (fill(EnumFacing.DOWN, fluidStack, doFill = false) >= 1000) {
                        fill(EnumFacing.DOWN, fluidStack, doFill = true)
                        energyStorage.extractEnergy(costToOperate / 4, false)
                        return true
                    }
                case lava: Blocks.lava.type if worldObj.getBlockState(position).getValue(BlockLiquid.LEVEL).intValue == 0 =>
                    val fluidStack = new FluidStack(FluidRegistry.LAVA, 1000)
                    if (fill(EnumFacing.DOWN, fluidStack, doFill = false) >= 1000) {
                        fill(EnumFacing.DOWN, fluidStack, doFill = true)
                        worldObj.setBlockState(position, Blocks.stone.getDefaultState)
                        energyStorage.extractEnergy(costToOperate, false)
                        return true
                    }
                case _ =>
            }
        }
        false
    }

    def tryOutput() : Unit = {
        for(dir <- EnumFacing.values) {
            worldObj.getTileEntity(pos.offset(dir)) match {
                case otherTank : IFluidHandler =>
                    if(tanks(INPUT_TANK).getFluid != null && tanks(INPUT_TANK).getFluid.getFluid != null && otherTank.canFill(dir.getOpposite, tanks(INPUT_TANK).getFluid.getFluid)
                            && otherTank.fill(dir.getOpposite, drain(dir, 1000, doDrain = false), false) > 0)
                        otherTank.fill(dir.getOpposite, drain(dir, 1000, doDrain = true), true)
                case _ =>
            }
        }

    }

    override def writeToNBT(tag : NBTTagCompound) : Unit = {
        super[TileEntity].writeToNBT(tag)
        super[FluidHandler].writeToNBT(tag)
        super[EnergyHandler].writeToNBT(tag)
    }

    override def readFromNBT(tag : NBTTagCompound) : Unit = {
        super[TileEntity].readFromNBT(tag)
        super[FluidHandler].readFromNBT(tag)
        super[EnergyHandler].readFromNBT(tag)
    }

    /*******************************************************************************************************************
      ************************************************** Fluid methods *************************************************
      ******************************************************************************************************************/

    lazy val INPUT_TANK = 0

    /**
      * Used to set up the tanks needed. You can insert any number of tanks
      */
    override def setupTanks(): Unit = tanks += new FluidTank(bucketsToMB(10))

    /**
      * Which tanks can input
      *
      * @return
      */
    override def getInputTanks: Array[Int] = Array(INPUT_TANK)

    /**
      * Which tanks can output
      *
      * @return
      */
    override def getOutputTanks: Array[Int] = Array(INPUT_TANK)

    /**
      * Called when something happens to the tank, you should mark the block for update here if a tile
      */
    override def onTankChanged(tank: FluidTank): Unit = worldObj.markBlockForUpdate(pos)

    /**
      * Returns true if the given fluid can be inserted into the given direction.
      *
      * More formally, this should return true if fluid is able to enter from the given direction.
      */
    override def canFill(from: EnumFacing, fluid: Fluid): Boolean = false

    /*******************************************************************************************************************
      ************************************************ Energy methods **************************************************
      ******************************************************************************************************************/

    /**
      * Used to define the default energy storage for this energy handler
      *
      * @return
      */
    def defaultEnergyStorageSize : Int = 8000

    /**
      * Return true if you want this to be able to provide energy
      *
      * @return
      */
    def isProvider : Boolean = false

    /**
      * Return true if you want this to be able to receive energy
      *
      * @return
      */
    def isReceiver : Boolean = true

    /*******************************************************************************************************************
      ************************************************** Misc methods **************************************************
      ******************************************************************************************************************/

    /*override def returnWailaBody(tipList: java.util.List[String]): java.util.List[String] = {
        var color = ""
        if (getEnergyStored(null) > 0)
            color = GuiColor.GREEN.toString
        else
            color = GuiColor.RED.toString
        tipList.add(color + ClientUtils.formatNumber(getEnergyStored(null)) + " / " +
                ClientUtils.formatNumber(getMaxEnergyStored(null)) + " RF")
        tipList
    }*/
}
