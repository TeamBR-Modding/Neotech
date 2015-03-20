package com.dyonovan.jatm;

import com.dyonovan.jatm.client.modelfactory.ModelGenerator;
import com.dyonovan.jatm.handlers.*;
import com.dyonovan.jatm.lib.Constants;
import com.dyonovan.jatm.common.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(name = Constants.MODNAME, modid = Constants.MODID, version = Constants.VERSION)
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
    public void preInit(FMLPreInitializationEvent event)
    {
        BlockHandler.preInit();
        ItemHandler.PreInit();
        CraftingHandler.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        if (event.getSide() == Side.CLIENT) {
            RenderHandler.init();
            ModelGenerator.register();
        }

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent evt)
    {

    }
}
