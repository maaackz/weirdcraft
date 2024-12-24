package com.maaackz.weirdcraft.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpectralArrowEntity.class)
public abstract class SpectralArrowEntityMixin {

    @Inject(at = @At(value = "HEAD"), method = "onHit")
    private void onHitPlayer(LivingEntity target, CallbackInfo ci) {
        // Cast this mixin to SpectralArrowEntity to access its methods and fields
        SpectralArrowEntity arrow = (SpectralArrowEntity) (Object) this;
        World world = arrow.getWorld();

        // Check if the shooter exists and is in a valid world
        Entity shooter = arrow.getOwner();
        if (shooter != null && world != null && !world.isClient) {
            // Play a sound at the shooter's position
            world.playSound(
                    null, // Player to exclude from hearing the sound (null = all players hear it)
                    shooter.getX(), shooter.getY(), shooter.getZ(), // Shooter's position
                    net.minecraft.sound.SoundEvents.ENTITY_ARROW_HIT_PLAYER, // Sound event
                    net.minecraft.sound.SoundCategory.PLAYERS, // Sound category
                    1.0F, // Volume
                    1.0F // Pitch
            );
        }
    }
}
