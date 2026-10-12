package org.afv.arqueoutils.util;

import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.object.PlayerTextObjectContents;
import net.minecraft.world.World;

import java.util.UUID;

public class TextUtils {
    public static void broadcast(MinecraftServer server, Text msg, boolean actionbar) {
        for (World world : server.getWorlds()) {
            for (PlayerEntity player : world.getPlayers()) {
                player.sendMessage(msg, actionbar);
            }
        }
    }

    public static void broadcastWithSound(MinecraftServer server, Text msg, boolean actionbar, SoundEvent soundEvent, float volume, float pitch) {
        for (World world : server.getWorlds()) {
            for (PlayerEntity player : world.getPlayers()) {
                SoundUtils.sendSoundClient(player, soundEvent, SoundCategory.MASTER, volume, pitch);
                player.sendMessage(msg, actionbar);
            }
        }
    }

    public static Text getHeadText(UUID uuid) {
        return Text.object(new PlayerTextObjectContents(ProfileComponent.ofDynamic(uuid), true));
    }

    public static Text getHeadText(PlayerEntity player) {
        return Text.object(new PlayerTextObjectContents(ProfileComponent.ofDynamic(player.getUuid()), true));
    }

    public static String formatTime(long secs) {
        long hours = secs / 3600;
        long minutes = (secs % 3600) / 60;
        long seconds = secs % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String formatTimeWithDays(long secs) {
        long days = secs / (24 * 3600);
        long hours = (secs % (24 * 3600)) / 3600;
        long minutes = (secs % 3600) / 60;
        long seconds = secs % 60;
        return String.format("%01d:%02d:%02d:%02d", days, hours, minutes, seconds);
    }
}
