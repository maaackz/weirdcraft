package com.maaackz.weirdcraft.item;

import com.maaackz.weirdcraft.Weirdcraft;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class CustomArmorMaterials {
    public static final RegistryEntry<ArmorMaterial> WEIRDIUM_ARMOR_MATERIAL = registerArmorMaterial("weirdium",
            () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, 4);       // Diamond: 3
                map.put(ArmorItem.Type.LEGGINGS, 7);    // Diamond: 6
                map.put(ArmorItem.Type.CHESTPLATE, 9);  // Diamond: 8
                map.put(ArmorItem.Type.HELMET, 4);      // Diamond: 3
                map.put(ArmorItem.Type.BODY, 12);        // Custom addition
            }), 15,                                     // Durability multiplier (Diamond: 33)
                    SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND,
                    () -> Ingredient.ofItems(CustomItems.WEIRDIUM_INGOT),
                    List.of(new ArmorMaterial.Layer(Identifier.of(Weirdcraft.MOD_ID, "weirdium"))),
                    3.0f,                               // Toughness (Diamond: 2.0f)
                    0.1f));                             // Knockback resistance (Diamond: 0.0f)

    public static final RegistryEntry<ArmorMaterial> POOP_ARMOR_MATERIAL = registerArmorMaterial("poop",
            () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, 1);       // Diamond: 3
                map.put(ArmorItem.Type.LEGGINGS, 2);    // Diamond: 6
                map.put(ArmorItem.Type.CHESTPLATE, 3);  // Diamond: 8
                map.put(ArmorItem.Type.HELMET, 1);      // Diamond: 3
                map.put(ArmorItem.Type.BODY, 3);        // Custom addition
            }), 15,                                     // Durability multiplier (Diamond: 33)
                    SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
                    () -> Ingredient.ofItems(CustomItems.POOP),
                    List.of(new ArmorMaterial.Layer(Identifier.of(Weirdcraft.MOD_ID, "poop"))),
                    0.0f,                               // Toughness (Diamond: 2.0f)
                    -0.1f));                             // Knockback resistance (Diamond: 0.0f)


    public static RegistryEntry<ArmorMaterial> registerArmorMaterial(String name, Supplier<ArmorMaterial> material) {
        return Registry.registerReference(Registries.ARMOR_MATERIAL, Identifier.of(Weirdcraft.MOD_ID, name), material.get());
    }

}
