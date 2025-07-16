package com.maaackz.weirdcraft.mixin.client;

import com.maaackz.weirdcraft.DreamcastingClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererCameraMixin {
    
    @Inject(method = "renderWorld", at = @At("HEAD"))
    private void beforeRenderWorld(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (DreamcastingClient.isDreamcasting() && client.cameraEntity != null && client.cameraEntity != client.player) {
            try {
                // Force the camera entity's rotation to be used for rendering
                Entity cameraEntity = client.cameraEntity;
                float yaw = cameraEntity.getHeadYaw();
                float pitch = cameraEntity.getPitch();
                
                // Set the camera entity's rotation
                cameraEntity.setYaw(yaw);
                cameraEntity.setPitch(pitch);
                
                // Also force the camera's internal rotation
                Camera camera = client.gameRenderer.getCamera();
                if (camera != null) {
                    try {
                        // Use reflection to set camera rotation directly
                        java.lang.reflect.Field yawField = Camera.class.getDeclaredField("yaw");
                        java.lang.reflect.Field pitchField = Camera.class.getDeclaredField("pitch");
                        yawField.setAccessible(true);
                        pitchField.setAccessible(true);
                        yawField.setFloat(camera, yaw);
                        pitchField.setFloat(camera, pitch);
                    } catch (Exception e) {
                        // Fallback: just set the entity rotation
                    }
                }
                
                // Debug logging
                if (System.currentTimeMillis() % 1000 < 50) {
                    System.out.println("[GameRenderer] Forced camera entity rotation - yaw: " + yaw + ", pitch: " + pitch);
                }
            } catch (Exception e) {
                System.out.println("[GameRenderer] Error setting camera rotation: " + e.getMessage());
            }
        }
    }
} 