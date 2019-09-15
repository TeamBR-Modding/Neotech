package com.teambrmodding.neotech.common.item;

import com.teambr.nucleus.common.IAdvancedToolTipProvider;
import com.teambr.nucleus.util.ClientUtils;
import com.teambrmodding.neotech.lib.Reference;
import com.teambrmodding.neotech.managers.ItemManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * This file was created for Neotech
 *
 * Neotech is licensed under the
 * Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License:
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @author James Rogers - Dyonovan
 * @since 9/8/2019
 */
public class DiskBase extends Item implements IAdvancedToolTipProvider {

    private String name;

    public DiskBase(String name) {
        super(new Properties()
                .maxStackSize(1)
                .group(ItemManager.itemGroupNeotech));
        setRegistryName(name);

        this.name = name;
    }

    /*******************************************************************************************************************
     * IAdvancedToolTipProvider                                                                                        *
     *******************************************************************************************************************/

    @OnlyIn(Dist.CLIENT)
    @Nullable
    @Override
    public List<String> getAdvancedToolTip(@Nonnull ItemStack stack) {
        return Collections.singletonList(ClientUtils.translate(Reference.MOD_ID + "." + name + ".desc"));
    }
}
