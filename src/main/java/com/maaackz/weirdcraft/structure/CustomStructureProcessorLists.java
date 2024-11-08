package com.maaackz.weirdcraft.structure;

import com.google.common.collect.ImmutableList;
import com.maaackz.weirdcraft.Weirdcraft;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.processor.*;
import net.minecraft.util.Identifier;

import java.util.List;

public class CustomStructureProcessorLists {

    public static final RegistryKey<StructureProcessorList> PYRAMID_PROCESSOR = registerKey("pyramid_processor");
    private static final float SHRINE_MOSSIFY_CHANCE = 0.5f;
    private static final float SHRINE_WOOL_CHANCE = 0.05f;
    private static final float SHRINE_PATH_CHANCE = 0.35f;

    public static void bootstrap(Registerable<StructureProcessorList> context) {
        //RegistryEntryLookup<Block> blockRegistryEntryLookup = context.getRegistryLookup(RegistryKeys.BLOCK);

        register(context, PYRAMID_PROCESSOR, ImmutableList.of(
                new RuleStructureProcessor(ImmutableList.of(
//                        new StructureProcessorRule(new RandomBlockStateMatchRuleTest(Blocks.STONE_BRICK_WALL.getDefaultState().with(WallBlock.UP, false).with(WallBlock.NORTH_SHAPE, WallShape.TALL).with(WallBlock.SOUTH_SHAPE, WallShape.TALL), SHRINE_MOSSIFY_CHANCE), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_STONE_BRICK_WALL.getDefaultState().with(WallBlock.UP, false).with(WallBlock.NORTH_SHAPE, WallShape.TALL).with(WallBlock.SOUTH_SHAPE, WallShape.TALL)), new StructureProcessorRule(new RandomBlockStateMatchRuleTest(Blocks.STONE_BRICK_WALL.getDefaultState().with(WallBlock.UP, false).with(WallBlock.EAST_SHAPE, WallShape.TALL).with(WallBlock.WEST_SHAPE, WallShape.TALL), SHRINE_MOSSIFY_CHANCE),
//                                AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_STONE_BRICK_WALL.getDefaultState().with(WallBlock.UP, false).with(WallBlock.EAST_SHAPE, WallShape.TALL).with(WallBlock.WEST_SHAPE, WallShape.TALL)),
//                        new StructureProcessorRule(new RandomBlockStateMatchRuleTest(Blocks.STONE_BRICK_WALL.getDefaultState().with(WallBlock.UP, true).with(WallBlock.NORTH_SHAPE, WallShape.TALL).with(WallBlock.SOUTH_SHAPE, WallShape.TALL).with(WallBlock.EAST_SHAPE, WallShape.TALL), SHRINE_MOSSIFY_CHANCE),
//                                AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_STONE_BRICK_WALL.getDefaultState().with(WallBlock.UP, true).with(WallBlock.NORTH_SHAPE, WallShape.TALL).with(WallBlock.SOUTH_SHAPE, WallShape.TALL).with(WallBlock.EAST_SHAPE, WallShape.TALL)),
//                        new StructureProcessorRule(new RandomBlockStateMatchRuleTest(Blocks.STONE_BRICK_WALL.getDefaultState().with(WallBlock.UP, true).with(WallBlock.EAST_SHAPE, WallShape.TALL).with(WallBlock.WEST_SHAPE, WallShape.TALL).with(WallBlock.NORTH_SHAPE, WallShape.TALL), SHRINE_MOSSIFY_CHANCE),
//                                AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_STONE_BRICK_WALL.getDefaultState().with(WallBlock.UP, true).with(WallBlock.EAST_SHAPE, WallShape.TALL).with(WallBlock.WEST_SHAPE, WallShape.TALL).with(WallBlock.NORTH_SHAPE, WallShape.TALL)),
//                        new StructureProcessorRule(new RandomBlockStateMatchRuleTest(Blocks.STONE_BRICK_WALL.getDefaultState().with(WallBlock.UP, true).with(WallBlock.NORTH_SHAPE, WallShape.TALL).with(WallBlock.SOUTH_SHAPE, WallShape.TALL).with(WallBlock.WEST_SHAPE, WallShape.TALL), SHRINE_MOSSIFY_CHANCE),
//                                AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_STONE_BRICK_WALL.getDefaultState().with(WallBlock.UP, true).with(WallBlock.NORTH_SHAPE, WallShape.TALL).with(WallBlock.SOUTH_SHAPE, WallShape.TALL).with(WallBlock.WEST_SHAPE, WallShape.TALL)),
//                        new StructureProcessorRule(new RandomBlockStateMatchRuleTest(Blocks.STONE_BRICK_WALL.getDefaultState().with(WallBlock.UP, true).with(WallBlock.EAST_SHAPE, WallShape.TALL).with(WallBlock.WEST_SHAPE, WallShape.TALL).with(WallBlock.SOUTH_SHAPE, WallShape.TALL), SHRINE_MOSSIFY_CHANCE),
//                                AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_STONE_BRICK_WALL.getDefaultState().with(WallBlock.UP, true).with(WallBlock.EAST_SHAPE, WallShape.TALL).with(WallBlock.WEST_SHAPE, WallShape.TALL).with(WallBlock.SOUTH_SHAPE, WallShape.TALL)),
//                        new StructureProcessorRule(new RandomBlockStateMatchRuleTest(Blocks.STONE_BRICK_WALL.getDefaultState().with(WallBlock.UP, true).with(WallBlock.EAST_SHAPE, WallShape.TALL).with(WallBlock.SOUTH_SHAPE, WallShape.TALL), SHRINE_MOSSIFY_CHANCE),
//                                AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_STONE_BRICK_WALL.getDefaultState().with(WallBlock.UP, true).with(WallBlock.EAST_SHAPE, WallShape.TALL).with(WallBlock.SOUTH_SHAPE, WallShape.TALL)),
//                        new StructureProcessorRule(new RandomBlockStateMatchRuleTest(Blocks.STONE_BRICK_WALL.getDefaultState().with(WallBlock.UP, true).with(WallBlock.EAST_SHAPE, WallShape.TALL).with(WallBlock.NORTH_SHAPE, WallShape.TALL), SHRINE_MOSSIFY_CHANCE),
//                                AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_STONE_BRICK_WALL.getDefaultState().with(WallBlock.UP, true).with(WallBlock.EAST_SHAPE, WallShape.TALL).with(WallBlock.NORTH_SHAPE, WallShape.TALL)),
//                        new StructureProcessorRule(new RandomBlockStateMatchRuleTest(Blocks.STONE_BRICK_WALL.getDefaultState().with(WallBlock.UP, true).with(WallBlock.WEST_SHAPE, WallShape.TALL).with(WallBlock.NORTH_SHAPE, WallShape.TALL), SHRINE_MOSSIFY_CHANCE),
//                                AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_STONE_BRICK_WALL.getDefaultState().with(WallBlock.UP, true).with(WallBlock.WEST_SHAPE, WallShape.TALL).with(WallBlock.NORTH_SHAPE, WallShape.TALL)),
//                        new StructureProcessorRule(new RandomBlockStateMatchRuleTest(Blocks.STONE_BRICK_WALL.getDefaultState().with(WallBlock.UP, true).with(WallBlock.WEST_SHAPE, WallShape.TALL).with(WallBlock.SOUTH_SHAPE, WallShape.TALL), SHRINE_MOSSIFY_CHANCE),
//                                AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_STONE_BRICK_WALL.getDefaultState().with(WallBlock.UP, true).with(WallBlock.WEST_SHAPE, WallShape.TALL).with(WallBlock.SOUTH_SHAPE, WallShape.TALL)),
//                        new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.STONE_BRICKS, SHRINE_MOSSIFY_CHANCE), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_STONE_BRICKS.getDefaultState()),
//                        new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHITE_WOOL, SHRINE_WOOL_CHANCE), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
//                        new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.WHITE_WOOL, SHRINE_WOOL_CHANCE), AlwaysTrueRuleTest.INSTANCE, Blocks.COBWEB.getDefaultState()),
//                        new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.DIRT_PATH, SHRINE_PATH_CHANCE), AlwaysTrueRuleTest.INSTANCE, Blocks.AIR.getDefaultState()),
//                        new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.POLISHED_BLACKSTONE_BRICKS, SHRINE_MOSSIFY_CHANCE), AlwaysTrueRuleTest.INSTANCE, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.getDefaultState())
                )),

                new BlockRotStructureProcessor(RegistryEntryList.of(Registries.BLOCK.getEntry(Blocks.DARK_OAK_TRAPDOOR)), 0.75f),
                new ProtectedBlocksStructureProcessor(BlockTags.FEATURES_CANNOT_REPLACE),
                BlockIgnoreStructureProcessor.IGNORE_AIR
        ));
    }

    @SuppressWarnings("SameParameterValue")
    private static RegistryKey<StructureProcessorList> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.PROCESSOR_LIST, Identifier.of(Weirdcraft.MOD_ID, name));
    }

    @SuppressWarnings("SameParameterValue")
    private static void register(Registerable<StructureProcessorList> processorListRegisterable, RegistryKey<StructureProcessorList> key, List<StructureProcessor> processors) {
        processorListRegisterable.register(key, new StructureProcessorList(processors));
    }
}