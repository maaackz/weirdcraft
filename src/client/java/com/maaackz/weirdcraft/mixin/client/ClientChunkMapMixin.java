package com.maaackz.weirdcraft.mixin.client;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "net.minecraft.client.world.ClientChunkManager$ClientChunkMap")
public abstract class ClientChunkMapMixin {

//    @Inject(method = "isInRadius", at = @At("HEAD"), cancellable = true)
//    private void ignoreRadiusCheck(int chunkX, int chunkZ, CallbackInfoReturnable<Boolean> cir) {
//        // Always return true to bypass the radius check
////        cir.cancel();
//        cir.setReturnValue(true);
//    }

}

