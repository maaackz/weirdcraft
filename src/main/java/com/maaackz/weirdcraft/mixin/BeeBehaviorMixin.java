package com.maaackz.weirdcraft.mixin;

import com.maaackz.weirdcraft.FollowPlayerGoal;
import net.minecraft.entity.passive.BeeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(BeeEntity.class)
public abstract class BeeBehaviorMixin {

    private static final UUID TARGET_UUID = UUID.fromString("8e7caa87-df66-4cef-afd7-594c915e8e01");

    @Inject(method = "initGoals", at = @At("HEAD"))
    private void onInitGoals(CallbackInfo ci) {
        BeeEntity bee = (BeeEntity) (Object) this;

        // Add FollowPlayerGoal to follow the player with the given UUID
        bee.getGoalSelector().add(1, new FollowPlayerGoal(bee, TARGET_UUID));
    }
}
