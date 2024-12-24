package com.maaackz.weirdcraft.datagen;

import com.maaackz.weirdcraft.block.CustomBlocks;
import com.maaackz.weirdcraft.item.CustomItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class CustomLootTableProvider extends FabricBlockLootTableProvider {
    public CustomLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        RegistryWrapper.Impl<Enchantment> impl = this.registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);

        addDrop(CustomBlocks.WEIRDIUM_BLOCK);
        addDrop(CustomBlocks.RAW_WEIRDIUM_BLOCK);
//        addDrop(CustomBlocks.MAGIC_BLOCK);

        addDrop(CustomBlocks.WEIRDIUM_ORE, oreDrops(CustomBlocks.WEIRDIUM_ORE, CustomItems.RAW_WEIRDIUM));
        addDrop(CustomBlocks.DEEPSLATE_WEIRDIUM_ORE, multipleOreDrops(CustomBlocks.DEEPSLATE_WEIRDIUM_ORE, CustomItems.RAW_WEIRDIUM, 3, 7));

//        addDrop(CustomBlocks.PINK_GARNET_STAIRS);
//        addDrop(CustomBlocks.PINK_GARNET_SLAB, slabDrops(CustomBlocks.PINK_GARNET_SLAB));

//        addDrop(CustomBlocks.PINK_GARNET_BUTTON);
//        addDrop(CustomBlocks.PINK_GARNET_PRESSURE_PLATE);

//        addDrop(CustomBlocks.PINK_GARNET_WALL);
//        addDrop(CustomBlocks.PINK_GARNET_FENCE);
//        addDrop(CustomBlocks.PINK_GARNET_FENCE_GATE);

//        addDrop(CustomBlocks.PINK_GARNET_DOOR, doorDrops(CustomBlocks.PINK_GARNET_DOOR));
//        addDrop(CustomBlocks.PINK_GARNET_TRAPDOOR);

//        addDrop(CustomBlocks.PINK_GARNET_END_ORE, multipleOreDrops(CustomBlocks.PINK_GARNET_END_ORE, ModItems.RAW_PINK_GARNET, 4, 9));
//        addDrop(CustomBlocks.PINK_GARNET_NETHER_ORE, multipleOreDrops(CustomBlocks.PINK_GARNET_NETHER_ORE, ModItems.RAW_PINK_GARNET, 3, 8));

//        addDrop(CustomBlocks.DRIFTWOOD_LOG);
//        addDrop(CustomBlocks.DRIFTWOOD_WOOD);
//        addDrop(CustomBlocks.STRIPPED_DRIFTWOOD_LOG);
//        addDrop(CustomBlocks.STRIPPED_DRIFTWOOD_WOOD);
//        addDrop(CustomBlocks.DRIFTWOOD_PLANKS);
//        addDrop(CustomBlocks.DRIFTWOOD_SAPLING);

//        addDrop(CustomBlocks.DRIFTWOOD_LEAVES, leavesDrops(CustomBlocks.DRIFTWOOD_LEAVES, CustomBlocks.DRIFTWOOD_SAPLING, 0.0625f));
    }

    public LootTable.Builder multipleOreDrops(Block drop, Item item, float minDrops, float maxDrops) {
        RegistryWrapper.Impl<Enchantment> impl = this.registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
        return this.dropsWithSilkTouch(drop, this.applyExplosionDecay(drop, ((LeafEntry.Builder<?>)
                ItemEntry.builder(item).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(minDrops, maxDrops))))
                .apply(ApplyBonusLootFunction.oreDrops(impl.getOrThrow(Enchantments.FORTUNE)))));
    }
}