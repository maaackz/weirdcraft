package com.maaackz.weirdcraft.mixin.client;


import com.maaackz.weirdcraft.JesusGui;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class NearDeathMixin {
    private boolean displayedOnce = false; // Flag to track if the display has already been triggered

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo info) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            PlayerEntity player = client.player;
            World world = player.getWorld();

            boolean isInWorld = world != null;
            boolean isAlive = player.isAlive();

            if (isInWorld && isAlive) {
                if (player.getHealth() <= 3 && !player.isCreative() && !displayedOnce) {
                    System.out.println("Display triggered");
                    JesusGui.display();
                    displayedOnce = true; // Mark as triggered
                } else if (player.getHealth() > 3) {
                    displayedOnce = false; // Reset the flag when health recovers
                }
            }
        }
    }
}
