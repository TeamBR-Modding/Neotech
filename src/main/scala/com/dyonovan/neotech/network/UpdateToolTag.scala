package com.dyonovan.neotech.network

import com.dyonovan.neotech.tools.armor.ItemElectricArmor
import com.dyonovan.neotech.tools.tools.BaseElectricTool
import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.PacketBuffer
import net.minecraftforge.fml.common.network.simpleimpl.{IMessage, IMessageHandler, MessageContext}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis "pauljoda"
  * @since 3/4/2016
  */
class UpdateToolTag extends IMessage with IMessageHandler[UpdateToolTag, IMessage] {

    var slot : Int = -1
    var tag : NBTTagCompound = null

    def this(s : Int, t : NBTTagCompound) {
        this()
        slot = s
        tag = t
    }

    override def toBytes(buf: ByteBuf): Unit = {
        buf.writeInt(slot)
        new PacketBuffer(buf).writeNBTTagCompoundToBuffer(tag)
    }

    override def fromBytes(buf: ByteBuf): Unit = {
        slot = buf.readInt()
        tag = new PacketBuffer(buf).readNBTTagCompoundFromBuffer()
    }

    override def onMessage(message: UpdateToolTag, ctx: MessageContext): IMessage = {
        if(ctx.side.isServer && message.slot != -1 && message.tag != null) {
            val player = ctx.getServerHandler.playerEntity
            if(message.slot < 10 && player.inventory.getStackInSlot(message.slot) != null &&
                    player.inventory.getStackInSlot(message.slot).getItem.isInstanceOf[BaseElectricTool]) {
                player.inventory.getStackInSlot(message.slot).setTagCompound(message.tag)
            } else {
                val armorSlot = message.slot - 10
                if(player.inventory.armorInventory(armorSlot) != null &&
                        player.inventory.armorInventory(armorSlot).getItem.isInstanceOf[ItemElectricArmor])
                    player.inventory.armorInventory(armorSlot).setTagCompound(message.tag)
            }
        }
        null
    }
}