package com.maaackz.weirdcraft.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayerEntity.class)
public abstract class ChunkTrackingMixin {
//    @Shadow
//    private ServerPlayNetworkHandler networkHandler;
//
//    @Inject(method = "updatePosition", at = @At("HEAD"))
//    private void forceTrackChunks(CallbackInfo ci) {
//        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
//        if (player.getCameraEntity() instanceof CustomEntity entity) {
//            ChunkPos chunkPos = new ChunkPos(entity.getBlockPos());
//            player.getWorld().getChunkManager().addTicket(ChunkStatus.FULL, chunkPos, 1, player);
//        }
//    }
}
