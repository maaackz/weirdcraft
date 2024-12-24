package com.maaackz.weirdcraft.item;

import com.maaackz.weirdcraft.Weirdcraft;
import com.maaackz.weirdcraft.item.custom.EnchantedGoldenBananaItem;
import com.maaackz.weirdcraft.item.custom.PocketWatchItem;
import com.maaackz.weirdcraft.item.custom.WeirdiumToolMaterial;
import com.maaackz.weirdcraft.sound.CustomSounds;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class CustomItems {

    public static final Item SAND_OCEAN_MUSIC_DISC = registerItem("sand_ocean_music_disc",
        new Item(new Item.Settings().jukeboxPlayable(CustomSounds.SAND_OCEAN_KEY).maxCount(1)));

//    public static final Item POOP = registerItem("poop",
//            new Item(7, CustomSounds.poop, new FabricItemSettings().maxCount(1), 79));

    public static final Item POOP = registerItem("poop",
            new Item(new Item.Settings().food(CustomFoodComponents.POOP)));

    public static final Item BANANA = registerItem("banana",
            new Item(new Item.Settings().food(CustomFoodComponents.BANANA)));

    public static final Item GOLDEN_BANANA = registerItem("golden_banana",
            new Item(new Item.Settings()
                    .food(CustomFoodComponents.GOLDEN_BANANA)
                    .rarity(Rarity.RARE)
            )
    );

    public static final Item ENCHANTED_GOLDEN_BANANA = registerItem("enchanted_golden_banana",
            new EnchantedGoldenBananaItem(new Item.Settings()
                    .food(CustomFoodComponents.ENCHANTED_GOLDEN_BANANA)
                    .rarity(Rarity.EPIC)
            )
    );

    public static final Item RAW_WEIRDIUM = registerItem("raw_weirdium",
            new Item(new Item.Settings())
    );

    public static final Item WEIRDIUM_INGOT = registerItem("weirdium_ingot",
            new Item(new Item.Settings())
    );

    public static final Item POCKET_WATCH = registerItem("pocket_watch",
            new PocketWatchItem(new Item.Settings()
                    .rarity(Rarity.EPIC)
            )
    );

    public static final Item RAINES_CLOUD = registerItem("raines_cloud",
            new Item(new Item.Settings()
                    .rarity(Rarity.EPIC)
            )
    );

    public static final Item NOCTURNES_KISS = registerItem("nocturnes_kiss",
            new Item(new Item.Settings()
                    .rarity(Rarity.EPIC)
            )
    );

    // Armor pieces
    public static final Item WEIRDIUM_HELMET = registerItem("weirdium_helmet",
            new ArmorItem(CustomArmorMaterials.WEIRDIUM_ARMOR_MATERIAL, ArmorItem.Type.HELMET,
                    new Item.Settings().rarity(Rarity.EPIC))
    );

    public static final Item WEIRDIUM_CHESTPLATE = registerItem("weirdium_chestplate",
            new ArmorItem(CustomArmorMaterials.WEIRDIUM_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE,
                    new Item.Settings().rarity(Rarity.EPIC))
    );

    public static final Item WEIRDIUM_LEGGINGS = registerItem("weirdium_leggings",
            new ArmorItem(CustomArmorMaterials.WEIRDIUM_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS,
                    new Item.Settings().rarity(Rarity.EPIC))
    );

    public static final Item WEIRDIUM_BOOTS = registerItem("weirdium_boots",
            new ArmorItem(CustomArmorMaterials.WEIRDIUM_ARMOR_MATERIAL, ArmorItem.Type.BOOTS,
                    new Item.Settings().rarity(Rarity.EPIC))
    );

    // Tools
    public static final Item WEIRDIUM_SWORD = registerItem("weirdium_sword",
            new SwordItem(WeirdiumToolMaterial.INSTANCE, new Item.Settings().rarity(Rarity.EPIC).fireproof()
                    .attributeModifiers(SwordItem.createAttributeModifiers(WeirdiumToolMaterial.INSTANCE,12,0.6f)))
    );

    public static final Item WEIRDIUM_PICKAXE = registerItem("weirdium_pickaxe",
            new PickaxeItem(WeirdiumToolMaterial.INSTANCE, new Item.Settings().rarity(Rarity.EPIC).fireproof()
                    .attributeModifiers(PickaxeItem.createAttributeModifiers(WeirdiumToolMaterial.INSTANCE,5,0.2f)))
    );

    public static final Item WEIRDIUM_AXE = registerItem("weirdium_axe",
            new AxeItem(WeirdiumToolMaterial.INSTANCE, new Item.Settings().rarity(Rarity.EPIC).fireproof()
                    .attributeModifiers(AxeItem.createAttributeModifiers(WeirdiumToolMaterial.INSTANCE,15,0.4f)))
    );

    public static final Item WEIRDIUM_SHOVEL = registerItem("weirdium_shovel",
            new ShovelItem(WeirdiumToolMaterial.INSTANCE, new Item.Settings().rarity(Rarity.EPIC).fireproof()
                    .attributeModifiers(ShovelItem.createAttributeModifiers(WeirdiumToolMaterial.INSTANCE,8,0.5f)))
    );

    public static final Item WEIRDIUM_HOE = registerItem("weirdium_hoe",
            new HoeItem(WeirdiumToolMaterial.INSTANCE, new Item.Settings().rarity(Rarity.EPIC).fireproof()
                    .attributeModifiers(HoeItem.createAttributeModifiers(WeirdiumToolMaterial.INSTANCE,7,1f)))
    );


//    public static final Item SHOCK_COLLAR = registerItem("shock_collar",
//            new ShockCollarItem(
//                    ArmorMaterials.LEATHER,
//                    ArmorItem.Type.HELMET,
//                    new FabricItemSettings()
//
//            )
//    );

    private static void addItemsToIngredientItemsGroup(FabricItemGroupEntries entries) {

    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Weirdcraft.MOD_ID, name), item);
    }

    public static void registerItems() {
        Weirdcraft.LOGGER.info(("Registering mod items for " + Weirdcraft.MOD_ID));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(CustomItems::addItemsToIngredientItemsGroup);
    }
}
