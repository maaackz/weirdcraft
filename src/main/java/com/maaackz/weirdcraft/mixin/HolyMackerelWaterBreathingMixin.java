package com.maaackz.weirdcraft.mixin;


import com.maaackz.weirdcraft.item.CustomItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class HolyMackerelWaterBreathingMixin extends Entity {

    @Shadow
    public abstract boolean canBreatheInWater();

    protected HolyMackerelWaterBreathingMixin(EntityType<? extends Entity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "canBreatheInWater", at = @At("HEAD"), cancellable = true)
    private void breatheInWater(CallbackInfoReturnable<Boolean> cir) {
        // Check if this entity is a player
        if ((Object) this instanceof PlayerEntity player) { // Cast the current instance to PlayerEntity
            // Check if the player has the Holy Mackerel item in their inventory
            if (player.getInventory().contains(new ItemStack(CustomItems.HOLY_MACKEREL))) {
                cir.setReturnValue(true); // Override the method to allow breathing in water
            }
        }
    }
}
