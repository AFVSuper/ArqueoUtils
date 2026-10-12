package org.afv.arqueoutils.world;

import net.minecraft.util.math.BlockPos;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DeathEntry {
    private final String playerName;
    private final String timestampUTC;
    private final int x, y, z;
    private final String dimension;
    private final long time;

    public DeathEntry(String playerName, BlockPos pos, String dimension, long time) {
        this.time = time;
        this.playerName = playerName;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.dimension = dimension;

        this.timestampUTC = DateTimeFormatter.ISO_INSTANT
                .format(Instant.now().atOffset(ZoneOffset.UTC));
    }

    public long getTime() {
        return time;
    }

    public String getDimension() {
        return dimension;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getTimestampUTC() {
        return timestampUTC;
    }

    public String getLocString() {
        return x + ", " + y + ", " + z;
    }
}