package com.maaackz.weirdcraft.datagen;

import com.maaackz.weirdcraft.block.CustomBlocks;
import com.maaackz.weirdcraft.item.CustomItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CustomRecipeProvider extends FabricRecipeProvider {
    public CustomRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        List<ItemConvertible> WEIRDIUM_SMELTABLES = List.of(CustomItems.RAW_WEIRDIUM, CustomBlocks.WEIRDIUM_ORE,
                CustomBlocks.DEEPSLATE_WEIRDIUM_ORE);

        offerSmelting(exporter, WEIRDIUM_SMELTABLES, RecipeCategory.MISC, CustomItems.WEIRDIUM_INGOT, 0.25f, 200, "weirdium");
        offerBlasting(exporter, WEIRDIUM_SMELTABLES, RecipeCategory.MISC, CustomItems.WEIRDIUM_INGOT, 0.25f, 100, "weirdium");

        offerReversibleCompactingRecipes(exporter, RecipeCategory.BUILDING_BLOCKS, CustomItems.WEIRDIUM_INGOT, RecipeCategory.DECORATIONS, CustomBlocks.WEIRDIUM_BLOCK);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, CustomBlocks.RAW_WEIRDIUM_BLOCK)
                .pattern("RRR")
                .pattern("RRR")
                .pattern("RRR")
                .input('R', CustomItems.RAW_WEIRDIUM)
                .criterion(hasItem(CustomItems.RAW_WEIRDIUM), conditionsFromItem(CustomItems.RAW_WEIRDIUM))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, CustomItems.RAW_WEIRDIUM, 9)
                .input(CustomBlocks.RAW_WEIRDIUM_BLOCK)
                .criterion(hasItem(CustomBlocks.RAW_WEIRDIUM_BLOCK), conditionsFromItem(CustomBlocks.RAW_WEIRDIUM_BLOCK))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, CustomItems.GOLDEN_BANANA)
                .pattern("GGG")
                .pattern("GAG")
                .pattern("GGG")
                .input('G', Items.GOLD_INGOT)
                .input('A', CustomItems.BANANA)
                .criterion(hasItem(CustomItems.BANANA), conditionsFromItem(CustomItems.BANANA))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, CustomItems.ENCHANTED_GOLDEN_BANANA)
                .pattern("GGG")
                .pattern("GAG")
                .pattern("GGG")
                .input('G', Items.GOLD_BLOCK)
                .input('A', CustomItems.GOLDEN_BANANA)
                .criterion(hasItem(CustomItems.GOLDEN_BANANA), conditionsFromItem(CustomItems.GOLDEN_BANANA))
                .offerTo(exporter);

        // Weirdium Armor Recipes
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, CustomItems.WEIRDIUM_HELMET)
                .pattern("III")
                .pattern("I I")
                .input('I', CustomItems.WEIRDIUM_INGOT)
                .criterion(hasItem(CustomItems.WEIRDIUM_INGOT), conditionsFromItem(CustomItems.WEIRDIUM_INGOT))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, CustomItems.WEIRDIUM_CHESTPLATE)
                .pattern("I I")
                .pattern("III")
                .pattern("III")
                .input('I', CustomItems.WEIRDIUM_INGOT)
                .criterion(hasItem(CustomItems.WEIRDIUM_INGOT), conditionsFromItem(CustomItems.WEIRDIUM_INGOT))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, CustomItems.WEIRDIUM_LEGGINGS)
                .pattern("III")
                .pattern("I I")
                .pattern("I I")
                .input('I', CustomItems.WEIRDIUM_INGOT)
                .criterion(hasItem(CustomItems.WEIRDIUM_INGOT), conditionsFromItem(CustomItems.WEIRDIUM_INGOT))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, CustomItems.WEIRDIUM_BOOTS)
                .pattern("I I")
                .pattern("I I")
                .input('I', CustomItems.WEIRDIUM_INGOT)
                .criterion(hasItem(CustomItems.WEIRDIUM_INGOT), conditionsFromItem(CustomItems.WEIRDIUM_INGOT))
                .offerTo(exporter);

        // Weirdium Tool Recipes
        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, CustomItems.WEIRDIUM_SWORD)
                .pattern(" I ")
                .pattern(" I ")
                .pattern(" S ")
                .input('I', CustomItems.WEIRDIUM_INGOT)
                .input('S', Items.STICK)
                .criterion(hasItem(CustomItems.WEIRDIUM_INGOT), conditionsFromItem(CustomItems.WEIRDIUM_INGOT))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, CustomItems.WEIRDIUM_PICKAXE)
                .pattern("III")
                .pattern(" S ")
                .pattern(" S ")
                .input('I', CustomItems.WEIRDIUM_INGOT)
                .input('S', Items.STICK)
                .criterion(hasItem(CustomItems.WEIRDIUM_INGOT), conditionsFromItem(CustomItems.WEIRDIUM_INGOT))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, CustomItems.WEIRDIUM_AXE)
                .pattern("II ")
                .pattern("IS ")
                .pattern(" S ")
                .input('I', CustomItems.WEIRDIUM_INGOT)
                .input('S', Items.STICK)
                .criterion(hasItem(CustomItems.WEIRDIUM_INGOT), conditionsFromItem(CustomItems.WEIRDIUM_INGOT))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, CustomItems.WEIRDIUM_SHOVEL)
                .pattern(" I ")
                .pattern(" S ")
                .pattern(" S ")
                .input('I', CustomItems.WEIRDIUM_INGOT)
                .input('S', Items.STICK)
                .criterion(hasItem(CustomItems.WEIRDIUM_INGOT), conditionsFromItem(CustomItems.WEIRDIUM_INGOT))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, CustomItems.WEIRDIUM_HOE)
                .pattern("II ")
                .pattern(" S ")
                .pattern(" S ")
                .input('I', CustomItems.WEIRDIUM_INGOT)
                .input('S', Items.STICK)
                .criterion(hasItem(CustomItems.WEIRDIUM_INGOT), conditionsFromItem(CustomItems.WEIRDIUM_INGOT))
                .offerTo(exporter);
    }
}
