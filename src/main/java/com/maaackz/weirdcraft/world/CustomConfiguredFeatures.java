package com.maaackz.weirdcraft.world;

import com.maaackz.weirdcraft.Weirdcraft;
import com.maaackz.weirdcraft.block.CustomBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;

import java.util.List;

public class CustomConfiguredFeatures {
    public static final RegistryKey<ConfiguredFeature<?, ?>> WEIRDIUM_ORE_KEY = registerKey("pink_garnet_ore");
//    public static final RegistryKey<ConfiguredFeature<?, ?>> NETHER_PINK_GARNET_ORE_KEY = registerKey("nether_pink_garnet_ore");
//    public static final RegistryKey<ConfiguredFeature<?, ?>> END_PINK_GARNET_ORE_KEY = registerKey("end_pink_garnet_ore");

    public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context) {
        RuleTest stoneReplaceables = new TagMatchRuleTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateReplaceables = new TagMatchRuleTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        RuleTest netherReplaceables = new TagMatchRuleTest(BlockTags.BASE_STONE_NETHER);
        RuleTest endReplaceables = new BlockMatchRuleTest(Blocks.END_STONE);

        List<OreFeatureConfig.Target> overworldPinkGarnetOres =
                List.of(OreFeatureConfig.createTarget(stoneReplaceables, CustomBlocks.WEIRDIUM_ORE.getDefaultState()),
                        OreFeatureConfig.createTarget(deepslateReplaceables, CustomBlocks.DEEPSLATE_WEIRDIUM_ORE.getDefaultState()));
//        List<OreFeatureConfig.Target> netherPinkGarnetOres =
//                List.of(OreFeatureConfig.createTarget(netherReplaceables, CustomBlocks.PINK_GARNET_NETHER_ORE.getDefaultState()));
//        List<OreFeatureConfig.Target> endPinkGarnetOres =
//                List.of(OreFeatureConfig.createTarget(endReplaceables, CustomBlocks.PINK_GARNET_END_ORE.getDefaultState()));

        register(context, WEIRDIUM_ORE_KEY, Feature.ORE, new OreFeatureConfig(overworldPinkGarnetOres, 4));
//        register(context, NETHER_PINK_GARNET_ORE_KEY, Feature.ORE, new OreFeatureConfig(netherPinkGarnetOres, 9));
//        register(context, END_PINK_GARNET_ORE_KEY, Feature.ORE, new OreFeatureConfig(endPinkGarnetOres, 9));
    }

    public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Identifier.of(Weirdcraft.MOD_ID, name));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(Registerable<ConfiguredFeature<?, ?>> context,
                                                                                   RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}