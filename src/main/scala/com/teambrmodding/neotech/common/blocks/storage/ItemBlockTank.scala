package com.teambrmodding.neotech.common.blocks.storage

import java.text.NumberFormat
import java.util.Locale

import com.teambrmodding.neotech.managers.BlockManager
import com.teambr.bookshelf.client.gui.GuiColor
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
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
class ItemBlockTank(block: Block) extends ItemBlock(block){

    setNoRepair()
    setMaxStackSize(1)
    setMaxDamage(16)
    setHasSubtypes(true)

    @SideOnly(Side.CLIENT)
    override def addInformation(stack: ItemStack, player: EntityPlayer, list: java.util.List[String], boolean: Boolean): Unit = {
        if (ItemStack.areItemsEqual(stack, new ItemStack(Item.getItemFromBlock(BlockManager.voidTank)))) return
        if (getFluid(stack) != null) {
            list.asInstanceOf[java.util.List[String]].add(GuiColor.WHITE + getFluid(stack).getLocalizedName)
            list.asInstanceOf[java.util.List[String]].add(GuiColor.ORANGE +
                    NumberFormat.getNumberInstance(Locale.forLanguageTag(Minecraft.getMinecraft.gameSettings.language))
                            .format(getFluid(stack).amount) + " / " +
                    NumberFormat.getNumberInstance(Locale.forLanguageTag(Minecraft.getMinecraft.gameSettings.language))
                            .format(getTankInfo._2) + " mb")
        } else {
            list.asInstanceOf[java.util.List[String]].add(GuiColor.GRAY + "Empty")
            list.asInstanceOf[java.util.List[String]].add(GuiColor.RED + "0 / " +
                    NumberFormat.getNumberInstance(Locale.forLanguageTag(Minecraft.getMinecraft.gameSettings.language))
                            .format(getTankInfo._2) + " mb")
        }
    }

    def updateDamage(stack: ItemStack): Unit = {
        if(block != BlockManager.voidTank) {
            if (getFluid(stack) != null) {
                val r = getFluid(stack).amount.toFloat / getCapacity(stack)
                val res = 16 - (r * 16).toInt
                stack.setItemDamage(res)
            } else
                stack.setItemDamage(16)
        }
    }

    override def onUpdate(stack: ItemStack, worldIn: World, entityIn: Entity, itemSlot: Int, isSelected: Boolean): Unit = {
        updateDamage(stack)
    }

    def getFluid(container: ItemStack): FluidStack = {
        if (!container.hasTagCompound || !container.getTagCompound.hasKey("Tanks")) {
            return null
        }
        val tagList = container.getTagCompound.getTagList("Tanks", 10)
        FluidStack.loadFluidStackFromNBT(tagList.getCompoundTagAt(0))
    }

    def getCapacity(container: ItemStack): Int = {
        getTankInfo._2
    }

    private def getTankInfo: (Int, Int) = {
        block match {
            case BlockManager.ironTank => (1, FluidContainerRegistry.BUCKET_VOLUME * 8)
            case BlockManager.goldTank => (2, FluidContainerRegistry.BUCKET_VOLUME * 16)
            case BlockManager.diamondTank => (3, FluidContainerRegistry.BUCKET_VOLUME * 64)
            case BlockManager.creativeTank => (4, FluidContainerRegistry.BUCKET_VOLUME * 8)
            case _ => (1, FluidContainerRegistry.BUCKET_VOLUME * 1)
        }
    }
}
