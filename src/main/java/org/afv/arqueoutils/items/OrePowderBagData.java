package org.afv.arqueoutils.items;

import com.mojang.serialization.Codec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record OrePowderBagData(long[] amounts) {
    public static final int SIZE = 9;
    public static final OrePowderBagData EMPTY = new OrePowderBagData(new long[SIZE]);

    public static final Codec<OrePowderBagData> CODEC = Codec.LONG.listOf().xmap(
            list -> {
                long[] arr = new long[SIZE];
                for (int i = 0; i < Math.min(list.size(), SIZE); i++) {
                    arr[i] = list.get(i);
                }
                return new OrePowderBagData(arr);
            },
            data -> {
                List<Long> list = new ArrayList<>(SIZE);
                for (long v : data.amounts) {
                    list.add(v);
                }
                return list;
            }
    );

    public OrePowderBagData {
        if (amounts.length != SIZE) {
            amounts = Arrays.copyOf(amounts, SIZE);
        }
    }

    public long get(int typeIndex) {
        return amounts[typeIndex];
    }

    public OrePowderBagData withDelta(int typeIndex, long delta) {
        long[] copy = amounts.clone();
        long cur = copy[typeIndex];
        long next;
        if (delta >= 0) {
            try {
                next = Math.addExact(cur, delta);
            } catch (ArithmeticException e) {
                next = Long.MAX_VALUE;
            }
        } else {
            next = cur + delta;
            if (next < 0) {
                next = 0;
            }
        }
        copy[typeIndex] = next;
        return new OrePowderBagData(copy);
    }

    public OrePowderBagData withSet(int typeIndex, long value) {
        long[] copy = amounts.clone();
        copy[typeIndex] = Math.max(0, value);
        return new OrePowderBagData(copy);
    }
}
