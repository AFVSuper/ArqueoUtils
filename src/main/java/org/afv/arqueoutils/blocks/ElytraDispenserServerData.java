package org.afv.arqueoutils.blocks;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Uuids;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class ElytraDispenserServerData {
    private final Set<UUID> rewardedPlayers = new ObjectLinkedOpenHashSet<>();
    boolean dirty;

    static Codec<ElytraDispenserServerData> codec = RecordCodecBuilder.create((instance) ->
            instance.group(Uuids.LINKED_SET_CODEC.lenientOptionalFieldOf("rewarded_players", Set.of())
                    .forGetter((data) -> data.rewardedPlayers))
                    .apply(instance, ElytraDispenserServerData::new));

    ElytraDispenserServerData(Set<UUID> rewardedPlayers) {
        this.rewardedPlayers.addAll(rewardedPlayers);
    }

    ElytraDispenserServerData() {}

    public boolean hasRewardedPlayer(PlayerEntity player) {
        return rewardedPlayers.contains(player.getUuid());
    }

    @VisibleForTesting
    public void markPlayerAsRewarded(PlayerEntity player) {
        this.rewardedPlayers.add(player.getUuid());
        if (this.rewardedPlayers.size() > 128) {
            Iterator<UUID> iterator = this.rewardedPlayers.iterator();
            if (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
        }

        this.markDirty();
    }

    void copyFrom(ElytraDispenserServerData data) {
        this.rewardedPlayers.clear();
        this.rewardedPlayers.addAll(data.rewardedPlayers);
    }

    private void markDirty() {
        this.dirty = true;
    }
}
