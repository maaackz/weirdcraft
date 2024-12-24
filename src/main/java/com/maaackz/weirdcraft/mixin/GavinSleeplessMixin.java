package com.maaackz.weirdcraft.mixin;

import com.maaackz.weirdcraft.Weirdcraft;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ServerPlayerEntity.class)
public abstract class GavinSleeplessMixin {

    @Unique
    private long ticksSinceSlept = 9999999;

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void onReadNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("TicksSinceSlept")) {
            ticksSinceSlept = nbt.getLong("TicksSinceSlept");
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void onWriteNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putLong("TicksSinceSlept", ticksSinceSlept);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        if (!player.isSleeping() && !player.getWorld().isClient) {
//            if (player.getWorld().getTimeOfDay() % 24000L >= 13000L) { // Only increment at night
                ticksSinceSlept++;
//            }
            updateStrengthModifier(player);
        } else if (player.isSleeping()) {
            ticksSinceSlept = 0;
            updateStrengthModifier(player);
        }
    }

    @Unique
    private void updateStrengthModifier(PlayerEntity player) {
        // Remove existing modifiers
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE))
                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "sleepless_str_mod"));
        Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE))
                .removeModifier(Identifier.of(Weirdcraft.MOD_ID, "daytime_weakness_mod"));

        if (ticksSinceSlept > 0) {

            double daysSinceSlept = ticksSinceSlept / 24000.0;
            double additionalDamage = Math.log1p(daysSinceSlept) * 0.5; // Logarithmic scaling

            if (player.getWorld().getTimeOfDay() % 24000L >= 13000L) { // Add bonus at night
                EntityAttributeModifier modifier = new EntityAttributeModifier(
                        Identifier.of(Weirdcraft.MOD_ID, "sleepless_str_mod"),
                        additionalDamage,
                        EntityAttributeModifier.Operation.ADD_VALUE
                );
                Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE))
                        .addPersistentModifier(modifier);
                System.out.println("STRENGTH:");
                System.out.println(additionalDamage);
            } else { // Add weakness during the day
                double weakness = Math.log1p(daysSinceSlept) * 0.5;
                EntityAttributeModifier weaknessModifier = new EntityAttributeModifier(
                        Identifier.of(Weirdcraft.MOD_ID, "daytime_weakness_mod"),
                        -weakness, // Negative value for weakness
                        EntityAttributeModifier.Operation.ADD_VALUE
                );
                Objects.requireNonNull(player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE))
                        .addPersistentModifier(weaknessModifier);
                System.out.println("WEAKNESS:");
                System.out.println(weakness);
            }
            System.out.println(ticksSinceSlept);

        }
    }
}
