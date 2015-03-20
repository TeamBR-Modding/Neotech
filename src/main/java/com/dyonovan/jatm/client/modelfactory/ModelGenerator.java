package com.dyonovan.jatm.client.modelfactory;

import com.dyonovan.jatm.common.blocks.BlockBakeable;
import com.dyonovan.jatm.common.blocks.BlockMachine;
import com.dyonovan.jatm.handlers.BlockHandler;
import com.dyonovan.jatm.lib.Constants;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class ModelGenerator {
    public static ModelGenerator INSTANCE = new ModelGenerator();
    public static HashMap<String, TextureAtlasSprite> iconMap;

    public static void register() {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @SubscribeEvent
    // Allows us to add entries for our icons
    public void textureStitch(TextureStitchEvent.Pre event) {
        iconMap = new HashMap<>();

        TextureMap textureMap = event.map;

        //Register Side Icons
        textureMap.registerSprite(new ResourceLocation(Constants.MODID, "machine_side"));

        //Register Front Icons
        for(BlockBakeable machines : BlockHandler.blockRegistry) {
            textureMap.registerSprite(machines.getFrontIcon());
            iconMap.put(machines.getName(), textureMap.getAtlasSprite(machines.getFrontIcon().toString()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    // This allows us to create and add Baked Models to the registry
    public void bakeModels(ModelBakeEvent event) {
        ModelRegistry.models = new ArrayList<>();
        ModelRegistry.invModels = new ArrayList<>();
        ItemModelMesher itemModelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();

        for(BlockBakeable block : BlockHandler.blockRegistry) {
            //Block Model
            ModelResourceLocation modelResourceLocation = ModelBuilder.getModelResourceLocation(block.getDefaultState());

            //Baked Model For State
            IFlexibleBakedModel baseModel = (IFlexibleBakedModel)event.modelManager.getBlockModelShapes().getModelForState(block.getDefaultState());



        }
    }
}
