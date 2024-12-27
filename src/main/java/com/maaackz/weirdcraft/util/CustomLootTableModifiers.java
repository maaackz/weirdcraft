package com.maaackz.weirdcraft.util;

import com.maaackz.weirdcraft.item.CustomItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.condition.RandomChanceWithEnchantedBonusLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.EnchantedCountIncreaseLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;

public class CustomLootTableModifiers {
    private static final Identifier DESERT_TEMPLE_ID = Identifier.of("minecraft", "chests/desert_pyramid");
    private static final Identifier JUNGLE_LEAVES_ID = Identifier.of("minecraft", "blocks/jungle_leaves");

    public static void modifyLootTables() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            Identifier id = key.getValue();

            if (DESERT_TEMPLE_ID.equals(id)) {
                LootPool pool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.025f))
                        .with(ItemEntry.builder(CustomItems.SAND_OCEAN_MUSIC_DISC))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)).build())
                        .build();

                // Use modifyPools to add the pool
                tableBuilder.pool(pool);
            }

            if (JUNGLE_LEAVES_ID.equals(id)) {
                LootPool pool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(1f))
                        .conditionally(RandomChanceWithEnchantedBonusLootCondition.builder(registries, 0.025f, 0.01f))
                        .with(ItemEntry.builder(CustomItems.BANANA))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)).build())
                        .apply(EnchantedCountIncreaseLootFunction.builder(registries, UniformLootNumberProvider.create(0.0f, 1.0f)).withLimit(3).build())
                        .build();

                // Use modifyPools to add the pool
                tableBuilder.pool(pool);
            }
        });
    }
}
