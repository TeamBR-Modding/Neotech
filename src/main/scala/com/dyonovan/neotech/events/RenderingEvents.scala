package com.dyonovan.neotech.events

import com.dyonovan.neotech.tools.ToolHelper
import com.dyonovan.neotech.tools.modifier.ModifierAOE
import com.teambr.bookshelf.util.RenderUtils
import net.minecraft.block.material.Material
import net.minecraft.block.{BlockChest, BlockEnderChest, BlockSign, BlockSkull}
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.{GlStateManager, Tessellator, VertexBuffer}
import net.minecraft.client.resources.{IResourceManager, IResourceManagerReloadListener}
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.util.math.{BlockPos, RayTraceResult, Vec3d}
import net.minecraft.world.World
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import org.lwjgl.opengl.GL11

/**
  * This file was created for NeoTech
  *
  * NeoTech is licensed under the
  * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
  * http://creativecommons.org/licenses/by-nc-sa/4.0/
  *
  * Based work from Tinkers Construct https://github.com/SlimeKnights/TinkersConstruct/blob/master/src/main/java/slimeknights/tconstruct/tools/client/RenderEvents.java
  *
  * @author Paul Davis <pauljoda>
  * @since 2/24/2016
  */
@SideOnly(Side.CLIENT)
object RenderingEvents extends IResourceManagerReloadListener {
    private val destroyIcons = new Array[TextureAtlasSprite](10)

    // Doesn't seem to rotate
    /*lazy val TEXTURE_ELYTRA = new ResourceLocation("textures/entity/elytra.png")

    @SubscribeEvent
    def renderPlayer(event : RenderPlayerEvent.Pre) : Unit = {
        if(event.entityPlayer.inventory.armorInventory(EntityEquipmentSlot.CHEST.getIndex) != null &&
                event.entityPlayer.inventory.armorInventory(EntityEquipmentSlot.CHEST.getIndex).getItem ==
                        ItemManager.electricArmorChestplate) {
            val entity = event.entityPlayer
            var limbSwing = entity.limbSwing - entity.limbSwingAmount * (1.0F - event.partialRenderTick)
            var limbSwingAmount = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * event.partialRenderTick
            val ageInTicks = entity.ticksExisted + event.partialRenderTick

            var f: Float = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, event.partialRenderTick)
            val f1: Float = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, event.partialRenderTick)
            var netHeadYaw: Float = f1 - f

            val headPitch: Float = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * event.partialRenderTick

            if (entity.isChild) {
                limbSwing *= 3.0F
            }

            if (limbSwingAmount > 1.0F) {
                limbSwingAmount = 1.0F
            }

            val shouldSit: Boolean = entity.isRiding && (entity.getRidingEntity != null && entity.getRidingEntity.shouldRiderSit)
            if (shouldSit && entity.getRidingEntity.isInstanceOf[EntityLivingBase]) {
                val entitylivingbase: EntityLivingBase = entity.getRidingEntity.asInstanceOf[EntityLivingBase]
                f = this.interpolateRotation(entitylivingbase.prevRenderYawOffset, entitylivingbase.renderYawOffset, event.partialRenderTick)
                netHeadYaw = f1 - f
            }

            GL11.glPushMatrix()
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F)

            if (Minecraft.getMinecraft.thePlayer.isPlayerInfoSet && Minecraft.getMinecraft.thePlayer.getLocationElytra != null) {
                event.renderer.bindTexture(Minecraft.getMinecraft.thePlayer.getLocationElytra)
            }
            else if (Minecraft.getMinecraft.thePlayer.hasPlayerInfo && Minecraft.getMinecraft.thePlayer.getLocationCape != null
                    && Minecraft.getMinecraft.thePlayer.isWearing(EnumPlayerModelParts.CAPE)) {
                event.renderer.bindTexture(Minecraft.getMinecraft.thePlayer.getLocationCape)
            }
            else {
                event.renderer.bindTexture(TEXTURE_ELYTRA)
            }

            val scale = prepareScale(entity, event.partialRenderTick)
            GlStateManager.pushMatrix()
            GlStateManager.translate(0.0F, 0.0F, 0.125F)

            val modelElytra = event.renderer.layerRenderers.get(event.renderer.layerRenderers.size() - 1)
                    .asInstanceOf[LayerElytra].modelElytra
            modelElytra.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, Minecraft.getMinecraft.thePlayer)
            modelElytra.render(Minecraft.getMinecraft.thePlayer, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale)

            GlStateManager.popMatrix()
            GlStateManager.popMatrix()
        }
    }*/

