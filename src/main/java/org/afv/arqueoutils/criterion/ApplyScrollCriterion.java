package org.afv.arqueoutils.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class ApplyScrollCriterion extends AbstractCriterion<ApplyScrollCriterion.Conditions> {

    @Override
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    // Trigger with value
    public void trigger(ServerPlayerEntity player, int quality) {
        this.trigger(player, conditions -> conditions.matches(quality));
    }

    public record Conditions(Optional<LootContextPredicate> player, int quality)
            implements AbstractCriterion.Conditions {

        public static final Codec<Conditions> CODEC =
                RecordCodecBuilder.create(instance -> instance.group(
                        LootContextPredicate.CODEC
                                .optionalFieldOf("player")
                                .forGetter(Conditions::player),

                        Codec.INT
                                .fieldOf("quality")
                                .forGetter(Conditions::quality)
                ).apply(instance, Conditions::new));

        public boolean matches(int value) {
            return value >= quality; // default behavior: "at least"
        }

        @Override
        public Optional<LootContextPredicate> player() {
            return player;
        }
    }
}
