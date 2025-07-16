package com.maaackz.weirdcraft.mixin.client;

import com.maaackz.weirdcraft.DreamcastingClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class DreamcastingCameraRotationMixin {
    @Inject(method = "update", at = @At("HEAD"))
    private void beforeUpdate(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (DreamcastingClient.isDreamcasting() && focusedEntity != null && !(focusedEntity instanceof PlayerEntity)) {
            try {
                // Set the focused entity's rotation to match the dreamcasted entity
                focusedEntity.setYaw(focusedEntity.getHeadYaw());
                focusedEntity.setPitch(focusedEntity.getPitch());
                
                // Also force the camera entity rotation if it's different from the focused entity
                MinecraftClient client = MinecraftClient.getInstance();
                if (client != null && client.cameraEntity != null && client.cameraEntity != focusedEntity) {
                    client.cameraEntity.setYaw(focusedEntity.getHeadYaw());
                    client.cameraEntity.setPitch(focusedEntity.getPitch());
                }
                
                // Debug logging
                if (System.currentTimeMillis() % 1000 < 50) {
                    System.out.println("[Camera Rotation] Set entity yaw: " + focusedEntity.getYaw() + ", pitch: " + focusedEntity.getPitch());
                }
            } catch (Exception e) {
                System.out.println("[Camera Rotation] Error: " + e.getMessage());
            }
        }
    }
    
    @Inject(method = "update", at = @At("TAIL"))
    private void afterUpdate(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (DreamcastingClient.isDreamcasting() && focusedEntity != null && !(focusedEntity instanceof PlayerEntity)) {
            try {
                // Double-check and force rotation at the end as well
                focusedEntity.setYaw(focusedEntity.getHeadYaw());
                focusedEntity.setPitch(focusedEntity.getPitch());
                
                // Force camera entity rotation again
                MinecraftClient client = MinecraftClient.getInstance();
                if (client != null && client.cameraEntity != null) {
                    client.cameraEntity.setYaw(focusedEntity.getHeadYaw());
                    client.cameraEntity.setPitch(focusedEntity.getPitch());
                }
            } catch (Exception e) {
                // Ignore errors in the tail injection
            }
        }
    }
} 