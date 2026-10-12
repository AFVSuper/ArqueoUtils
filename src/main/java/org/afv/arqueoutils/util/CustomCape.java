package org.afv.arqueoutils.util;

public enum CustomCape {
    ARQUEO4("arqueo4"),
    ARQUEO("arqueo_cape"),
    TATTERED_RED("tattered_red"),
    DARK_MIGRATION("dark_migration"),
    PENGUIN("penguin");

    private final String id;

    CustomCape(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getPath() {
        return "entity/cape/" + id;
    }

    public static CustomCape getById(String id) {
        for (CustomCape cape : CustomCape.values()) {
            if (cape.getId().equals(id)) return cape;
        }
        return null;
    }
}
