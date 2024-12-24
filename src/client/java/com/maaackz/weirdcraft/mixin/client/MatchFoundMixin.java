package com.maaackz.weirdcraft.mixin.client;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MatchFoundMixin {

    @Inject(at = @At("HEAD"), method = "isConnectedToServer", cancellable = true)
    private void onJoin(CallbackInfoReturnable<Boolean> cir){
        System.out.println("Joining a game MATCH FOUND");
    }
}
