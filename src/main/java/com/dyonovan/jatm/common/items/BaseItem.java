package com.dyonovan.jatm.common.items;

import com.dyonovan.jatm.JATM;
import com.dyonovan.jatm.lib.Constants;
import net.minecraft.item.Item;

public class BaseItem extends Item {

    private String name;

    public BaseItem(String name, int stackSize) {
        this.setCreativeTab(JATM.tabJATM);
        this.setMaxStackSize(stackSize);
        this.setUnlocalizedName(Constants.MODID + ":" + name);

        this.name = name;
    }

    public String getName() {
        return name;
    }
}
