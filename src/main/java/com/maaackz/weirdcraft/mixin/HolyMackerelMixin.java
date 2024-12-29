package com.maaackz.weirdcraft.mixin;

import com.maaackz.weirdcraft.Weirdcraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;
import java.util.Objects;

@Mixin(ServerPlayerEntity.class)
public abstract class HolyMackerelMixin {

    @Unique
    private boolean isRainBuffActive = false;
    @Unique
    private boolean isWaterBuffActive = false;

    @Unique
    private static Method isBeingRainedOnMethod;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        boolean isRainingOnPlayer = isBeingRainedOn(player);
        boolean isTouchingWater = player.isTouchingWaterOrRain() || player.isWet() || player.isSubmergedInWater() || player.isInsideWaterOrBubbleColumn() || player.isSwimming();

        // Manage buffs based on water and rain states
        handleRainBuff(player, isRainingOnPlayer);
        handleWaterBuff(player, isTouchingWater);

        // Custom logic for Holy Mackerel item
        if (isWieldingHolyMackerel(player)) {
//            player.sendMessage(Text.literal("§bYou feel the power of the Holy Mackerel!"), false);
//            player.setAir(-1);
        }
    }

    @Unique
    private void handleRainBuff(ServerPlayerEntity player, boolean isRaining) {
        if (isRaining && !isRainBuffActive) {

            applyBuff(player); // 20% damage boost in rain
            isRainBuffActive = true;
        } else if (!isRaining && isRainBuffActive) {
            removeBuff(player);
            isRainBuffActive = false;
        }
    }

    @Unique
    private void handleWaterBuff(ServerPlayerEntity player, boolean isTouchingWater) {
        if (isTouchingWater && !isWaterBuffActive) {
            isWaterBuffActive = true;
            applyBuff(player); // 10% speed boost in water

        } else if (!isTouchingWater && isWaterBuffActive) {
            removeBuff(player);
            isWaterBuffActive = false;
        }
    }



    @Unique
    private void applyBuff(ServerPlayerEntity player) {

        EntityAttributeModifier water_atk_buff = new EntityAttributeModifier(
                Identifier.of(Weirdcraft.MOD_ID, "water_atk_buff"),
                1.25,
                EntityAttributeModifier.Operation.ADD_VALUE
        );

        EntityAttributeModifier water_atk_speed_buff = new EntityAttributeModifier(
                Identifier.of(Weirdcraft.MOD_ID, "water_atk_speed_buff"),
                1.25,
                EntityAttributeModifier.Operation.ADD_VALUE
        );

        EntityAttributeModifier water_kb_buff = new EntityAttributeModifier(
                Identifier.of(Weirdcraft.MOD_ID, "water_kb_buff"),
                1.25,
                EntityAttributeModifier.Operation.ADD_VALUE
        );

        EntityAttributeModifier water_luck_buff = new EntityAttributeModifier(
                Identifier.of(Weirdcraft.MOD_ID, "water_luck_buff"),
                2,
                EntityAttributeModifier.Operation.ADD_VALUE
        );

        EntityAttributeModifier water_movement_buff = new EntityAttributeModifier(
                Identifier.of(Weirdcraft.MOD_ID, "water_movement_buff"),
                2,
                EntityAttributeModifier.Operation.ADD_VALUE
        );

        EntityAttributeModifier water_mining_buff = new EntityAttributeModifier(
                Identifier.of(Weirdcraft.MOD_ID, "water_mining_buff"),
                2,
                EntityAttributeModifier.Operation.ADD_VALUE
        );

        EntityAttributeModifier water_armor_buff = new EntityAttributeModifier(
                Identifier.of(Weirdcraft.MOD_ID, "water_armor_buff"),
                1.25,
                EntityAttributeModifier.Operation.ADD_VALUE
        );

        EntityAttributeModifier water_armor_toughness_buff = new EntityAttributeModifier(
                Identifier.of(Weirdcraft.MOD_ID, "water_armor_toughness_buff"),
                1.25,
                EntityAttributeModifier.Operation.ADD_VALUE
        );

        if (!player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "water_atk_buff"))) {
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE))
                    .addPersistentModifier(water_atk_buff);
        }
        if (!player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_SPEED).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "water_atk_speed_buff"))) {
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_SPEED))
                    .addPersistentModifier(water_atk_speed_buff);
        }
        if (!player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_KNOCKBACK).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "water_kb_buff"))) {
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_KNOCKBACK))
                    .addPersistentModifier(water_kb_buff);
        }
        if (!player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_LUCK).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "water_luck_buff"))) {
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_LUCK))
                    .addPersistentModifier(water_luck_buff);
        }
        if (!player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "water_armor_buff"))) {
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR))
                    .addPersistentModifier(water_armor_buff);
        }
        if (!player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "water_armor_toughness_buff"))) {
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS))
                    .addPersistentModifier(water_armor_toughness_buff);
        }
        if (!player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_WATER_MOVEMENT_EFFICIENCY).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "water_movement_buff"))) {
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_WATER_MOVEMENT_EFFICIENCY))
                    .addPersistentModifier(water_movement_buff);
        }
        if (!player.getAttributes().getCustomInstance(EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED).hasModifier(Identifier.of(Weirdcraft.MOD_ID, "water_mining_buff"))) {
            Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.PLAYER_SUBMERGED_MINING_SPEED))
                    .addPersistentModifier(water_mining_buff);
        }
    }


    @Unique
    private void removeBuff(ServerPlayerEntity player) {
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE))
                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "water_atk_buff"));
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_SPEED))
                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "water_atk_speed_buff"));
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_KNOCKBACK))
                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "water_kb_buff"));
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_LUCK))
                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "water_luck_buff"));
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR))
                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "water_armor_buff"));
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS))
                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "water_armor_toughness_buff"));
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR))
                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "water_movement_buff"));
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS))
                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "water_mining_buff"));

        // Remove water breathing effect
//        player.removeStatusEffect(StatusEffects.WATER_BREATHING);
    }

    @Unique
    private boolean isBeingRainedOn(ServerPlayerEntity player) {
        try {
            if (isBeingRainedOnMethod == null) {
                isBeingRainedOnMethod = Entity.class.getDeclaredMethod("isBeingRainedOn");
                isBeingRainedOnMethod.setAccessible(true);
            }
            return (boolean) isBeingRainedOnMethod.invoke(player);
        } catch (Exception e) {
            Weirdcraft.LOGGER.error("Failed to invoke isBeingRainedOn: ", e);
            return false;
        }
    }

    @Unique
    public boolean isWieldingHolyMackerel(ServerPlayerEntity player) {
        Item holyMackerelItem = Registries.ITEM.get(Identifier.of(Weirdcraft.MOD_ID, "holy_mackerel"));
        return player.getInventory().contains(new ItemStack(holyMackerelItem));
    }

}