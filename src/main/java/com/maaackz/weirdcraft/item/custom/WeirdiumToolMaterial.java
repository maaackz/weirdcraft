package com.maaackz.weirdcraft.item.custom;


import com.maaackz.weirdcraft.item.CustomItems;
import net.minecraft.block.Block;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

public class WeirdiumToolMaterial implements ToolMaterial {
    @Override
    public int getDurability() {
        return 8192;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return 30f;
    }

    @Override
    public float getAttackDamage() {
        return 10f;
    }

    @Override
    public TagKey<Block> getInverseTag() {
        return BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
    }

    @Override
    public int getEnchantability(){
        return 30;
    }

    @Override
    public Ingredient getRepairIngredient(){
        return Ingredient.ofItems(CustomItems.WEIRDIUM_INGOT);
    }

    public static final WeirdiumToolMaterial INSTANCE = new WeirdiumToolMaterial();
}