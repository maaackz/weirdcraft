package com.maaackz.weirdcraft.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.render.Camera;

@Mixin(Camera.class)
public interface CameraAccessor {
    @Accessor("yaw")
    void setYaw(float yaw);

    @Accessor("pitch") 
    void setPitch(float pitch);
    
    @Accessor("yaw")
    float getYaw();
    
    @Accessor("pitch")
    float getPitch();
    
    @Accessor("focusedEntity")
    void setFocusedEntity(net.minecraft.entity.Entity entity);
    
    @Accessor("focusedEntity")
    net.minecraft.entity.Entity getFocusedEntity();
} 