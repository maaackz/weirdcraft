package com.maaackz.weirdcraft.datagen;

import com.maaackz.weirdcraft.block.CustomBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class CustomBlockTagProvider  extends FabricTagProvider.BlockTagProvider {
    public CustomBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(CustomBlocks.WEIRDIUM_BLOCK)
                .add(CustomBlocks.RAW_WEIRDIUM_BLOCK)
                .add(CustomBlocks.WEIRDIUM_ORE)
                .add(CustomBlocks.DEEPSLATE_WEIRDIUM_ORE);

        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                .add(CustomBlocks.DEEPSLATE_WEIRDIUM_ORE);

//        getOrCreateTagBuilder(CustomTags.Blocks.NEEDS_WEIRDIUM_TOOL)
//                .add(CustomBlocks.MAGIC_BLOCK)
//                .addTag(BlockTags.NEEDS_IRON_TOOL);

    }
}
