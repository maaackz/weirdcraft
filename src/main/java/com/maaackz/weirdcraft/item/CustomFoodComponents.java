package com.maaackz.weirdcraft.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.component.type.FoodComponent;

public class CustomFoodComponents {

    public static final FoodComponent POOP = new FoodComponent.Builder()
            .nutrition(1)
            .saturationModifier(-0.25f)
//            .statusEffect(new StatusEffectInstance(StatusEffects.LUCK, 200), 0.25f)
            .alwaysEdible()
            .build();

    public static final FoodComponent BANANA = new FoodComponent.Builder()
            .nutrition(4)  // Same as apple
            .saturationModifier(0.3f)  // Same as apple
            .build();

    public static final FoodComponent GOLDEN_BANANA = new FoodComponent.Builder()
            .nutrition(4)  // Same as golden apple
            .saturationModifier(1.2f)  // Same as golden apple
            .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 1), 1.0f)  // 5 seconds of Regeneration
            .statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 1200, 0), 1.0f)  // 2 minutes of Absorption
            .alwaysEdible()  // Golden Apples can be eaten even with full hunger
            .build();

    public static final FoodComponent ENCHANTED_GOLDEN_BANANA = new FoodComponent.Builder()
            .nutrition(4)  // Same as enchanted golden apple
            .saturationModifier(1.2f)  // Same as enchanted golden apple
            .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 600, 1), 1.0f)  // 30 seconds of Regeneration
            .statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 1200, 0), 1.0f)  // 2 minutes of Absorption
            .statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 6000, 0), 1.0f)  // 5 minutes of Resistance
            .statusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 6000, 0), 1.0f)  // 5 minutes of Fire Resistance
            .alwaysEdible()  // Enchanted golden apples can be eaten even with full hunger
            .build();


}