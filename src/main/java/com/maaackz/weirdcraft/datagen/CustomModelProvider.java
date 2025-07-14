package com.maaackz.weirdcraft.datagen;

import com.maaackz.weirdcraft.block.CustomBlocks;
import com.maaackz.weirdcraft.item.CustomItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.item.ArmorItem;

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
        blockStateModelGenerator.registerSimpleCubeAll(CustomBlocks.POOP_BLOCK);
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

        itemModelGenerator.registerArmor(((ArmorItem) CustomItems.WEIRDIUM_HELMET));
        itemModelGenerator.registerArmor(((ArmorItem) CustomItems.WEIRDIUM_CHESTPLATE));
        itemModelGenerator.registerArmor(((ArmorItem) CustomItems.WEIRDIUM_LEGGINGS));
        itemModelGenerator.registerArmor(((ArmorItem) CustomItems.WEIRDIUM_BOOTS));

        itemModelGenerator.register(CustomItems.WEIRDIUM_SWORD, Models.GENERATED);
        itemModelGenerator.register(CustomItems.WEIRDIUM_AXE, Models.GENERATED);
        itemModelGenerator.register(CustomItems.WEIRDIUM_HOE, Models.GENERATED);
        itemModelGenerator.register(CustomItems.WEIRDIUM_PICKAXE, Models.GENERATED);
        itemModelGenerator.register(CustomItems.WEIRDIUM_SHOVEL, Models.GENERATED);

        itemModelGenerator.registerArmor(((ArmorItem) CustomItems.POOP_HELMET));
        itemModelGenerator.registerArmor(((ArmorItem) CustomItems.POOP_CHESTPLATE));
        itemModelGenerator.registerArmor(((ArmorItem) CustomItems.POOP_LEGGINGS));
        itemModelGenerator.registerArmor(((ArmorItem) CustomItems.POOP_BOOTS));

        itemModelGenerator.register(CustomItems.POOP_SWORD, Models.GENERATED);
        itemModelGenerator.register(CustomItems.POOP_AXE, Models.GENERATED);
        itemModelGenerator.register(CustomItems.POOP_HOE, Models.GENERATED);
        itemModelGenerator.register(CustomItems.POOP_PICKAXE, Models.GENERATED);
        itemModelGenerator.register(CustomItems.POOP_SHOVEL, Models.GENERATED);

        itemModelGenerator.register(CustomItems.POCKET_WATCH, Models.GENERATED);
        itemModelGenerator.register(CustomItems.RAINES_CLOUD, Models.GENERATED);
        itemModelGenerator.register(CustomItems.NOCTURNES_KISS, Models.GENERATED);
        itemModelGenerator.register(CustomItems.HOLY_MACKEREL, Models.GENERATED);
    }
}