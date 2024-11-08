package com.maaackz.weirdcraft.util;

import com.maaackz.weirdcraft.Weirdcraft;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.Structure;

import java.util.concurrent.CompletableFuture;

public class CustomTags {
//    public static class Blocks {
//        public static final TagKey<Block> METAL_DETECTOR_DETECTABLE_BLOCKS =
//                createTag("metal_detector_detectable_blocks");
//
//        private static TagKey<Block> createTag(String name) {
//            return TagKey.of(RegistryKeys.BLOCK, new Identifier(weirdcraft.MOD_ID, name));
//        }
//    }

    public static class Items {


        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(Weirdcraft.MOD_ID, name));
        }
    }

    public static class Biomes {

        public static final TagKey<Biome> PYRAMID_HAS_STRUCTURE = createTag("has_structure/pyramid");

        @SuppressWarnings("SameParameterValue")
        private static TagKey<Biome> createTag(String name) {
            return create(Weirdcraft.MOD_ID, name);
        }

        @SuppressWarnings("unused")
        private static TagKey<Biome> createCommonTag(String name) {
            return create("c", name);
        }

        private static TagKey<Biome> create(String namespace, String name) {
            return TagKey.of(RegistryKeys.BIOME, Identifier.of(namespace, name));
        }
    }
//
//        public static class Structure {
//
//            public static final TagKey<Biome> PYRAMID = createTag("has_structure/pyramid");
//
//            @SuppressWarnings("SameParameterValue")
//            private static TagKey<Biome> createTag(String name) {
//                return create(weirdcraft.MOD_ID, name);
//            }
//
//            @SuppressWarnings("unused")
//            private static TagKey<Biome> createCommonTag(String name) {
//                return create("c", name);
//            }
//
//            private static TagKey<Biome> create(String namespace, String name) {
//                return TagKey.of(RegistryKeys.BIOME, new Identifier(namespace, name));
//            }
//
//        }

    public static class Structures extends FabricTagProvider<Structure> {
        public Structures(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, RegistryKeys.STRUCTURE, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup registries) {
//            getOrCreateTagBuilder(Biomes.PYRAMID_HAS_STRUCTURE)
//                    .add(CustomStructures.PYRAMID);
        }
    }
}