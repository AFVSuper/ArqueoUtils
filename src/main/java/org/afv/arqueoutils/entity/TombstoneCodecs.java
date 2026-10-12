package org.afv.arqueoutils.entity;

import com.mojang.serialization.Codec;

import java.util.List;

public class TombstoneCodecs {
    public static final Codec<List<TombSlot>> TOMB_SLOT_LIST_CODEC =
            TombSlot.CODEC.listOf();
}