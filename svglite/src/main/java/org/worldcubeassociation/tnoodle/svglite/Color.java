package org.worldcubeassociation.tnoodle.svglite;

public class Color {
    public static final Color RED = new Color(255, 0, 0);
    public static final Color GREEN = new Color(0, 255,  0);
    public static final Color BLUE = new Color(0, 0, 255);
    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color GRAY = new Color(128, 128, 128);
    public final static Color YELLOW = new Color(255, 255, 0);
    public final static Color ORANGE = new Color(255, 128, 0);
    public final static Color PURPLE = new Color(124, 2, 158);
    // Megaminx MF8 scheme
    public final static Color YELLOW_GOLD = new Color(255, 204, 0);
    public final static Color BLUE_NAVY = new Color(0, 0, 179);
    public final static Color RED_VERMILION = new Color(221, 0, 0);
    public final static Color GREEN_DARK = new Color(0, 102, 0);
    public final static Color PURPLE_ORCHID = new Color(138, 26, 255);
    public final static Color GRAY_MEDIUM = new Color(153, 153, 153);
    public final static Color YELLOW_CREAM = new Color(255, 255, 179);
    public final static Color PINK = new Color(255, 153, 255);
    public final static Color GREEN_LIME = new Color(113, 230, 0);
    public final static Color ORANGE_TANGERINE = new Color(255, 132, 51);
    public final static Color BLUE_SKY = new Color(136, 221, 255);
    // Clock contrast colors
    public final static Color BLUE_DEEP = new Color(17, 51, 102);
    public final static Color BLUE_BRIGHT = new Color(204, 221, 238);
    public final static Color BLUE_ICE = new Color(136, 170, 204);
    public final static Color BLUE_ASPHALT = new Color(68, 102, 153);
    public final static Color YELLOW_SUNFLOWER = new Color(255, 204, 68);
    public final static Color ORANGE_BRONZE = new Color(204, 102, 0);

    private int r, g, b, a;
    public Color(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color(int r, int g, int b) {
        this(r, g, b, 255);
    }

    public Color(int rgba) {
        this((rgba >>> 8*2) & 0xff,
             (rgba >>> 8) & 0xff,
             rgba & 0xff,
             (rgba >>> 8*3) & 0xff);
    }

    private static int hexToRGB(String htmlHex) throws InvalidHexColorException {
        if(htmlHex.startsWith("#")) {
            htmlHex = htmlHex.substring(1);
        }

        switch(htmlHex.length()) {
            case 3:
                char c0 = htmlHex.charAt(0);
                char c1 = htmlHex.charAt(1);
                char c2 = htmlHex.charAt(2);
                htmlHex = "" + c0 + c0 + c1 + c1 + c2 + c2;
            case 6:
                return Integer.parseInt(htmlHex, 16);
            default:
                throw new InvalidHexColorException(htmlHex);
        }
    }

    public Color(String htmlHex) throws InvalidHexColorException {
        this(hexToRGB(htmlHex));
    }

    public Color invertColor() {
        return new Color(255 - r, 255 - g, 255 - b);
    }

    public String toHex() {
        return Integer.toHexString(0x1000000 | (getRGB() & 0xffffff)).substring(1);
    }

    public int getRed() {
        return r;
    }

    public int getGreen() {
        return g;
    }

    public int getBlue() {
        return b;
    }

    public int getRGB() {
        return (a << 8*3) | (r << 8*2) | (g << 8) | b;
    }

    public String toString() {
        return "<color #" + toHex() + ">";
    }

}
