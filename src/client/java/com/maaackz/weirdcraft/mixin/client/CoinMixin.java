package com.maaackz.weirdcraft.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class CoinMixin {

    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At("HEAD"), cancellable = true)
    private void renderGuiItemIcon(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        if (stack.getName().getString().toLowerCase().contains("coin")) {
            // Adjust scale dynamically based on render mode
            float scale = 1.0F; // Default scale
            if (renderMode == ModelTransformationMode.GUI) {
                scale = 1.0F; // Hotbar
            } else if (renderMode == ModelTransformationMode.FIRST_PERSON_LEFT_HAND || renderMode == ModelTransformationMode.FIRST_PERSON_RIGHT_HAND) {
                scale = 0.35F; // Smaller in hand
            } else if (renderMode == ModelTransformationMode.THIRD_PERSON_LEFT_HAND || renderMode == ModelTransformationMode.THIRD_PERSON_RIGHT_HAND) {
                scale = 0.35F; // Adjust for third-person rendering
            } else if (renderMode == ModelTransformationMode.GROUND) {
                scale = 0.35F; // Adjust for third-person rendering
            }

            renderCircularIcon(stack, matrices, vertexConsumers, light, overlay, scale);
            ci.cancel(); // Prevent default rendering
        }
    }

    private void renderCircularIcon(ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, float scale) {
        MinecraftClient client = MinecraftClient.getInstance();

        // Get the baked model and texture of the item
        BakedModel model = client.getItemRenderer().getModel(stack, client.world, null, 0);
        Sprite sprite = model.getParticleSprite();
        Identifier textureId = sprite.getAtlasId();

        // Push the matrix stack
        matrices.push();

        // Scale the rendering to fit within the GUI or world
        matrices.scale(scale, scale, scale);
        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();

        // Enable custom rendering
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, textureId);

        // Check if the stack is a block or item
        boolean isBlock = stack.getItem() instanceof net.minecraft.item.BlockItem;

        // Determine zoom factor: zoom in for items, normal for blocks
        float zoomFactor = isBlock ? 1.0F : 1.75F;

        // Create a vertex consumer for the circle rendering
        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getTranslucent());
        renderCircle(buffer, positionMatrix, light, overlay, sprite, zoomFactor);
        renderCircle2(buffer, positionMatrix, light, overlay, sprite, zoomFactor);
        // Pop the matrix stack
        matrices.pop();
    }


    private void renderCircle(VertexConsumer buffer, Matrix4f matrix, int light, int overlay, Sprite sprite, float zoomFactor) {
        float radius = 0.35F; // Adjust as needed
        int segments = 32; // Number of segments for the circle

        // Texture center for UV mapping
        float uCenter = sprite.getMinU() + (sprite.getMaxU() - sprite.getMinU()) / 2.0F;
        float vCenter = sprite.getMinV() + (sprite.getMaxV() - sprite.getMinV()) / 2.0F;

        // Zoomed radius for UV mapping
        float uRadius = ((sprite.getMaxU() - sprite.getMinU()) / 2.0F) / zoomFactor;
        float vRadius = ((sprite.getMaxV() - sprite.getMinV()) / 2.0F) / zoomFactor;

        for (int i = 0; i < segments; i++) {
            float angle1 = (float) (2 * Math.PI * (i+2) / segments);
            float angle2 = (float) (2 * Math.PI * (i + 1) / segments);

            // Circle vertex positions
            float x1 = (float) Math.cos(angle1) * radius;
            float y1 = (float) Math.sin(angle1) * radius;
            float x2 = (float) Math.cos(angle2) * radius;
            float y2 = (float) Math.sin(angle2) * radius;

            // Rotation angle in radians (180 degrees = π radians)
            float rotationAngle = (float) Math.PI;

// Apply rotation to UV coordinates
            float u1 = uCenter + (float) (Math.cos(rotationAngle) * (Math.cos(angle1) * uRadius) - Math.sin(rotationAngle) * (Math.sin(angle1) * vRadius));
            float v1 = vCenter + (float) (Math.sin(rotationAngle) * (Math.cos(angle1) * uRadius) + Math.cos(rotationAngle) * (Math.sin(angle1) * vRadius));
            float u2 = uCenter + (float) (Math.cos(rotationAngle) * (Math.cos(angle2) * uRadius) - Math.sin(rotationAngle) * (Math.sin(angle2) * vRadius));
            float v2 = vCenter + (float) (Math.sin(rotationAngle) * (Math.cos(angle2) * uRadius) + Math.cos(rotationAngle) * (Math.sin(angle2) * vRadius));



            // Draw triangles for the circle
            buffer.vertex(matrix, 0, 0, 0) // Center vertex
                    .color(255, 255, 255, 255) // White
                    .texture(uCenter, vCenter) // Center UV
                    .light(light)
                    .overlay(overlay)
                    .normal(0, 0, 1);
//                    .next();
            buffer.vertex(matrix, x1, y1, 0) // First triangle vertex
                    .color(255, 255, 255, 255)
                    .texture(u1, v1)
                    .light(light)
                    .overlay(overlay)
                    .normal(0, 0, 1);
//                    .next();
            buffer.vertex(matrix, x2, y2, 0) // Second triangle vertex
                    .color(255, 255, 255, 255)
                    .texture(u2, v2)
                    .light(light)
                    .overlay(overlay)
                    .normal(0, 0, 1);
//                    .next();
        }
    }


    private void renderCircle2(VertexConsumer buffer, Matrix4f matrix, int light, int overlay, Sprite sprite, float zoomFactor) {
        float radius = 0.35F; // Adjust as needed
        int segments = 32; // Number of segments for the circle

        // Texture center and radius for proper UV mapping
        float uCenter = sprite.getMinU() + (sprite.getMaxU() - sprite.getMinU()) / 2.0F;
        float vCenter = sprite.getMinV() + (sprite.getMaxV() - sprite.getMinV()) / 2.0F;
        float uRadius = ((sprite.getMaxU() - sprite.getMinU()) / 2.0F) / zoomFactor;
        float vRadius = ((sprite.getMaxV() - sprite.getMinV()) / 2.0F) / zoomFactor;

        for (int i = 0; i < segments; i++) {
            float angle1 = (float) (2 * Math.PI * i / segments);
            float angle2 = (float) (2 * Math.PI * (i+2) / segments);

            // Circle vertex positions
            float x1 = (float) Math.cos(angle1) * radius;
            float y1 = (float) Math.sin(angle1) * radius;
            float x2 = (float) Math.cos(angle2) * radius;
            float y2 = (float) Math.sin(angle2) * radius;

            // Rotation angle in radians (180 degrees = π radians)
            float rotationAngle = (float) Math.PI;

// Apply rotation to UV coordinates
            float u1 = uCenter + (float) (Math.cos(rotationAngle) * (Math.cos(angle1) * uRadius) - Math.sin(rotationAngle) * (Math.sin(angle1) * vRadius));
            float v1 = vCenter + (float) (Math.sin(rotationAngle) * (Math.cos(angle1) * uRadius) + Math.cos(rotationAngle) * (Math.sin(angle1) * vRadius));
            float u2 = uCenter + (float) (Math.cos(rotationAngle) * (Math.cos(angle2) * uRadius) - Math.sin(rotationAngle) * (Math.sin(angle2) * vRadius));
            float v2 = vCenter + (float) (Math.sin(rotationAngle) * (Math.cos(angle2) * uRadius) + Math.cos(rotationAngle) * (Math.sin(angle2) * vRadius));


            // Draw triangles for the circle
            buffer.vertex(matrix, 0, 0, 0) // Center vertex
                    .color(255, 255, 255, 255) // White
                    .texture(uCenter, vCenter) // Center UV
                    .light(light)
                    .overlay(overlay)
                    .normal(0, 0, 1);
            buffer.vertex(matrix, x1, y1, 0) // First triangle vertex
                    .color(255, 255, 255, 255)
                    .texture(u1, v1)
                    .light(light)
                    .overlay(overlay)
                    .normal(0, 0, 1);
            buffer.vertex(matrix, x2, y2, 0) // Second triangle vertex
                    .color(255, 255, 255, 255)
                    .texture(u2, v2)
                    .light(light)
                    .overlay(overlay)
                    .normal(0, 0, 1);
        }
    }



}