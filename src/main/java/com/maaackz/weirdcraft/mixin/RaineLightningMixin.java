package com.maaackz.weirdcraft.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;
import java.util.UUID;

@Mixin(ServerPlayerEntity.class)
public abstract class RaineLightningMixin {
    @Unique
    private static final Random RANDOM = new Random();
    // Replace this with the UUID of the target player
    private static final UUID TARGET_UUID = UUID.fromString("a07cae6c-e5e9-46f5-b2f4-a1cf814e568d");

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if (player.getUuid().equals(TARGET_UUID)) {
            spawnLightning(player.getWorld(), player.getBlockPos());
        }
    }

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void onDisconnected(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if (player.getUuid().equals(TARGET_UUID)) {
            spawnLightning(player.getWorld(), player.getBlockPos());
        }
    }

    public void spawnLightning(World world, BlockPos pos) {

        int radius = RANDOM.nextInt(11) + 20;
        double angle = RANDOM.nextDouble() * 2 * Math.PI; // Random angle in radians
        int xOffset = MathHelper.floor(radius * Math.cos(angle));
        int zOffset = MathHelper.floor(radius * Math.sin(angle));
        BlockPos newPos = pos.add(xOffset, 0, zOffset);

        LightningEntity lightningEntity = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
        lightningEntity.setFireTicks(0);
        lightningEntity.setOnFire(false);
        lightningEntity.setOnFireFor(0);
        lightningEntity.extinguish();
        lightningEntity.setPosition(newPos.getX(), newPos.getY(), newPos.getZ());
        world.spawnEntity(lightningEntity);

    }
}
