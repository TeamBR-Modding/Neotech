package com.dyonovan.neotech.common.tiles.machines.operators

import java.util
import java.util.Comparator

import cofh.api.energy.{EnergyStorage, IEnergyReceiver}
import com.dyonovan.neotech.managers.BlockManager
import com.teambr.bookshelf.api.waila.Waila
import com.teambr.bookshelf.client.gui.GuiColor
import com.teambr.bookshelf.common.tiles.traits.{FluidHandler, UpdatingTile}
import net.minecraft.block.BlockLiquid
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.{BlockPos, EnumFacing}
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
class TilePump extends UpdatingTile with FluidHandler with IEnergyReceiver with Waila {

    val RANGE = 50
    def costToOperate = 1000
    var energy = new EnergyStorage(4000 * 20)

    val TANK = 0
    var pumpingFrom = new BlockPos(pos)
    var isBuildingCache = false
    lazy val cache : util.Queue[BlockPos] = new util.PriorityQueue[BlockPos](new Comparator[BlockPos] {
        override def compare(o1: BlockPos, o2: BlockPos): Int =
            -o1.distanceSq(pumpingFrom.getX, pumpingFrom.getY, pumpingFrom.getZ)
                        .compareTo(o2.distanceSq(pumpingFrom.getX, pumpingFrom.getY, pumpingFrom.getZ))
    })

    override def setupTanks(): Unit = {
        tanks += new FluidTank(bucketsToMB(10))
    }

    override def onTankChanged(tank: FluidTank): Unit = worldObj.markBlockForUpdate(pos)

    def operationDelay = 20

    var ticker = operationDelay
    override def onServerTick() : Unit = {
        ticker -= 1
        if(!isBuildingCache && ticker <= 0 && energy.getEnergyStored >= costToOperate) {
            ticker = operationDelay
            buildPipeline()
        }
        tryOutput()
    }

