package org.afv.arqueoutils.criterion;

import com.mojang.serialization.Codec;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class InvisFrameCriterion extends AbstractCriterion<InvisFrameCriterion.Conditions> {

    @Override
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player) {
        this.trigger(player, conditions -> true);
    }

    public record Conditions(Optional<LootContextPredicate> player)
            implements AbstractCriterion.Conditions {

        public static final Codec<Conditions> CODEC =
                LootContextPredicate.CODEC
                        .optionalFieldOf("player")
                        .xmap(Conditions::new, Conditions::player)
                        .codec();

        public boolean test() {
            return true;
        }

        @Override
        public Optional<LootContextPredicate> player() {
            return player;
        }
    }
}
