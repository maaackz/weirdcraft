package com.maaackz.weirdcraft.item.custom;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class NocturnesKissItem extends Item {
    public NocturnesKissItem(Settings settings) {
        super(settings);

    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }


}
