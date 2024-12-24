package com.maaackz.weirdcraft.item.custom;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

public class PocketWatchItem extends Item {
    public PocketWatchItem(Settings settings) {
        super(settings);

    }



    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    static int TIME_INCREMENT = 50;
    public static void advanceTime(ServerWorld world) {
        long currentTime = world.getTimeOfDay();
        long newTime = (currentTime + TIME_INCREMENT) % 24000;
        world.setTimeOfDay(newTime);
        System.out.println("Time advanced to: " + newTime);
    }

    public static void reverseTime(ServerWorld world) {
        long currentTime = world.getTimeOfDay();
        long newTime = (currentTime - TIME_INCREMENT) % 24000;
        if (newTime < 0) {
            newTime += 24000; // Wrap around to the end of the day
        }
        world.setTimeOfDay(newTime);
        System.out.println("Time reversed to: " + newTime);
    }
}
