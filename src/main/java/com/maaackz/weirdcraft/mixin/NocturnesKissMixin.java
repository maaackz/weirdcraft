package com.maaackz.weirdcraft.mixin;

import com.maaackz.weirdcraft.Weirdcraft;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ServerPlayerEntity.class)
public abstract class NocturnesKissMixin {

    @Shadow public abstract ServerWorld getServerWorld();

    @Unique
    private long lastSleepTime = -1;  // Time of day (ticks) when the player last slept
    @Unique
    private boolean wasInWeakness = false;  // Track the previous state (weakness)
    @Unique
    private boolean wasInStrength = false;   // Track the previous state (strength)

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void onReadNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("LastSleepTime")) {
            lastSleepTime = nbt.getLong("LastSleepTime");
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void onWriteNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putLong("LastSleepTime", lastSleepTime);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        long currentTime = player.getWorld().getTimeOfDay(); // Use global game time

        if (!player.isSleeping() && !player.getWorld().isClient) {
            // Only apply the effect if the player is holding "Nocturne's Kiss"
            if (isWieldingNocturnesKiss(player)) {
                if (lastSleepTime == -1) {
                    // If player has never slept, set the initial time
                    lastSleepTime = currentTime;
                } else {
                    // Calculate the elapsed time considering time wrapping and reversals
                    long elapsedTime = calculateElapsedTime(currentTime, lastSleepTime);

                    updateStrengthModifier(player, elapsedTime);
                }
            }
        } else if (player.isSleeping()) {
            // Player is sleeping, reset last sleep time to current time
            lastSleepTime = currentTime;
            updateStrengthModifier(player, 0); // No effect when sleeping
        }
    }

    @Unique
    private long calculateElapsedTime(long currentTime, long lastSleepTime) {
        long currentTimeOfDay = this.getServerWorld().getTimeOfDay();
        return currentTimeOfDay - lastSleepTime;
    }

    @Unique
    private void updateStrengthModifier(ServerPlayerEntity player, long elapsedTime) {
        // Remove existing modifiers for attack damage
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE))
                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "sleepless_str_mod"));
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE))
                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "daytime_weakness_mod"));

        if (elapsedTime > 0) {
            // Convert elapsed time to the number of days since the player last slept
            double daysSinceSlept = elapsedTime / 24000.0; // Assuming each Minecraft day is 24000 ticks
            double additionalDamage = Math.log1p(daysSinceSlept) * 0.65; // Reduced scaling factor for smoother growth

            // Log the calculated additional damage once

            boolean isDay = getServerWorld().isDay();

            // Apply appropriate modifier based on time of day
            if (!isDay) {
                applyStrengthModifier(player, additionalDamage);
            } else {
                applyWeaknessModifier(player, additionalDamage);
            }
        }
    }

    @Unique
    private long lastMessageDay = -1; // Tracks the last day a message was sent

    @Unique
    private void applyStrengthModifier(ServerPlayerEntity player, double additionalDamage) {
        double strengthValue = additionalDamage * 1.25;

        EntityAttributeModifier modifier = new EntityAttributeModifier(
                Identifier.of(Weirdcraft.MOD_ID, "sleepless_str_mod"),
                strengthValue,
                EntityAttributeModifier.Operation.ADD_VALUE
        );
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE))
                .addPersistentModifier(modifier);

        long currentDay = getServerWorld().getTimeOfDay() / 24000; // Calculate the current day

        if (!wasInStrength) {
            long currentTime = player.getWorld().getTimeOfDay(); // Use global game time
            long elapsedTime = calculateElapsedTime(currentTime, lastSleepTime);
            double daysSinceSlept = elapsedTime / 24000.0; // Assuming each Minecraft day is 24000 ticks
            System.out.println("Days since last slept: " + daysSinceSlept + ", Additional Damage: " + additionalDamage);
            System.out.println("Time disparity: " + elapsedTime);
            player.sendMessage(Text.literal("§7§oNocturne whispers to you: You feel a surge of strength."), false);
            wasInStrength = true;
            wasInWeakness = false;
        }
    }

    @Unique
    private void applyWeaknessModifier(ServerPlayerEntity player, double additionalDamage) {
        double weaknessValue = -additionalDamage * 0.75;

        EntityAttributeModifier weaknessModifier = new EntityAttributeModifier(
                Identifier.of(Weirdcraft.MOD_ID, "daytime_weakness_mod"),
                weaknessValue,
                EntityAttributeModifier.Operation.ADD_VALUE
        );
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE))
                .addPersistentModifier(weaknessModifier);

        long currentDay = getServerWorld().getTimeOfDay() / 24000; // Calculate the current day

        if (!wasInWeakness) {
            player.sendMessage(Text.literal("§7§oNocturne whispers to you: You are in weakness."), false);
            wasInWeakness = true;
            wasInStrength = false;
            long currentTime = player.getWorld().getTimeOfDay(); // Use global game time
            long elapsedTime = calculateElapsedTime(currentTime, lastSleepTime);
            double daysSinceSlept = elapsedTime / 24000.0; // Assuming each Minecraft day is 24000 ticks
            System.out.println("Days since last slept: " + daysSinceSlept + ", Additional Damage: " + additionalDamage);
            System.out.println("Time disparity: " + elapsedTime);
        }
    }




    @Unique
    private boolean isWieldingNocturnesKiss(ServerPlayerEntity player) {
        // Check if the player has the "Nocturne's Kiss" item anywhere in their inventory
        Item nocturnesKissItem = Registries.ITEM.get(Identifier.of(Weirdcraft.MOD_ID, "nocturnes_kiss"));

        // Use player.getInventory().contains() to check if the item is in the inventory
        return player.getInventory().contains(new ItemStack(nocturnesKissItem));

    }
}
