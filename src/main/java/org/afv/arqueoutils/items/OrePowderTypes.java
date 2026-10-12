package org.afv.arqueoutils.items;

import java.util.List;

public final class OrePowderTypes {
    private OrePowderTypes() {}

    /** Order matches powder indices in the ore powder bag component and GUI rows. */
    public static final List<String> ORDER = List.of(
            "coal", "quartz", "gold", "copper", "redstone", "diamond", "iron", "lapis", "emerald"
    );

    public static int getColor(String type) {
        int color = 0;
        switch (type) {
            case "coal" -> color = 0xFFbdbdbd;
            case "copper" -> color = 0xFFff8a4f;
            case "iron" -> color = 0xFFFFFFFF;
            case "gold" -> color = 0xFFffff4f;
            case "redstone" -> color = 0xFFff6969;
            case "lapis" -> color = 0xFF8c92ff;
            case "quartz" -> color = 0xFFffcfcf;
            case "diamond" -> color = 0xFF96efff;
            case "emerald" -> color = 0xFF88ff78;
            default -> {}
        }
        return color;
    }

    public static int indexOf(String type) {
        return ORDER.indexOf(type);
    }

    public static boolean isValid(String type) {
        return type != null && indexOf(type) >= 0;
    }
}
