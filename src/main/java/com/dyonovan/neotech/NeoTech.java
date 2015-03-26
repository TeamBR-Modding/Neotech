package com.dyonovan.neotech;

import com.dyonovan.neotech.events.OnCraftingEvent;
import com.dyonovan.neotech.events.ToolTipEvent;
import com.dyonovan.neotech.handlers.*;
import com.dyonovan.neotech.lib.Constants;
import com.dyonovan.neotech.common.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(name = Constants.MODNAME, modid = Constants.MODID, version = Constants.VERSION, dependencies = Constants.DEPENDENCIES)
public class NeoTech {

    @Mod.Instance(Constants.MODID)
    public static NeoTech instance;

    @SidedProxy(clientSide = "com.dyonovan.neotech.client.ClientProxy",
            serverSide = "com.dyonovan.neotech.common.CommonProxy")
    public static CommonProxy proxy;

    public static CreativeTabs tabNeoTech = new CreativeTabs("tabNeoTech") {
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
        FMLCommonHandler.instance().bus().register(new OnCraftingEvent());

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
