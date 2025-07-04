package com.sarkhan.backend.model.enums;

import lombok.Getter;

@Getter
public enum Color {
    RED("#FF0000", 255, 0, 0),
    GREEN("#008000", 0, 128, 0),
    BLUE("#0000FF", 0, 0, 255),
    YELLOW("#FFFF00", 255, 255, 0),
    BLACK("#000000", 0, 0, 0),
    WHITE("#FFFFFF", 255, 255, 255),
    ORANGE("#FFA500", 255, 165, 0),
    PURPLE("#800080", 128, 0, 128),
    PINK("#FFC0CB", 255, 192, 203),
    BROWN("#A52A2A", 165, 42, 42),
    GRAY("#808080", 128, 128, 128),
    CYAN("#00FFFF", 0, 255, 255),
    MAGENTA("#FF00FF", 255, 0, 255),
    NAVY("#000080", 0, 0, 128),
    TEAL("#008080", 0, 128, 128),
    LIME("#00FF00", 0, 255, 0),
    OLIVE("#808000", 128, 128, 0),
    MAROON("#800000", 128, 0, 0),
    SILVER("#C0C0C0", 192, 192, 192),
    GOLD("#FFD700", 255, 215, 0),
    BEIGE("#F5F5DC", 245, 245, 220),
    TURQUOISE("#40E0D0", 64, 224, 208),
    VIOLET("#EE82EE", 238, 130, 238),
    INDIGO("#4B0082", 75, 0, 130),
    IVORY("#FFFFF0", 255, 255, 240),
    CHARCOAL("#36454F", 54, 69, 79),
    CORAL("#FF7F50", 255, 127, 80),
    CRIMSON("#DC143C", 220, 20, 60),
    SALMON("#FA8072", 250, 128, 114),
    KHAKI("#F0E68C", 240, 230, 140),
    TAN("#D2B48C", 210, 180, 140),
    PLUM("#DDA0DD", 221, 160, 221),
    MINT("#98FF98", 152, 255, 152),
    PEACH("#FFE5B4", 255, 229, 180),
    LAVENDER("#E6E6FA", 230, 230, 250),
    BRONZE("#CD7F32", 205, 127, 50),
    COPPER("#B87333", 184, 115, 51),
    AMBER("#FFBF00", 255, 191, 0),
    EMERALD("#50C878", 80, 200, 120),
    RUBY("#E0115F", 224, 17, 95),
    SAPPHIRE("#0F52BA", 15, 82, 186),
    MUSTARD("#FFDB58", 255, 219, 88),
    CHOCOLATE("#D2691E", 210, 105, 30),
    SNOW("#FFFAFA", 255, 250, 250),
    SLATE("#708090", 112, 128, 144),
    SEPIA("#704214", 112, 66, 20),
    SKY("#87CEEB", 135, 206, 235),
    AZURE("#F0FFFF", 240, 255, 255),
    SAND("#C2B280", 194, 178, 128),
    ICE("#E0FFFF", 224, 255, 255),
    ALMOND("#EFDECD", 239, 222, 205);

    private final String hexCode;
    private final int red, green, blue;

    Color(String hexCode, int red, int green, int blue) {
        this.hexCode = hexCode;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public String toRgbString() {
        return String.format("rgb(%d, %d, %d)", red, green, blue);
    }

    public static Color fromHex(String hex) {
        for (Color color : values()) {
            if (color.hexCode.equalsIgnoreCase(hex)) {
                return color;
            }
        }
        throw new IllegalArgumentException("No color with hex " + hex + " found.");
    }
}
