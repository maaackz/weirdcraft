package com.maaackz.weirdcraft.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;


public record TimePayload(boolean advancing, boolean active) implements CustomPayload {
    public static final CustomPayload.Id<TimePayload> ID = new CustomPayload.Id<>(Networking.TIME_PACKET_ID);
     public static final PacketCodec<RegistryByteBuf, TimePayload> CODEC = PacketCodec.tuple(
             PacketCodecs.BOOL, TimePayload::advancing,
             PacketCodecs.BOOL, TimePayload::active,
             TimePayload::new
     );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
