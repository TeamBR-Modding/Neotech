package com.dyonovan.jatm;

import com.dyonovan.jatm.client.modelfactory.ModelGenerator;
import com.dyonovan.jatm.events.ToolTipEvent;
import com.dyonovan.jatm.handlers.*;
import com.dyonovan.jatm.lib.Constants;
import com.dyonovan.jatm.common.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;

@Mod(name = Constants.MODNAME, modid = Constants.MODID, version = Constants.VERSION, dependencies = Constants.DEPENDENCIES)
public class JATM {

    @Mod.Instance(Constants.MODID)
    public static JATM instance;

    @SidedProxy(clientSide = "com.dyonovan.jatm.client.ClientProxy",
            serverSide = "com.dyonovan.jatm.common.CommonProxy")
    public static CommonProxy proxy;

    public static CreativeTabs tabJATM = new CreativeTabs("tabJATM") {
        @Override
        public Item getTabIconItem() {
            return Item.getItemFromBlock(BlockHandler.electricFurnace);
        }
    };

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigHandler.init(new Configuration(event.getSuggestedConfigurationFile()));

        BlockHandler.preInit();
        ItemHandler.PreInit();
        CraftingHandler.preInit();

        MinecraftForge.EVENT_BUS.register(new ToolTipEvent());

        GameRegistry.registerWorldGenerator(new WorldGenHandler(), 2);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        /*if (event.getSide() == Side.CLIENT) {
            RenderHandler.init();
            ModelGenerator.register();
        }*/
        proxy.init();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent evt) {}
}
