package com.dyonovan.jatm.client.modelfactory;

import com.dyonovan.jatm.common.blocks.BlockBakeable;
import com.dyonovan.jatm.common.cable.FakeState;
import com.dyonovan.jatm.handlers.BlockHandler;
import com.dyonovan.jatm.lib.Constants;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    // Allows us to add entries for our icons
    public void textureStitch(TextureStitchEvent.Pre event) {
        iconMap = new HashMap<>();

        TextureMap textureMap = event.map;

        //Register Side Icons
        iconMap.put("side", textureMap.registerSprite(new ResourceLocation(Constants.MODID, "blocks/" + "machine_side")));

        //Register Cable Icons
        iconMap.put("cableNoEdges", textureMap.registerSprite(new ResourceLocation(Constants.MODID + ":blocks/cableNoEdge")));
        iconMap.put("cablePlus", textureMap.registerSprite(new ResourceLocation(Constants.MODID + ":blocks/cablePlus")));
        iconMap.put("cableSquare", textureMap.registerSprite(new ResourceLocation(Constants.MODID + ":blocks/cableSquare")));

        //Register Front Icons
        for(BlockBakeable block : BlockHandler.blockRegistry) {
            iconMap.put(block.getName(), textureMap.registerSprite(block.getFrontIcon()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    // This allows us to create and add Baked Models to the registry
    public void bakeModels(ModelBakeEvent event) {
        ModelRegistry.models = new ArrayList<>();
        ModelRegistry.invModels = new ArrayList<>();
        ItemModelMesher itemModelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();

        for(BlockBakeable block : BlockHandler.blockRegistry) {
            for(IBlockState state : block.createDefaultStates()) {
                /**
                 * Blocks
                 */
                //Get resource
                ModelResourceLocation modelResourceLocation = ModelMachine.getModelResourceLocation(state);

                //Baked Model For State
                IFlexibleBakedModel baseModel = (IFlexibleBakedModel) event.modelManager.getBlockModelShapes().getModelForState(state);

                //Build new Model
                ModelRegistry.models.add(ModelMachine.changeIcon(baseModel, iconMap.get("side"), iconMap.get(block.getName()), block.convertStateToEnum(state)));

                //Drop it in the registry
                event.modelRegistry.putObject(modelResourceLocation, ModelRegistry.models.get(ModelRegistry.models.size() - 1));
            }

            /**
             * Cables
             */
            event.modelRegistry.putObject(new ModelResourceLocation(Constants.MODID + ":cable", "normal"), new ModelCable());
            event.modelRegistry.putObject(new ModelResourceLocation(Constants.MODID + ":cable", "inventory"), new ModelCable());
            itemModelMesher.register(Item.getItemFromBlock(BlockHandler.blockCable), 0, new ModelResourceLocation(Constants.MODID + ":cable", "inventory"));

            /**
             * Items
             */
            //Get Model
            IFlexibleBakedModel itemModel = (IFlexibleBakedModel) itemModelMesher.getItemModel(new ItemStack(block));

            ModelResourceLocation modelResourceLocation = ModelMachine.getModelResourceLocation(block.getDefaultState());
            //Get the inventory resource
            ModelResourceLocation inventory = new ModelResourceLocation(modelResourceLocation, "inventory");

            //Build New Model
            ModelRegistry.invModels.add(ModelMachine.changeIcon(itemModel, iconMap.get("side"), iconMap.get(block.getName()), EnumFacing.NORTH));

            //Drop it in the registry
            event.modelRegistry.putObject(inventory, ModelRegistry.invModels.get(ModelRegistry.invModels.size() - 1));

            //Register to the ItemModelMesher
            itemModelMesher.register(Item.getItemFromBlock(block), 0, inventory);
        }
    }
}
