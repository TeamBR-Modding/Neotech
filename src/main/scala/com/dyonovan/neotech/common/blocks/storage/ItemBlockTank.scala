package com.dyonovan.neotech.common.blocks.storage

import com.dyonovan.neotech.managers.BlockManager
import com.teambr.bookshelf.client.gui.GuiColor
import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{Item, ItemBlock, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraftforge.fluids.{FluidContainerRegistry, FluidStack, IFluidContainerItem}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Dyonovan
  * @since August 16, 2015
  */
class ItemBlockTank(block: Block) extends ItemBlock(block) with IFluidContainerItem {

    setNoRepair()
    setMaxStackSize(1)
    setMaxDamage(16)
    setHasSubtypes(true)

    @SideOnly(Side.CLIENT)
    override def addInformation(stack: ItemStack, player: EntityPlayer, list: java.util.List[String], boolean: Boolean): Unit = {
        if (ItemStack.areItemsEqual(stack, new ItemStack(Item.getItemFromBlock(BlockManager.voidTank)))) return
        if (getFluid(stack) != null) {
            list.asInstanceOf[java.util.List[String]].add(GuiColor.WHITE + getFluid(stack).getLocalizedName)
            list.asInstanceOf[java.util.List[String]].add(GuiColor.ORANGE + getFluid(stack).amount.toString + "/" +
                    getTankInfo._2 + " mb")
        } else {
            list.asInstanceOf[java.util.List[String]].add(GuiColor.GRAY + "Empty")
            list.asInstanceOf[java.util.List[String]].add(GuiColor.RED + "0/" + getTankInfo._2 + " mb")
        }
    }

    def updateDamage(stack: ItemStack): Unit = {
        if (getFluid(stack) != null) {
            val r = getFluid(stack).amount.toFloat / getCapacity(stack)
            val res = 16 - (r * 16).toInt
            stack.setItemDamage(res)
        } else
            stack.setItemDamage(16)
    }

    override def onUpdate(stack: ItemStack, worldIn: World, entityIn: Entity, itemSlot: Int, isSelected: Boolean): Unit = {
        updateDamage(stack)
    }

    def getFluid(container: ItemStack): FluidStack = {
        if (!container.hasTagCompound || !container.getTagCompound.hasKey("Fluid")) {
            return null
        }
        FluidStack.loadFluidStackFromNBT(container.getTagCompound.getCompoundTag("Fluid"))
    }

    def getCapacity(container: ItemStack): Int = {
        getTankInfo._2
    }

    def fill(container: ItemStack, resource: FluidStack, doFill: Boolean): Int = {
        if (resource == null) {
            return 0
        }
        if (!doFill) {
            if (!container.hasTagCompound || !container.getTagCompound.hasKey("Fluid")) {
                return Math.min(getTankInfo._2, resource.amount)
            }
            val stack: FluidStack = FluidStack.loadFluidStackFromNBT(container.getTagCompound.getCompoundTag("Fluid"))
            if (stack == null) {
                return Math.min(getTankInfo._2, resource.amount)
            }
            if (!stack.isFluidEqual(resource)) {
                return 0
            }
            return Math.min(getTankInfo._2 - stack.amount, resource.amount)
        }
        if (!container.hasTagCompound) {
            container.setTagCompound(new NBTTagCompound)
        }
        if (!container.getTagCompound.hasKey("Fluid")) {
            val fluidTag: NBTTagCompound = resource.writeToNBT(new NBTTagCompound)
            if (getTankInfo._2 < resource.amount) {
                fluidTag.setInteger("Amount", getTankInfo._2)
                container.getTagCompound.setTag("Fluid", fluidTag)
                return getTankInfo._2
            }
            container.getTagCompound.setTag("Fluid", fluidTag)
            return resource.amount
        }
        val fluidTag: NBTTagCompound = container.getTagCompound.getCompoundTag("Fluid")
        val stack: FluidStack = FluidStack.loadFluidStackFromNBT(fluidTag)
        if (!stack.isFluidEqual(resource)) {
            return 0
        }
        var filled: Int = getTankInfo._2 - stack.amount
        if (resource.amount < filled) {
            stack.amount += resource.amount
            filled = resource.amount
        }
        else {
            stack.amount = getTankInfo._2
        }
        container.getTagCompound.setTag("Fluid", stack.writeToNBT(fluidTag))
        if(doFill)
            updateDamage(container)
        filled
    }

    def drain(container: ItemStack, maxDrain: Int, doDrain: Boolean): FluidStack = {
        if (!container.hasTagCompound || !container.getTagCompound.hasKey("Fluid")) {
            return null
        }
        val stack: FluidStack = FluidStack.loadFluidStackFromNBT(container.getTagCompound.getCompoundTag("Fluid"))
        if (stack == null) {
            return null
        }
        val currentAmount: Int = stack.amount
        stack.amount = Math.min(stack.amount, maxDrain)
        if (doDrain) {
            if (currentAmount == stack.amount) {
                container.getTagCompound.removeTag("Fluid")
                if (container.getTagCompound.hasNoTags) {
                    container.setTagCompound(null)
                }
                return stack
            }
            val fluidTag: NBTTagCompound = container.getTagCompound.getCompoundTag("Fluid")
            fluidTag.setInteger("Amount", currentAmount - stack.amount)
            container.getTagCompound.setTag("Fluid", fluidTag)
        }
        if(doDrain)
            updateDamage(container)
        stack
    }

    private def getTankInfo: (Int, Int) = {
        block match {
            case BlockManager.ironTank => (1, FluidContainerRegistry.BUCKET_VOLUME * 8)
            case BlockManager.goldTank => (2, FluidContainerRegistry.BUCKET_VOLUME * 16)
            case BlockManager.diamondTank => (3, FluidContainerRegistry.BUCKET_VOLUME * 64)
            case BlockManager.creativeTank => (4, FluidContainerRegistry.BUCKET_VOLUME * 8)
            case _ => (1, FluidContainerRegistry.BUCKET_VOLUME * 8)
        }
    }
}
