package com.maaackz.weirdcraft.item;

import com.maaackz.weirdcraft.Weirdcraft;
import com.maaackz.weirdcraft.item.custom.EnchantedGoldenBananaItem;
import com.maaackz.weirdcraft.item.custom.PocketWatchItem;
import com.maaackz.weirdcraft.sound.CustomSounds;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
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
