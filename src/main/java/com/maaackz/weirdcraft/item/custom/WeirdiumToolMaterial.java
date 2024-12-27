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
        return 3122; // Same as diamond tools
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return 8f; // Slightly better than diamond (7f)
    }

    @Override
    public float getAttackDamage() {
        return 3.5f; // Same as diamond tools
    }

    @Override
    public TagKey<Block> getInverseTag() {
        return BlockTags.INCORRECT_FOR_DIAMOND_TOOL; // Assuming this is correct for the tool
    }

    @Override
    public int getEnchantability() {
        return 15; // Same as diamond tools
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(CustomItems.WEIRDIUM_INGOT);
    }

    public static final WeirdiumToolMaterial INSTANCE = new WeirdiumToolMaterial();
}
