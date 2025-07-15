package com.maaackz.weirdcraft.world;

import com.maaackz.weirdcraft.Weirdcraft;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;

import java.util.List;

public class CustomPlacedFeatures {
    public static final RegistryKey<PlacedFeature> WEIRDIUM_ORE_PLACED_KEY = registerKey("weirdium_ore_placed");
//    public static final RegistryKey<PlacedFeature> NETHER_PINK_GARNET_ORE_PLACED_KEY = registerKey("nether_pink_garnet_ore_placed");
//    public static final RegistryKey<PlacedFeature> END_PINK_GARNET_ORE_PLACED_KEY = registerKey("end_pink_garnet_ore_placed");

    public static void bootstrap(Registerable<PlacedFeature> context) {
        var configuredFeatures = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        register(context, WEIRDIUM_ORE_PLACED_KEY, configuredFeatures.getOrThrow(CustomConfiguredFeatures.WEIRDIUM_ORE_KEY),
                CustomOrePlacement.modifiersWithCount(4,
                        HeightRangePlacementModifier.trapezoid(YOffset.fixed(-69), YOffset.fixed(45))));
//        register(context, NETHER_PINK_GARNET_ORE_PLACED_KEY, configuredFeatures.getOrThrow(CustomConfiguredFeatures.NETHER_PINK_GARNET_ORE_KEY),
//                CustomOrePlacement.modifiersWithCount(14,
//                        HeightRangePlacementModifier.uniform(YOffset.fixed(-80), YOffset.fixed(80))));
//        register(context, END_PINK_GARNET_ORE_PLACED_KEY, configuredFeatures.getOrThrow(CustomConfiguredFeatures.END_PINK_GARNET_ORE_KEY),
//                CustomOrePlacement.modifiersWithCount(14,
//                        HeightRangePlacementModifier.uniform(YOffset.fixed(-80), YOffset.fixed(80))));

    }

    public static RegistryKey<PlacedFeature> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(Weirdcraft.MOD_ID, name));
    }

    private static void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key, RegistryEntry<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key,
                                                                                   RegistryEntry<ConfiguredFeature<?, ?>> configuration,
                                                                                   PlacementModifier... modifiers) {
        register(context, key, configuration, List.of(modifiers));
    }
}