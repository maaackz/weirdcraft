package com.maaackz.weirdcraft;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DreamcastCameraEntity extends Entity {
    private Entity targetEntity;

    public DreamcastCameraEntity(World world) {
        super(EntityType.MARKER, world); // Use marker entity type
        this.setInvisible(true);
        this.setNoGravity(true);
        this.setInvulnerable(true);
    }

    public void setTargetEntity(Entity target) {
        this.targetEntity = target;
    }

    @Override
    public void tick() {
        super.tick();
        if (targetEntity != null && targetEntity.isAlive()) {
            // Sync position and rotation to target entity
            Vec3d pos = targetEntity.getPos();
            this.setPosition(pos.x, pos.y + targetEntity.getStandingEyeHeight(), pos.z);
            this.setYaw(targetEntity.getHeadYaw());
            this.setPitch(targetEntity.getPitch());
        }
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {}
    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {}
    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {}
    @Override
    public boolean shouldRender(double distance) { return false; }
    @Override
    public boolean shouldRenderName() { return false; }
    @Override
    public boolean isCollidable() { return false; }
    @Override
    public boolean isPushable() { return false; }
    @Override
    public boolean canHit() { return false; }
    @Override
    public boolean isAttackable() { return false; }
    @Override
    public boolean isAlive() { return true; }
} 