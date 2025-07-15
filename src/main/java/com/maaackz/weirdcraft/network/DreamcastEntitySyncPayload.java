package com.maaackz.weirdcraft.network;

import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public record DreamcastEntitySyncPayload(Identifier entityTypeId, int entityId, BlockPos pos, Vec3d velocity, float yaw, float pitch, NbtCompound nbt) implements CustomPayload {
    public static final CustomPayload.Id<DreamcastEntitySyncPayload> ID = new CustomPayload.Id<>(Networking.DREAMCAST_ENTITY_SYNC_ID);

    // Custom codec for Vec3d, since there is no built-in Vec3d.PACKET_CODEC
    public static final PacketCodec<RegistryByteBuf, Vec3d> VEC3D_PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.DOUBLE, v -> v.x,
            PacketCodecs.DOUBLE, v -> v.y,
            PacketCodecs.DOUBLE, v -> v.z,
            Vec3d::new
    );

    public static final PacketCodec<RegistryByteBuf, DreamcastEntitySyncPayload> CODEC = PacketCodec.tuple(
            Identifier.PACKET_CODEC, DreamcastEntitySyncPayload::entityTypeId,
            PacketCodecs.INTEGER, DreamcastEntitySyncPayload::entityId,
            BlockPos.PACKET_CODEC, DreamcastEntitySyncPayload::pos,
            VEC3D_PACKET_CODEC, DreamcastEntitySyncPayload::velocity,
            PacketCodecs.FLOAT, DreamcastEntitySyncPayload::yaw,
            // We can only have 6 arguments in a tuple, so we nest the last two.
            PacketCodec.tuple(
                    PacketCodecs.FLOAT, (Object[] arr) -> (Float) arr[0],
                    PacketCodecs.NBT_COMPOUND, (Object[] arr) -> (NbtCompound) arr[1],
                    (p, n) -> new Object[]{p, n}
            ),
            (payload) -> new Object[]{payload.pitch(), payload.nbt()},
            // Reconstruct the payload from the 6 arguments
            (entityTypeId, entityId, pos, velocity, yaw, pitchAndNbt) -> new DreamcastEntitySyncPayload(
                    entityTypeId,
                    entityId,
                    pos,
                    velocity,
                    yaw,
                    (Float) pitchAndNbt[0],
                    (NbtCompound) pitchAndNbt[1]
            )
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
} 