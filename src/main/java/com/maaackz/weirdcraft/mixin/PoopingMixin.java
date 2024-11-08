package com.maaackz.weirdcraft.mixin;

import com.maaackz.weirdcraft.item.CustomItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnimalEntity.class)
public abstract class PoopingMixin extends PassiveEntity {
    private int poopTicks = setRandomPoopTime(); // Time until next poop

    protected PoopingMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
        // Initialize poopTicks to avoid instant pooping upon spawn
        setRandomPoopTime();
    }

    // Set a random time for pooping between 5 to 10 minutes (in ticks)
    private int setRandomPoopTime() {
        int seconds = this.random.nextBetween(300, 600); // 5 to 10 minutes in seconds
        this.poopTicks = 20 * seconds; // Convert seconds to ticks (20 ticks per second)
        return seconds * 20;
    }

    // Handle pooping in tickMovement like egg laying
    @Inject(method = "tickMovement()V", at = @At("TAIL"))
    private void handlePooping(CallbackInfo ci) {
        if (!this.getWorld().isClient && this.isAlive()) {
            // Decrement poopTicks each tick, but only act when it reaches 0 or less
            if (--this.poopTicks <= 0) {
                // Reset poop timer
                setRandomPoopTime();

                // Drop poop item
                this.dropItem(CustomItems.POOP.getDefaultStack().getItem());

                // Play pooping sound
                this.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);

                // Create poop particle effects
                this.getWorld().addParticle(ParticleTypes.CLOUD, this.getPos().x, this.getPos().y, this.getPos().z, 0, -.2, 0);
                this.getWorld().addParticle(ParticleTypes.CLOUD, this.getPos().x, this.getPos().y, this.getPos().z, -.2, -.2, 0);
                this.getWorld().addParticle(ParticleTypes.CLOUD, this.getPos().x, this.getPos().y, this.getPos().z, +.2, -.2, 0);
                this.getWorld().addParticle(ParticleTypes.CLOUD, this.getPos().x, this.getPos().y, this.getPos().z, 0, -.2, -.2);
                this.getWorld().addParticle(ParticleTypes.CLOUD, this.getPos().x, this.getPos().y, this.getPos().z, 0, -.2, +.2);
            }
        }
    }
}
