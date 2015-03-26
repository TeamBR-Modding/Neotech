package com.dyonovan.neotech.common.items;

import com.dyonovan.neotech.NeoTech;
import com.dyonovan.neotech.lib.Constants;
import net.minecraft.item.Item;

public class BaseItem extends Item {

    private String name;

    public BaseItem(String name, int stackSize) {
        this.setCreativeTab(NeoTech.tabNeoTech);
        this.setMaxStackSize(stackSize);
        this.setUnlocalizedName(Constants.MODID + ":" + name);

        this.name = name;
    }

    public String getName() {
        return name;
    }
}
