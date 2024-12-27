package com.maaackz.weirdcraft.mixin;

import net.minecraft.server.network.ChunkDataSender;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChunkDataSender.class)
public abstract class ChunkDataSenderMixin {

    private ServerWorld world;

    protected ChunkDataSenderMixin(ServerWorld world) {
        this.world = world;
    }

//    // Inject into the `unload` method to prevent unloading if the player is sleeping
//    @Inject(method = "unload", at = @At("HEAD"), cancellable = true)
//    private void onUnload(ServerPlayerEntity player, ChunkPos pos, CallbackInfo ci) {
//
//        if (player.isSleeping()) {
//            // Cancel unloading the chunk if any player is sleeping
//            ci.cancel();
//            return;
//        }
//
//    }
//


    // Inject into the `unload` method to prevent unloading if the player is sleeping
//    @Inject(method = "unload", at = @At("HEAD"), cancellable = true)
//    private void onUnload(ServerPlayerEntity player, ChunkPos pos, CallbackInfo ci) {
//
//        if (player.isSleeping()) {
//            // Cancel unloading the chunk if any player is sleeping
//            ci.cancel();
//            return;
//        }
//
//    }

}
