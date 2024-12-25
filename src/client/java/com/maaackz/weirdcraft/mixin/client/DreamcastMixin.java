package com.maaackz.weirdcraft.mixin.client;

import com.maaackz.weirdcraft.ClientDreamcasting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class DreamcastMixin {

    // Inject into the method where the sleep state is changed
    @Inject(method = "wakeUp()V", at = @At("HEAD"))
    private void onWake(CallbackInfo ci) {

        MinecraftClient client = MinecraftClient.getInstance();
//        ClientDreamcasting.dreamcast(client, false);
        ClientDreamcasting.stopSpectating(client);
    }

    @Inject(method = "wakeUp(ZZ)V", at = @At("HEAD"))
    private void onWakeAlso(CallbackInfo ci) {

        MinecraftClient client = MinecraftClient.getInstance();
//        ClientDreamcasting.dreamcast(client, false);
        ClientDreamcasting.stopSpectating(client);
    }
}
