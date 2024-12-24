package com.maaackz.weirdcraft.mixin.client;

import net.minecraft.client.render.entity.BeeEntityRenderer;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(BeeEntityRenderer.class)
public class BumbleBeeMixin {

    // Set of acceptable names for the Bumble bee
    private static final Set<String> ACCEPTABLE_NAMES = Set.of("bumble","bumblefooc","bumblefuc","bumblefuck");

    @Inject(at = @At("HEAD"), method = "getTexture(Lnet/minecraft/entity/passive/BeeEntity;)Lnet/minecraft/util/Identifier;", cancellable = true)
    private void init(BeeEntity beeEntity, CallbackInfoReturnable<Identifier> cir) {
        // Check if the bee has a custom name and if it's in the acceptable names set
        if (beeEntity.hasCustomName() && ACCEPTABLE_NAMES.contains(beeEntity.getName().getString())) {
            // Choose the appropriate texture based on the bee's state
            if (beeEntity.hasAngerTime()) {
                if (beeEntity.hasNectar()) {
                    cir.setReturnValue(Identifier.of("weirdcraft", "textures/entity/bee/bee_angry_nectar_alt.png"));
                } else {
                    cir.setReturnValue(Identifier.of("weirdcraft", "textures/entity/bee/bee_angry_alt.png"));
                }
            } else {
                if (beeEntity.hasNectar()) {
                    cir.setReturnValue(Identifier.of("weirdcraft", "textures/entity/bee/bee_nectar_alt.png"));
                } else {
                    cir.setReturnValue(Identifier.of("weirdcraft", "textures/entity/bee/bee_alt.png"));
                }
            }
        }
    }
}
