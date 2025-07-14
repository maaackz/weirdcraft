package com.maaackz.weirdcraft.mixin;


import com.maaackz.weirdcraft.item.CustomItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class HolyMackerelWaterBreathingMixin extends Entity {

    protected HolyMackerelWaterBreathingMixin(EntityType<? extends Entity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "canBreatheInWater", at = @At("RETURN"), cancellable = true)
    private void breatheInWater(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (livingEntity instanceof ServerPlayerEntity serverPlayer && !serverPlayer.getServerWorld().isClient) { // Cast the current instance to PlayerEntity
            // Check if the player has the Holy Mackerel item in their inventory
            System.out.println("Checking water breathing...");
            boolean hasHolyMackerel = serverPlayer.getInventory().main.stream()
                    .anyMatch(stack -> stack.getItem() == CustomItems.HOLY_MACKEREL) || serverPlayer.getInventory().offHand.stream()
                    .anyMatch(stack -> stack.getItem() == CustomItems.HOLY_MACKEREL);

            if (hasHolyMackerel) {
                System.out.println("has holy mackerel");
                cir.setReturnValue(true); // Override the method to allow breathing in water
            } else {
                System.out.println("does not has holy mackerel");
            }

        }
    }
}