    def buildPipeline() : Unit = {
        val position = findBlockUnderPipeline()
        if(worldObj.isAirBlock(position)) {
            worldObj.setBlockState(position, BlockManager.mechanicalPipe.getDefaultState)
            energy.extractEnergy(costToOperate, false)
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
        if(tanks(TANK).getFluidAmount < tanks(TANK).getCapacity) {
            val shouldMatch = tanks(TANK).getFluid != null
            worldObj.getBlockState(position).getBlock match {
                case fluid: BlockFluidBase =>
                    return fluid.getFilledPercentage(worldObj, position) == 1.0F && (if(shouldMatch) tanks(TANK).getFluid.getFluid == fluid.getFluid else true)
                case water: Blocks.water.type if worldObj.getBlockState(position).getValue(BlockLiquid.LEVEL).intValue == 0 =>
                    return if (shouldMatch) tanks(TANK).getFluid.getFluid == FluidRegistry.WATER else true
                case lava: Blocks.lava.type if worldObj.getBlockState(position).getValue(BlockLiquid.LEVEL).intValue == 0 =>
                    return if (shouldMatch) tanks(TANK).getFluid.getFluid == FluidRegistry.LAVA else true
                case _ =>
            }
        }
        false
    }

    def buildCache(startPos : BlockPos) : Unit = {
        isBuildingCache = true
        val stack : util.Stack[BlockPos] = new util.Stack[BlockPos]()
        stack.push(startPos)
        pumpingFrom = findBlockUnderPipeline()
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
        if(tanks(TANK).getFluidAmount < tanks(TANK).getCapacity) {
            worldObj.getBlockState(position).getBlock match {
                case fluid: BlockFluidBase =>
                    val level = fluid.getFilledPercentage(worldObj, position)
                    if (level == 1.0F) {
                        val fluidStack = new FluidStack(fluid.getFluid, 1000)
                        if (fill(EnumFacing.DOWN, fluidStack, doFill = false) >= 1000) {
                            fill(EnumFacing.DOWN, fluidStack, doFill = true)
                            worldObj.setBlockState(position, Blocks.stone.getDefaultState)
                            energy.extractEnergy(costToOperate, false)
                            return true
                        }
                    }
                case water: Blocks.water.type if worldObj.getBlockState(position).getValue(BlockLiquid.LEVEL).intValue == 0 =>
                    val fluidStack = new FluidStack(FluidRegistry.WATER, 1000)
                    if (fill(EnumFacing.DOWN, fluidStack, doFill = false) >= 1000) {
                        fill(EnumFacing.DOWN, fluidStack, doFill = true)
                        energy.extractEnergy(costToOperate / 4, false)
                        return true
                    }
                case lava: Blocks.lava.type if worldObj.getBlockState(position).getValue(BlockLiquid.LEVEL).intValue == 0 =>
                    val fluidStack = new FluidStack(FluidRegistry.LAVA, 1000)
                    if (fill(EnumFacing.DOWN, fluidStack, doFill = false) >= 1000) {
                        fill(EnumFacing.DOWN, fluidStack, doFill = true)
                        worldObj.setBlockState(position, Blocks.stone.getDefaultState)
                        energy.extractEnergy(costToOperate, false)
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
                    if(tanks(TANK).getFluid != null && tanks(TANK).getFluid.getFluid != null && otherTank.canFill(dir.getOpposite, tanks(TANK).getFluid.getFluid)
                            && otherTank.fill(dir.getOpposite, drain(dir, 1000, doDrain = false), false) > 0)
                        otherTank.fill(dir.getOpposite, drain(dir, 1000, doDrain = true), true)
                case _ =>
            }
        }

    }

    override def writeToNBT(tag : NBTTagCompound) : Unit = {
        super[TileEntity].writeToNBT(tag)
        super[FluidHandler].writeToNBT(tag)
        energy.writeToNBT(tag)
    }

    override def readFromNBT(tag : NBTTagCompound) : Unit = {
        super[TileEntity].readFromNBT(tag)
        super[FluidHandler].readFromNBT(tag)
        energy.readFromNBT(tag)
    }

    /*******************************************************************************************************************
      ************************************************ Energy methods **************************************************
      ******************************************************************************************************************/

    /**
      * Add energy to an IEnergyReceiver, internal distribution is left entirely to the IEnergyReceiver.
      *
      * @param from Orientation the energy is received from.
      * @param maxReceive Maximum amount of energy to receive.
      * @param simulate If TRUE, the charge will only be simulated.
      * @return Amount of energy that was (or would have been, if simulated) received.
      */
    override def receiveEnergy(from: EnumFacing, maxReceive: Int, simulate: Boolean): Int = {
        if (energy != null) {
            val actual = energy.receiveEnergy(maxReceive, simulate)
            if (worldObj != null)
                worldObj.markBlockForUpdate(pos)
            actual
        } else 0
    }

   /* /**
      * Used to extract energy from this tile. You should return zero if you don't want to be able to extract
      *
      * @param from The direction pulling from
      * @param maxExtract The maximum amount to extract
      * @param simulate True to just simulate, not actually drain
      * @return How much energy was/should be drained
      */
    override def extractEnergy(from: EnumFacing, maxExtract: Int, simulate: Boolean): Int = 0*/

    /**
      * Get the current energy stored in the energy tank
      *
      * @param from The side to check (can be used if you have different energy storages)
      * @return
      */
    override def getEnergyStored(from: EnumFacing): Int = energy.getEnergyStored

    /**
      * Get the maximum energy this handler can store, not the current
      *
      * @param from The side to check from (can be used if you have different energy storages)
      * @return The maximum potential energy
      */
    override def getMaxEnergyStored(from: EnumFacing): Int = energy.getMaxEnergyStored

    /**
      * Checks if energy can connect to a given side
      *
      * @param from The face to check
      * @return True if the face allows energy flow
      */
    override def canConnectEnergy(from: EnumFacing): Boolean = true

    override def returnWailaBody(tipList: java.util.List[String]): java.util.List[String] = {
        var color = ""
        if (getEnergyStored(null) > 0)
            color = GuiColor.GREEN.toString
        else
            color = GuiColor.RED.toString
        tipList.add(color + getEnergyStored(null) + "/" + getMaxEnergyStored(null) + " RF")
        tipList
    }
}
