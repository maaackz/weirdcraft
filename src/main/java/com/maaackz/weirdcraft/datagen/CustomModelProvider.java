package com.maaackz.weirdcraft.datagen;

import com.maaackz.weirdcraft.block.CustomBlocks;
import com.maaackz.weirdcraft.item.CustomItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public class CustomModelProvider extends FabricModelProvider {
    public CustomModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

        blockStateModelGenerator.registerSimpleCubeAll(CustomBlocks.WEIRDIUM_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(CustomBlocks.WEIRDIUM_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(CustomBlocks.RAW_WEIRDIUM_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(CustomBlocks.DEEPSLATE_WEIRDIUM_ORE);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

        itemModelGenerator.register(CustomItems.SAND_OCEAN_MUSIC_DISC, Models.GENERATED);

        itemModelGenerator.register(CustomItems.BANANA, Models.GENERATED);
        itemModelGenerator.register(CustomItems.GOLDEN_BANANA, Models.GENERATED);

//        itemModelGenerator.register(CustomItems.SHOCK_COLLAR, Models.GENERATED);
        itemModelGenerator.register(CustomItems.POOP, Models.GENERATED);
        itemModelGenerator.register(CustomItems.ENCHANTED_GOLDEN_BANANA, Models.GENERATED);

//        itemModelGenerator.register(CustomItems.ENCHANTED_GOLDEN_BANANA, Models.GENERATED);
//        itemModelGenerator.register(CustomItems.ENCHANTED_GOLDEN_BANANA, Models.GENERATED);

        itemModelGenerator.register(CustomItems.RAW_WEIRDIUM, Models.GENERATED);
        itemModelGenerator.register(CustomItems.WEIRDIUM_INGOT, Models.GENERATED);

        itemModelGenerator.register(CustomItems.POCKET_WATCH, Models.GENERATED);
        itemModelGenerator.register(CustomItems.RAINES_CLOUD, Models.GENERATED);
        itemModelGenerator.register(CustomItems.NOCTURNES_KISS, Models.GENERATED);
    }
}