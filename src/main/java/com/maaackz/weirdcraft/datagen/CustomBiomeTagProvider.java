package com.maaackz.weirdcraft.datagen;

import com.maaackz.weirdcraft.util.CustomTags;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.vanilla.VanillaBiomeTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import java.util.concurrent.CompletableFuture;

public class CustomBiomeTagProvider extends VanillaBiomeTagProvider {

    public CustomBiomeTagProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registryLookup) {
        getOrCreateTagBuilder(CustomTags.Biomes.PYRAMID_HAS_STRUCTURE)
                .add(BiomeKeys.DESERT);
    }

    public static final TagKey<Biome> HAS_PYRAMID = TagKey.of(RegistryKeys.BIOME, Identifier.of("weirdcraft:has_structure/pyramid"));
//    public static final TagKey<Structure> ON_ISLAND_MAPS = TagKey.of(RegistryKeys.STRUCTURE, GadgetsOfTheSky.id("on_island_maps"));

}