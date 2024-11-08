package com.maaackz.weirdcraft.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;

public class ShockCollarItem extends DyeableArmorItem {

    public ShockCollarItem(ArmorMaterial armorMaterial, Type type, Settings settings) {
        super(armorMaterial, type, settings);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        // Allow the Shock Collar to be enchanted
        return true;
    }

    @Override
    public int getEnchantability() {
        // Sets enchantability, but only Curse of Binding will be allowed
        return 10;
    }


//    @Override
//    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
//        // Only allow Curse of Binding
//        return enchantment == Enchantments.BINDING_CURSE;
//    }
//
//    @Override
//    public boolean isAcceptableEnchantments(ItemStack stack, Enchantment enchantment) {
//        // Ensure that only Curse of Binding can be applied
//        return enchantment == Enchantments.BINDING_CURSE;
//    }
 
    @Override
    public EquipmentSlot getSlotType() {
        // Define which slot the item goes in, for the Shock Collar, let's assume chest
        return EquipmentSlot.HEAD;
    }
}
