package com.maaackz.weirdcraft;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public class FakeCameraPlayerEntity extends ClientPlayerEntity {
    private Entity targetEntity;

    public FakeCameraPlayerEntity(ClientWorld world, ClientPlayNetworkHandler networkHandler, PlayerEntity originalPlayer) {
        super(
            MinecraftClient.getInstance(),
            world,
            networkHandler,
            new StatHandler(),
            new ClientRecipeBook(),
            false, // lastSneaking
            false  // lastSprinting
        );
        this.setInvisible(true);
        this.setNoGravity(true);
        this.setInvulnerable(true);
        this.setSilent(true);
    }

    public void setTargetEntity(Entity target) {
        this.targetEntity = target;
    }

    @Override
    public void tick() {
        super.tick();
        if (targetEntity != null && targetEntity.isAlive()) {
            Vec3d pos = targetEntity.getPos();
            this.setPosition(pos.x, pos.y + targetEntity.getStandingEyeHeight(), pos.z);
            this.setYaw(targetEntity.getHeadYaw());
            this.setPitch(targetEntity.getPitch());
            this.prevYaw = this.getYaw();
            this.prevPitch = this.getPitch();
        }
    }

    // Prevent all interactions
    @Override public void swingHand(Hand hand) {}
    @Override public void setStackInHand(Hand hand, ItemStack stack) {}
    @Override public void equipStack(EquipmentSlot slot, ItemStack stack) {}
    @Override public boolean isSpectator() { return true; }
    @Override public boolean isCreative() { return false; }
    @Override public boolean shouldRenderName() { return false; }
    @Override public boolean shouldRender(double distance) { return false; }
    @Override public boolean isCollidable() { return false; }
    @Override public boolean isPushable() { return false; }
    @Override public boolean canHit() { return false; }
    @Override public boolean isAttackable() { return false; }
    @Override public boolean isAlive() { return true; }
} 