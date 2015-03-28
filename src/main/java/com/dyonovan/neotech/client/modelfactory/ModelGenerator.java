package com.dyonovan.neotech.client.modelfactory;

import com.dyonovan.neotech.client.modelfactory.models.ModelBlock;
import com.dyonovan.neotech.client.modelfactory.models.ModelPipe;
import com.dyonovan.neotech.client.modelfactory.models.ModelTank;
import com.dyonovan.neotech.common.blocks.BlockBakeable;
import com.dyonovan.neotech.common.blocks.pipe.BlockPipe;
import com.dyonovan.neotech.common.blocks.pipe.item.BlockPipeBasicItem;
import com.dyonovan.neotech.common.blocks.storage.BlockTank;
import com.dyonovan.neotech.handlers.BlockHandler;
import com.dyonovan.neotech.lib.Constants;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
        textureMap.registerSprite(new ResourceLocation(Constants.MODID, "blocks/machine_side"));

        //Register Pipe Stuff
        textureMap.registerSprite(new ResourceLocation(Constants.MODID, "blocks/pipeExtract"));

        //Register Front Icons
        for(BlockBakeable block : BlockHandler.blockRegistry) {
            if(block instanceof BlockPipe) {
                if(textureMap.getAtlasSprite(((BlockPipe)block).getBackgroundTexture()) == null)
                    textureMap.registerSprite(new ResourceLocation(((BlockPipe)block).getBackgroundTexture()));
                textureMap.registerSprite(new ResourceLocation(Constants.MODID, "blocks/" + block.getName()));
                if(block instanceof BlockPipeBasicItem)
                    textureMap.registerSprite(new ResourceLocation(((BlockPipeBasicItem)block).getJunction()));
            } else {
                for(ResourceLocation location : block.registerIcons()) {
                    textureMap.registerSprite(location);
                }
            }
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
            if(block instanceof BlockPipe) {
                //Build Normal Model
                event.modelRegistry.putObject(block.getNormal(), new ModelPipe((BlockPipe)block));
                //Build Inventory Model
                event.modelRegistry.putObject(block.getInventory(), new ModelPipe((BlockPipe)block));
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
                    event.modelRegistry.putObject(ModelBlock.getModelResourceLocation(state), new ModelBlock(block));
                }
                //Build Inventory block
                event.modelRegistry.putObject(block.getInventory(), new ModelBlock(block));
                //Register Item Model
                itemModelMesher.register(Item.getItemFromBlock(block), 0, block.getInventory());
            }
        }
    }
}