    @SubscribeEvent
    def renderBlockEvent(event : RenderWorldLastEvent): Unit = {
        val controllerMP = Minecraft.getMinecraft.playerController
        val player = Minecraft.getMinecraft.thePlayer
        val world = player.worldObj
        var blockList : java.util.List[BlockPos] = null

        // Add Boxes
        if(player.getHeldItemMainhand != null && ModifierAOE.getAOELevel(player.getHeldItemMainhand) > 0) {
            val movingObjectPosition = player.rayTrace(controllerMP.getBlockReachDistance, event.partialTicks)
            if(movingObjectPosition != null && world.getBlockState(movingObjectPosition.getBlockPos)
                    .getBlock.getMaterial(world.getBlockState(movingObjectPosition.getBlockPos)) != Material.air) {
                val level = ModifierAOE.getAOELevel(player.getHeldItemMainhand)
                blockList = ToolHelper.getBlockList(level, movingObjectPosition, player, world, player.getHeldItemMainhand)
                for(x <- blockList.toArray)
                    event.context.drawSelectionBox(player,
                        new RayTraceResult(new Vec3d(0, 0, 0), null, x.asInstanceOf[BlockPos]), 0, event.partialTicks)
            }
        }

        // Draw Breaking
        if(blockList != null && !blockList.isEmpty && controllerMP.isHittingBlock) {
            if(controllerMP.currentItemHittingBlock != null && ModifierAOE.getAOELevel(controllerMP.currentItemHittingBlock) > 0) {
                drawDamageOnBlocks(Tessellator.getInstance(), Tessellator.getInstance().getBuffer,
                    player, event.partialTicks, world, controllerMP.currentBlock, blockList)
            }
        }
    }

    def drawDamageOnBlocks(tessellatorIn : Tessellator, worldRendererIn : VertexBuffer,
                           entityIn : Entity, partialTicks : Float, world : World, blockIn : BlockPos,
                           blocks : java.util.List[BlockPos]) : Unit = {
        // Interpolate to player movement
        val d0 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks.toDouble
        val d1 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks.toDouble
        val d2 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks.toDouble

        val progress = (Minecraft.getMinecraft.playerController.curBlockDamageMP * 10F).toInt - 1 // Scale between 0 - 10

        if(progress < 0) return //If something shouldn't be rendered

        // Set up state
        RenderUtils.bindMinecraftBlockSheet()
        GlStateManager.tryBlendFuncSeparate(774, 768, 1, 0)
        GlStateManager.enableBlend()
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F)
        GlStateManager.doPolygonOffset(-3.0F, -3.0F)
        GlStateManager.enablePolygonOffset()
        GlStateManager.alphaFunc(516, 0.1F)
        GlStateManager.enableAlpha()
        GlStateManager.pushMatrix()

        worldRendererIn.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK)
        worldRendererIn.setTranslation(-d0, -d1, -d2)

        // Loop blocks, add vertex
        for(pos <- blocks.toArray) {
            val blockPosition = pos.asInstanceOf[BlockPos]
            val block = world.getBlockState(blockPosition).getBlock
            val tile = world.getTileEntity(blockPosition)
            var breaksSelf = block.isInstanceOf[BlockChest] || block.isInstanceOf[BlockEnderChest] ||
                    block.isInstanceOf[BlockSign] || block.isInstanceOf[BlockSkull]
            if(!breaksSelf) breaksSelf = tile != null && tile.canRenderBreaking
            if(!breaksSelf && blockPosition != blockIn) {
                val state = world.getBlockState(blockPosition)
                if(state.getBlock.getMaterial(world.getBlockState(blockPosition)) != Material.air)
                    Minecraft.getMinecraft.getBlockRendererDispatcher
                            .renderBlockDamage(state, blockPosition,  destroyIcons(progress), world)
            }
        }

        tessellatorIn.draw()
        worldRendererIn.setTranslation(0.0D, 0.0D, 0.0D)
        GlStateManager.disableAlpha()
        GlStateManager.doPolygonOffset(0.0F, 0.0F)
        GlStateManager.disablePolygonOffset()
        GlStateManager.enableAlpha()
        GlStateManager.depthMask(true)
        GlStateManager.popMatrix()
    }

    override def onResourceManagerReload(resourceManager: IResourceManager): Unit = {
        val textureMap = Minecraft.getMinecraft.getTextureMapBlocks
        for(x <- destroyIcons.indices)
            destroyIcons(x) = textureMap.getAtlasSprite("minecraft:blocks/destroy_stage_" + x)
    }

    def interpolateRotation (par1: Float, par2: Float, par3: Float) : Float = {
        var f: Float = 0.0F

        f = par2 - par1
        while (f < -180.0F) {
            {
            }
            f += 360.0F
        }

        while (f >= 180.0F) {
            f -= 360.0F
        }
        par1 + par3 * f
    }

    def prepareScale(entity : EntityLivingBase, partialTicks : Float): Float = {
        GlStateManager.enableRescaleNormal()
        GlStateManager.scale(-1.0F, -1.0F, 1.0F)
        val f: Float = 0.0625F
        GlStateManager.translate(0.0F, -1.501F, 0.0F)
        f
    }
}
