package com.dyonovan.jatm.client.modelfactory;

import com.dyonovan.jatm.client.modelfactory.models.ModelBlock;
import com.dyonovan.jatm.client.modelfactory.models.ModelCable;
import com.dyonovan.jatm.client.modelfactory.models.ModelTank;
import com.dyonovan.jatm.common.blocks.BlockBakeable;
import com.dyonovan.jatm.common.blocks.BlockBasicCable;
import com.dyonovan.jatm.common.blocks.BlockMachine;
import com.dyonovan.jatm.common.blocks.BlockTank;
import com.dyonovan.jatm.handlers.BlockHandler;
import com.dyonovan.jatm.lib.Constants;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class ModelGenerator {
    public static ModelGenerator INSTANCE = new ModelGenerator();

    public static void register() {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    // Allows us to add entries for our icons
    public void textureStitch(TextureStitchEvent.Pre event) {

        TextureMap textureMap = event.map;

        //Register Side Icons
        textureMap.registerSprite(new ResourceLocation(Constants.MODID, "blocks/" + "machine_side"));

        //Register Cable Icons
        textureMap.registerSprite(new ResourceLocation(Constants.MODID + ":blocks/basicCableNoEdge"));
        textureMap.registerSprite(new ResourceLocation(Constants.MODID + ":blocks/basicCablePlus"));
        textureMap.registerSprite(new ResourceLocation(Constants.MODID + ":blocks/basicCableSquare"));

        //Register Front Icons
        for(BlockBakeable block : BlockHandler.blockRegistry) {
            textureMap.registerSprite(block.getFrontIcon());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    // This allows us to create and add Baked Models to the registry
    public void bakeModels(ModelBakeEvent event) {
        ItemModelMesher itemModelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();

        for(BlockBakeable block : BlockHandler.blockRegistry) {
            /**
             * Cables
             */
            if(block instanceof BlockBasicCable) {
                //Build Normal Model
                event.modelRegistry.putObject(block.getNormal(), new ModelCable());
                //Build Inventory Model
                event.modelRegistry.putObject(block.getInventory(), new ModelCable());
                //Register the Model to the item
                itemModelMesher.register(Item.getItemFromBlock(block), 0, block.getInventory());
            }

            /**
             * Tank
             */
            else if(block instanceof BlockTank) {
                //Build Normal Block
                event.modelRegistry.putObject(block.getNormal(), new ModelTank());
                //Build Inventory Model
                event.modelRegistry.putObject(block.getInventory(), new ModelTank());
                //Register item model
                itemModelMesher.register(Item.getItemFromBlock(block), 0, block.getInventory());
            }

            /**
             * Blocks
             */
            else {
                for(IBlockState state : block.generateRotatableStates()) {
                    //Build Normal Block
                    event.modelRegistry.putObject(ModelBlock.getModelResourceLocation(state), new ModelBlock());
                }
                //Build Inventory block
                event.modelRegistry.putObject(block.getInventory(), new ModelBlock());
                //Register Item Model
                itemModelMesher.register(Item.getItemFromBlock(block), 0, block.getInventory());
            }
        }
    }
}
