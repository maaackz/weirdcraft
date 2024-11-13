package com.maaackz.weirdcraft.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ServerPlayerEntity.class)
public abstract class YinchariDeathMixin {
    // Define the UUID for the specific player
    private static final UUID YINCHARI_UUID = UUID.fromString("901f74c9-a4b0-456e-af66-6479c2ecee7f");

    // Inject into the `onDeath` method of `PlayerEntity`
    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeathInject(CallbackInfo ci) {
        // `this` is the player entity dying
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        // Check if the player has the specified UUID
        if (player.getUuid().equals(YINCHARI_UUID)) {
            World world = player.getWorld();

            // Ensure we're in a server world to avoid client-side operations
            if (!world.isClient() && world instanceof ServerWorld) {
                ServerWorld serverWorld = (ServerWorld) world;

                // Get the player's death position
                BlockPos deathPos = player.getBlockPos();

                // Create a new rabbit entity
                RabbitEntity bunny = EntityType.RABBIT.create(serverWorld);

                if (bunny != null) {
                    // Set the position to the player's death position
                    bunny.refreshPositionAndAngles(deathPos, 0.0F, 0.0F);

                    // Spawn the bunny in the world
                    serverWorld.spawnEntity(bunny);

                    // Optional: Send a message to all players when the bunny spawns
                    //serverWorld.getServer().getPlayerManager().broadcast(Text.literal("A bunny has appeared at Yinchari's death location!"), false);
                }
            }
        }
    }
}
