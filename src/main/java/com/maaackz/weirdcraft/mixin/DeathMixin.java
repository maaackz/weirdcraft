package com.maaackz.weirdcraft.mixin;

import com.maaackz.weirdcraft.util.NameWeight;
import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Mixin(ServerPlayerEntity.class)
public abstract class DeathMixin {
    // Define UUIDs for the specific players
    private static final UUID YINCHARI_UUID = UUID.fromString("901f74c9-a4b0-456e-af66-6479c2ecee7f");
    private static final UUID BUMBLE_UUID = UUID.fromString("8e7caa87-df66-4cef-afd7-594c915e8e01");
    private static final UUID ECROW_UUID = UUID.fromString("00417c81-837a-4cfd-9f20-f8d6b0b446f9");
    private static final UUID DEL_UUID = UUID.fromString("00417c81-837a-4cfd-9f20-f8d6b0b446f9"); // Replace with the actual UUID

    // List of names with weights
    private static final List<NameWeight> ACCEPTABLE_PARROT_NAMES = List.of(
            new NameWeight("e", 5),
            new NameWeight("ecrow", 12),
            new NameWeight("e_crow", 8),
            new NameWeight("crow", 1)
    );

    private static final List<NameWeight> ACCEPTABLE_BEE_NAMES = List.of(
            new NameWeight("bumble", 12),
            new NameWeight("bumblefooc", 4),
            new NameWeight("bumblefuc", 2),
            new NameWeight("bumblefuck", 0)
    );

    private static final List<NameWeight> ACCEPTABLE_BUNNY_NAMES = List.of(
            new NameWeight("yin", 6),
            new NameWeight("yinchari", 4),
            new NameWeight("chari", 0)
    );

    private static final List<NameWeight> ACCEPTABLE_FROG_NAMES = List.of(
            new NameWeight("del", 5),
            new NameWeight("delaney", 0),
            new NameWeight("frizzy", 5)
    );

    private static final Random RANDOM = new Random();

    // Inject into the `onDeath` method of `PlayerEntity`
    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeathInject(CallbackInfo ci) {
        // `this` is the player entity dying
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        // Check if the player has a specific UUID
        if (player.getUuid().equals(YINCHARI_UUID)) {
            spawnEntityWithRandomName(player, EntityType.RABBIT, ACCEPTABLE_BUNNY_NAMES);
        } else if (player.getUuid().equals(BUMBLE_UUID)) {
            spawnEntityWithRandomName(player, EntityType.BEE, ACCEPTABLE_BEE_NAMES);
        } else if (player.getUuid().equals(ECROW_UUID)) {
            spawnEntityWithRandomName(player, EntityType.PARROT, ACCEPTABLE_PARROT_NAMES);
        } else if (player.getUuid().equals(DEL_UUID)) {
            spawnEntityWithRandomName(player, EntityType.FROG, ACCEPTABLE_FROG_NAMES);
        }
    }

    // Method to spawn an entity with a random name based on weights
    private void spawnEntityWithRandomName(ServerPlayerEntity player, EntityType<?> entityType, List<NameWeight> weightedNames) {
        World world = player.getWorld();

        // Ensure we're in a server world to avoid client-side operations
        if (!world.isClient() && world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;
            BlockPos deathPos = player.getBlockPos();

            // Create the entity
            var entity = entityType.create(serverWorld);

            if (entity != null) {
                // Choose a random name based on weights
                String randomName = getRandomWeightedName(weightedNames);
                //entity.setCustomName(Text.literal(randomName));

                // Set the position to the player's death position
                entity.refreshPositionAndAngles(deathPos, 0.0F, 0.0F);

                // Spawn the entity in the world
                serverWorld.spawnEntity(entity);
            }
        }
    }

    // Helper method to select a name based on weight
    private String getRandomWeightedName(List<NameWeight> names) {
        int totalWeight = names.stream().mapToInt(NameWeight::getWeight).sum();
        int randomIndex = RANDOM.nextInt(totalWeight);
        int cumulativeWeight = 0;

        for (NameWeight name : names) {
            cumulativeWeight += name.getWeight();
            if (randomIndex < cumulativeWeight) {
                return name.getName();
            }
        }

        // Fallback in case of error
        return "default_name";
    }


}
