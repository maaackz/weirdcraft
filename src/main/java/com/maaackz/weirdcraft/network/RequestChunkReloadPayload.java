package com.maaackz.weirdcraft.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record RequestChunkReloadPayload(int radius) implements CustomPayload {
    public static final CustomPayload.Id<RequestChunkReloadPayload> ID = new CustomPayload.Id<>(Networking.REQUEST_CHUNK_RELOAD_PACKET_ID);

    public static final PacketCodec<RegistryByteBuf, RequestChunkReloadPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, RequestChunkReloadPayload::radius,
            RequestChunkReloadPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
} 