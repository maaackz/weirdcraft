package com.maaackz.weirdcraft.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class CustomAdvancementProvider extends FabricAdvancementProvider {
    public CustomAdvancementProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup wrapperLookup, Consumer<AdvancementEntry> consumer) {

    }
//
//    public CustomAdvancementProvider(FabricDataOutput output) {
//        super(output);
//    }
//
//    @Override
//    public void generateAdvancement(RegistryWrapper.WrapperLookup registryLookup, Consumer<AdvancementEntry> consumer) {
//        AdvancementEntry rootAdvancement = Advancement.Builder.create()
//                .display(
//                        Blocks.CHISELED_SANDSTONE,
//                        Text.translatable("advancements.weirdcraft.find_vanilla_pyramid.title"),
//                        Text.translatable("advancements.weirdcraft.find_vanilla_pyramid.description"),
//                        Identifier.of("minecraft", "textures/block/sandstone_top.png"),
//                        AdvancementFrame.TASK,
//                        false,
//                        true,
//                        false
//                )
//                .criterion("desert_pyramid", TickCriterion.Conditions.createLocation(LocationPredicate.feature(StructureKeys.DESERT_PYRAMID)))
//                .rewards(AdvancementRewards.Builder.function(Identifier.of(Weirdcraft.MOD_ID, "play_pharaohs_curse_sound")))
//                .build(consumer, Weirdcraft.MOD_ID + "/root");
//
//        AdvancementEntry findPyramid = Advancement.Builder.create()
//                .display(
//                        Blocks.CHISELED_SANDSTONE,
//                        Text.translatable("advancements.weirdcraft.find_pyramid.title"),
//                        Text.translatable("advancements.weirdcraft.find_pyramid.description"),
//                        Identifier.of("minecraft", "textures/block/sandstone_top.png"),
//                        AdvancementFrame.TASK,
//                        true,
//                        true,
//                        false
//                )
//                .criterion("desert_pyramid", TickCriterion.Conditions.createLocation(LocationPredicate.feature(StructureKeys.DESERT_PYRAMID)))
//                .rewards(AdvancementRewards.Builder.function(Identifier.of(Weirdcraft.MOD_ID, "play_pharaohs_curse_sound")))
//                .build(consumer, Weirdcraft.MOD_ID + "/find_pyramid");
//    }
}
