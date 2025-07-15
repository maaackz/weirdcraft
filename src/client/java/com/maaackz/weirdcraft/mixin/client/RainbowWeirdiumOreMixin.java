package com.maaackz.weirdcraft.mixin.client;

import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockModelRenderer.class)
public class RainbowWeirdiumOreMixin {
    private static float hue = 0.0f; // Initialize hue

    @Inject(at = @At("HEAD"), method = "render")
    private void renderRainbowEffect(
            net.minecraft.world.BlockRenderView blockRenderView,
            net.minecraft.client.render.model.BakedModel bakedModel,
            net.minecraft.block.BlockState blockState,
            net.minecraft.util.math.BlockPos blockPos,
            MatrixStack matrices,
            net.minecraft.client.render.VertexConsumer vertexConsumer,
            boolean cull,
            Random random,
            long seed,
            int overlay,
            CallbackInfo ci) {
        hue += 0.01f; // Increment the hue
        if (hue > 1.0f) {
            hue = 0.0f; // Loop back to the start
        }

        // Convert hue to RGB
        int color = ColorHelper.Argb.getArgb(255,
                (int) (Math.sin(hue * Math.PI * 2) * 127 + 128),
                (int) (Math.sin((hue + 0.33f) * Math.PI * 2) * 127 + 128),
                (int) (Math.sin((hue + 0.66f) * Math.PI * 2) * 127 + 128)
        );

        // Apply the color to the emission texture logic
        applyEmissionColor(color);
    }

    private void applyEmissionColor(int color) {
        // Logic to apply the emission color to the block texture
        // You might need to interact with shaders or manipulate the block state renderer
    }
}
