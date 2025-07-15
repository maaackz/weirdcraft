package com.maaackz.weirdcraft.mixin.client;

import com.maaackz.weirdcraft.DreamcastingClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.ChunkSectionPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class DreamcastingCameraChunkMixin {
    @Inject(method = "setupTerrain", at = @At("HEAD"))
    private void onSetupTerrainHead(Camera camera, Frustum frustum, boolean hasForcedFrustum, boolean spectator, CallbackInfo ci) {
        if (DreamcastingClient.isDreamcasting()) {
            MinecraftClient client = MinecraftClient.getInstance();
            Entity dreamCamera = DreamcastingClient.getDreamcastingCameraEntity();
            System.out.println("[Dreamcasting] Camera override attempted");
            if (dreamCamera != null) {
                System.out.println("[Dreamcasting] Using entity: " + dreamCamera.getName().getString() + " (" + dreamCamera.getType().toString() + ") at " + dreamCamera.getBlockPos());
                ((WorldRendererAccessor) (Object) this).setCameraChunkX(ChunkSectionPos.getSectionCoord(dreamCamera.getX()));
                ((WorldRendererAccessor) (Object) this).setCameraChunkY(ChunkSectionPos.getSectionCoord(dreamCamera.getY()));
                ((WorldRendererAccessor) (Object) this).setCameraChunkZ(ChunkSectionPos.getSectionCoord(dreamCamera.getZ()));
                ((WorldRendererAccessor) (Object) this).getChunks().updateCameraPosition(dreamCamera.getX(), dreamCamera.getZ());
            } else {
                System.out.println("[Dreamcasting] WARNING: dreamCamera entity is null!");
            }
        }
    }
} 