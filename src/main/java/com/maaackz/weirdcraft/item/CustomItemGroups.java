package com.maaackz.weirdcraft.item;

import com.maaackz.weirdcraft.Weirdcraft;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CustomItemGroups {
    public static final ItemGroup WEIRDCRAFT_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(Weirdcraft.MOD_ID, "weirdcraft"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.weirdcraft"))
                    .icon(() -> new ItemStack(CustomItems.SAND_OCEAN_MUSIC_DISC)).entries((displayContext, entries) -> {

                        entries.add(CustomItems.SAND_OCEAN_MUSIC_DISC);

                    }).build());

    public static void registerToVanillaItemGroups() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {

            content.addAfter(Items.MUSIC_DISC_RELIC, CustomItems.SAND_OCEAN_MUSIC_DISC);

        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(content -> {

//            content.addAfter(Items.GOLDEN_APPLE, CustomItems.SAND_OCEAN_MUSIC_DISC);

        });
    }

    public static void registerItemGroups() {
        Weirdcraft.LOGGER.info("Registering item groups for " + Weirdcraft.MOD_ID);
    }
}