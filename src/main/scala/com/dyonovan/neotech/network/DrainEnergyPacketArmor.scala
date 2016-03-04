package com.dyonovan.neotech.network

import com.dyonovan.neotech.tools.tools.BaseElectricTool
import io.netty.buffer.ByteBuf
import net.minecraftforge.fml.common.network.simpleimpl.{IMessage, IMessageHandler, MessageContext}

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * @author Paul Davis "pauljoda"
  * @since 3/3/2016
  */
class DrainEnergyPacketArmor extends IMessage with IMessageHandler[DrainEnergyPacketArmor, IMessage] {
    var armorID : Int = -1
    var amount : Int = -1

    def this(a : Int, am : Int) {
        this()
        armorID = a
        amount = am
    }

    override def toBytes(buf: ByteBuf): Unit = {
        buf.writeInt(armorID)
        buf.writeInt(amount)
    }

    override def fromBytes(buf: ByteBuf): Unit = {
        armorID = buf.readInt()
        amount = buf.readInt()
    }

    override def onMessage(message: DrainEnergyPacketArmor, ctx: MessageContext): IMessage = {
        if(ctx.side.isServer && message.armorID != -1 && message.amount > 0) {
            val player = ctx.getServerHandler.playerEntity
            if(player.inventory.armorInventory(3 - message.armorID) != null && player.inventory.armorInventory(3 - message.armorID).getItem.isInstanceOf[BaseElectricTool]) {
                val armorItem = player.inventory.armorInventory(3 - message.armorID).getItem.asInstanceOf[BaseElectricTool]
                armorItem.extractEnergy(player.inventory.armorInventory(3 - message.armorID), message.amount, simulate = false)
                armorItem.updateDamage(player.inventory.armorInventory(3 - message.armorID))
            }
        }
        null
    }
}
