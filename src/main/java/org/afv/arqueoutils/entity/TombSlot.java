package org.afv.arqueoutils.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;

public record TombSlot(int slot, ItemStack stack) {

    public static final Codec<TombSlot> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            Codec.INT.fieldOf("slot").forGetter(TombSlot::slot),
                            ItemStack.CODEC.fieldOf("stack").forGetter(TombSlot::stack)
                    ).apply(instance, TombSlot::new)
            );
}