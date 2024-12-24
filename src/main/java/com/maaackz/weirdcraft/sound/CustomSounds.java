package com.maaackz.weirdcraft.sound;


import com.maaackz.weirdcraft.Weirdcraft;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class CustomSounds {
    public static final SoundEvent PHARAOHS_CURSE_SOUND = registerSoundEvent("pharaohs_curse");
    public static final SoundEvent AAAAA_SOUND = registerSoundEvent("aaaaa");
    public static final SoundEvent I_LUV = registerSoundEvent("iluv");
    public static final SoundEvent I_HATE = registerSoundEvent("ihate");
    public static final SoundEvent BALLSACKS_IN_MY_MOUTH = registerSoundEvent("ballsacksinmymouth");
    public static final SoundEvent IT_WAS_MODDED = registerSoundEvent("itwasmodded");
    public static final SoundEvent PAPAYA_EAT_SOUND = registerSoundEvent("papayaeatsound");
    public static final SoundEvent VALO_MATCH_FOUND = registerSoundEvent("valo_match_found");
    public static final SoundEvent JESUS_BELL_SOUND = registerSoundEvent("jesus_bell");

    public static final SoundEvent SAND_OCEAN = registerSoundEvent("sand_ocean");
    public static final RegistryKey<JukeboxSong> SAND_OCEAN_KEY =
            RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.of(Weirdcraft.MOD_ID, "sand_ocean"));

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.of(Weirdcraft.MOD_ID, name);
        Weirdcraft.LOGGER.info("Registered sound: " + id);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() { Weirdcraft.LOGGER.info("Registering sounds for " + Weirdcraft.MOD_ID); }
}