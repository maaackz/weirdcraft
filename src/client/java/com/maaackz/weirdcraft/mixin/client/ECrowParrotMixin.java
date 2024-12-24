package com.maaackz.weirdcraft.mixin.client;

import net.minecraft.client.render.entity.ParrotEntityRenderer;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(ParrotEntityRenderer.class)
public class ECrowParrotMixin {

    private static final Set<String> ACCEPTABLE_NAMES = Set.of("e", "ecrow", "e_crow", "crow");

    @Inject(at = @At("HEAD"), method = "getTexture(Lnet/minecraft/entity/passive/ParrotEntity;)Lnet/minecraft/util/Identifier;", cancellable = true)
    private void init(ParrotEntity parrotEntity, CallbackInfoReturnable<Identifier> cir) {
        if (parrotEntity.hasCustomName() && ACCEPTABLE_NAMES.contains(parrotEntity.getName().getString())) {
            cir.setReturnValue(Identifier.of("weirdcraft", "textures/entity/parrot/crow.png"));
        }
    }
}
