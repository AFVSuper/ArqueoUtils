package org.afv.arqueoutils.blocks;

import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;

public enum CoreColor implements StringIdentifiable {
    WHITE("white", TextColor.fromFormatting(Formatting.WHITE)),
    ORANGE("orange", TextColor.fromRgb(0xff8f1f)),
    MAGENTA("magenta", TextColor.fromRgb(0xff1ffb)),
    LIGHT_BLUE("light_blue", TextColor.fromFormatting(Formatting.AQUA)),
    YELLOW("yellow", TextColor.fromFormatting(Formatting.YELLOW)),
    LIME("lime", TextColor.fromFormatting(Formatting.GREEN)),
    PINK("pink", TextColor.fromRgb(0xff8ffd)),
    GRAY("gray", TextColor.fromFormatting(Formatting.GRAY)),
    LIGHT_GRAY("light_gray", TextColor.fromRgb(0xbdbdbd)),
    CYAN("cyan", TextColor.fromFormatting(Formatting.DARK_AQUA)),
    PURPLE("purple", TextColor.fromRgb(0xc852ff)),
    BLUE("blue", TextColor.fromFormatting(Formatting.BLUE)),
    BROWN("brown", TextColor.fromRgb(0x8f552c)),
    GREEN("green", TextColor.fromFormatting(Formatting.DARK_GREEN)),
    RED("red", TextColor.fromFormatting(Formatting.RED)),
    BLACK("black", TextColor.fromFormatting(Formatting.DARK_GRAY));

    private final String name;
    private final TextColor textColor;
    CoreColor(String name, TextColor textColor) {
        this.name = name;
        this.textColor = textColor;
    }

    public TextColor getTextColor() {
        return textColor;
    }

    public static CoreColor getById(String id) {
        for (CoreColor c : CoreColor.values()) {
            if (c.name.equalsIgnoreCase(id)) return c;
        }
        return null;
    }

    @Override
    public String asString() { return name; }
}