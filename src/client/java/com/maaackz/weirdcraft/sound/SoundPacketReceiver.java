package com.maaackz.weirdcraft.sound;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

import java.util.Objects;

public class SoundPacketReceiver {

    public static void registerPackets() {
        ClientPlayNetworking.registerGlobalReceiver(SoundPacketSender.SOUND_PACKET_ID, (client, handler, buf, responseSender) -> {
            System.out.println("Sound packet received");
            String action = buf.readString(32767);
            Identifier soundId = buf.readIdentifier();

            float volume; // Default value
            float pitch;  // Default value

            if (action.equals("play")) {
                volume = buf.readFloat();
                pitch = buf.readFloat();
            } else {
                pitch = 0;
                volume = 0;
            }


            client.execute(() -> {
                System.out.println(action);

                if (action.equals("play")) {
                    playSound(client, soundId, volume, pitch);
                } else if (action.equals("stop")) {
                    stopSound(client, soundId);
                }
            });
        });
    }

    private static void playSound(MinecraftClient client, Identifier soundId, float volume, float pitch) {
        assert client.player != null;
        client.player.playSound(SoundPacketReceiver.getSoundEvent(soundId), SoundCategory.MASTER, volume, pitch);
    }

    private static void stopSound(MinecraftClient client, Identifier soundId) {
//        client.getSoundManager().stop(SoundPacketReceiver.getSoundInstance(soundId));
        client.getSoundManager().stopSounds(soundId,SoundCategory.MASTER);
    }

    private static void adjustVolume(MinecraftClient client, Identifier soundId, float volume) {
        // Adjust volume for the sound instance
    }

    private static void adjustPitch(MinecraftClient client, Identifier soundId, float pitch) {
        // Adjust pitch for the sound instance
    }

    private static void fadeInSound(MinecraftClient client, Identifier soundId, float maxVolume, float pitch, int duration) {
        new Thread(() -> {
            float volume = 0.0F;
            int steps = duration / 50;
            for (int i = 0; i < steps; i++) {
                volume += maxVolume / steps;
                float finalVolume = volume;
                client.execute(() -> playSound(client, soundId, finalVolume, pitch));
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void fadeOutSound(MinecraftClient client, Identifier soundId, int duration) {
        new Thread(() -> {
            // Handle fade out with volume adjustment
        }).start();
    }

    public static SoundEvent getSoundEvent(Identifier soundId) {
        System.out.println(Registries.SOUND_EVENT.get(soundId));
        return Registries.SOUND_EVENT.get(soundId);
    }

    public static SoundInstance getSoundInstance(Identifier soundId) {
        // Access the Minecraft client's sound manager
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getSoundManager() == null) {
            return null;  // Return null if the client or sound manager is not available
        }

        // Iterate through the currently playing sounds and find the one with the matching soundId
        System.out.println(client.getSoundManager().get(soundId));
        System.out.println(Objects.requireNonNull(client.getSoundManager().get(soundId)).getSound(Random.create()));
        System.out.println(Objects.requireNonNull(Objects.requireNonNull(client.getSoundManager().get(soundId)).getSound(Random.create())).hashCode());
        return null;
    }

}
