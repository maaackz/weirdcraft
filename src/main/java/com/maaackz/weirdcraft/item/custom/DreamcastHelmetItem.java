package com.maaackz.weirdcraft.item.custom;

import net.minecraft.entity.Entity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;

import java.util.List;
import java.util.Random;

public class DreamcastHelmetItem extends ArmorItem {
    public DreamcastHelmetItem(Settings settings) {
        super(ArmorMaterials.LEATHER, Type.HELMET, settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    public void onSleep(ServerPlayerEntity player) {

        // if (player.getWorld().isClient) {
        //     // Get nearby entities
        //     List<Entity> nearbyEntities = player.getWorld().getEntitiesByClass(
        //             Entity.class,
        //             new Box(player.getX() - 50, player.getY() - 50, player.getZ() - 50,
        //                     player.getX() + 50, player.getY() + 50, player.getZ() + 50),
        //             entity -> entity != player // Exclude the player
        //     );

        //     if (!nearbyEntities.isEmpty()) {
        //         Entity randomEntity = nearbyEntities.get(new Random().nextInt(nearbyEntities.size()));

        //         // Use the helper class to set the camera
        //     }
        // }
    }

    public void onWakeUp(ServerPlayerEntity player) {
        if (player.getWorld().isClient) {
            // Use the helper class to reset the camera

        }
    }
}
