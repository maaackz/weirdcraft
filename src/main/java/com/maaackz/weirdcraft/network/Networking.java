package com.maaackz.weirdcraft.network;

import com.maaackz.weirdcraft.Weirdcraft;
import net.minecraft.util.Identifier;

public class Networking {
    public static final Identifier TIME_PACKET_ID = Identifier.of(Weirdcraft.MOD_ID, "time");
    public static final Identifier SLEEP_PACKET_ID = Identifier.of(Weirdcraft.MOD_ID, "sleep");
    public static final Identifier WEATHER_PACKET_ID = Identifier.of(Weirdcraft.MOD_ID, "weather");
    public static final Identifier ENTITY_REQUEST_PACKET_ID = Identifier.of(Weirdcraft.MOD_ID,"entity_request");
    public static final Identifier ENTITY_RESPONSE_PACKET_ID = Identifier.of(Weirdcraft.MOD_ID,"entity_response");
    public static final Identifier REQUEST_MACKEREL_PACKET_ID = Identifier.of(Weirdcraft.MOD_ID,"request_mackerel");
    public static final Identifier DREAMCAST_ENTITY_SYNC_PACKET_ID = Identifier.of(Weirdcraft.MOD_ID, "dreamcast_entity_sync");
    public static final Identifier DREAMCAST_ENTITY_SYNC_ID = Identifier.of(Weirdcraft.MOD_ID, "dreamcast_entity_sync");
    public static final Identifier REQUEST_CHUNK_RELOAD_PACKET_ID = Identifier.of(Weirdcraft.MOD_ID, "request_chunk_reload");


}
