package com.maaackz.weirdcraft.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SleepPayload(Boolean bool) implements CustomPayload {
    public static final CustomPayload.Id<SleepPayload> ID = new CustomPayload.Id<>(Networking.SLEEP_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, SleepPayload> CODEC = PacketCodec.tuple(PacketCodecs.BOOL, SleepPayload::bool, SleepPayload::new);
    // should you need to send more data, add the appropriate record parameters and change your codec:
    // public static final PacketCodec<RegistryByteBuf, BlockHighlightPayload> CODEC = PacketCodec.tuple(
    //         BlockPos.PACKET_CODEC, BlockHighlightPayload::blockPos,
    //         PacketCodecs.INTEGER, BlockHighlightPayload::myInt,
    //         Uuids.PACKET_CODEC, BlockHighlightPayload::myUuid,
    //         BlockHighlightPayload::new
    // );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}