package com.maaackz.weirdcraft.sound;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import io.netty.buffer.Unpooled;

public class SoundPacketSender {
    public static final Identifier SOUND_PACKET_ID = new Identifier("ifeelweird", "sound_packet");

    // Register packets (no need to pass the server here)
    public static void registerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(SOUND_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            String action = buf.readString(32767);
            Identifier soundId = buf.readIdentifier();
            float volume = buf.readFloat();
            float pitch = buf.readFloat();
            int fadeDuration = buf.readInt();

            // Process the sound packet on the server side
            server.execute(() -> {
                // Forward the packet to the client
                ServerPlayNetworking.send(player, SOUND_PACKET_ID, buf);
                System.out.println("Sound packet sent");
            });
        });
    }



    // Helper method to create the packet buffer
    private static PacketByteBuf createPacketBuf(String type, Identifier soundId) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeString(type);  // Type: play, stop, etc.
        buf.writeIdentifier(soundId);
        return buf;
    }

    private static PacketByteBuf createPacketBuf(String type, Identifier soundId, float volume, float pitch, int fadeDuration) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeString(type);  // Type: play, stop, etc.
        buf.writeIdentifier(soundId);
        buf.writeFloat(volume);
        buf.writeFloat(pitch);
        buf.writeInt(fadeDuration);
        return buf;
    }

    // Send sound packet to all players or a specific player
    public static PacketByteBuf sendSoundPacket(Object target, String type, Identifier soundId, float volume, float pitch, int fadeDuration, MinecraftServer server) {
        System.out.println("Sending sound packet: " + type + " for sound: " + soundId);

        // Check if the target is "all", then send the packet to all players
        if (target instanceof String && target.equals("all")) {
            sendToAllPlayers(server, type, soundId, volume, pitch, fadeDuration);
        } else if (target instanceof ServerPlayerEntity player) {
            // Otherwise, send it to the specified player
            PacketByteBuf buf = createPacketBuf(type, soundId, volume, pitch, fadeDuration);
            ServerPlayNetworking.send(
                    player,
                    SOUND_PACKET_ID,
                    buf
            );
            return buf;
        }
        return null;
    }

    // Method to send the packet to all players in the server
    public static PacketByteBuf sendToAllPlayers(MinecraftServer server, String type, Identifier soundId, float volume, float pitch, int fadeDuration) {
        System.out.println("Sending sound packet: " + type + " for sound: " + soundId + " to all");
        PacketByteBuf buf = createPacketBuf(type, soundId, volume, pitch, fadeDuration);
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(
                    player,
                    SOUND_PACKET_ID,
                    buf
            );
        }
        return buf;
    }

    public static PacketByteBuf sendToAllPlayers(MinecraftServer server, String type, Identifier soundId) {
        System.out.println("Sending sound packet: " + type + " for sound: " + soundId + " to all");
        PacketByteBuf buf = createPacketBuf(type, soundId);
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(
                    player,
                    SOUND_PACKET_ID,
                    buf
            );
        }
        return buf;
    }
}
