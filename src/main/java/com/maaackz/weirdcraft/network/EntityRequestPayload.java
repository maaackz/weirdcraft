package com.maaackz.weirdcraft.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record EntityRequestPayload(int entityId) implements CustomPayload {
    public static final CustomPayload.Id<EntityRequestPayload> ID = new CustomPayload.Id<>(Networking.ENTITY_REQUEST_PACKET_ID);

    public static final PacketCodec<RegistryByteBuf, EntityRequestPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, EntityRequestPayload::entityId,
            EntityRequestPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
