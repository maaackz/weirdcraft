package com.maaackz.weirdcraft.mixin.client;

import com.maaackz.weirdcraft.DreamcastingClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HideViewmodelMixin {
    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"), cancellable = true)
    private void hideFirstPersonItem(LivingEntity entity, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (DreamcastingClient.isDreamcasting()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderArmHoldingItem", at = @At("HEAD"), cancellable = true)
    private void hideArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, float swingProgress, float tickDelta, Hand hand, CallbackInfo ci) {
        if (DreamcastingClient.isDreamcasting()) {
            ci.cancel();
        }
    }
} 