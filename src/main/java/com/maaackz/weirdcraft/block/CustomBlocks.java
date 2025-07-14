package com.maaackz.weirdcraft.block;

import com.maaackz.weirdcraft.Weirdcraft;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class CustomBlocks {

    public static final Block RAW_WEIRDIUM_BLOCK = registerBlock("raw_weirdium_block",
            new Block(AbstractBlock.Settings.create().strength(3f)
                    .requiresTool()
                    .luminance(state -> 5)
//                    .sounds(BlockSoundGroup.AMETHYST_BLOCK))
    ));

    public static final Block WEIRDIUM_BLOCK = registerBlock("weirdium_block",
            new Block(AbstractBlock.Settings.create().strength(3f)
                    .requiresTool()
                    .luminance(state -> 10)
//                    .sounds(BlockSoundGroup.AMETHYST_BLOCK))
    ));

    public static final Block POOP_BLOCK = registerBlock("poop_block",
            new Block(AbstractBlock.Settings.create().strength(0f)
//                    .requiresTool()
//                    .luminance(state -> 10)
                    .sounds(BlockSoundGroup.FUNGUS)
            )
    );


    public static final Block WEIRDIUM_ORE = registerBlock("weirdium_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(2, 5),
                AbstractBlock.Settings.create().strength(3f)
                    .requiresTool()
                    .luminance(state -> 5)
//                    .sounds(BlockSoundGroup.STONE)
            ));

    public static final Block DEEPSLATE_WEIRDIUM_ORE = registerBlock("deepslate_weirdium_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(3, 6),
                    AbstractBlock.Settings.create().strength(3f)
                            .requiresTool()
                            .sounds(BlockSoundGroup.DEEPSLATE)
                            .luminance(state -> 5)
            ));


    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(Weirdcraft.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(Weirdcraft.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerBlocks() {
        Weirdcraft.LOGGER.info("Registering Mod Blocks for " + Weirdcraft.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            entries.add(CustomBlocks.WEIRDIUM_BLOCK);
            entries.add(CustomBlocks.WEIRDIUM_ORE);
            entries.add(CustomBlocks.RAW_WEIRDIUM_BLOCK);
            entries.add(CustomBlocks.DEEPSLATE_WEIRDIUM_ORE);
        });
    }
}
