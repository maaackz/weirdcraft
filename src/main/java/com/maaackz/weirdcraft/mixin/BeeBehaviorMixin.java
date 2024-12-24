package com.maaackz.weirdcraft.mixin;

import com.maaackz.weirdcraft.FollowPlayerGoal;
import net.minecraft.entity.passive.BeeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(BeeEntity.class)
public abstract class BeeBehaviorMixin {

    private static final UUID TARGET_UUID = UUID.fromString("8e7caa87-df66-4cef-afd7-594c915e8e01");

    // Inject into the `initGoals` method to add a goal for following the player
    @Inject(method = "initGoals", at = @At("HEAD"))
    private void onInitGoals(CallbackInfo ci) {
        BeeEntity bee = (BeeEntity) (Object) this;

        // Add FollowPlayerGoal to follow the player with the given UUID
        bee.getGoalSelector().add(1, new FollowPlayerGoal(bee, TARGET_UUID));
    }

    // Prevent bees from getting angry if the UUID matches TARGET_UUID
    @Inject(method = "setAngryAt", at = @At("HEAD"), cancellable = true)
    private void cancelSetAngryAt(UUID uuid, CallbackInfo ci) {
        BeeEntity bee = (BeeEntity) (Object) this;

        // Only cancel if this bee's UUID matches the TARGET_UUID
        if (bee.getUuid().equals(TARGET_UUID)) {
            ci.cancel();
        }
    }

    // Prevent the bee's anger from increasing over time if UUID matches TARGET_UUID
    @Inject(method = "getAngryAt", at = @At("HEAD"), cancellable = true)
    private void cancelAngerLogic(CallbackInfoReturnable<UUID> cir) {
        BeeEntity bee = (BeeEntity) (Object) this;

        // Only cancel if this bee's UUID matches the TARGET_UUID
        if (bee.getUuid().equals(TARGET_UUID)) {
            cir.cancel();
        }
    }

    // Ensure `setAngerTime` does nothing if UUID matches TARGET_UUID
    @Inject(method = "setAngerTime", at = @At("HEAD"), cancellable = true)
    private void overrideSetAngerTime(int angerTime, CallbackInfo ci) {
        BeeEntity bee = (BeeEntity) (Object) this;

        // Only cancel if this bee's UUID matches the TARGET_UUID
        if (bee.getUuid().equals(TARGET_UUID)) {
            ci.cancel();
        }
    }
}
