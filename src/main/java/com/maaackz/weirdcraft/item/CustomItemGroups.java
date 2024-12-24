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
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemgroup.weirdcraft"))
                    .icon(() -> new ItemStack(CustomItems.SAND_OCEAN_MUSIC_DISC))
                    .entries((displayContext, entries) -> {
                        // Add all custom items to the group
                        entries.add(CustomItems.SAND_OCEAN_MUSIC_DISC);
                        entries.add(CustomItems.POOP);
                        entries.add(CustomItems.BANANA);
                        entries.add(CustomItems.GOLDEN_BANANA);
                        entries.add(CustomItems.ENCHANTED_GOLDEN_BANANA);
                        entries.add(CustomItems.RAW_WEIRDIUM);
                        entries.add(CustomItems.WEIRDIUM_INGOT);
                        entries.add(CustomItems.POCKET_WATCH);
                        entries.add(CustomItems.RAINES_CLOUD);
                        entries.add(CustomItems.NOCTURNES_KISS);

                        // Add Weirdium armor pieces
                        entries.add(CustomItems.WEIRDIUM_HELMET);
                        entries.add(CustomItems.WEIRDIUM_CHESTPLATE);
                        entries.add(CustomItems.WEIRDIUM_LEGGINGS);
                        entries.add(CustomItems.WEIRDIUM_BOOTS);

                        // Add Weirdium tools
                        entries.add(CustomItems.WEIRDIUM_SWORD);
                        entries.add(CustomItems.WEIRDIUM_PICKAXE);
                        entries.add(CustomItems.WEIRDIUM_AXE);
                        entries.add(CustomItems.WEIRDIUM_SHOVEL);
                        entries.add(CustomItems.WEIRDIUM_HOE);
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