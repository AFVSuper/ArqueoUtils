package org.afv.arqueoutils.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.Uuids;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

import java.util.*;

public class PlaytimeState extends PersistentState {

    private final Map<UUID, Long> playtime = new HashMap<>();

    private static final String KEY = "playtime_state";

    public static final Codec<PlaytimeState> PLAYTIME_STATE_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.unboundedMap(Uuids.CODEC, Codec.LONG)
                            .fieldOf("playtime")
                            .forGetter(state -> state.playtime)
            ).apply(instance, (uuidLongMap -> {
                PlaytimeState state = new PlaytimeState();
                state.playtime.clear();
                state.playtime.putAll(uuidLongMap);
                return state;
            }))
    );

    public static final PersistentStateType<PlaytimeState> PLAYTIME_STATE_TYPE =
            new PersistentStateType<>(
                    KEY, // ID used in saving
                    PlaytimeState::new,      // No-arg constructor
                    PLAYTIME_STATE_CODEC,          // Codec for serialization
                    DataFixTypes.LEVEL   // Typical type for world saved data
            );

    public PlaytimeState() {}

    public static PlaytimeState get(MinecraftServer server) {
        return server.getSpawnWorld().getPersistentStateManager()
                .getOrCreate(PLAYTIME_STATE_TYPE);
    }

    public List<Pair<UUID, Long>> getSortedTimes() {
        ArrayList<Pair<UUID, Long>> lst = new ArrayList<>();
        Set<UUID> keys = new HashSet<>(Set.copyOf(playtime.keySet()));

        while (!keys.isEmpty()) {
            UUID maxKey = UUID.randomUUID();
            long maxTime = 0;
            for (UUID key : keys) {
                if (playtime.get(key) > maxTime) {
                    maxTime = playtime.get(key);
                    maxKey = key;
                }
            }
            keys.remove(maxKey);
            lst.add(new Pair<>(maxKey, maxTime));
        }

        return lst;
    }

    public long getPlaytimeTicks(UUID player) {
        return playtime.getOrDefault(player, 0L);
    }

    public void addPlaytimeTicks(UUID player, long ticks) {
        playtime.put(player, getPlaytimeTicks(player) + ticks);
        markDirty();
    }

    public void setPlaytimeTicks(UUID player, long ticks) {
        playtime.put(player, ticks);
        markDirty();
    }

    public static void registerEvent() {
        ServerTickEvents.END_SERVER_TICK.register(srv -> {
            PlaytimeState state = PlaytimeState.get(srv); // your method to get PersistentState

            for (ServerPlayerEntity player : srv.getPlayerManager().getPlayerList()) {
                UUID uuid = player.getUuid();
                Team team = player.getScoreboardTeam();

                boolean isAFK = team != null && team.getName().equalsIgnoreCase("ZZ_AFK");

                if (!isAFK) {
                    state.addPlaytimeTicks(uuid, 1); // 1 tick per server tick
                }
            }
        });
    }

    public void fromTag(NbtCompound tag) {
        NbtCompound players = tag.getCompound("players").orElse(new NbtCompound());

        for (String key : players.getKeys()) {
            UUID uuid = UUID.fromString(key);
            long time = players.getLong(key).orElse(0L);

            playtime.put(uuid, time);
        }
    }

    public NbtCompound toTag(NbtCompound tag) {
        NbtCompound players = new NbtCompound();

        for (var entry : playtime.entrySet()) {
            players.putLong(entry.getKey().toString(), entry.getValue());
        }

        tag.put("players", players);
        return tag;
    }
}