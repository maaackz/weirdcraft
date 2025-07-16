package com.maaackz.weirdcraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CustomSpectateCamera extends Entity {
    
    private final BlockPos targetPos;
    private final String targetName;
    private int updateTicks = 0;
    
    public CustomSpectateCamera(World world, BlockPos targetPos, String targetName) {
        super(EntityType.MARKER, world); // Use marker entity type as base
        this.targetPos = targetPos;
        this.targetName = targetName;
        
        // Set initial position slightly above and to the side of the target for better visibility
        this.setPosition(targetPos.getX() + 2.0, targetPos.getY() + 3.0, targetPos.getZ() + 2.0);
        
        // Make it invisible and non-colliding
        this.setInvisible(true);
        this.setNoGravity(true);
        this.setInvulnerable(true);
        
        // Prevent movement and rotation to avoid rendering issues
        this.setVelocity(0, 0, 0);
        this.setYaw(0);
        this.setPitch(0);
    }
    
    @Override
    public void tick() {
        super.tick();
        // Smoothly interpolate camera position toward the target
        double targetX = targetPos.getX() + 2.0;
        double targetY = targetPos.getY() + 3.0;
        double targetZ = targetPos.getZ() + 2.0;
        Vec3d current = this.getPos();
        double lerpFactor = 0.2; // 0.0 = no movement, 1.0 = instant snap
        double newX = current.x + (targetX - current.x) * lerpFactor;
        double newY = current.y + (targetY - current.y) * lerpFactor;
        double newZ = current.z + (targetZ - current.z) * lerpFactor;
        this.setPosition(newX, newY, newZ);
    }
    
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        // No data tracking needed for camera
    }
    
    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        // No custom data to read
    }
    
    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        // No custom data to write
    }
    
    @Override
    public boolean shouldRender(double distance) {
        return false; // Never render the camera entity
    }
    
    @Override
    public boolean shouldRenderName() {
        return false; // Never render the name
    }
    
    @Override
    public boolean isCollidable() {
        return false; // No collision
    }
    
    @Override
    public boolean isPushable() {
        return false; // Can't be pushed
    }
    
    @Override
    public boolean canHit() {
        return false; // Can't be hit
    }
    
    @Override
    public boolean isAttackable() {
        return false; // Can't be attacked
    }
    
    @Override
    public boolean isAlive() {
        return true; // Always alive for camera purposes
    }
    
    public BlockPos getTargetPosition() {
        return targetPos;
    }
    
    public String getTargetName() {
        return targetName;
    }
} 