package org.afv.arqueoutils.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public class SoundUtils {
    public static void sendSoundClient(PlayerEntity player, RegistryEntry<SoundEvent> sound, SoundCategory category, float volume, float pitch) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.networkHandler.sendPacket(new PlaySoundS2CPacket(
                    sound,
                    category,
                    player.getX(), player.getY(), player.getZ(),
                    volume,
                    pitch,
                    player.getRandom().nextLong()
            ));
        }
    }

    public static void sendSoundClient(PlayerEntity player, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        RegistryEntry<SoundEvent> entry = Registries.SOUND_EVENT.getEntry(sound);
        sendSoundClient(player, entry, category, volume, pitch);
    }
}
