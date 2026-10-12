package org.afv.arqueoutils.client.utils;

public class GlobalBWStateClient {
    private static boolean enabled = false;

    public static boolean getEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean value) {
        enabled = value;
    }
}