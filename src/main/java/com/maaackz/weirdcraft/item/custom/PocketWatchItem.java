package com.maaackz.weirdcraft.item.custom;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;

public class PocketWatchItem extends Item {
    public PocketWatchItem(Settings settings) {
        super(settings);

    }



    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    static long TIME_INCREMENT = 50L;
    public static void advanceTime(ServerWorld world) {
        // Get the server command source and create the command string
        ServerCommandSource source = world.getServer().getCommandSource();
        // Create a "silent" command source that suppresses output to the chat
        ServerCommandSource silentSource = source.withSilent();
        // Execute the command with the silent command source
        world.getServer().getCommandManager().executeWithPrefix(silentSource, "/time add " + TIME_INCREMENT);
//        System.out.println("Time advanced by: " + TIME_INCREMENT);
    }

    public static void reverseTime(ServerWorld world) {
        // Get the current time of day (0-24000 ticks)
        long currentTimeOfDay = world.getTimeOfDay();

        // Calculate the new time by subtracting 50 ticks
        long newTimeOfDay = currentTimeOfDay - 50L;

        // Prevent the time from going negative, ensure it stays within the 0-24000 range
        if (newTimeOfDay < 0) {
            newTimeOfDay = 24000 + newTimeOfDay; // Wrap around if negative
        }

        // Get the server command source and create the command string
        ServerCommandSource source = world.getServer().getCommandSource();

        // Create a "silent" command source to suppress chat output
        ServerCommandSource silentSource = source.withSilent();


        // Execute the time set command to reverse time
        world.getServer().getCommandManager().executeWithPrefix(silentSource, "/time set " + newTimeOfDay);

        // Print debug info
//        System.out.println("Current Time of Day: " + currentTimeOfDay);
//        System.out.println("New Time of Day: " + newTimeOfDay);
//        System.out.println("Time reversed by 50 ticks.");
    }


}
