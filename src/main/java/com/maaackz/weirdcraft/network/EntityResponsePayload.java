package com.maaackz.weirdcraft.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record EntityResponsePayload(int entityId, BlockPos entityPos, String entityName) implements CustomPayload {
    public static final CustomPayload.Id<EntityResponsePayload> ID = new CustomPayload.Id<>(Networking.ENTITY_RESPONSE_PACKET_ID);

    public static final PacketCodec<RegistryByteBuf, EntityResponsePayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, EntityResponsePayload::entityId,
            BlockPos.PACKET_CODEC, EntityResponsePayload::entityPos,
            PacketCodecs.STRING, EntityResponsePayload::entityName,
            EntityResponsePayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
