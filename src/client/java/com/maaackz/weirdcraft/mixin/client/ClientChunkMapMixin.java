package com.maaackz.weirdcraft.mixin.client;

import com.maaackz.weirdcraft.DreamcastingChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.client.world.ClientChunkManager$ClientChunkMap")
public abstract class ClientChunkMapMixin {

    @Inject(method = "isInRadius", at = @At("HEAD"), cancellable = true)
    private void ignoreRadiusCheck(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
        // During dreamcasting, bypass the radius check to allow chunks outside normal view distance
        if (DreamcastingChunkManager.isActive()) {
            cir.setReturnValue(true);
        }
    }

}

