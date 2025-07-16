package com.maaackz.weirdcraft.mixin.client;

import com.maaackz.weirdcraft.DreamcastingClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SleepingChatScreen.class)
public class SleepingChatScreenMixin {
    
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        // Force camera rotation during sleep screen if dreamcasting
        if (DreamcastingClient.isDreamcasting()) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.cameraEntity != null && client.cameraEntity != client.player) {
                try {
                    // Force the camera entity to maintain its rotation
                    float yaw = client.cameraEntity.getHeadYaw();
                    float pitch = client.cameraEntity.getPitch();
                    client.cameraEntity.setYaw(yaw);
                    client.cameraEntity.setPitch(pitch);
                    
                    // Debug logging
                    if (System.currentTimeMillis() % 2000 < 50) {
                        System.out.println("[SleepScreen] Forced camera rotation - yaw: " + yaw + ", pitch: " + pitch);
                    }
                } catch (Exception e) {
                    // Ignore errors
                }
            }
        }
    }
} 