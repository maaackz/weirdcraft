package com.maaackz.weirdcraft.item.custom;

import com.maaackz.weirdcraft.item.CustomItems;
import net.minecraft.block.Block;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

public class PoopToolMaterial implements ToolMaterial {
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
        return 0.5f; // Same as diamond tools
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
        return Ingredient.ofItems(CustomItems.POOP);
    }

    public static final PoopToolMaterial INSTANCE = new PoopToolMaterial();
}
