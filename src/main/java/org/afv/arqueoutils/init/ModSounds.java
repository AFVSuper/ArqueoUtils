package org.afv.arqueoutils.init;

import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;

public class ModSounds {
    // DISCS
    public static final SoundEvent MEGALOVANIA = registerDiscSoundEvent("megalovania");
    public static final RegistryKey<JukeboxSong> MEGALOVANIA_KEY =
            RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.of(ArqueoUtils.ModID, "megalovania"));

    public static final SoundEvent PAINT_IT_BLACK = registerDiscSoundEvent("paint_it_black");
    public static final RegistryKey<JukeboxSong> PAINT_IT_BLACK_KEY =
            RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.of(ArqueoUtils.ModID, "paint_it_black"));

    public static final SoundEvent DANCE_OF_THE_KNIGHTS = registerDiscSoundEvent("dance_of_the_knights");
    public static final RegistryKey<JukeboxSong> DANCE_OF_THE_KNIGHTS_KEY =
            RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.of(ArqueoUtils.ModID, "dance_of_the_knights"));

    public static final SoundEvent COGWORK_DANCERS = registerDiscSoundEvent("cogwork_dancers");
    public static final RegistryKey<JukeboxSong> COGWORK_DANCERS_KEY =
            RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.of(ArqueoUtils.ModID, "cogwork_dancers"));

    public static final SoundEvent MANTIS_LORDS = registerDiscSoundEvent("mantis_lords");
    public static final RegistryKey<JukeboxSong> MANTIS_LORDS_KEY =
            RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.of(ArqueoUtils.ModID, "mantis_lords"));

    public static final SoundEvent TETRIS_A = registerDiscSoundEvent("tetris_a");
    public static final RegistryKey<JukeboxSong> TETRIS_A_KEY =
            RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.of(ArqueoUtils.ModID, "tetris_a"));

    public static final SoundEvent PORTAL_RADIO = registerDiscSoundEvent("portal_radio");
    public static final RegistryKey<JukeboxSong> PORTAL_RADIO_KEY =
            RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.of(ArqueoUtils.ModID, "portal_radio"));

    public static final SoundEvent STILL_ALIVE = registerDiscSoundEvent("still_alive");
    public static final RegistryKey<JukeboxSong> STILL_ALIVE_KEY =
            RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.of(ArqueoUtils.ModID, "still_alive"));

    public static final SoundEvent GLORY_ARSTOTSZKA = registerDiscSoundEvent("glory_arstotszka");
    public static final RegistryKey<JukeboxSong> GLORY_ARSTOTSZKA_KEY =
            RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.of(ArqueoUtils.ModID, "glory_arstotszka"));

    public static final SoundEvent ROARING_KNIGHT = registerDiscSoundEvent("roaring_knight");
    public static final RegistryKey<JukeboxSong> ROARING_KNIGHT_KEY =
            RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.of(ArqueoUtils.ModID, "roaring_knight"));

    public static final SoundEvent GOURMET_RACE = registerDiscSoundEvent("gourmet_race");
    public static final RegistryKey<JukeboxSong> GOURMET_RACE_KEY =
            RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.of(ArqueoUtils.ModID, "gourmet_race"));

    public static final SoundEvent GERUDO_VALLEY = registerDiscSoundEvent("gerudo_valley");
    public static final RegistryKey<JukeboxSong> GERUDO_VALLEY_KEY =
            RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.of(ArqueoUtils.ModID, "gerudo_valley"));

    public static final SoundEvent CRYPTEQUE = registerDiscSoundEvent("crypteque");
    public static final RegistryKey<JukeboxSong> CRYPTEQUE_KEY =
            RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.of(ArqueoUtils.ModID, "crypteque"));

    public static final SoundEvent MALENIA = registerDiscSoundEvent("malenia");
    public static final RegistryKey<JukeboxSong> MALENIA_KEY =
            RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.of(ArqueoUtils.ModID, "malenia"));

    public static final SoundEvent DARK_NIGHT = registerDiscSoundEvent("dark_night");
    public static final RegistryKey<JukeboxSong> DARK_NIGHT_KEY =
            RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.of(ArqueoUtils.ModID, "dark_night"));

    public static final SoundEvent BALATRO = registerDiscSoundEvent("balatro");
    public static final RegistryKey<JukeboxSong> BALATRO_KEY =
            RegistryKey.of(RegistryKeys.JUKEBOX_SONG, Identifier.of(ArqueoUtils.ModID, "balatro"));

    // HELPER METHODS
    private static SoundEvent registerDiscSoundEvent(String name) {
        return registerSoundEvent(name);
    }

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.of(ArqueoUtils.ModID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void init() {}
}
